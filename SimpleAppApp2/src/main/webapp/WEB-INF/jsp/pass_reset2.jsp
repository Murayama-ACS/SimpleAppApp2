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
<form action="PassResetConfirm2" method="post">
新しいパスワード<input type="password" name="pass">
確認<input type="password" name="retype">
<input type="submit" value="提出">
</form>
<c:if test="${not empty eMsg }">
			<c:out value="${eMsg }" />
			<br>
		</c:if>
</body>
</html>