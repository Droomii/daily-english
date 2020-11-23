<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String yes = (String)request.getAttribute("yes");
	String no = (String)request.getAttribute("no");
	String msg = (String)request.getAttribute("msg");
%>

<!DOCTYPE html>
<html>
<head>
<script type="text/javascript">
if(confirm("<%=msg%>")){
	location.href = "<%=yes%>"
}else{
	location.href = "<%=no%>"
}





</script>
<meta charset="UTF-8">
<title>redirect</title>
</head>
<body>

</body>
</html>