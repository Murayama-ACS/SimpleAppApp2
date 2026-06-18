<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title>Password Reset</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/verify1.css">
</head>
<body>
	<div class="reset-card">
        <h2>本人確認</h2>
        <div class="step-indicator">ステップ 1 / 3</div>
        
        <div class="card-body">
	        <form action="Verify1" method="post">
	            <div class="form-group">
	                <label for="empId">社員ID</label>
	                <input type="text" id="empId" name="empId" placeholder="社員ID" required>
	            </div>
	            <div class="form-group">
	                <label for="email">Email</label>
	                <input type="email" id="email" name="email" placeholder="メールアドレス" required>
	            </div>
	            <c:if test="${not empty errorMessage}">
	    			<div class="error-message">
	        			${errorMessage}
	    			</div>
				</c:if>
	            <button type="submit" class="btn-submit">次へ進む</button>
	        </form>
            <a href="index.jsp" class="back-link">ログイン画面に戻る</a>
	    </div>
    </div>
</body>
</html>