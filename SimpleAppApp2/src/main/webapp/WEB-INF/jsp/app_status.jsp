<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="bean.ApplicationBean" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
    // 表示対象の申請データを取得
    ApplicationBean app = (ApplicationBean) request.getAttribute("application");
    // 更新処理成功フラグを取得
    Boolean showSuccessPopup = (Boolean) request.getAttribute("showSuccessPopup");
    
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>申請詳細・ステータス変更確認</title>
    <style>
        body { font-family: "Helvetica Neue", Arial, sans-serif; margin: 20px; background-color: #f5f7fa; color: #333; }
        h1 { font-size: 22px; border-bottom: 2px solid #333; padding-bottom: 8px; margin-bottom: 20px; }
        .detail-table { width: 100%; border-collapse: collapse; background: #fff; margin-bottom: 20px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
        .detail-table th, .detail-table td { border: 1px solid #cbd5e0; padding: 12px; font-size: 14px; text-align: left; }
        .detail-table th { background-color: #edf2f7; width: 20%; font-weight: bold; }
        .form-area { background-color: #fff; padding: 15px; border: 1px solid #cbd5e0; border-radius: 4px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
        .btn-group { margin-top: 5px; }
        .btn { display: inline-block; padding: 8px 16px; font-size: 14px; cursor: pointer; border-radius: 4px; text-decoration: none; border: 1px solid transparent; }
        .btn-primary { background-color: #3182ce; color: white; }
        .btn-primary:hover { background-color: #2b6cb0; }
        .btn-default { background-color: #e2e8f0; color: #4a5568; border-color: #cbd5e0; margin-left: 10px; }
        .btn-default:hover { background-color: #cbd5e0; }
    </style>
    <script>
        // サーブレットでの更新処理完了後にこの画面が再表示されたら、ポップアップを出し一覧へ遷移させる
        window.onload = function() {
            <% if (showSuccessPopup != null && showSuccessPopup) { %>
                alert("変更完了しました");
                // ポップアップを閉じたら、自動的に一覧画面へ戻る
                window.location.href = "<%= request.getContextPath() %>/ApplicationStatus";
            <% } %>
        };
    </script>
</head>
<body>

    <h1>申請ステータス変更詳細</h1>

    <% if (app != null) { %>
        <%-- 申請データの詳細表示部分 --%>
        <table class="detail-table">
            <tr>
                <th>申請ID</th>
                <td><%= app.getApctId() %></td>
            </tr>
            <tr>
                <th>申請者名</th>
                <td><%= app.getEmployeeName() != null ? app.getEmployeeName() : "" %></td>
            </tr>
            <tr>
                <th>部署名</th>
                <td><%= app.getDepartmentName() != null ? app.getDepartmentName() : "" %></td>
            </tr>
            <tr>
                <th>申請種別</th>
                <td><%= app.getType() != null ? app.getType() : "" %></td>
            </tr>
            <tr>
                <th>申請金額</th>
                <td><%= String.format("%,d円", app.getAmount()) %></td>
            </tr>
            <tr>
                <th>現在の状態</th>
                <td style="font-weight: bold; color: #2b6cb0;"><%= app.getStatusName() != null ? app.getStatusName() : "" %></td>
            </tr>
            <tr>
                <th>申請内容</th>
                <td><%= app.getContent() != null ? app.getContent() : "" %></td>
            </tr>
            <tr>
                <th>作成日時</th>
                <td><%= app.getCreateDate() != null ? app.getCreateDate().format(formatter) : "" %></td>
            </tr>
        </table>

        <%-- 更新完了後はフォームを非表示にする（二重送信の防止） --%>
        <% if (showSuccessPopup == null || !showSuccessPopup) { %>
            <%-- ステータス変更を実行するためのフォーム（コメント欄を削除） --%>
            <div class="form-area">
                <form action="<%= request.getContextPath() %>/ApplicationStatusEdit" method="post">
                    <%-- 必須パラメータとして申請IDのみを送信 --%>
                    <input type="hidden" name="apct_id" value="<%= app.getApctId() %>">

                    <div class="btn-group">
                        <button type="submit" class="btn btn-primary" onclick="return confirm('この申請のステータス変更を完了しますか？');">完了</button>
                        <a href="<%= request.getContextPath() %>/ApplicationStatus" class="btn btn-default">戻る</a>
                    </div>
                </form>
            </div>
        <% } %>

    <% } else { %>
        <p>該当する申請データが見つかりません。</p>
        <a href="<%= request.getContextPath() %>/ApplicationStatus" class="btn btn-default">一覧画面へ戻る</a>
    <% } %>

</body>
</html>