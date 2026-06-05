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

<form action="Login" method="post">
    Email:<input type="email" name="email"><br>
    Pass :<input type="password" name="pass"><br>
    <input type="submit" name="action" value="ログイン">
    <input type="submit" name="action" value="新規登録">
</form>

<c:if test="${not empty eMsg }">
    <c:out value="${eMsg }" /><br>
</c:if>
</body>
</html>