import parselmouth

import numpy as np
from scipy import interpolate
import scipy.spatial as sp
import scipy.io.wavfile as wav
import tempfile
import base64
import os
import glob

def pitch_score(example, answer, date, pitch_sample=20, time_sample=20, tolerance=0.5):
    # base64 문자열을 to ascii 바이트로 변경
    answer_ascii = answer.split(',')[1].encode('ascii')
    
    # base64 -> bytes
    answer_decoded = base64.b64decode(answer_ascii)
    
    # 임시파일 생성
    answer_temp_file = tempfile.mkstemp()[1]
    # 임시파일에 바이트 쓰기
    with open(answer_temp_file, 'wb') as f:
        f.write(answer_decoded)
    
    # ogg파일을 wav파일로 컨버팅
    os.system('mkvextract {0} tracks 0:{0}.ogg'.format(answer_temp_file))
    os.system('ffmpeg -i {0}.ogg {0}.wav'.format(answer_temp_file))

    # 사운드 파일 불러오기
    example_snd = parselmouth.Sound('/daily-english/tts/{}/{}.wav'.format(date, example))
    answer_snd = parselmouth.Sound(answer_temp_file + '.wav')
    
    # 임시 파일 삭제
    os.remove(answer_temp_file)
    os.remove(answer_temp_file + '.wav')
    
    # 음정 분석
    example_pitch = example_snd.to_pitch_ac(very_accurate=False, silence_threshold=0.1, voicing_threshold=0.50).smooth()
    pitch = answer_snd.to_pitch_ac(very_accurate=False, silence_threshold=0.1, voicing_threshold=0.50).smooth()
    
    # 음정 값을 상대적 수치로 변환
    normalized_xs, trimmed_pitch_values = normalize_pitch(pitch)
    example_normalized_xs, example_trimmed_pitch_values = normalize_pitch(example_pitch)
    
    # 사용자 음정과 원어민 음정의 최솟값, 최댓값 구하기
    pitch_max = np.nanmax(trimmed_pitch_values)
    pitch_min = np.nanmin(trimmed_pitch_values)
    example_pitch_max = np.nanmax(example_trimmed_pitch_values)
    example_pitch_min = np.nanmin(example_trimmed_pitch_values)
    all_min = min(pitch_min, example_pitch_min)
    all_max = max(pitch_max, example_pitch_max)
    
    # 함수 반환용 변수 선언
    answer_y = np.copy(trimmed_pitch_values)
    example_y = np.copy(example_trimmed_pitch_values)
    answer_y[np.isnan(answer_y)] = -1
    example_y[np.isnan(example_y)] = -1
    
    # 음정값 0~1(실수) 사이로 정규화
    trimmed_pitch_values -= all_min
    trimmed_pitch_values /= (all_max - all_min)
    example_trimmed_pitch_values -= all_min
    example_trimmed_pitch_values /= (all_max - all_min)
    
    # 음정값 샘플링(0~샘플값, 정수)
    trimmed_pitch_values *= (pitch_sample-1)
    example_trimmed_pitch_values *= (pitch_sample-1)
    
    
    # 점수 계수 책정
    score_tolerance = int(pitch_sample * tolerance)

    #--------------- resampling example pitch --------------------
    
    # 시간에 따른 음정을 grid matrix로 변경
    example_x = np.arange(len(example_trimmed_pitch_values))
    example_new_x = np.linspace(0, len(example_trimmed_pitch_values), time_sample)
    example_resampled_pitch_values = np.round_(np.interp(example_new_x, example_x, example_trimmed_pitch_values))

    example_pitch_matrix = np.zeros((time_sample, pitch_sample), dtype=int)

    for i in range(time_sample):
        try:
            pv = int(example_resampled_pitch_values[i])
            example_pitch_matrix[i, pv] = score_tolerance
        except ValueError:
            continue
    
    #--------------- resampling pitch --------------------
    x = np.arange(len(trimmed_pitch_values))
    new_x = np.linspace(0, len(trimmed_pitch_values), time_sample)
    resampled_pitch_values = np.round_(np.interp(new_x, x, trimmed_pitch_values))

    resampled_pitch_values
    pitch_matrix = np.zeros((time_sample, pitch_sample), dtype=int)
    
    score_tolerance = int(pitch_sample * tolerance)
    
    for i in range(time_sample):
        try:
            pv = int(resampled_pitch_values[i])
            pitch_matrix[i, pv] = score_tolerance
            for k in range(1, score_tolerance):
                if (pitch_sample-1)-(pv-k) < pitch_sample:
                    pitch_matrix[i, pv-k] = score_tolerance-k
                if (pitch_sample-1)-(pv+k) > -1:
                    pitch_matrix[i, pv+k] = score_tolerance-k
        except ValueError:
            continue
    
    # 사용자 음성과 원어민 음성을 대조하여 점수 측정
    a = np.ndarray.flatten(example_pitch_matrix - pitch_matrix)
    a[a<0] = 0
    score = 1 - sum(a) / np.sum(example_pitch_matrix)
    print("score : {}".format(score))

    example_dynamics_range = np.nanquantile(example_y[example_y>0], [0.1, 0.9])
    example_dynamics = np.diff(example_dynamics_range)[0]
    
    example_dynamics = np.nanstd(example_y[example_y>0])
    
    answer_dynamics_range = np.nanquantile(answer_y[answer_y>0], [0.1, 0.9])
    answer_dynamics = np.diff(answer_dynamics_range)[0]
    answer_dynamics = np.nanstd(answer_y[answer_y>0])
    
    dynamics_score = answer_dynamics / example_dynamics
    dynamics_score = 1 if dynamics_score > 1 else dynamics_score
    dynamics_score *= 100
    
    # 결과값을 저장하기 위한 딕셔너리
    result_dict = {'example_x': example_normalized_xs.tolist(),
                   'example_y' : example_y.tolist(),
                   'answer_x' : normalized_xs.tolist(),
                   'answer_y' : answer_y.tolist(),
                   'score' : score,
                   'answer_temp_file' : answer_temp_file+'.ogg',
                   'dynamics_score' : dynamics_score,
                   'example_dynamics' : example_dynamics,
                   'answer_dynamics' : answer_dynamics,
                   'answer_range' : answer_dynamics_range.tolist(),
                   'example_range' : example_dynamics_range.tolist()}

    return result_dict

