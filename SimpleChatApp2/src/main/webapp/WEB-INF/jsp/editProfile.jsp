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

<form action="EditProfile" method="post">
    Email: <c:out value="${userBean.email}" /><br>
    
    新しい名前: <input type="text" name="name"><br>
    新しいPass: <input type="password" name="pass"><br>
    
    <input type="submit" value="変更を保存する">
</form>

<c:if test="${not empty editMsg}">
    <p><c:out value="${editMsg}" /></p>
</c:if>

<br>
<a href="Main">チャット画面に戻る</a>
</body>
</html>