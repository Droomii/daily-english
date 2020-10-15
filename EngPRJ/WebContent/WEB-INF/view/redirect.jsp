<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String url = (String)request.getAttribute("url");
	String msg = (String)request.getAttribute("msg");
%>

<!DOCTYPE html>
<html>
<head>
<script type="text/javascript">
alert("<%=msg%>")
location.href = "<%=url%>"

</script>
<meta charset="UTF-8">
<title>redirect</title>
</head>
<body>

</body>
</html>