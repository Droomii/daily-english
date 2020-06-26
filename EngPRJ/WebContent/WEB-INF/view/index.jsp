<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%

	String pageTitle = "pageTemplate";

%>
<!DOCTYPE html>
<html class="loading" lang="en" data-textdirection="ltr">
<head>
  <%@ include file="/WEB-INF/view/header.jsp" %>
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
			<div class="card" onclick="location.href='/today/todayQuiz.do'">
				<div class="card-body">
					<h4 class="card-title text-center mb-0" style="font-size:1.5rem;"><i class="la la-book" style="font-size:2rem;vertical-align:center;"></i><span style="font-size:2rem">오늘의 뉴스</span></h4>
				</div>
			</div>
		</div>
		<div class="col-6">
			<div class="card" onclick="location.href='/review/reviewQuiz.do'">
			<div class="card-body text-center pt-1 pb-0">
			<i class="la la-check-circle" style="font-size:5rem"></i>
			</div>
				<div class="card-body pt-0">
					<h4 class="card-title text-center mb-0" style="font-size:1.5rem">복습하기</h4>
				</div>
			</div>
		</div>
		<div class="col-6">
			<div class="card" onclick="location.href='/today/todaySentence.do'">
			<div class="card-body text-center pt-1 pb-0">
			<i class="la la-sticky-note" style="font-size:5rem"></i>
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