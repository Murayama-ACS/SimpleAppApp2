<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>Password Reset</title>
    <%-- 共通のナビゲーションCSS（読み込み用）と、刷新された専用CSSの読み込み --%>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/pass_reset2.css">
</head>
<body>
    <div class="reset-card">
        <%-- カードヘッダー領域（タイトルとステップ表示） --%>
        <div class="card-header">
            <h2>新しいパスワードの設定</h2>
            <div class="step-indicator">ステップ 3 / 3</div>
        </div>
        
        <%-- カードボディ領域（フォーム入力とエラー表示） --%>
        <div class="card-body">
            <form action="PassReset" method="post">
                <%-- 前の画面から引き継いだ社員IDを隠しパラメータで伝送 --%>
                <input type="hidden" name="empId" value="<%= request.getParameter("empId") != null ? request.getParameter("empId") : "" %>">

                <%-- 新しいパスワード入力項目 --%>
                <div class="form-group">
                    <label for="newPassword">新しいパスワード</label>
                    <input type="password" id="newPassword" name="newPassword" placeholder="8文字以上の英数字" required minlength="8">
                </div>
                
                <%-- 確認用パスワード入力項目 --%>
                <div class="form-group">
                    <label for="confirmPassword">確認</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" placeholder="もう一度入力してください" required minlength="8">
                </div>

                <%-- サーブレット側でエラー（パスワード不一致など）が発生した場合に警告バッジを表示 --%>
                <c:if test="${not empty errorMessage}">
                    <div class="error-message">
                        ${errorMessage}
                    </div>
                </c:if>

                <%-- パスワード変更確定ボタン --%>
                <button type="submit" class="btn-submit">パスワードを変更する</button>
            </form>
        </div> 
    </div>
</body>
</html>