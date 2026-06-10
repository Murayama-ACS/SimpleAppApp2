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
<form action="PassReset1" method="post">
<p>社員ID<input type="text" name="emp_id"></p>
<p>メールアドレス<input type="email" name="email"></p>
<input type="submit" value="提出">
</form>
<c:if test="${not empty eMsg }">
			<c:out value="${eMsg }" />
			<br>
		</c:if>
</body>
</html>