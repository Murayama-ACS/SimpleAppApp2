<%--
モックのため必ず削除すること
 --%>
 <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="bean.ApplicationBean" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
    // サーブレットから渡された申請一覧データを取得
    List<ApplicationBean> applications = (List<ApplicationBean>) request.getAttribute("applications");
    // エラーメッセージの取得
    String errorMessage = (String) request.getAttribute("errorMessage");

    // 日時表示用のフォーマッター定義
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>申請ステータス変更一覧</title>
    <style>
        body {
            font-family: "Helvetica Neue", Arial, "Hiragino Kaku Gothic ProN", "Hiragino Sans", Meiryo, sans-serif;
            margin: 20px;
            background-color: #f5f7fa;
            color: #333;
        }
        h1 {
            font-size: 24px;
            border-bottom: 2px solid #333;
            padding-bottom: 10px;
            margin-bottom: 20px;
        }
        .alert-error {
            background-color: #fde8e8;
            border: 1px solid #e53e3e;
            color: #c53030;
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 20px;
        }
        .nav-actions {
            margin-bottom: 15px;
        }
        .btn {
            display: inline-block;
            padding: 6px 12px;
            font-size: 14px;
            cursor: pointer;
            border-radius: 4px;
            text-decoration: none;
            border: 1px solid transparent;
        }
        .btn-default {
            background-color: #e2e8f0;
            color: #4a5568;
            border-color: #cbd5e0;
        }
        .btn-default:hover {
            background-color: #cbd5e0;
        }
        .btn-primary {
            background-color: #3182ce;
            color: #fff;
        }
        .btn-primary:hover {
            background-color: #2b6cb0;
        }
        .list-table {
            width: 100%;
            border-collapse: collapse;
            background-color: #fff;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }
        .list-table th, .list-table td {
            border: 1px solid #e2e8f0;
            padding: 10px;
            text-align: left;
            font-size: 14px;
        }
        .list-table th {
            background-color: #edf2f7;
            font-weight: bold;
        }
        .list-table tr:hover {
            background-color: #f7fafc;
        }
        .badge-urgent {
            background-color: #fed7d7;
            color: #9b2c2c;
            padding: 2px 6px;
            border-radius: 4px;
            font-size: 12px;
            font-weight: bold;
        }
    </style>
</head>
<body>

    <h1>申請ステータス変更一覧</h1>

    <%-- エラーメッセージが存在する場合に表示 --%>
    <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
        <div class="alert-error">
            <%= errorMessage %>
        </div>
    <% } %>

    <div class="nav-actions">
        <a href="<%= request.getContextPath() %>/toppage.jsp" class="btn btn-default">トップページへ戻る</a>
    </div>

    <%-- 申請一覧のテーブル表示 --%>
    <table class="list-table">
        <thead>
            <tr>
                <th>申請ID</th>
                <th>申請者氏名</th>
                <th>部署名</th>
                <th>申請種別</th>
                <th>金額</th>
                <th>緊急度</th>
                <th>現在の状態</th>
                <th>申請日時</th>
                <th>操作</th>
            </tr>
        </thead>
        <tbody>
            <% 
                if (applications != null && !applications.isEmpty()) { 
                    for (ApplicationBean app : applications) {
            %>
                <tr>
                    <td><%= app.getApctId() %></td>
                    <td><%= app.getEmployeeName() != null ? app.getEmployeeName() : "" %></td>
                    <td><%= app.getDepartmentName() != null ? app.getDepartmentName() : "" %></td>
                    <td><%= app.getType() != null ? app.getType() : "" %></td>
                    <td><%= String.format("%,d円", app.getAmount()) %></td>
                    <td>
                        <% if ("緊急".equals(app.getUrgent())) { %>
                            <span class="badge-urgent">緊急</span>
                        <% } else { %>
                            <%= app.getUrgent() != null ? app.getUrgent() : "通常" %>
                        <% } %>
                    </td>
                    <td><%= app.getStatusName() != null ? app.getStatusName() : "" %></td>
                    <td><%= app.getCreateDate() != null ? app.getCreateDate().format(formatter) : "" %></td>
                    <td>
                        <%-- 
                          「ステータス変更」ボタン。
                          押下時に ApplicationStatus（POST）へ申請IDを送信し、詳細画面（app_status.jsp）を表示させます。
                        --%>
                        <form action="<%= request.getContextPath() %>/ApplicationStatus" method="post" style="margin: 0;">
                            <input type="hidden" name="apct_id" value="<%= app.getApctId() %>">
                            <button type="submit" class="btn btn-primary">ステータス変更</button>
                        </form>
                    </td>
                </tr>
            <% 
                    }
                } else { 
            %>
                <tr>
                    <td colspan="9" style="text-align: center; color: #718096;">対象の申請データは存在しません。</td>
                </tr>
            <% } %>
        </tbody>
    </table>

</body>
</html>