<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%

	String pageTitle = "뉴스 원문";

%>
<!DOCTYPE html>
<html class="loading" lang="en" data-textdirection="ltr">
<head>
  <%@ include file="/WEB-INF/view/header.jsp" %>
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
		<div class="col-lg-12 col-md-12">
			<div class="card">
				<div class="card-header">
					<h4 class="card-title mb-0" style="font-size:1.5rem">출석체크</h4>
				</div>
			</div>
		</div>
		
		<div class="col-lg-12 col-md-12">
			<div class="card">
				<div class="card-body">
					<h4 class="card-title text-center mb-0" style="font-size:1.5rem;"><i class="la la-book" style="font-size:2rem;vertical-align:center;"></i><span style="font-size:2rem">오늘의 뉴스</span></h4>
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