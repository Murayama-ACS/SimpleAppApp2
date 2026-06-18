<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>Password Reset</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/pass_reset2.css">
</head>
<body>
    <div class="reset-card">
        <h2>新しいパスワードの設定</h2>
        <div class="step-indicator">ステップ 3 / 3</div>
        
        <div class="card-body">
            <form action="PassReset" method="post">
                <input type="hidden" name="empId" value="<%= request.getParameter("empId") != null ? request.getParameter("empId") : "" %>">

                <div class="form-group">
                    <label for="newPassword">新しいパスワード</label>
                    <input type="password" id="newPassword" name="newPassword" placeholder="8文字以上の英数字" required minlength="8">
                </div>
                
                <div class="form-group">
                    <label for="confirmPassword">確認</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" placeholder="もう一度入力してください" required minlength="8">
                </div>
                <c:if test="${not empty errorMessage}">
                    <div class="error-message">
                        ${errorMessage}
                    </div>
                </c:if>
                <button type="submit" class="btn-submit">パスワードを変更する</button>
            </form>
        </div> 
    </div>
</body>
</html>