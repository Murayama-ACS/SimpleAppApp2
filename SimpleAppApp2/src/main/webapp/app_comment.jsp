<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="bean.ApplicationBean" %>
<%@ page import="bean.ApprovalBean" %>
<%
    // サーブレットからのリクエスト属性の取得
    ApplicationBean app = (ApplicationBean) request.getAttribute("application");
    ApprovalBean approval = (ApprovalBean) request.getAttribute("approvalData");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>申請詳細・コメント モック</title>
</head>
<body>
    <h2>申請詳細・承認情報</h2>

    <% if (app != null) { %>
        <h3>【申請内容】</h3>
        <table border="1" cellpadding="5" cellspacing="0">
            <tr>
                <th style="background-color: #f2f2f2; width: 150px;">申請ID (apct_id)</th>
                <td><%= app.getApctId() %></td>
            </tr>
            <tr>
                <th style="background-color: #f2f2f2;">社員ID (emp_id)</th>
                <td><%= app.getEmployeeId() %></td>
            </tr>
            <tr>
                <th style="background-color: #f2f2f2;">申請種別 (type)</th>
                <td><%= app.getType() %></td>
            </tr>
            <tr>
                <th style="background-color: #f2f2f2;">精算方式 (method)</th>
                <td><%= app.getPaymentMethod() %></td>
            </tr>
            <tr>
                <th style="background-color: #f2f2f2;">申請金額 (amount)</th>
                <td><%= app.getAmount() %> 円</td>
            </tr>
            <tr>
                <th style="background-color: #f2f2f2;">申請内容 (content)</th>
                <td><%= app.getContent() %></td>
            </tr>
            <tr>
                <th style="background-color: #f2f2f2;">申請理由 (reason)</th>
                <td><%= app.getReason() %></td>
            </tr>
            <tr>
                <th style="background-color: #f2f2f2;">備考 (remark)</th>
                <td><%= app.getNote() %></td>
            </tr>
            <tr>
                <th style="background-color: #f2f2f2;">緊急度 (urgent)</th>
                <td><%= app.getUrgent() %></td>
            </tr>
            <tr>
                <th style="background-color: #f2f2f2;">ステータス (status)</th>
                <td><%= app.getStatus() %></td>
            </tr>
            <tr>
                <th style="background-color: #f2f2f2;">作成日時 (create_date)</th>
                <td><%= app.getCreateDate() %></td>
            </tr>
        </table>

        <h3>【承認・コメント履歴】</h3>
        <% if (approval != null) { %>
            <table border="1" cellpadding="5" cellspacing="0">
                <tr>
                    <th style="background-color: #f2f2f2; width: 150px;">履歴ID (approval_id)</th>
                    <td><%= approval.getApprovalId() %></td>
                </tr>
                <tr>
                    <th style="background-color: #f2f2f2;">対応者社員ID (emp_id)</th>
                    <td><%= approval.getEmployeeId() %></td>
                </tr>
                <tr>
                    <th style="background-color: #f2f2f2;">承認時種別 (apct_type)</th>
                    <td><%= approval.getApctType() %></td>
                </tr>
                <tr>
                    <th style="background-color: #f2f2f2;">コメント (comment)</th>
                    <td><%= approval.getComment() %></td>
                </tr>
                <tr>
                    <th style="background-color: #f2f2f2;">処理時間 (time)</th>
                    <td><%= approval.getCreateDate() %></td>
                </tr>
            </table>
        <% } else { %>
            <p style="color: gray;">この申請に対する承認・コメント履歴はまだ登録されていません。</p>
        <% } %>

    <% } else { %>
        <p style="color: red;">申請データが正常に読み込めませんでした。</p>
    <% } %>

    <br>
    <form action="<%= request.getContextPath() %>/ApplicationWaitList" method="get">
        <input type="hidden" name="pendingStatus" value="1">
        <button type="submit">一覧画面へ戻る</button>
    </form>
</body>
</html>