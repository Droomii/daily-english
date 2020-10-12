<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%

	String pageTitle = "pageTemplate";
	Calendar c = Calendar.getInstance();
	int today = c.get(Calendar.DAY_OF_MONTH);
	c.set(Calendar.DAY_OF_MONTH, 1);
	int dayOfWeek = c.get(Calendar.DAY_OF_WEEK)-1;
	Calendar lastDay = Calendar.getInstance();
	lastDay.set(Calendar.DAY_OF_MONTH, lastDay.getActualMaximum(Calendar.DAY_OF_MONTH));
	int lastDayOfWeek = lastDay.get(Calendar.DAY_OF_WEEK); 

%>
<!DOCTYPE html>
<html class="loading" lang="en" data-textdirection="ltr">
<head>
  <%@ include file="/WEB-INF/view/header.jsp" %>
  
  <style>
  	.calendar {
  		display:table;
  		width:100%;
  		height:100%;
  	}
  	.calendar-row{
  		display:flex;
  		width:100%;
  	}
  	.calendar-cell{
  		font-size:100%;
  		width:100%;
  		height:3rem;
  		display:inline-block;
  		background-color:beige;
  		margin: 1px 1px 1px 1px;
  		border-radius: 10%;
  		
  	}
  	.calendar-head{
  		color:white;
  		background-color:goldenrod;
  		width:100%;
  		display:inline-block;
  		text-align:center;
  		border:0;
  		
  	}
  	
  	.not-current-month{
  		background-color:azure;
  	}
  	.attended{
  		background-image: url("/resources/img/attend.png");
  		background-size:contain;
  		background-repeat: no-repeat;
  		background-position: center;
  	}
  	.today{
  		background-color:lightpink;
  		color:white;
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
		<div class="col-lg-6 col-md-12">
			<div class="card">
				<div class="card-header">
					<h4 class="card-title mb-0" style="font-size:1.5rem">출석 현황</h4>
				</div>
				<div class="card-body pt-0">
				<div style="font-size: 1.5rem; font-weight:700; text-align:center;"><%=c.get(Calendar.MONTH)+1 %>월</div>
				
				<div class="calendar">
					<div class="calendar-row">
						<div class="calendar-head">일</div>
						<div class="calendar-head">월</div>
						<div class="calendar-head">화</div>
						<div class="calendar-head">수</div>
						<div class="calendar-head">목</div>
						<div class="calendar-head">금</div>
						<div class="calendar-head">토</div>
					</div>
					<div class="calendar-row">
					<%for(int i = 0; i < dayOfWeek; i++){ %>
						<div class="calendar-cell not-current-month"></div>
					<%} %>
					<%for(int i = 0; i < c.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {%>
						<%if(dayOfWeek==0) {%>
							</div>
							<div class="calendar-row">
						<%} %>
						<div class="calendar-cell attended <%=(i+1)==today ? "today" : ""%>">
							<%=i+1 %>
						</div>
						<%dayOfWeek = (dayOfWeek+1)%7; %>
					<%} %>
					<%for(int i = lastDayOfWeek; i < 7; i++){ %>
						<div class="calendar-cell not-current-month"></div>
					<%} %>
					</div>
					</div>
				</div>
			</div>
		</div>
		
		<div class="col-lg-12 col-md-12">
			<div class="card">
				<div class="card-body">
					<img alt="" src="/resources/img/attend.png">
				</div>
			</div>
		</div>
		<div class="col-6">
			<div class="card">
			<div class="card-body text-center pt-1 pb-0">
			<i class="la la-check-circle" style="font-size:5rem"></i>
			</div>
				<div class="card-body pt-0">
					<h4 class="card-title text-center mb-0" style="font-size:1.5rem">복습하기</h4>
				</div>
			</div>
		</div>
		<div class="col-6">
			<div class="card">
			<div class="card-body text-center pt-1 pb-0">
			<i class="la la-sticky-note" style="font-size:5rem"></i>
			</div>
				<div class="card-body pt-0">
					<h4 class="card-title text-center mb-0" style="font-size:1.5rem">단어장</h4>
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