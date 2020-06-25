<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%

	String pageTitle = "실력 측정";

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
		<div class="col-lg-4 offset-lg-4 col-md-12">
			<div class="card">
				<div class="card-header pb-0">
					<h4 class="card-title mb-0" style="font-size:1.5rem">실력 측정 테스트</h4>
				</div>
				<div id="card-content">
				<div class="card-body">
				<p style="font-weight:bold">
				밑줄 친 단어의 알맞은 해석을 고르세요.
				</p>
				<p>
				<span id="no"></span>. <span id="word"></span> : <span id="sentence"></span>
				</p>
				</div>
				<div class="card-body">
				<button type="button" id="a" class="btn mb-1 choice btn-secondary btn-sm btn-block">example</button>
				<button type="button" id="b" class="btn mb-1 choice btn-secondary btn-sm btn-block">hello</button>
				<button type="button" id="c" class="btn mb-1 choice btn-secondary btn-sm btn-block">world</button>
				<button type="button" id="d" class="btn mb-1 choice btn-secondary btn-sm btn-block">wrong</button>
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
	var choices = $(".choice")
	var no = 0;
	var index = 0;
	$(document).ready(function(){
		$.ajax({
	           type:"POST",
	           url:"/submitTestAnswer.do",
	           dataType:"JSON",
	           success : function(json) {
	        	   $('.chosen').removeClass('chosen');
	        	   $("#no").html(++no);
	        	   $("#word").html(json.word);
	        	   $("#sentence").html(json.sentence);
	        	   $("#a").html(json.a);
	        	   $("#b").html(json.b);
	        	   $("#c").html(json.c);
	        	   $("#d").html(json.d);
	        	   index = json.no * 1;
	           },
	           error : function(xhr, status, error) {
	        	   console.log('error!!');
	           }
	     });
		
	})
	
	// when choices are clicked
	choices.on("click", function(e){
		if( $(this).hasClass('chosen') ){
			$.ajax({
		           type:"POST",
		           url:"/submitTestAnswer.do",
		           data: {answer : $(this).attr("id"),
		        	   index : index},
		           dataType:"JSON",
		           success : function(json) {
		        	   
		        	   if(json.finalLevel!=null){
		        		   var resultContent = 
		        			   "<div class='card-body'>"
		        		   		+ "<h4 class='card-title mb-0 text-center' style='font-size:1.5rem'>"
		        		   		+ "당신의 영어 실력은...</h4><h4 class='card-title mt-3 text-center' style='font-size:2rem'>"
		        		   		+ "Level " + json.finalLevel + "</h4></div>";
		        		   $("#card-content").html(resultContent);
		        		   
		        	   }else{
		        		   $('.chosen').removeClass('chosen');
			        	   $("#no").html(++no);
			        	   $("#word").html(json.word);
			        	   $("#sentence").html(json.sentence);
			        	   $("#a").html(json.a);
			        	   $("#b").html(json.b);
			        	   $("#c").html(json.c);
			        	   $("#d").html(json.d);
			        	   index = json.no * 1;   
		        	   }
		        	   
		           },
		           error : function(xhr, status, error) {
		        	   console.log('error!!');
		           }
		     });
		}else{
			$('.chosen').removeClass('chosen');
			$(this).addClass('chosen')
		}
	})
	</script>
    <%@ include file="/WEB-INF/view/footer.jsp" %>
    <!-- END PAGE LEVEL JS-->
  </body>
</html>