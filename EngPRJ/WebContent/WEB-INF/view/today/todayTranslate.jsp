<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.List"%>
<%@page import="poly.dto.NewsDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%

	String pageTitle = "번역 연습하기";
	NewsDTO rDTO = (NewsDTO)request.getAttribute("nDTO");

%>
<!DOCTYPE html>
<html class="loading" lang="en" data-textdirection="ltr">
<head>
  <%@ include file="/WEB-INF/view/header.jsp" %>
  <style type="text/css">
  .hl{
  	background-color: #FFFF00;
  	font-weight:700;
  }
  .related-content{
  	overflow: hidden;
	text-overflow: ellipsis;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
  }
  
  .related-card:hover .related-content{
  	text-decoration: underline;
  }
  
  .answer{
  	color: green;
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
		<div style="max-width:50rem">
			<div class="card">
				<div class="card-header">
					<h4 class="card-title text-center mb-0" style="font-size:1.5rem;"><span style="font-size:2rem">번역 연습하기</span></h4>
				</div>
				<div class="card-body">
				<div class="card-text">
				<h2 style="font-weight:700"><%=rDTO.getNewsTitle() %></h2>
				<h4><%="("+rDTO.getTranslatedTitle() + ")"%></h4>
				<br>
				</div>
				<div class="card-text">
				<%
				int i = 0;
				for(String stc : rDTO.getOriginalSentences().subList(0, rDTO.getOriginalSentences().size()-1)){ %>
				<hr>
				<p>
				<%=stc %>
				</p>
				
				<fieldset class="form-group">
					<div class="answer mb-1"></div>
                    <textarea class="form-control pr-1 pl-1" rows="3" placeholder="위 문장의 번역을 입력해주세요"></textarea>
                    <div class="progress mb-0 hidden">
							<div class="progress-bar progress-bar-striped progress-bar-animated bg-info" role="progressbar" aria-valuenow="100" aria-valuemin="100" aria-valuemax="100" style="width:100%"></div>
						</div>
                    <button type="button" data-idx="<%=i %>" class="submit-btn float-right mt-1 btn btn-sm btn-info">채점하기</button>
                    <div class="score float-right"></div>
                </fieldset>
				<br>
				<%i++;} %>
				</div>
				</div>
				<div class="card-footer">
					<button type="button" onclick="location.href='/today/todayNews.do'" class="btn btn-primary btn-icon float-right">뉴스 원문 보기 &gt; </button>
					<button type="button" onclick="location.href='/today/todaySentence.do'" class="btn btn-primary btn-icon float-left">&lt; 발음 연습하기 </button>
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
  <script type="text/javascript">
  $(".submit-btn").on("click", function(){
	  	parent = this.parentElement;
	  	this.classList.add("hidden");
	  	var progressBar = parent.querySelector(".progress");
	  	progressBar.classList.remove("hidden");
	  	var userAnswer = parent.querySelector("textarea").value;
	  	var answer = parent.querySelector(".answer");
	  	var scoreTag = parent.querySelector(".score");
		var idx = $(this).attr("data-idx");
		var query = {userAnswer : userAnswer, idx : idx};
		
		$.ajax({
          type: 'POST',
          url: 'scoreTranslate.do',
          data: query,
          dataType: "JSON",
          success: function(json){
        	  answer.innerHTML = "모범 답안) "+ json.original;
        	  progressBar.classList.add("hidden");
        	  var score = parseFloat(json.score);
        	  score *= 10000;
        	  score = Math.round(score) / 100
        	  scoreTag.innerHTML = "유사도 : " + score + "%";
        }});
  })
  
  </script>
  </body>
</html>