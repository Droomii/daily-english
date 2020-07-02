<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%

	String pageTitle = "회원가입";

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
                        END 회원가입
                    </div>
                </div>
                <div class="card-content">
                   
                    <div class="card-body">
                        <form class="form-horizontal needs-validation" id="registerForm" action="/doRegister.do" method="post">
                            <fieldset class="form-group position-relative has-icon-left">
                            <label for="user-email">이메일</label>
                                <input type="text" class="form-control round" name="email" id="user-email" placeholder="이메일 입력">
                                <div id="email-feedback" class='feedback pl-1' hidden=hidden>올바른 이메일을 입력해주세요</div>
                            </fieldset>
                            <fieldset class="form-group position-relative has-icon-left">
                            <label for="user-email">닉네임</label>
                                <input type="text" class="form-control round" name="nick" id="user-nick" placeholder="닉네임 입력">
                                <div id="nick-feedback" class='feedback pl-1' hidden=hidden>닉네임은 한글만 가능합니다(2~7글자)</div>
                            </fieldset>
                            <fieldset class="form-group position-relative has-icon-left">
                            <label for="user-pw">암호</label>
                                <input type="password" class="form-control round" name="pw" id="user-password" placeholder="암호 입력">
                                <div id="pw-feedback" class='feedback pl-1' hidden=hidden>필수 입력 사항입니다</div>
                            </fieldset>
                            <fieldset class="form-group position-relative has-icon-left">
                            <label for="user-pw">암호 확인</label>
                                <input type="password" class="form-control round" name="verifyPw" id="verify-password" placeholder="암호 확인">
                                <div id="verify-pw-feedback" class='feedback pl-1' hidden=hidden>암호가 일치하지 않습니다</div>
                            </fieldset>
                            <div class="form-group text-center">
                                <button type="button" id="submit-btn" onclick="doRegister();" class="btn round btn-block btn-glow btn-bg-gradient-x-purple-blue col-12 mr-1 mb-1">회원가입</button>    
                            </div>
                           
                        </form>
                    </div>
                    <p class="card-subtitle text-muted text-right font-small-3 mx-2 my-1"><span>이미 계정이 있으신가요?<a href="/login.do" class="card-link">로그인</a></span></p>                    
                </div>
            </div>
        </div>
    </div>
</section>
<script>
function doRegister(){
	
	var email = $("#user-email");
	var pw = $("#user-password");
	var verifyPw = $("#verify-password");
	var nick = $("#user-nick");
	var allOK = true;
	
	// email verification
	if(!Boolean(email.val().trim())){
		$("#email-feedback").html('이메일을 입력해주세요')
		$("#email-feedback").removeAttr('hidden')
		allOK = false;
	}else{
		var emailVal = email.val();
		var emailRegex = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/;
		if(!Boolean(emailVal.match(emailRegex))){
			$("#email-feedback").html('올바른 형식의 이메일이 아닙니다')
			$("#email-feedback").removeAttr('hidden')
			allOK = false;
		}else{
			$.ajax({
				data : {email : emailVal},
				type : "POST",
				url : "/checkEmailDuplicate.do",
				success : function(data) {
					if(data!="0"){
						$("#email-feedback").html('이미 사용중인 이메일입니다')
						$("#email-feedback").removeAttr('hidden')
						allOK = false;
					}else{
						$("#email-feedback").html('');
						$("#email-feedback").attr('hidden', 'hidden');
					}
				},
			})
		}
		
	}
	
	// nick verification
	if(!Boolean(nick.val().trim())){
		$("#nick-feedback").html('닉네임을 입력해주세요')
		$("#nick-feedback").removeAttr('hidden')
		allOK = false;
	}else{
		var nickVal = nick.val();
		var nickRegex = /^[가-힣A-z]{2,7}$/;
		if(!Boolean(nickVal.match(nickRegex))){
			$("#nick-feedback").html('닉네임은 한글로만 2~7글자까지 가능합니다')
			$("#nick-feedback").removeAttr('hidden')
			allOK = false;
		}else{
			$("#nick-feedback").attr('hidden', 'hidden');
		}
	}
	
	// pw verification
	if(!Boolean(pw.val().trim())){
		$("#pw-feedback").html('필수 입력사항입니다')
		$("#pw-feedback").removeAttr('hidden')
		allOK = false;
	}else{
		var pwVal = pw.val();
		var pwRegex = /(?=^.{8,}$)((?=.*\d)|(?=.*\W+))(?![.\n])(?=.*[A-Za-z]).*$/;
		if(!Boolean(pwVal.match(pwRegex))){
			$("#pw-feedback").html('숫자와 영문을 조합하여 8글자 이상으로 입력해주세요')
			$("#pw-feedback").removeAttr('hidden')
			allOK = false;
		}else{
			$("#pw-feedback").attr('hidden', 'hidden');
			if(!Boolean(verifyPw.val())){
				$("#verify-pw-feedback").html('필수 입력사항입니다')
				$("#verify-pw-feedback").removeAttr('hidden')
				allOK = false;
			}else if(pw.val() != verifyPw.val()){
				$("#verify-pw-feedback").html('암호가 일치하지 않습니다')
				$("#verify-pw-feedback").removeAttr('hidden')
				allOK = false;
			}else{
				$("#verify-pw-feedback").attr('hidden', 'hidden');
			}
		}
	}
	
	if(allOK && $("#email-feedback").html()==""){
		$("#registerForm").submit();
	}
}

</script>
        </div>
      </div>
    </div>
    <!-- content end -->

    <%@ include file="/WEB-INF/view/footer.jsp" %>
    <!-- END PAGE LEVEL JS-->
  </body>
</html>