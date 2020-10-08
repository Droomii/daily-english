<%@page import="poly.dto.NewsDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%

	String pageTitle = "뉴스 원문";
	NewsDTO rDTO = (NewsDTO)request.getAttribute("news");

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
		<div class="col-lg-6 offset-lg-3 col-md-12">
			<div class="card">
				<div class="card-header">
					<h4 class="card-title text-center mb-0" style="font-size:1.5rem;"><i class="la la-book" style="font-size:2rem;vertical-align:center;"></i><span style="font-size:2rem">오늘의 뉴스</span></h4>
				</div>
				<div class="card-body">
				<div class="card-text">
				<h4 style="font-weight:700"><%=rDTO.getNewsTitle() %></h4>
				<h4><%="("+rDTO.getTranslatedTitle() + ")"%></h4>
				<br>
				</div>
				<div class="card-text">
				<%
				int i = 0;
				for(String stc : rDTO.getOriginalSentences()){ %>
				<p>
				<%=stc %>
				</p>
				
				<p>
				<%="("+rDTO.getTranslation().get(i)+")" %>
				</p>
				<br>
				<%i++;} %>
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
</html>