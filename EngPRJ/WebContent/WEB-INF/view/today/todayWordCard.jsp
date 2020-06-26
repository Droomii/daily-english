<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%

	String pageTitle = "오늘의 퀴즈";

%>
<!DOCTYPE html>
<html class="loading" lang="en" data-textdirection="ltr">
<head>
  <%@ include file="/WEB-INF/view/header.jsp" %>
  <style>
  .choice{
  	background-color:rgb(200,200,200);
  	border-color:rgb(200,200,200);
  	color:black;
  }
  
  .wrong{
  	background-color:crimson !important;
  	border-color:crimson !important;
  	color:white !important;
  }
  
  .correct{
  	background-color:green !important;
  	border-color:green !important;
  	color:green !important;
  }
  
  .choice:focus{
  	background-color:rgb(200,200,200);
  	border-color:rgb(200,200,200);
  	color:black;
  }
  
  .choice:hover{
  	background-color:rgb(200,200,200);
  	border-color:rgb(200,200,200);
  	color:black;
  }
  .choice:active{
  	background-color:rgb(200,200,200) !important;
  	border-color:rgb(200,200,200) !important;
  	color:black !important;
  }
  
  .chosen, .chosen:active{
  	background-color:orange !important;
  	border-color:orange !important;
  	color:white !important;
  }
  .hl{
  	text-decoration:underline;
  	font-weight:700;
  	color:blue;
  }
  .word{
  	text-align:center;
  	width:100%;
  	margin:auto;
  }
  .meaning{
  	text-align:left;
  	width:100%;
  	margin:auto;
  }
  
  .card-flip{
  	position:relative;
  }
	/*
	flip card
	*/
	.card-flip > div {
	  backface-visibility: hidden;
	  transition: transform 200ms;
	  transition-timing-function: linear;
	  width: 100%;
	  height: 100%;
	  margin: 0;
	  display: flex;
	}
	
	.card-front {
	  transform: rotateY(0deg);
	}
	
	.card-back {
	  transform: rotateY(180deg);
	  position: absolute;
	  top:0;
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
		<div class="col-lg-4 offset-lg-4 col-md-12">
			<div class="card-flip">
							<!-- front content -->
							<div class="card">
							  <div id="card-content">
							  <div class="card-header">
							  <h4>틀린 단어 다시보기</h4>
							  </div>
							<div class="card-body">
							<div class="card-flip" style="height: 400px;" id="word-card">
								<div class="shadow rounded card-front" id="word" style="background-color:rgb(250,250,250);border: 1px solid rgb(230,230,230)">
									<div class="word" style="font-size:1.5rem">Front</div>
								</div>
								<div class="shadow p-2 rounded card-back" id="meaning" style="background-color:rgb(250,250,250);border: 1px solid rgb(230,230,230)">
									<div class="meaning">뜻</div>
								</div>
							</div>
							</div>
							<div class="row">
								<div class="col-12 mt-1 mb-1 text-center">
									<button type="button" disabled='disabled' id="prev" class="btn btn-info btn-icon ">&lt;</button>
									<button type="button" id="next" class="btn btn-info btn-icon ">&gt;</button>
								</div>
								<div class="col-12 mt-1 mb-1 text-center">
									<button type="button" id="gotoPronounce" onclick="location.href='/today/todaySentence.do'" class="btn btn-info btn-icon ">발음 연습하러 가기 &gt; </button>
								</div>
							</div>
							<div class="row">
								<div class="col-12 mb-3 text-center">
								(<span id="current">n</span> / <span id="all">n</span>)
								</div>
							</div>
							</div>
							</div>
							<!-- back content -->
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
    <script src="/js/jquery.flip.min.js"></script>
    <script type="text/javascript">
    // card flip logic
    var cardFlipped = false;
    
    function flipCard(){
    	var currentFront = cardFlipped ? "#meaning" : "#word";
    	var currentBack = !cardFlipped ? "#meaning" : "#word";
    	
   		$(currentBack).css("transform", "rotateY(0deg)");
   		$(currentFront).css("transform", "rotateY(-180deg)");
   		cardFlipped = !cardFlipped;
    }
    
    $("#word-card").on("click", function(e){
    	flipCard();
    	
    })
    
    </script>
    <script type="text/javascript">
    
    var wordList;
    var wordIdx = 0;
    $(document).ready(function(){
    	$.ajax({
			type : "POST",
			url : "getWrongWords.do",
			dataType : "JSON",
			success : function(json) {
				wordList = json.res;
				$("#word > div").html(wordList[0].word);
				$("#meaning > div").html(wordList[0].meaning);
				$("#all").html(wordList.length);
				$("#current").html(wordIdx+1);
			}
    	})
    })
    
    $("#next").on("click", function(){
    	$("#word > div").html(wordList[++wordIdx].word);
    	$("#meaning > div").html(wordList[wordIdx].meaning);
    	refresh();
    });
    
    $("#prev").on("click", function(){
    	$("#word > div").html(wordList[--wordIdx].word);
    	$("#meaning > div").html(wordList[wordIdx].meaning);
    	refresh();
    });
    
    function refresh(){
    	$("#current").html(wordIdx+1);
    	if(wordIdx==0){
    		$("#prev").attr("disabled", "disabled");
    	}
    	else{
    		$("#prev").removeAttr("disabled");
    	}
    	if(wordIdx+1 == wordList.length){
    		$("#next").attr("disabled", "disabled");
    		$("#gotoPronounce").removeAttr("disabled");
    		
    	}else{
    		$("#next").removeAttr("disabled");
    	}
    	if(cardFlipped){
    		flipCard();    		
    	}
    	
    }
    
    
    </script>
    <%@ include file="/WEB-INF/view/footer.jsp" %>
    <!-- END PAGE LEVEL JS-->
  </body>
  
</html>