<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>날씨알리미</title>
<script src="/js/annyang.js"></script>
<script src="/js/jquery-3.5.0.min.js"></script>

<script type="text/javascript">
annyang.start({
	autoRestart : true,
	continuous : true
})

var recognition = annyang.getSpeechRecognizer();

var final_transcript = "";

recognition.interimResults = false;

recognition.onresult = function(event){
	var interim_transcript = "";
	final_transcript = "";
	for(var i = event.resultIndex; i< event.results.length; ++i){
		if (event.results[i].isFinal){
			final_transcript += event.results[i][0].transcript;
		}
	}
	$("#view_msg").html(final_transcript);
	$("#send_msg").val(final_transcript);
	
	$.ajax({
		url : "/weather/getWeather.do",
		type : "post",
		dataType : "JSON",
		data : $('form').serialize(),
		success : function(json){
			var res = "";
			res += final_transcript.indexOf("오늘") > -1 ? "오늘 " : final_transcript.indexOf("내일") > -1 ? "내일 " : "";
			res += final_transcript.indexOf("서울") > -1 ? "서울 " : "전국 ";
			res += "날씨는 다음과 같습니다 : <br><br>"
			res += final_transcript.indexOf("오늘") > -1 ? json.today : final_transcript.indexOf("내일") > -1 ? json.tomorrow :
				"오늘 : " + json.today + "<br>"+
				"내일 : " + json.tomorrow + "<br>"+
				"모레 : " + json.twodays + "<br>";
				
			$("#weather_result").html(res);
		}
	})
}
</script>
</head>
<body>
<h1>날씨 도우미</h1>
<hr>
<div id="view_msg"></div>
<br>
<h1>날씨 불러오기 결과</h1>
<hr>
<div id="weather_result"></div>
<form name="form" method="post">
	<input type="hidden" name="send_msg" id="send_msg">
</form>
</body>
</html>