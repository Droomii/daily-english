from flask import Flask, request
from analyzer import pitch_score
from tempfile import TemporaryFile



app = Flask(__name__)

@app.route('/')
def hello_world():
    return 'Hello, World!'

@app.route("/me")
def me_api():
    return {
        "username": 'dowoo',
        "theme": 'hello',
        "image": 'wow',
    }

@app.route('/score', methods=['POST'])
def score():
    example = None
    answer = None
    if request.form:
        example = request.form['idx']
        answer = request.form['data']
        date = request.form['date']
        if example is None:
            return 'example is null!!'
        if answer is None:
            return 'answer is null!!'
    else:
        return 'file is null!!'
    example_pitch_matrix, pitch_matrix, score = pitch_score(example, answer, date, pitch_sample=50, time_sample=100)
    a = {}
    a['example_pitch_matrix'] = example_pitch_matrix
    a['pitch_matrix'] = pitch_matrix
    a['score'] = score
    return a

@app.route('/score2', methods=['POST'])
def score2():
    return str(request.form)

@app.route('/tts')
def tts():
    import google.cloud.texttospeech as tts
    client = tts.TextToSpeechClient()
    
    synthesis_input = tts.SynthesisInput(text="Is this a sample sentence?")
    
    voice = tts.VoiceSelectionParams(
        language_code="en-US",
        name="en-US-Wavenet-D"
    )
    
    audio_config = tts.AudioConfig(audio_encoding=tts.AudioEncoding.LINEAR16)
    
    response = client.synthesize_speech(input=synthesis_input, voice=voice, audio_config=audio_config)
    
    with open("/daily-english/texttospeech/sampletest.wav", "wb") as out:
        out.write(response.audio_content)
    
    return "success"