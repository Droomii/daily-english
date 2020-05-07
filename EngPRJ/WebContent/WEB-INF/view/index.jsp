<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%

	String pageTitle = "pageTemplate";

%>
<!DOCTYPE html>
<html class="loading" lang="en" data-textdirection="ltr">
  <%@ include file="/WEB-INF/view/header.jsp" %>
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
    <div class="app-content content">
      <div class="content-wrapper">
        <div class="content-wrapper-before"></div>
        <div class="content-body"><!-- ../../../theme-assets/images/carousel/22.jpg -->

<!-- Header footer section start -->
<section id="header-footer">
	<div class="row match-height">
		<div class="col-lg-12 col-md-12">
			<div class="card">
				<div class="card-header">
					<h4 class="card-title">출석체크</h4>
				</div>
			</div>
		</div>
		
		<div class="col-lg-12 col-md-12">
			<div class="card">
				<div class="card-body">
					<h4 class="card-title">오늘의 뉴스</h4>
				</div>
			</div>
		</div><div class="col-6">
			<div class="card">
				<div class="card-body">
					<h4 class="card-title">복습하기</h4>
				</div>
			</div>
		</div><div class="col-6">
			<div class="card">
				<div class="card-body">
					<h4 class="card-title">단어장</h4>
				</div>
			</div>
		</div>
		
		<div class="col-lg-4 col-md-12">
			<div class="card">
				<div class="card-body">
					<h4 class="card-title">Basic Card</h4>
					<h6 class="card-subtitle text-muted">Basic Card With Header &amp; Footer</h6>
				</div>
				<img class="" src="theme-assets/images/carousel/24.jpg" alt="Card image cap">
				<div class="card-body">
					<p class="card-text">Some quick example text to build on the card title and make up the bulk of the card's content.</p>
					<a href="#" class="card-link">Card link</a>
					<a href="#" class="card-link">Another link</a>
				</div>
				<div class="card-footer border-top-blue-grey border-top-lighten-5 text-muted">
					<span class="float-left">3 hours ago</span>
					<span class="float-right">
						<a href="#" class="card-link">Read More
							<i class="la la-angle-right"></i>
						</a>
					</span>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- Header footer section End -->

<!-- Content types section start -->
<section id="content-types">
	<div class="row">
		<div class="col-12 mt-3 mb-1">
			<h4 class="text-uppercase">Content Types</h4>
			<p>Cards support a wide variety of content, including images, text, list groups, links, and more. Mix and match multiple
				content types to create the card you need.</p>
		</div>
	</div>
	<div class="row match-height">
		<div class="col-xl-6 col-md-6 col-sm-12">
			<div class="card">
				<div class="card-content">
					<div class="card-body">
						<h4 class="card-title">List Group</h4>
						<p class="card-text">Some quick example text to build on the card title and make up the bulk of the card's content.</p>
					</div>
					<ul class="list-group list-group-flush">
						<li class="list-group-item">
							Cras justo odio
						</li>
						<li class="list-group-item">
							Dapibus ac facilisis in
						</li>
						<li class="list-group-item">
							Morbi leo risus
						</li>
						<li class="list-group-item">
							Porta ac consectetur ac
						</li>
						<li class="list-group-item">
							Vestibulum at eros
						</li>
					</ul>
					<div class="card-body">
						<a href="#" class="card-link">Card link</a>
						<a href="#" class="card-link">Another link</a>
					</div>
				</div>
			</div>
		</div>

		<div class="col-xl-6 col-md-12">
			<div class="card">
				<div class="card-content">
					<div class="card-body">
						<h4 class="card-title">Contact Form</h4>
						<h6 class="card-subtitle text-muted">Support card subtitle</h6>
					</div>
					<div class="card-body">
						<form class="form">
							<div class="form-body">
								<div class="form-group">
									<label for="donationinput1" class="sr-only">First Name</label>
									<input type="text" id="donationinput1" class="form-control" placeholder="First Name" name="fname">
								</div>
								<div class="form-group">
									<label for="donationinput2" class="sr-only">Last Name</label>
									<input type="text" id="donationinput2" class="form-control" placeholder="Last Name" name="lanme">
								</div>
								<div class="form-group">
									<label for="donationinput3" class="sr-only">E-mail</label>
									<input type="email" id="donationinput3" class="form-control" placeholder="E-mail" name="email">
								</div>

								<div class="form-group">
									<label for="donationinput4" class="sr-only">Contact Number</label>
									<input type="text" id="donationinput4" class="form-control" placeholder="Phone" name="phone">
								</div>
								<div class="form-group">
									<label for="donationinput7" class="sr-only">Message</label>
									<textarea id="donationinput7" rows="5" class="form-control square" name="message" placeholder="message"></textarea>
								</div>

							</div>
							<div class="form-actions center">
								<button type="submit" class="btn btn-outline-primary">Send</button>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- Content types section end -->

