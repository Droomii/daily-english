<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%

	String pageTitle = "오늘의 퀴즈";

%>
<!DOCTYPE html>
<html class="loading" lang="en" data-textdirection="ltr">
<head>
  <%@ include file="/WEB-INF/view/header.jsp" %>
  <style>
  .choice{
  	background-color:rgb(200,200,200);
  	border-color:rgb(200,200,200);
  	color:black;
  }
  
  .wrong{
  	background-color:crimson !important;
  	border-color:crimson !important;
  	color:white !important;
  }
  
  .correct{
  	background-color:green !important;
  	border-color:green !important;
  	color:green !important;
  }
  
  .choice:focus{
  	background-color:rgb(200,200,200);
  	border-color:rgb(200,200,200);
  	color:black;
  }
  
  .choice:hover{
  	background-color:rgb(200,200,200);
  	border-color:rgb(200,200,200);
  	color:black;
  }
  .choice:active{
  	background-color:rgb(200,200,200) !important;
  	border-color:rgb(200,200,200) !important;
  	color:black !important;
  }
  
  .chosen, .chosen:active{
  	background-color:orange !important;
  	border-color:orange !important;
  	color:white !important;
  }
  .hl{
  	text-decoration:underline;
  	font-weight:700;
  	color:blue;
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
	<div class="row match-height justify-content-center">
		<div class='col-sm-12' style="max-width:50rem">
			<div class="card">
				<div class="card-header pb-0">
					<h4 class="card-title mb-0" style="font-size:1.5rem">오늘의 퀴즈</h4>
				</div>
				<div id="card-content">
				<div class="card-body">
				<p style="font-weight:bold">
				빈 칸에 알맞은 단어를 입력하세요
				</p>
				<p>
				단어 중요도 : <span id="lvl"></span>
				</p>
				<p>
				<span id="no"></span>. <span id="sentence"></span>
				</p>
				<p>
				(<span id="translation"></span>)
				</p>
				<div class="card-body">
				<h4 class="card-title mb-0 text-center" id="result" style="font-size:2rem"></h4>
				</div>
				<div class="card-body">
				<form id="answerForm" autocomplete="off">
                      <fieldset class="form-group">
                          <input type="text" class="form-control" id="wordInput" placeholder="빈 칸에 들어갈 단어를 입력하세요">
                      </fieldset>
                      <input type="submit" id="submit" class="btn mb-1 btn-success btn-lg btn-block" value="제출" disabled="disabled">
                      </form>
                 <button type="button" id="next" class="btn mb-1 btn-info btn-lg btn-block" hidden="hidden" disabled="disabled">다음(Enter)&gt;</button>
                 </div>
                 <div class="card-body">
                 	<div class="card-text text-center">
                 	(<span id="no2"></span> / <span id="total"></span>) 
                 	</div>
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
	<script type="text/javascript">

		// pseudo problem number
		var no = 0;
		
		// real problem index
		var idx = 0;
		
		// get random today quiz
		$(document).ready(function() {
			$.ajax({
				type : "POST",
				url : "getRandomTodayQuiz.do",
				dataType : "JSON",
				success : function(json) {
					if(json.idx * 1 != -1){
						$("#no").html(json.answeredQCount+1);
						$("#no2").html(json.answeredQCount+1);
						$("#total").html(json.totalQs);
						$("#sentence").html(json.sentence);
						$("#translation").html(json.translation);
						var lvl = parseInt(json.lvl)
						for(var i = 0; i < 3; i++){
							if(i < lvl){
								document.getElementById('lvl').innerHTML += "<i class='la la-star warning'></i>"
							}else{
								document.getElementById('lvl').innerHTML += "<i class='la la-star muted'></i>"
							}
							 
						}
						idx = json.idx * 1;
						$("#wordInput").val(json.answer.substr(0, 2));
						$("#result").attr("hidden", "hidden");
						$("#next").attr("hidden", "hidden");
						$("#next").attr("disabled", "disabled");
						$("#answerForm").removeAttr("hidden");
						$("#wordInput").focus();
						$("#submit").attr("disabled", "disabled");
					}else{
						$("#no").html(++no);
						alert("오늘의 퀴즈를 이미 완료했습니다");
						location.href = "todayWordCard.do";
					}
				},
				error : function(xhr, status, error) {
					console.log('error!!');
				}
			});

		})
		

		$(document).keypress(function(event) {
			var keycode = (event.keyCode ? event.keyCode : event.which);
			if (keycode == '13') {
				if($("#next").prop("disabled")==false){
					if(!$("#next").is(":focus"))
						nextQuestion();
				}
			}
		});

		// enabling submit button
		$("#wordInput").on("change paste keyup", function(e) {

			if ($("#wordInput").val()) {
				$("#submit").removeAttr("disabled", "disabled");
			} else {
				$("#submit").attr("disabled", "disabled");
			}
		});

		// submitting answer and displaying result
		$("#answerForm").on("submit", function(e) {
			e.preventDefault();
			$.ajax({
				type : "POST",
				url : "submitTodayQuizAnswer.do",
				data : {
					answer : $("#wordInput").val(),
					quizIdx : idx
				},
				dataType : "JSON",
				success : function(json) {
					if (json.result == "1") {
						$("#result").html("정답입니다!!")
					} else {
						$("#result").html("틀렸습니다!!")
					}
					$("#sentence").html(json.answerSentence);
					$("#answerForm").attr("hidden", "hidden");
					$("#next").removeAttr("hidden");
					$("#next").removeAttr("disabled");
					$("#result").removeAttr("hidden");
				},
				error : function(xhr, status, error) {
					console.log('error!!');
				}
			});

		})

		// getting next question
		$("#next").on("click", function(e) {
			nextQuestion();
		});
		
		function nextQuestion(){
			$.ajax({
				type : "POST",
				url : "getRandomTodayQuiz.do",
				dataType : "JSON",
				success : function(json) {
					if(json.idx * 1 != -1){
						$("#no").html(json.answeredQCount+1);
						$("#no2").html(json.answeredQCount+1);
						$("#total").html(json.totalQs);
						$("#sentence").html(json.sentence);
						$("#translation").html(json.translation);
						var lvl = parseInt(json.lvl)
						document.getElementById('lvl').innerHTML = "";
						for(var i = 0; i < 3; i++){
							if(i < lvl){
								document.getElementById('lvl').innerHTML += "<i class='la la-star warning'></i>"
							}else{
								document.getElementById('lvl').innerHTML += "<i class='la la-star muted'></i>"
							}
							 
						}
						idx = json.idx * 1;
						$("#wordInput").val(json.answer.substr(0, 2));
						$("#result").attr("hidden", "hidden");
						$("#next").attr("hidden", "hidden");
						$("#next").attr("disabled", "disabled");
						$("#answerForm").removeAttr("hidden");
						$("#wordInput").focus();
						$("#submit").attr("disabled", "disabled");
					}else{
						$("#no").html(++no);
						alert("오늘의 퀴즈를 모두 보았습니다");
						location.href = "todayWordCard.do";
					}
					
					
					

				},
				error : function(xhr, status, error) {
					console.log('error!!');
				}
			});
		}
	</script>
    <%@ include file="/WEB-INF/view/footer.jsp" %>
    <!-- END PAGE LEVEL JS-->
  </body>
</html>