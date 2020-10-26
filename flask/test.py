import urllib3
import json
import base64
openApiURL = "http://aiopen.etri.re.kr:8000/WiseASR/Pronunciation"
accessKey = "5e134c02-d27e-41fc-b85e-2ee4a9b4dbad"
audioFilePath = "C:\\Users\\data-08\\Documents\\audio\\0.wav"
languageCode = "english"
script = "PRONUNCIATION_SCRIPT"
 
file = open(audioFilePath, "rb")
audioContents = base64.b64encode(file.read()).decode("utf8")
file.close()
 
requestJson = {
    "access_key": accessKey,
    "argument": {
        "language_code": languageCode,
        "audio": audioContents
    }
}
 
http = urllib3.PoolManager()
response = http.request(
    "POST",
    openApiURL,
    headers={"Content-Type": "application/json; charset=UTF-8"},
    body=json.dumps(requestJson)
)
 
print("[responseCode] " + str(response.status))
print("[responBody]")
print(str(response.data,"utf-8"))
                                        
