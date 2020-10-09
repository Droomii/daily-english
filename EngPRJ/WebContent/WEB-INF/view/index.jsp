<%@page import="poly.dto.NewsDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%

	String pageTitle = "pageTemplate";
	NewsDTO nDTO = (NewsDTO)request.getAttribute("nDTO");
	String imgUrl = (String)request.getAttribute("imgUrl");

%>
<!DOCTYPE html>
<html class="loading" lang="en" data-textdirection="ltr">
<head>
  <%@ include file="/WEB-INF/view/header.jsp" %>
  <style>
  /*
  .card:hover{
  	background-color:rgb(230,230,230);
  	cursor: pointer;
  }
  */
  
  .headline-card:hover{
  	cursor: pointer;
  }
  .headline-card:hover .headline-content{
  	text-decoration: underline;
  }
  .headline-content{
  	overflow: hidden;
	text-overflow: ellipsis;
	display: -webkit-box;
	-webkit-line-clamp: 3;
	-webkit-box-orient: vertical;
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
		<div class="col-lg-6 col-md-12">
			<div class="card headline-card" onclick="location.href='/today/todayQuiz.do'" >
				<div class="card-header">
					<h4 class="card-title mb-0 text-center" style="font-size:2rem;">오늘의 주요 뉴스</h4>
				</div>
				<img class="img-fluid" src="<%=imgUrl %>" alt="Card image cap">
				<div class="card-body">
					<h1 style="font-weight:1000"><%=nDTO.getNewsTitle() %></h1>
					<div class="headline-content"><%=String.join(" ", nDTO.getOriginalSentences().subList(0, 5)) %></div>
				</div>
				<div class="card-footer info text-right">
				퀴즈 풀러가기 &gt;
				</div>
			</div>
		</div>
	</div>
	<div class="row match-height">
		<div class="col-6">
			<div class="card" onclick="location.href='/review/reviewQuiz.do'" >
			<div class="card-body text-center pt-1 pb-0">
			<i class="la la-check-circle" style="font-size:5rem"></i>
			</div>
				<div class="card-body pt-0">
					<h4 class="card-title text-center mb-0" style="font-size:1.5rem">복습하기</h4>
				</div>
			</div>
		</div>
		<div class="col-6">
			<div class="card" onclick="location.href='/today/todaySentence.do'" >
			<div class="card-body text-center pt-1 pb-0">
			<i class="la la-volume-up" style="font-size:5rem"></i>
			</div>
				<div class="card-body pt-0">
					<h4 class="card-title text-center mb-0" style="font-size:1.5rem">발음연습</h4>
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
</html>