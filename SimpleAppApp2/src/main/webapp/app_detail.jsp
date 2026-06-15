<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="bean.ApplicationBean" %>
<%
    // サーブレットから渡された申請データを取得
    ApplicationBean app = (ApplicationBean) request.getAttribute("application");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>申請詳細確認</title>
<style>
    body { font-family: sans-serif; margin: 20px; }
    .detail-table { width: 100%; max-width: 600px; border-collapse: collapse; margin-top: 20px; }
    .detail-table th, .detail-table td { border: 1px solid #ccc; padding: 12px; text-align: left; }
    .detail-table th { background-color: #f5f5f5; width: 30%; }
    .btn-back { display: inline-block; margin-top: 20px; padding: 10px 20px; background-color: #6c757d; color: white; text-decoration: none; border-radius: 4px; font-size: 14px; }
    .btn-back:hover { background-color: #5a6268; }
    .urgent-badge { background-color: #dc3545; color: white; padding: 2px 6px; border-radius: 3px; font-size: 12px; font-weight: bold; }
</style>
</head>
<body>

    <h2>申請詳細確認</h2>

    <% if (app != null) { %>
        <table class="detail-table">
            <tr>
                <th>申請ID</th>
                <td><%= app.getApctId() %></td>
            </tr>
            <tr>
                <th>申請者氏名</th>
                <td><%= app.getEmployeeName() %></td>
            </tr>
            <tr>
                <th>所属部門</th>
                <td><%= app.getDepartmentName() != null ? app.getDepartmentName() : "未設定" %></td>
            </tr>
            <tr>
                <th>申請種別</th>
                <td><%= app.getType() %></td>
            </tr>
            <tr>
                <th>支払方法</th>
                <td><%= app.getPaymentMethod() %></td>
            </tr>
            <tr>
                <th>金額</th>
                <td><strong><%= String.format("%,d円", app.getAmount()) %></strong></td>
            </tr>
            <tr>
                <th>緊急度</th>
                <td>
                    <% if ("緊急".equals(app.getUrgent())) { %>
                        <span class="urgent-badge">緊急</span>
                    <% } else { %>
                        通常
                    <% } %>
                </td>
            </tr>
            <tr>
                <th>申請状況</th>
                <td>
                    <%
                        int sid = app.getStatus_id();
                        if (sid == 1) out.print("未承認");
                        else if (sid == 2) out.print("上長承認");
                        else if (sid == 3) out.print("管理部承認");
                        else if (sid == 4) out.print("社長承認");
                        else if (sid == 5) out.print("完了");
                        else if (sid == 6) out.print("却下");
                        else if (sid == 7) out.print("削除");
                    %>
                </td>
            </tr>
            <tr>
                <th>申請内容</th>
                <td><%= app.getContent() != null ? app.getContent().replace("\n", "<br>") : "" %></td>
            </tr>
            <tr>
                <th>申請理由</th>
                <td><%= app.getReason() != null ? app.getReason().replace("\n", "<br>") : "" %></td>
            </tr>
            <tr>
                <th>備考（却下理由など）</th>
                <td><%= app.getNote() != null ? app.getNote().replace("\n", "<br>") : "---" %></td>
            </tr>
            <tr>
                <th>申請日時</th>
                <td><%= app.getCreateDate() != null ? app.getCreateDate().format(java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")) : "---" %></td>
            </tr>
        </table>
    <% } else { %>
        <p>申請データの読み込みに失敗しました。</p>
    <% } %>

    <a href="ApplicationHistoryServlet" class="btn-back">履歴一覧へ戻る</a>

</body>
</html>