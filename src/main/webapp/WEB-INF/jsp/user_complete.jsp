<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>登録完了 - AppApp システム</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/navbar.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/user_complete.css">
</head>
<body>
    <%@ include file="/WEB-INF/jsp/header.jsp" %>

    <div class="container"> 
        <div class="complete-card">
            <div class="success-icon">✔</div>
            
            <h2 class="card-title">登録が完了しました</h2>
            <p class="card-message">新しい社員情報のデータベースへの保存が正常に完了しました。</p>
            
            <div class="action-area">
                <a href="${pageContext.request.contextPath}/EmployeeInfo" class="btn-home">⬅ 社員一覧に戻る</a>
            </div>
        </div> 
    </div>
</body>
</html>