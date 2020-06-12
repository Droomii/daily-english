from flask import Flask, request
from analyzer import pitch_score
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
    print('hello world!!')
    example = request.files.get('example')
    answer = request.files.get('answer')
    if example is None:
        return 'example is null!!'
    if answer is None:
        return 'answer is null!!'
    
    example_pitch_matrix, pitch_matrix, score = pitch_score(example, answer, pitch_sample=50, time_sample=100)
    a = {}
    a['example_pitch_matrix'] = example_pitch_matrix
    a['pitch_matrix'] = pitch_matrix
    a['score'] = score
    return a
