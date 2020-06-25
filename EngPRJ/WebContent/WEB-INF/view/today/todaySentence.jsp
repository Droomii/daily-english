<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%

	String pageTitle = "읽기 연습";

%>
<!DOCTYPE html>
<html class="loading" lang="en" data-textdirection="ltr">
<head>
  <%@ include file="/WEB-INF/view/header.jsp" %>
  <link rel="stylesheet" type="text/css" href="/css/loading-bar.css" />
<script type="text/javascript" src="/js/loading-bar.js"></script>

<script src="https://cdnjs.cloudflare.com/ajax/libs/p5.js/0.8.0/p5.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/p5.js/0.8.0/addons/p5.sound.js"></script>
<!-- amchart -->
<script src="https://www.amcharts.com/lib/4/core.js"></script>
<script src="https://www.amcharts.com/lib/4/charts.js"></script>
<script src="https://www.amcharts.com/lib/4/themes/animated.js"></script>
<script src="https://unpkg.com/wavesurfer.js"></script>

<style>
	#mic{
		background-image:url("/resources/img/mic_disabled.png");
		background-size:15em 15em;
	}
	.ldBar-label{
		display:none;
		}
	#analyzingCircle {
	  border: 0.5em solid #f3f3f3; /* Light grey */
	  border-top: 0.5em solid #3498db; /* Blue */
	  border-radius: 50%;
	  width: 15em;
	  height: 15em;
	  animation: spin 2s linear infinite;
 	  visibility:hidden; 
	}
	
	@keyframes spin {
	  0% { transform: rotate(0deg); }
	  100% { transform: rotate(360deg); }
	}
	</style>
  </head>
  <body class="vertical-layout vertical-menu 2-columns   menu-expanded fixed-navbar" data-open="click" data-menu="vertical-menu" data-color="bg-chartbg" data-col="2-columns">

    <!-- fixed-top-->
    <%@ include file="/WEB-INF/view/nav.jsp" %>

    <!-- ////////////////////////////////////////////////////////////////////////////-->


    <%@ include file="/WEB-INF/view/menu.jsp" %>
    

<!-- content start -->
<div class="app-content content mt-1">
      <div class="content-wrapper">
        <div class="content-body"><!-- ../../../theme-assets/images/carousel/22.jpg -->

<!-- Header footer section start -->
<section id="header-footer">
	<div class="row match-height">
		<div class="col-lg-12 col-md-12">
			<div class="card">
				<div class="card-header">
					<h4 class="card-title mb-0" style="font-size:1.5rem">오늘의 문장 따라하기</h4>
					<div id="waveform" hidden='hidden'></div>
				</div>
				<div class="card-body pb-0">
				<p>
				<span id="no"></span>. <span id="sentence"></span>
				</p>
				<div class="text-center"><button type="button" id="listen" style="border-radius:100%;"class="btn btn-icon btn-info p-0"><i style="font-size:2rem" class="la la-volume-up"></i></button></div>
				</div>
				<div class="card-body p-0" id="timerBlock">
							<br>
							<div id="mic" style="width:15em;height:15em;margin:auto">
									<div id="progressCircle" style="width:15em;height:15em;color:white;" data-stroke="red" data-preset="circle" class="label-center" data-value="100" data-precision="0.01"></div>
									<div id="analyzingCircle"></div>
							</div>
						<div class="text-center">
							<h1 id="timer"></h1>
							<button id="startInterview" class="btn btn-danger">녹음 시작</button>
							<button hidden='hidden' id="resetInterview" class="btn btn-warning">다시 녹음</button>
							<button style="display:none" id="stopInterview" class="btn btn-danger">녹음 종료</button>
							<button style="display:none" id="submitInterview" class="btn btn-danger">제출</button>
						</div>
					</div>
				<div class="card-body p-0" id="resultBlock" hidden="hidden">
					<div class="row">
						<div class="col-12 mt-1 mb-1 text-center">
						<div id="chartdiv" style="width:100%;height:500px;"></div>
						<button id="resetInterview2" class="btn btn-warning">다시 녹음</button>
						</div>
					</div>
				</div>
				<div class="card-body p-0">
					<div class="row" id="navigator">
						<div class="col-12 mt-1 mb-1 text-center">
							<button type="button" disabled='disabled' id="prev" class="btn btn-info btn-icon ">&lt;</button>
							<button type="button" id="next" class="btn btn-info btn-icon ">&gt;</button>
						</div>
					</div>
					<div class="row">
						<div class="col-12 mb-3 text-center">
						(<span id="current">n</span> / <span id="all">n</span>)
						</div>
					</div>
				</div>
			</div>
		</div>
		
	</div>
