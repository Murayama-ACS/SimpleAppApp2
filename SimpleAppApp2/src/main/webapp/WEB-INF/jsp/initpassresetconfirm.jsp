<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>SimpleAppApp2</title>
</head>
<body>
<h2>新しいパスワード、秘密の質問の登録が完了しました！</h2>
<c:if test="${not empty eMsg }">
			<c:out value="${eMsg }" />
			<br>
</c:if>
<a href="toppage.jsp">トップページ</a>
</body>
</html>