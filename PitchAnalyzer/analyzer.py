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
    # TODO : example, answer parameter are Base64.
    # Need to convert it into numpy ndarray
    
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
    
    for f in glob.glob(answer_temp_file + "*"):
        os.remove(f)
    
    # get pitch of sounds
    example_pitch = example_snd.to_pitch_ac(very_accurate=False, silence_threshold=0.1, voicing_threshold=0.50).smooth()
    pitch = answer_snd.to_pitch_ac(very_accurate=False, silence_threshold=0.1, voicing_threshold=0.50).smooth()
    
    # ------------------------ answer pitch -------------------------
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

    # normalize pitch values between 0 to 1
    #trimmed_pitch_values -= np.nanmin(trimmed_pitch_values)
    #trimmed_pitch_values /= np.nanmax(trimmed_pitch_values)

    # linear interpolation of nans
    trimmed_pitch_values = fill_nan(trimmed_pitch_values)
    
    # smoothening
    #window = 10;
    #trimmed_smoothened_list = []
    
    #for i in range(window, len(trimmed_pitch_values)):
    #    trimmed_smoothened_list.append(np.mean(trimmed_pitch_values[i-window:i]))
    #print('original length : ', len(trimmed_pitch_values))
    #print('trimmed length : ', len(trimmed_smoothened_list))
    #trimmed_pitch_values = np.array(trimmed_smoothened_list)
    
    
    
    # trim time and normalize
    xs = pitch.xs()[pitch_start_index:pitch_end_index+1]
    xs -= np.min(xs)
    normalized_xs = xs / np.max(xs)

    pitch_max = np.max(trimmed_pitch_values)
    pitch_min = np.min(trimmed_pitch_values)
    
    # --------------------------- example pitch -------------------------------
    
    example_pitch_values = example_pitch.selected_array['frequency']
    example_pitch_values[example_pitch_values==0] = np.nan

    # remove outlier
    example_median = np.nanmedian(example_pitch_values)
    example_low_outlier = example_median / 2
    example_high_outlier = example_median * 2
    example_pitch_values[example_pitch_values > example_high_outlier] = np.nan
    example_pitch_values[example_pitch_values < example_low_outlier] = np.nan

    #get start and end pitch value index
    example_pitch_start_index = np.where(~np.isnan(example_pitch_values))[0][0]
    example_pitch_end_index = np.where(~np.isnan(example_pitch_values))[0][-1]

    # trim pitch values by start and end
    example_trimmed_pitch_values = example_pitch_values



    # divide pitch values by pitch median(to get relational values)
    example_trimmed_pitch_values /= np.nanmedian(example_trimmed_pitch_values)

    # linear interpolation of nans
    example_trimmed_pitch_values = fill_nan(example_trimmed_pitch_values)
    example_trimmed_pitch_values[np.isnan(example_trimmed_pitch_values)] = 0

    #example_trimmed_smoothened_list = []
    #for i in range(window, len(example_trimmed_pitch_values)):
    #    example_trimmed_smoothened_list.append(np.mean(example_trimmed_pitch_values[i-window:i]))
    #print('original length : ', len(example_trimmed_pitch_values))
    #print('trimmed length : ', len(example_trimmed_smoothened_list))
    #example_trimmed_pitch_values = np.array(example_trimmed_smoothened_list)
    
    # trim time and normalize
    example_xs = example_pitch.xs()
    example_xs -= np.min(example_xs)
    example_normalized_xs = example_xs / np.max(example_xs)

    example_pitch_max = np.max(example_trimmed_pitch_values)
    example_pitch_min = np.min(example_trimmed_pitch_values)
    
    # ------------------------ end of example pitch-------------------------
    
    # get min and max of all
    all_min = min(pitch_min, example_pitch_min)
    all_max = max(pitch_max, example_pitch_max)
    # normalize both pitch values to between 0 and 1
    
    answer_y = np.copy(trimmed_pitch_values)
    example_y = np.copy(example_trimmed_pitch_values)
    
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
        pv = int(example_resampled_pitch_values[i])
        example_pitch_matrix[i, pv] = score_tolerance
    
    #--------------- resampling pitch --------------------
    x = np.arange(len(trimmed_pitch_values))
    new_x = np.linspace(0, len(trimmed_pitch_values), time_sample)
    resampled_pitch_values = np.round_(np.interp(new_x, x, trimmed_pitch_values))

    resampled_pitch_values
    pitch_matrix = np.zeros((time_sample, pitch_sample), dtype=int)
    
    score_tolerance = int(pitch_sample * tolerance)
    
    for i in range(time_sample):
        pv = int(resampled_pitch_values[i])
        pitch_matrix[i, pv] = score_tolerance
        for k in range(1, score_tolerance):
            if (pitch_sample-1)-(pv-k) < pitch_sample:
                pitch_matrix[i, pv-k] = score_tolerance-k
            if (pitch_sample-1)-(pv+k) > -1:
                pitch_matrix[i, pv+k] = score_tolerance-k
                    
    a = np.ndarray.flatten(example_pitch_matrix - pitch_matrix)
    a[a<0] = 0
    score = 1 - sum(a) / np.sum(example_pitch_matrix)
    print("score : {}".format(score))
    
    return example_normalized_xs.tolist(), example_y.tolist(), normalized_xs.tolist(), answer_y.tolist(), score


def fill_nan(A):
    '''
    interpolate to fill nan values
    '''
    inds = np.arange(A.shape[0])
    good = np.where(np.isfinite(A))
    f = interpolate.interp1d(inds[good], A[good],bounds_error=False)
    B = np.where(np.isfinite(A),A,f(inds))
    return B