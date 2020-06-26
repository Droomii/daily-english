<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%

	String pageTitle = "로그인";

%>
<!DOCTYPE html>
<html class="loading" lang="en" data-textdirection="ltr">
<head>
  <%@ include file="/WEB-INF/view/header.jsp" %>
  
  <style>
  .feedback {
  	color:crimson;
  }
  </style>
  </head>
  <body class="vertical-layout vertical-menu 1-column  bg-full-screen-image blank-page blank-page  pace-done" data-open="click" data-menu="vertical-menu" data-color="bg-gradient-x-purple-blue" data-col="1-column">

<div class="app-content content">
      <div class="content-wrapper">
        <div class="content-wrapper-before"></div>
        <div class="content-header row">
        </div>
        <div class="content-body"><section class="flexbox-container">
    <div class="col-12 d-flex align-items-center justify-content-center">
        <div class="col-lg-4 col-md-6 col-10 box-shadow-2 p-0">
            <div class="card border-grey border-lighten-3 px-1 py-1 m-0">
                <div class="card-header border-0">
                    <div class="font-large-1  text-center">                       
                        END에 로그인
                    </div>
                </div>
                <div class="card-content">
                   
                    <div class="card-body">
                        <form class="form-horizontal needs-validation" id="loginForm">
                            <fieldset class="form-group position-relative has-icon-left">
                                <input type="email" class="form-control round" name="email" id="user-email" placeholder="이메일 입력">
                                <div class="form-control-position">
                                    <i class="ft-mail"></i>
                                </div>
                                <div id="email-feedback" class='feedback pl-1' hidden=hidden>올바른 이메일을 입력해주세요</div>
                            </fieldset>
                            <fieldset class="form-group position-relative has-icon-left">
                                <input type="password" class="form-control round" name="pw" id="user-password" placeholder="암호 입력">
                                <div class="form-control-position">
                                    <i class="ft-lock"></i>
                                </div>
                                <div id="pw-feedback" class='feedback pl-1' hidden=hidden>암호와 이메일을 모두 입력해주세요</div>
                            </fieldset>
                            <div class="form-group row">
                                <div class="col-md-6 col-12 text-center text-sm-left">
                                   
                                </div>
                                <div class="col-md-6 col-12 float-sm-left text-center text-sm-right"><a href="/recoverPw.do" class="card-link">암호를 잊으셨나요?</a></div>
                            </div>                           
                            <div class="form-group text-center">
                                <button type="submit" id="submit-btn" class="btn round btn-block btn-glow btn-bg-gradient-x-purple-blue col-12 mr-1 mb-1">로그인</button>    
                            </div>
                           
                        </form>
                    </div>
                    <p class="card-subtitle text-muted text-right font-small-3 mx-2 my-1"><span>계정이 없으신가요?<a href="/register.do" class="card-link">회원가입</a></span></p>                    
                </div>
            </div>
        </div>
    </div>
</section>
<script>
$("#loginForm").submit(function(e){
	e.preventDefault();
	var email = $("#user-email");
	var pw = $("#user-password");
	if(!Boolean(email.val().trim()) || !Boolean(pw.val().trim())){
		$("#pw-feedback").removeAttr('hidden')
	}else{
		$("#pw-feedback").attr('hidden', 'hidden')
		$.ajax({
			data : {email : email.val(), pw : pw.val()},
			type : "POST",
			url : "doLogin.do",
			success : function(data) {
				if(data=="0"){
					location.href = "/index.do";
				}else{
					$("#pw-feedback").html('이메일 혹은 암호가 올바르지 않습니다.');
					$("#pw-feedback").removeAttr('hidden')
				}
			},
			error: function (xhr, ajaxOptions, thrownError) {
				$("#pw-feedback").html('서버 오류입니다.');
				$("#pw-feedback").removeAttr('hidden')
		      }
	})
	}
})

</script>
        </div>
      </div>
    </div>
    <!-- content end -->

    <%@ include file="/WEB-INF/view/footer.jsp" %>
    <!-- END PAGE LEVEL JS-->
  </body>
</html>