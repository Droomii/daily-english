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
    <nav class="header-navbar navbar-expand-md navbar navbar-with-menu navbar-without-dd-arrow fixed-top navbar-semi-light">
      <div class="navbar-wrapper">
        <div class="navbar-container content">
          <div class="collapse navbar-collapse show" id="navbar-mobile">
            <ul class="nav navbar-nav mr-auto float-left">
              <li class="nav-item d-block d-md-none"><a class="nav-link nav-menu-main menu-toggle hidden-xs" href="#"><i class="ft-menu"></i></a></li>
              <li class="nav-item d-none d-md-block"><a class="nav-link nav-link-expand" href="#"><i class="ficon ft-maximize"></i></a></li>
            </ul>
            <ul class="nav navbar-nav float-right">
              <li class="dropdown dropdown-user nav-item"><a class="dropdown-toggle nav-link dropdown-user-link" href="#" data-toggle="dropdown">             <span class="avatar avatar-online"><img src="/resources/theme-assets/images/portrait/small/avatar-s-19.png" alt="avatar"><i></i></span></a>
                <div class="dropdown-menu dropdown-menu-right">
                  <div class="arrow_box_right"><a class="dropdown-item" href="#"><span class="avatar avatar-online"><img src="/resources/theme-assets/images/portrait/small/avatar-s-19.png" alt="avatar"><span class="user-name text-bold-700 ml-1">John Doe</span></span></a>
                    <div class="dropdown-divider"></div><a class="dropdown-item" href="#"><i class="ft-user"></i> Edit Profile</a><a class="dropdown-item" href="#"><i class="ft-mail"></i> My Inbox</a><a class="dropdown-item" href="#"><i class="ft-check-square"></i> Task</a><a class="dropdown-item" href="#"><i class="ft-message-square"></i> Chats</a>
                    <div class="dropdown-divider"></div><a class="dropdown-item" href="#"><i class="ft-power"></i> Logout</a>
                  </div>
                </div>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </nav>

    <!-- ////////////////////////////////////////////////////////////////////////////-->


    <%@ include file="/WEB-INF/view/menu.jsp" %>
    

<!-- content start -->
<div class="app-content content mt-1">
      <div class="content-wrapper">
        <div class="content-body"><!-- ../../../theme-assets/images/carousel/22.jpg -->

<!-- Header footer section start -->
<section id="header-footer">
	<div class="row match-height">
		<div class="col-lg-4 offset-lg-4 col-md-12">
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
					$("#no").html(++no);
					$("#sentence").html(json.sentence);
					$("#translation").html(json.translation);
					idx = json.idx * 1;
					$("#wordInput").val(json.answer.substr(0, 2));
					$("#wordInput").focus();
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
						$("#no").html(++no);
						$("#sentence").html(json.sentence);
						$("#translation").html(json.translation);
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
						$("#sentence").html("You have seen all quizzes");
						$("#translation").html("모든 단어를 보았습니다");
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