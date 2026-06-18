<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>パスワード再設定結果</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/navbar.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/reset_confirm.css">
</head>
<body>
    <%@ include file="/WEB-INF/jsp/header.jsp" %>

    <div class="container">
        <div class="result-card">
            <c:choose>
                <c:when test="${isSuccess}">
                    <div class="result-icon">📗</div>
                    <div class="result-title success">設定成功</div>
                    <div class="result-desc">${message}</div>
                    <a href="${pageContext.request.contextPath}/index.jsp" class="btn-action">ログイン画面へ戻る</a>
                </c:when>
                <c:otherwise>
                    <div class="result-icon">⚠️</div>
                    <div class="result-title error">設定エラー</div>
                    <div class="result-desc">${message}</div>
                    <a href="${pageContext.request.contextPath}/ForgotPasswordServlet" class="btn-action secondary">最初からやり直す</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</body>
</html>