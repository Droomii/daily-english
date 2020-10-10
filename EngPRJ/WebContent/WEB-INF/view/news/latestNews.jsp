<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.List"%>
<%@page import="poly.dto.NewsDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%

	String pageTitle = "뉴스 원문";
	NewsDTO rDTO = (NewsDTO)request.getAttribute("news");
	List<NewsDTO> relatedArticles = (List<NewsDTO>)request.getAttribute("relatedArticles");
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat sdfOut = new SimpleDateFormat("yyyy-MM-dd");
	String imgUrl = (String)request.getAttribute("imgUrl");

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
					<h4 class="card-title text-center mb-0" style="font-size:1.5rem;"><i class="la la-book" style="font-size:2rem;vertical-align:center;"></i><span style="font-size:2rem">오늘의 뉴스</span></h4>
				</div>
				<div class="card-body">
				<div class="card-text">
				<h2 style="font-weight:700"><%=rDTO.getNewsTitle() %></h2>
				<h4><%="("+rDTO.getTranslatedTitle() + ")"%></h4>
				<br>
				</div>
				<img class="img-fluid mb-3" src="<%=imgUrl %>" alt="Card image cap">
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
				<hr>
				<h3 style="font-weight:700">비슷한 영문기사 더 보기</h3>
				<%for(NewsDTO related : relatedArticles){ %>
				<a href="http://www.koreaherald.com/view.php?ud=<%=related.getNewsUrl()%>" target='_blank'>
				<div class='card-text p-1 mt-1 shadow-sm rounded related-card' style='border:1px solid rgb(240,240,240)'>
				<h4 class='related-title' style="font-weight:700"><%=related.getNewsTitle() %></h4>
				<div class='related-content' style='color:grey'><%=String.join(" ", related.getOriginalSentences().subList(0, 2)) %></div>
				<div class='mt-1' style='color:rgb(200,200,200)'><%=sdfOut.format(sdf.parse(related.getNewsUrl().substring(0,8))) %></div>
				</div>
				</a>
				<%} %>
				<hr>
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