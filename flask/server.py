from flask import Flask, request
from analyzer import pitch_score
from tempfile import TemporaryFile
from pymongo import MongoClient
from redis import Redis
import news_recommender
from translate import NLP

nlp = NLP()

# test
db = MongoClient('127.0.0.1',
    username='droomii',
    password='Data19!@',
    authSource='admin',
    authMechanism='SCRAM-SHA-256').MyDB

rd = Redis(host='localhost', password='Data19!@')

app = Flask(__name__)

@app.route('/apiTest',methods=['POST'])
def api_test():
    return str(request.form)


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
        date = rd.get('todayNewsUrl').decode('utf-8')
        if example is None:
            return 'example is null!!'
        if answer is None:
            return 'answer is null!!'
    else:
        return 'file is null!!'
    return pitch_score(example, answer, date, pitch_sample=50, time_sample=100)


@app.route('/scoreTranslate', methods=['POST'])
def compare_translate():
    
    original = request.form['original']
    user_answer = request.form['userAnswer']
    return nlp.compare(original, user_answer)


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
    
@app.route('/saveRelatedArticles')
def save_related_articles():
    news_recommender.save_related_articles(db)
    return 'success'

if __name__=='__main__':
    app.run()