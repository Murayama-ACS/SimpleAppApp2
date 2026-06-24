<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>パスワード再設定結果 - AppApp システム</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/reset_confirm.css">
    <%-- Font Awesome（アイコン）の読み込み --%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
    <div class="container">
        <div class="result-card">
            <c:choose>
                <%-- 成功時（パスワード更新完了） --%>
                <c:when test="${isSuccess}">
                    <div class="result-icon success-icon">
                        <i class="fa-regular fa-circle-check"></i>
                    </div>
                    <div class="result-title success">設定成功</div>
                    <div class="result-desc">${message}</div>
                    <a href="${pageContext.request.contextPath}/index.jsp" class="btn-action">ログイン画面へ戻る</a>
                </c:when>

                <%-- 失敗時（エラー発生） --%>
                <c:otherwise>
                    <div class="result-icon error-icon">
                        <i class="fa-solid fa-triangle-exclamation"></i>
                    </div>
                    <div class="result-title error">設定エラー</div>
                    <div class="result-desc">${message}</div>
                    <a href="${pageContext.request.contextPath}/ForgotPasswordServlet" class="btn-action secondary">最初からやり直す</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</body>
</html>