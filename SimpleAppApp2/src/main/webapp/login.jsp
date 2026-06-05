<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>SimpleChatApp2</title>
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/css/logControl.css"
</head>
<body>
	<div class="container">
		<h1>SimpleAppApp2</h1>
		<form action="Login" method="post">
			社員ID or Email:<input type="text" name="identifier"><br> 
			Pass :<input type="password" name="pass"><br> 
			<input type="submit" value="ログイン" class="login"><br>
		</form>
		<a href="AddUser">新規ユーザーの追加はこちら</a><br>
		<c:if test="${not empty eMsg }">
			<c:out value="${eMsg }" />
			<br>
		</c:if>
	</div>
</body>
</html>