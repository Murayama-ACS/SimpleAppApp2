<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>SimpleChatApp2</title>
</head>
<body>
<h1>SimpleChatApp2</h1>
<form action="Register" method="post">
    名前: <input type="text" name="name"><br>
    Email: <input type="email" name="email"><br>
    Pass : <input type="password" name="pass"><br>
    <input type="submit" value="アカウントを作成">
</form>

<c:if test="${not empty rMsg}">
    <p><c:out value="${rMsg}" /></p>
</c:if>

<br>
<a href="index.jsp">ログイン画面に戻る</a>
</body>
</html>