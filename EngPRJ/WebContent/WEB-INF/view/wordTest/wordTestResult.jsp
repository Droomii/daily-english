<%

	String lvl = (String)request.getAttribute("lvl");

%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<div class="card-body">
	<h4 class="card-title mb-0 text-center" style="font-size:1.5rem">당신의 영어 실력은...</h4>
	<h4 class="card-title mt-3 text-center" style="font-size:2rem">Level <%=lvl %></h4>
	<div class="row text-center">
	<button type="button" id="next" class="btn mb-1 btn-info btn-lg btn-block" onclick="location.href='/index.do'">처음으로</button>
	</div>
</div>