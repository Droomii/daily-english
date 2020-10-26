from flask import Flask, request

app = Flask(__name__)

@app.route('/apiTest',methods=['POST'])
def api_test():
    return str(request.form)

if __name__=='__main__':
    app.run()