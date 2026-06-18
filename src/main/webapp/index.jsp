<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>AppApp System</title>
<link rel="stylesheet" type="text/css" href="css/index.css">
</head>
<body>
	    <div class="login-card">
        <div class="card-header">
            <h1>AppApp System</h1>
            <div class="subtitle">申請管理システム</div>
        </div>

        <div class="card-body">
            <h2>ログイン</h2>    
            
            <c:if test="${not empty eMsg}">
                <div class="error-message">
                    ${eMsg}
                </div>
            </c:if>
            <c:if test="${param.changed == 'true'}">
    			<div class="success-message">
       				 初期設定が完了しました。新しいパスワードでログインしてください。
    			</div>
			</c:if>
      
            <form action="Login" method="post">
                <div class="form-group">
                    <label for="empId">社員ID または Email</label>
                    <input type="text" id="empId" name="empId" placeholder="IDかEmailを入力" required autofocus>
                </div>
                
                <div class="form-group">
                    <label for="password">パスワード</label>
                    <input type="password" id="password" name="password" placeholder="パスワードを入力" required>
                </div>
                
                <div class="forgot-password">
                    <a href="Verify1">パスワードをお忘れですか？</a>
                </div>
                
                <button type="submit" class="btn-submit">ログイン</button>
            </form>
        </div>
    </div>
	
</body>
</html>