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
    # convert base64 string to ascii bytes
    answer_ascii = answer.split(',')[1].encode('ascii')
    
    # decode base64 to bytes
    answer_decoded = base64.b64decode(answer_ascii)
    
    # make temporary files
    answer_temp_file = tempfile.mkstemp()[1]
    # write bytes to tempfile
    with open(answer_temp_file, 'wb') as f:
        f.write(answer_decoded)
    
    os.system('mkvextract {0} tracks 0:{0}.ogg'.format(answer_temp_file))
    os.system('ffmpeg -i {0}.ogg {0}.wav'.format(answer_temp_file))

    example_snd = parselmouth.Sound('/daily-english/tts/{}/{}.wav'.format(date, example))
    answer_snd = parselmouth.Sound(answer_temp_file + '.wav')
    
    os.remove(answer_temp_file)
    os.remove(answer_temp_file + '.wav')
    
    # get pitch of sounds
    example_pitch = example_snd.to_pitch_ac(very_accurate=False, silence_threshold=0.1, voicing_threshold=0.50).smooth()
    pitch = answer_snd.to_pitch_ac(very_accurate=False, silence_threshold=0.1, voicing_threshold=0.50).smooth()
    
    # normalize pitch
    normalized_xs, trimmed_pitch_values = normalize_pitch(pitch)
    example_normalized_xs, example_trimmed_pitch_values = normalize_pitch(example_pitch)
    
    # get min max of two pitches
    pitch_max = np.nanmax(trimmed_pitch_values)
    pitch_min = np.nanmin(trimmed_pitch_values)
    example_pitch_max = np.nanmax(example_trimmed_pitch_values)
    example_pitch_min = np.nanmin(example_trimmed_pitch_values)
    all_min = min(pitch_min, example_pitch_min)
    all_max = max(pitch_max, example_pitch_max)
    
    answer_y = np.copy(trimmed_pitch_values)
    example_y = np.copy(example_trimmed_pitch_values)
    answer_y[np.isnan(answer_y)] = -1
    example_y[np.isnan(example_y)] = -1
    
    trimmed_pitch_values -= all_min
    trimmed_pitch_values /= (all_max - all_min)
    example_trimmed_pitch_values -= all_min
    example_trimmed_pitch_values /= (all_max - all_min)
    
    # pitch values 0~sample
    trimmed_pitch_values *= (pitch_sample-1)
    example_trimmed_pitch_values *= (pitch_sample-1)
    
    
    # score tolerance
    score_tolerance = int(pitch_sample * tolerance)

    #--------------- resampling example pitch --------------------
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
    
def normalize_pitch(pitch):

    pitch_values = pitch.selected_array['frequency']
    pitch_values[pitch_values==0] = np.nan

    # remove outlier
    median = np.nanmedian(pitch_values)
    low_outlier = median / 2
    high_outlier = median * 2
    pitch_values[pitch_values > high_outlier] = np.nan
    pitch_values[pitch_values < low_outlier] = np.nan

    #get start and end pitch value index
    pitch_start_index = np.where(~np.isnan(pitch_values))[0][0]
    pitch_end_index = np.where(~np.isnan(pitch_values))[0][-1]

    # trim pitch values by start and end
    trimmed_pitch_values = pitch_values[pitch_start_index:pitch_end_index+1]



    # divide pitch values by pitch median(to get relational values)
    trimmed_pitch_values /= np.nanmedian(trimmed_pitch_values)
    
    # remove outliers
    needs_more_removal = True
    while needs_more_removal:
        
        needs_more_removal = False
        # remove abnormal pitch change
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

        # linear interpolation of nans
        trimmed_pitch_values = fill_nan(trimmed_pitch_values)
    
    # insert trimmed pitch values
    pitch_values[pitch_start_index:pitch_end_index+1] = trimmed_pitch_values
    
    # trim time and normalize
    xs = pitch.xs()
    xs -= np.min(xs)
    normalized_xs = xs / np.max(xs)
    
    return normalized_xs, pitch_values



    
def fill_nan(A):
    '''
    interpolate to fill nan values
    '''
    inds = np.arange(A.shape[0])
    good = np.where(np.isfinite(A))
    f = interpolate.interp1d(inds[good], A[good],bounds_error=False)
    B = np.where(np.isfinite(A),A,f(inds))
    return B
    