<!-- Text Alignment section start -->
<section id="text-alignments">
	<div class="row">
		<div class="col-12 mt-3 mb-1">
			<h4 class="text-uppercase">Text Alignment</h4>
			<p>You can quickly change the text alignment of any card—in its entirety or specific parts—with our text align classes.</p>
		</div>
	</div>
	<div class="row">
		<div class="col-lg-4 col-md-12">
			<div class="card">
				<div class="card-body">
					<h4 class="card-title">Text Align Left</h4>
					<p class="card-text">Jelly beans sugar plum cheesecake cookie oat cake soufflé.Tootsie roll bonbon liquorice tiramisu pie powder.Donut sweet
						roll marzipan pastry cookie cake tootsie roll oat cake cookie.</p>
					<p class="card-text">Sweet roll marzipan pastry halvah. Cake bear claw sweet. Tootsie roll pie marshmallow lollipop chupa chups donut fruitcake
						cake.Jelly beans sugar plum cheesecake cookie oat cake soufflé. Tart lollipop carrot cake sugar plum. Marshmallow wafer
						tiramisu jelly beans.</p>
					<a href="#" class="btn btn-primary">Go somewhere</a>
				</div>
			</div>
		</div>
		<div class="col-lg-4 col-md-12">
			<div class="card text-center">
				<div class="card-body">
					<h4 class="card-title">Text Align Center</h4>
					<p class="card-text">Jelly beans sugar plum cheesecake cookie oat cake soufflé.Tootsie roll bonbon liquorice tiramisu pie powder.Donut sweet
						roll marzipan pastry cookie cake tootsie roll oat cake cookie.</p>
					<p class="card-text">Sweet roll marzipan pastry halvah. Cake bear claw sweet. Tootsie roll pie marshmallow lollipop chupa chups donut fruitcake
						cake.Jelly beans sugar plum cheesecake cookie oat cake soufflé. Tart lollipop carrot cake sugar plum. Marshmallow wafer
						tiramisu jelly beans.</p>
					<a href="#" class="btn btn-primary">Go somewhere</a>
				</div>
			</div>
		</div>
		<div class="col-lg-4 col-md-12">
			<div class="card text-right">
				<div class="card-body">
					<h4 class="card-title">Text Align Right</h4>
					<p class="card-text">Jelly beans sugar plum cheesecake cookie oat cake soufflé.Tootsie roll bonbon liquorice tiramisu pie powder.Donut sweet
						roll marzipan pastry cookie cake tootsie roll oat cake cookie.</p>
					<p class="card-text">Sweet roll marzipan pastry halvah. Cake bear claw sweet. Tootsie roll pie marshmallow lollipop chupa chups donut fruitcake
						cake.Jelly beans sugar plum cheesecake cookie oat cake soufflé. Tart lollipop carrot cake sugar plum. Marshmallow wafer
						tiramisu jelly beans.</p>
					<a href="#" class="btn btn-primary">Go somewhere</a>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- Text Alignment section end -->

<!-- Card Headings section start -->
<section id="card-headings">
	<div class="row">
		<div class="col-12 mt-3 mb-1">
			<h4 class="text-uppercase">Card Headings</h4>
		</div>
	</div>

	<div class="row">
		<div class="col-md-6 col-sm-12">
			<div class="card text-center">
				<div class="card-header">
					Featured
				</div>
				<div class="card-body">
					<h5 class="card-title">Special title treatment</h5>
					<p class="card-text">With supporting text below as a natural lead-in to additional content.</p>
					<a href="#" class="btn btn-primary">Go somewhere</a>
				</div>
				<div class="card-footer text-muted">
					2 days ago
				</div>
			</div>
		</div>
		<div class="col-md-6 col-sm-12">
			<div class="card">
				<div class="card-header">
					<h4 class="card-title" id="heading-multiple-thumbnails">Multiple Thumbnail</h4>
					<a class="heading-elements-toggle">
						<i class="la la-ellipsis-v font-medium-3"></i>
					</a>
					<div class="heading-elements">
						<span class="avatar">
							<img src="theme-assets/images/portrait/small/avatar-s-2.png" alt="avatar">
						</span>
						<span class="avatar">
							<img src="theme-assets/images/portrait/small/avatar-s-3.png" alt="avatar">
						</span>
						<span class="avatar">
							<img src="theme-assets/images/portrait/small/avatar-s-4.png" alt="avatar">
						</span>
					</div>
				</div>
				<div class="card-content">
					<div class="card-body">
						<h4 class="card-title">Content title</h4>
						<p class="card-text">Jelly beans sugar plum cheesecake cookie oat cake soufflé.Tootsie roll bonbon liquorice tiramisu pie powder.Donut sweet
							roll marzipan pastry cookie cake tootsie roll oat cake cookie.</p>
						<p class="card-text">Sweet roll marzipan pastry halvah. Cake bear claw sweet. Tootsie roll pie marshmallow lollipop chupa chups donut fruitcake
							cake.Jelly beans sugar plum cheesecake cookie oat cake soufflé. Tart lollipop carrot cake sugar plum. Marshmallow
							wafer tiramisu jelly beans.</p>
					</div>
				</div>
			</div>
		</div>
	</div>

</section>
<!-- // Card Headings section end -->
        </div>
      </div>
    </div>
    <!-- content end -->

    <%@ include file="/WEB-INF/view/footer.jsp" %>
    <!-- END PAGE LEVEL JS-->
  </body>
</html>