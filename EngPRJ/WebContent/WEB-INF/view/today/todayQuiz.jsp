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
  .stress{
  	text-decoration:underline;
  	font-weight:bold;
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
                      <fieldset class="form-group">
                          <input type="text" class="form-control" id="basicInput" placeholder="빈 칸에 들어갈 단어를 입력하세요">
                      </fieldset>
                      <button type="button" class="btn mb-1 btn-success btn-lg btn-block">제출</button>
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
	// choices(a, b, c, d)
	var no = 0;
	var index = 0;
	$(document).ready(function(){
		$.ajax({
	           type:"GET",
	           url:"submitTodayQuizAnswer.do",
	           dataType:"JSON",
	           success : function(json) {
	        	   $("#no").html(++no);
	        	   $("#sentence").html(json.sentence);
	        	   $("#translation").html(json.translation);
	        	   index = json.no * 1;
	           },
	           error : function(xhr, status, error) {
	        	   console.log('error!!');
	           }
	     });
		
	})
	
	// when choices are clicked

		$("#submit").on("click", function(e) {
			$.ajax({
				type : "GET",
				url : "submitTodayQuizAnswer.do",
				data : {
					answer : $(this).attr("id"),
					index : index
				},
				dataType : "JSON",
				success : function(json) {
					$("#no").html(++no);
					$("#sentence").html(json.sentence);
					$("#translation").html(json.translation);
					index = json.no * 1;

				},
				error : function(xhr, status, error) {
					console.log('error!!');
				}
			});

		})
	</script>
    <%@ include file="/WEB-INF/view/footer.jsp" %>
    <!-- END PAGE LEVEL JS-->
  </body>
</html>