# 음정을 정규화 시키는 함수
def normalize_pitch(pitch):
    
    # 음정값 불러오기
    pitch_values = pitch.selected_array['frequency']
    pitch_values[pitch_values==0] = np.nan

    # 음정의 중앙값 계산
    median = np.nanmedian(pitch_values)
    
    # 1옥타브 이하 음정, 1옥타브 이상의 음정을 아웃라이어로 지정
    low_outlier = median / 2
    high_outlier = median * 2
    
    # 기준에 따른 아웃라이어 제거
    pitch_values[pitch_values > high_outlier] = np.nan
    pitch_values[pitch_values < low_outlier] = np.nan

    # 목소리 시작점과 종료점 찾기
    pitch_start_index = np.where(~np.isnan(pitch_values))[0][0]
    pitch_end_index = np.where(~np.isnan(pitch_values))[0][-1]

    # 목소리 시작점 이전과 종료점 이후 잘라내기
    trimmed_pitch_values = pitch_values[pitch_start_index:pitch_end_index+1]

    # 음정을 중앙값으로 나누기(중앙값과 가까울 수록 1에 수렴)
    trimmed_pitch_values /= np.nanmedian(trimmed_pitch_values)
    
    # 오인식으로 인해 급격히 변화하는 음정 아웃라이어 제거
    needs_more_removal = True
    while needs_more_removal:
        
        needs_more_removal = False
        
        time_step = pitch.get_time_step()
        last_pitch = None
        for i in range(len(trimmed_pitch_values)):
            if last_pitch is None:
                if np.isnan(trimmed_pitch_values[i]):
                    continue
                last_pitch = trimmed_pitch_values[i]
            else:
                if np.isnan(trimmed_pitch_values[i]):
                    last_pitch = None
                else:
                    current_pitch = trimmed_pitch_values[i]
                    if current_pitch / last_pitch > 1.05 or current_pitch / last_pitch < 0.95:
                        trimmed_pitch_values[i] = np.nan
                        trimmed_pitch_values[i-1] = np.nan
                        needs_more_removal = True
                    last_pitch = current_pitch

        trimmed_pitch_values = fill_nan(trimmed_pitch_values)
    
    # 가공된 값을 원본에 덮어씌우기
    pitch_values[pitch_start_index:pitch_end_index+1] = trimmed_pitch_values
    
    # 값 정규화
    xs = pitch.xs()
    xs -= np.min(xs)
    normalized_xs = xs / np.max(xs)
    
    return normalized_xs, pitch_values



# 음정이 없는 구간을 보간하는 함수
def fill_nan(A):
    inds = np.arange(A.shape[0])
    good = np.where(np.isfinite(A))
    f = interpolate.interp1d(inds[good], A[good],bounds_error=False)
    B = np.where(np.isfinite(A),A,f(inds))
    return B
    
