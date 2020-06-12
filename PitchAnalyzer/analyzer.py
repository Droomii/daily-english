import parselmouth

import numpy as np
from scipy import interpolate
import scipy.spatial as sp
import scipy.io.wavfile as wav
from tempfile import TemporaryFile
import base64

def pitch_score(example, answer, pitch_sample=20, time_sample=20, tolerance=0.5):
    # TODO : example, answer parameter are Base64.
    # Need to convert it into numpy ndarray
    
    # convert base64 string to ascii bytes
    example_ascii = example.encode('ascii')
    asnwer_ascii = answer.encode('ascii')
    
    # decode base64 to bytes
    example_decoded = base64.b64decode(example_ascii)
    answer_decoded = base64.b64decode(answer_ascii)
    
    # make temporary files
    example_temp_file = TemporaryFile()
    answer_temp_file = TemporaryFile()
    
    # write bytes to tempfile
    example_temp_file.write(example_decoded)
    answer_temp_file.write(answer_decoded)
    example_temp_file.seek(0)
    answer_temp_file.seek(0)
    
    # read decoded sound
    example_snd = wav.read(example_temp_file)
    answer_snd = wav.read(answer_temp_file)
    
    example_snd = parselmouth.Sound(example_snd[1], sampling_frequency=example_snd[0])
    answer_snd = parselmouth.Sound(answer_snd[1], sampling_frequency=answer_snd[0])
    
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
    example_trimmed_pitch_values = example_pitch_values[example_pitch_start_index:example_pitch_end_index+1]



    # divide pitch values by pitch median(to get relational values)
    example_trimmed_pitch_values /= np.nanmedian(example_trimmed_pitch_values)

    # linear interpolation of nans
    example_trimmed_pitch_values = fill_nan(example_trimmed_pitch_values)


    # trim time and normalize
    example_xs = example_pitch.xs()[example_pitch_start_index:example_pitch_end_index+1]
    example_xs -= np.min(example_xs)
    example_normalized_xs = example_xs / np.max(example_xs)

    example_pitch_max = np.max(example_trimmed_pitch_values)
    example_pitch_min = np.min(example_trimmed_pitch_values)
    
    # ------------------------ end of example pitch-------------------------
    
    # get min and max of all
    all_min = min(pitch_min, example_pitch_min)
    all_max = max(pitch_max, example_pitch_max)
    # normalize both pitch values to between 0 and 1
    
    trimmed_pitch_values -= all_min
    trimmed_pitch_values /= (all_max - all_min)
    print(np.max(example_trimmed_pitch_values))
    example_trimmed_pitch_values -= all_min
    example_trimmed_pitch_values /= (all_max - all_min)
    
    # pitch values 0~sample
    trimmed_pitch_values *= (pitch_sample-1)
    example_trimmed_pitch_values *= (pitch_sample-1)
    print(np.max(example_trimmed_pitch_values))
    
    
    
    
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
                    
    score = 1 - sum(a) / np.sum(grid1)
    
    return example_pitch_matrix, pitch_matrix, score
