<%--
モックのため必ず削除すること 
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="bean.ApplicationBean" %>
<%
    ApplicationBean app = (ApplicationBean) request.getAttribute("application");
    
    String apctId = "";
    String type = "";
    String method = "";
    int amount = 0;
    String content = "";
    String reason = "";
    String note = "";
    String urgent = "";

    if (app != null) {
        apctId = app.getApctId();
        type = app.getType();
        method = app.getPaymentMethod();
        amount = app.getAmount();
        content = app.getContent();
        reason = app.getReason();
        note = app.getNote();
        urgent = app.getUrgent();
    }
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>申請編集画面 モック</title>
</head>
<body>
    <h2>申請編集フォーム（モック）</h2>
    <p>履歴一覧から指定された申請情報を編集し、提出します。</p>

    <form action="<%= request.getContextPath() %>/ApplicationHistory" method="post">
        <table border="1" cellpadding="5" cellspacing="0">
            <tr>
                <td><label>申請ID (apct_id):</label></td>
                <td>
                    <input type="text" name="apct_id" value="<%= apctId %>" readonly style="background:#e9e9e9;">
                    <span>（編集不可）</span>
                </td>
            </tr>
            <tr>
                <td><label for="applicationType">申請種別 (type):</label></td>
                <td><input type="text" id="applicationType" name="applicationType" value="<%= type %>" required></td>
            </tr>
            <tr>
                <td><label for="paymentMethod">精算方法 (method):</label></td>
                <td><input type="text" id="paymentMethod" name="paymentMethod" value="<%= method %>"></td>
            </tr>
            <tr>
                <td><label for="amount">金額 (amount):</label></td>
                <td><input type="number" id="amount" name="amount" value="<%= amount %>" required></td>
            </tr>
            <tr>
                <td><label for="content">申請内容 (content):</label></td>
                <td><textarea id="content" name="content" rows="4" cols="40" required><%= content %></textarea></td>
            </tr>
            <tr>
                <td><label for="reason">申請理由 (reason):</label></td>
                <td><textarea id="reason" name="reason" rows="4" cols="40" required><%= reason %></textarea></td>
            </tr>
            <tr>
                <td><label for="note">備考 (remark):</label></td>
                <td><input type="text" id="note" name="note" value="<%= note != null ? note : "" %>"></td>
            </tr>
            <tr>
                <td><label for="urgentFlag">緊急フラグ (urgent):</label></td>
                <td>
                    <input type="checkbox" id="urgentFlag" name="urgentFlag" value="true" <%= "緊急".equals(urgent) ? "checked" : "" %>> 緊急
                </td>
            </tr>
        </table>
        <br>
        <input type="submit" value="提出する">
    </form>
    
    <br>
    <a href="<%= request.getContextPath() %>/ApplicationHistory">変更せずに履歴一覧へ戻る</a>
</body>
</html>