</section>
<!-- Header footer section End -->

<!-- Content types section start -->
<!-- Content types section end -->

<!-- Text Alignment section start -->
<!-- Text Alignment section end -->

<!-- Card Headings section start -->
<!-- // Card Headings section end -->
        </div>
      </div>
    </div>
    <!-- content end -->

    <%@ include file="/WEB-INF/view/footer.jsp" %>
    <!-- END PAGE LEVEL JS-->
  </body>
  <script>
  var sentenceList;
  var sentenceIdx = 0;
  var sentenceAudioIdx;
  var audio = WaveSurfer.create({
	  container:"#waveform",
	  waveColor:"blue",
	  progressColor:"purple"
  });
  
  
  var audioIdx = -1;
  var resultJSON;
  var exampleStart;
  var exampleEnd;
  var exampleRatio;
  var chart;
  var exampleSeries;
  var answerSeries;
  var cursorX;
  var cursorY;
  $(document).ready(function(){
  	$.ajax({
			type : "POST",
			url : "getTodaySentences.do",
			dataType : "JSON",
			success : function(json) {
				sentenceList = json.res;
				$("#all").html(sentenceList.length);
				$("#sentence").html(sentenceList[0].sentence);
				sentenceAudioIdx = sentenceList[0].index * 1;
				audio.load('/audio/getTodaySentenceAudio.do?idx=0');
				audioIdx = 0;
				sentenceAudioIdx = 0;
				$("#current").html(sentenceIdx+1);
				$("#no").html(sentenceIdx+1);
			}
  	})
  })
  
  $("#next").on("click", function(){
	  $("#sentence").html(sentenceList[++sentenceIdx].sentence);
    	refresh();
    });
    
    $("#prev").on("click", function(){
    	$("#sentence").html(sentenceList[--sentenceIdx].sentence);
    	refresh();
    });
    
    function refresh(){
    	
    	$("#timerBlock").removeAttr("hidden");
    	$("#resultBlock").attr("hidden", "hidden");
    	$("#startInterview").removeAttr("hidden");
    	$("#timer").attr("hidden", "hidden");
    	$("#stopInterview").css("display", "None");
	    $("#mic").css("background-image", 'url("/resources/img/mic_disabled.png")');
	    $("#resetInterview").attr("hidden", "hidden");
	    $("#submitInterview").css("display", "None");
	    $("#analyzingCircle").css("visibility", "hidden");
	    
    	$("#current").html(sentenceIdx+1);
    	$("#no").html(sentenceIdx+1);
    	sentenceAudioIdx = sentenceList[sentenceIdx].index * 1;
    	if(sentenceIdx==0){
    		$("#prev").attr("disabled", "disabled");
    	}
    	else{
    		$("#prev").removeAttr("disabled");
    	}
    	if(sentenceIdx+1 == sentenceList.length){
    		$("#next").attr("disabled", "disabled");
    	}else{
    		$("#next").removeAttr("disabled");
    	}
    	audio.pause();
    	audio.currentTime = 0;
    }
  $("#listen").on("click", function(){
	  if(audioIdx != sentenceAudioIdx){
		  audio.destroy()
		  audio = WaveSurfer.create({
			  container:"#waveform",
			  waveColor:"blue",
			  progressColor:"purple"
		  });
	  	audio.load('/audio/getTodaySentenceAudio.do?idx=' + sentenceAudioIdx);
	  	audio.on('ready', audio.play.bind(audio));
	  	audioIdx = sentenceAudioIdx;
	  }else{
		  audio.stop();
	  }
	  audio.play();
  })
  
  </script>
  <script>
	// 음성 데이터 담는 글로벌 변수
	var globalAudioData = {};
	
	$("#resetInterview").on("click", function(){
		refresh();
	})
	$("#resetInterview2").on("click", function(){
		refresh();
	})
	
	var randQ = '';
	$("#startInterview").on("click", function(){
		audio.stop();
		$("#startInterview").attr("hidden", "hidden");
		$("#timer").removeAttr("hidden");
		//Countdown Timer
		var countDown = new Date().getTime()+4000;

		// Update the count down every 1 second
		var x = setInterval(function() {
			
			  // Get today's date and time
			    var now = new Date().getTime();
			  
				
			  // Find the distance between now and the count down date
			  var distance = countDown - now;
			
			  // Time calculations for days, hours, minutes and seconds
			  var seconds = Math.floor((distance % (1000 * 60)) / 1000);
				
			  var timer =  document.getElementById("timer");
			  // Display the result in the element with id="demo"
			 $("#navigator").attr("hidden", "hidden");
			 timer.innerHTML = "녹음 시작까지 "+seconds+"초";
			 
			  // If the count down is finished, write some text 
			  if (seconds <=0) {
			    clearInterval(x);
			    audio.play();
			    $("#resetInterview").removeAttr("hidden");
			    $("#mic").css("background-image", 'url("/resources/img/mic.png")');
			    $("#progressCircle").css("display", "inline");
			    timer.style.color = "red";
			    timer.innerHTML = Math.ceil(parseFloat(audio.getDuration()));
			    
			    var bar1 = new ldBar("#progressCircle");
				var bar2 = document.getElementById('progressCircle').ldBar;
				
				
				var constraints = {
					    audio: true,
					    video: false
					}
				navigator.mediaDevices.getUserMedia(constraints)
				  .then(stream => {
				    const mediaRecorder = new MediaRecorder(stream);
				    
				    mediaRecorder.start();

				    const audioChunks = [];
				    mediaRecorder.addEventListener("dataavailable", event => {
				      audioChunks.push(event.data);
				    });

				    mediaRecorder.addEventListener("stop", () => {
				      const audioBlob = new Blob(audioChunks);
				      console.log(audioBlob);
				      const audioUrl = URL.createObjectURL(audioBlob);
				      
				      var reader = new FileReader();
				      var base64data;
				      reader.readAsDataURL(audioBlob); 
				      reader.onloadend = function() {
				          base64data = reader.result;                
				          console.log(base64data);
					      globalAudioData.data = base64data;
					      globalAudioData.sentenceAudioIdx = sentenceAudioIdx;
				       }
				      
				    });
					
				    
				    
				    const stopButton = document.getElementById('stopInterview');
				    const resetButton = document.getElementById('resetInterview');
				    
				    resetButton.addEventListener('click', function(){
				    	stopInterview=true;
				    	console.log('stopped')
				    	$("#stopInterview").css("display", "none");
				    	$("#resetInterview").attr("hidden", "hidden");
				    	$("#submitInterview").css("display", "none");
				    })
				    var stopInterview = false;
				    var countDownTimeMilliseconds = parseInt(audio.getDuration() * 1000)
				    var countDown2 = new Date().getTime()+ countDownTimeMilliseconds;
					var x2 = setInterval(function() {
						
						  // Get today's date and time
						  var now2 = new Date().getTime();
						
						  // Find the distance between now and the count down date
						  var distance2 = (countDown2 - now2);
						
						  // Time calculations for days, hours, minutes and seconds
						  var progress = (distance2 % (1000 * 60)) / 1000;
						  var seconds2 = Math.ceil(progress);
							
						  var timer2 =  document.getElementById("timer");
						  // Display the result in the element with id="demo"
						 timer2.innerHTML = seconds2;
						  
						 bar1.set(progress*100 / countDownTimeMilliseconds * 1000);
						  // If the count down is finished, write some text 
						  if (seconds2 <=0 || stopInterview == true) {
							  if(!stopInterview)
								$("#submitInterview").css("display", "inline");
						    clearInterval(x2);
						    mediaRecorder.stop();
						    console.table(mediaRecorder);
						    $("#navigator").removeAttr("hidden");
						    $("#progressCircle").css("display", "none");
						    timer.style.color = "black";
						    timer.innerHTML = "제출 버튼을 누르시면 분석을 시작합니다";
						    bar1.set(0);
						    $("#mic").css("background-image", 'url("/resources/img/mic_disabled.png")');
						  }
					  });
				    
				     
				  });
			    
			  }
		  });
	});

	$("#submitInterview").click(function(){
		$("#resetInterview").attr('hidden', 'hidden');
		$("#submitInterview").hide();
		$("#analyzingCircle").css("visibility", "visible");
		var timer =  document.getElementById("timer");
		timer.innerHTML = "분석중입니다...";
		$.ajax({
	          type: 'POST',
	          url: 'analyzeAudio.do',
	          data: globalAudioData,
	          dataType: "JSON",
	          success: function(json){
	        	  $("#timerBlock").attr('hidden', 'hidden');
	        	  $("#resultBlock").removeAttr("hidden");
	        	  $("#resetInterview").removeAttr('hidden');
	        	  resultJSON = json;
	        	  
	        		// Themes begin
	        		am4core.useTheme(am4themes_animated);
	        		// Themes end
	
	        		chart = am4core.create("chartdiv", am4charts.XYChart);
					var data = [];
					// add data
					
					
	        		for(var i=0; i<json.example_x.length;i++){
	        			if(json.example_y[i] * 1 > 0){
	        				data.push({"exampleTime":json.example_x[i] * 1, "examplePitch":json.example_y[i] * 1})
	        			}
	        			
	        		}
	        		
	        		for(var i=0; i<json.answer_x.length;i++){
	        			if(json.answer_y[i] * 1 > 0){
	        				data.push({"answerTime":json.answer_x[i] * 1, "answerPitch":json.answer_y[i]*1})
	        			}
	        		}
					
					
	        		chart.data = data;
	
	        		// Create axes
	        		var xAxis = chart.xAxes.push(new am4charts.ValueAxis());
					xAxis.min = 0;
					xAxis.max = 1;
					xAxis.strictMinMax = true;
					
	        		var yAxis = chart.yAxes.push(new am4charts.ValueAxis());
	
	        		// Create series
	        		exampleSeries = chart.series.push(new am4charts.LineSeries());
	        		exampleSeries.dataFields.valueX = "exampleTime";
	        		exampleSeries.dataFields.valueY = "examplePitch";
	        		exampleSeries.tooltipText = "{exampleTime}"
	        		exampleSeries.strokeWidth = 2;
	        		exampleSeries.stroke = am4core.color("#ff7575");
	        		
	        		exampleSeries.tooltip.pointerOrientation = "vertical";
	        		
	        		// Create series
	        		answerSeries = chart.series.push(new am4charts.LineSeries());
	        		answerSeries.dataFields.valueX = "answerTime";
	        		answerSeries.dataFields.valueY = "answerPitch";
	        		answerSeries.tooltipText = "{answerTime}"
	        		answerSeries.strokeWidth = 2;
	        		answerSeries.stroke = am4core.color("#758eff");
	        		
	        		answerSeries.tooltip.pointerOrientation = "vertical";
	
	        		chart.cursor = new am4charts.XYCursor();
	        		chart.cursor.xAxis = xAxis;
	
	        		//chart.scrollbarY = new am4core.Scrollbar();
	        		chart.scrollbarX = new am4core.Scrollbar();
	        		
	        		chart.cursor.events.on("cursorpositionchanged", function(ev) {
        			  var xAxis = ev.target.chart.xAxes.getIndex(0);
        			  var yAxis = ev.target.chart.yAxes.getIndex(0);
        			  cursorX = xAxis.positionToValue(xAxis.toAxisPosition(ev.target.xPosition));
        			  cursorY = yAxis.positionToValue(yAxis.toAxisPosition(ev.target.yPosition));
        			});
	        		
	        		chart.events.on("hit", function(e){
	        			audio.stop();
	        			audio.seekTo(cursorX);
	        			audio.play();
	        		})
	          }
	          
	      }).done(function(data) {
	             console.log(data); 
	             
	      });
	});
</script>
</html>