<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="beans.ChatBean"%>
<%@ page import="beans.UserBean"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>SimpleChatApp</title>
</head>
<h1>SimpleChatApp2</h1>
<body>

	<c:if test="${not empty userBean.name}">
		<p>
			<c:out value="${userBean.name}" />
			さん、ログイン中 <a href="EditProfile">プロフィール変更</a>
		</p>
	</c:if>
	<form action="Main" method="post">
		<input type="text" name="Msg"> <input type="submit" value="📩">
	</form>
	<c:if test="${not empty noMsg}">
		<p>
			<c:out value="${noMsg}" />
		</p>
	</c:if>
	<c:if test="${not empty ChatList}">
		<c:forEach var="chat" items="${ChatList}">
			<p>
				<c:out value="${chat.date}" />
				<c:out value="${chat.time}" />
				<c:out value="${chat.name}" />
				:
				<c:out value="${chat.text}" />
			</p>
		</c:forEach>
	</c:if>


	<a href="Logout">ログアウト</a>
</body>
</html>