<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>削除完了 - AppApp システム</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/navbar.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/remove_confirm.css">
</head>
<body>
    <%@ include file="/WEB-INF/jsp/header.jsp" %>

    <div class="container"> 
        <div class="complete-card">
            <div class="delete-icon">🗑️</div>
            
            <h2 class="card-title">削除が完了しました</h2>
            <p class="card-message">指定された社員データは、システムから正常に削除（論理削除）されました。</p>
            
            <div class="action-area">
                <a href="${pageContext.request.contextPath}/EmployeeInfo" class="btn-home">⬅ 社員一覧に戻る</a>
            </div>
        </div> 
    </div>
</body>
</html>