<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="bean.ApplicationBean" %>
<%@ page import="bean.EmployeeBean" %>
<%
    // セッション情報の確認
    EmployeeBean employee = (EmployeeBean) session.getAttribute("EmployeeBean");
    String empName = (employee != null) ? employee.getEmp_name() : "ゲスト";

    // サーブレットからのリクエスト属性の取得
    List<ApplicationBean> list = (List<ApplicationBean>) request.getAttribute("applications");
    String currentStatus = (String) request.getAttribute("currentStatus");
    String errorMessage = (String) request.getAttribute("errorMessage");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>申請一覧（未承認） モック</title>
</head>
<body>
    <h2>申請一覧画面（ステータス: <%= currentStatus %>）</h2>
    <p>ログインユーザー: <%= empName %> さん</p>

    <% if (errorMessage != null) { %>
        <p style="color: red;"><%= errorMessage %></p>
    <% } %>

    <form action="<%= request.getContextPath() %>/ApplicationWaitList" method="get">
        <label>表示ステータス: </label>
        <input type="text" name="pendingStatus" value="<%= currentStatus %>" size="5">
        <button type="submit">切り替え</button>
    </form>
    <br>

    <table border="1" cellpadding="5" cellspacing="0">
        <thead>
            <tr style="background-color: #f2f2f2;">
                <th>申請ID</th>
                <th>社員ID</th>
                <th>申請種別</th>
                <th>金額</th>
                <th>緊急度</th>
                <th>状態</th>
                <th>申請日時</th>
                <th>操作</th>
            </tr>
        </thead>
        <tbody>
            <% if (list != null && !list.isEmpty()) { %>
                <% for (ApplicationBean app : list) { %>
                    <tr>
                        <td><%= app.getApctId() %></td>
                        <td><%= app.getEmployeeId() %></td>
                        <td><%= app.getType() %></td>
                        <td><%= app.getAmount() %> 円</td>
                        <td><%= app.getUrgent() %></td>
                        <td><%= app.getStatus() %></td>
                        <td><%= app.getCreateDate() %></td>
                        <td>
                            <form action="<%= request.getContextPath() %>/ApplicationComment" method="post" style="margin:0;">
                                <input type="hidden" name="apct_id" value="<%= app.getApctId() %>">
                                <button type="submit">詳細・コメント</button>
                            </form>
                        </td>
                    </tr>
                <% } %>
            <% } else { %>
                <tr>
                    <td colspan="8" style="text-align: center;">対象の申請データが存在しません。</td>
                </tr>
            <% } %>
        </tbody>
    </table>
    
    <br>
    <a href="<%= request.getContextPath() %>/login_mock.jsp">ログイン画面へ戻る</a>
</body>
</html>