<%--
モックのため必ず削除すること 
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // サーブレットから渡された処理結果種別（"承認" または "却下"）を取得
    String processType = (String) request.getAttribute("processType");
    if (processType == null) {
        processType = "処理";
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>処理完了 モック</title>
</head>
<body>
    <h2>申請処理完了</h2>
    <p style="color: green; font-size: 1.2em; font-weight: bold;">
        <%= processType %>が完了しました。
    </p>
    <br>
    
    <%-- 一覧表示サーブレットのdoGetを利用して未承認申請一覧に戻るボタン --%>
    <form action="<%= request.getContextPath() %>/ApplicationWaitList" method="get">
        <input type="hidden" name="pendingStatus" value="1">
        <button type="submit" style="padding: 5px 15px;">未承認申請一覧に戻る</button>
    </form>
</body>
</html>