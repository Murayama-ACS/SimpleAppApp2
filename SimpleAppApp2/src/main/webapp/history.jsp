<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="bean.ApplicationBean" %>
<%@ page import="bean.EmployeeBean" %>
<%
    // セッションからログインユーザー情報を取得（ヌルチェックはサーブレットで実施済み）
    EmployeeBean employee = (EmployeeBean) session.getAttribute("loginEmployee");
    String empId = (employee != null) ? employee.getEmp_id() : "";
    String dptId = (employee != null) ? employee.getDpt_id() : "";
    String posId = (employee != null) ? employee.getPos_id() : "";

    // サーブレットから渡された各種属性を取得
    List<ApplicationBean> historyList = (List<ApplicationBean>) request.getAttribute("appList");
    String dptName = (String) request.getAttribute("dpt_name");
    String currentScope = (String) request.getAttribute("currentScope");
    String currentStatusFilter = (String) request.getAttribute("currentStatusFilter");

    // デフォルト値の安全な補填
    if (currentScope == null) currentScope = "self";
    if (currentStatusFilter == null) currentStatusFilter = "incomplete";

    // 【修正】画面に「操作」列を表示するかどうかの判定
    // フィルターが未完了（incomplete）であるか、リスト内にstatus_idが1〜4（未承認〜社長承認）のデータが1つでもある場合
    boolean showActionColumn = "incomplete".equals(currentStatusFilter);
    if (!showActionColumn && historyList != null) {
        for (ApplicationBean app : historyList) {
            if (app.getStatus_id() >= 1 && app.getStatus_id() <= 4) {
                showActionColumn = true;
                break;
            }
        }
    }
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>申請履歴一覧</title>
<style>
    body { font-family: sans-serif; margin: 20px; }
    .header-info { margin-bottom: 20px; padding: 10px; background-color: #f0f0f0; }
    .tab-group { margin-bottom: 15px; }
    .tab { display: inline-block; padding: 10px 20px; margin-right: 5px; background-color: #e0e0e0; text-decoration: none; color: #333; border-radius: 4px; }
    .tab.active { background-color: #007bff; color: white; font-weight: bold; }
    table { width: 100%; border-collapse: collapse; margin-top: 10px; }
    th, td { border: 1px solid #ccc; padding: 10px; text-align: left; }
    th { background-color: #f5f5f5; }
    .btn { padding: 5px 10px; border: none; border-radius: 3px; cursor: pointer; text-decoration: none; color: white; font-size: 13px; }
    .btn-edit { background-color: #28a745; margin-right: 5px; }
    .btn-delete { background-color: #dc3545; }
</style>
<script type="text/javascript">
    function confirmDelete() {
        return confirm("この申請を削除してもよろしいですか？");
    }
</script>
</head>
<body>

    <h2>申請履歴一覧</h2>

    <div class="header-info">
        ログイン社員: <%= (employee != null) ? employee.getEmp_name() : "" %> 
        (部署: <%= dptName != null ? dptName : "未設定" %> / 
         役職: <% 
            if("E04".equals(posId)) out.print("社長");
            else if("E03".equals(posId)) out.print("本部長");
            else if("E02".equals(posId)) out.print("部長");
            else if("E01".equals(posId)) out.print("課長");
            else out.print("一般社員");
         %>)
    </div>

    <% if (!"E00".equals(posId) || "D100".equals(dptId)) { %>
    <div class="tab-group">
        <a href="ApplicationHistoryServlet?scope=self&filter=<%= "incomplete".equals(currentStatusFilter) ? "unapproved" : "all" %>" 
           class="tab <%= "self".equals(currentScope) ? "active" : "" %>">自身</a>
        
        <% if (!"E00".equals(posId)) { // 上長用 %>
            <a href="ApplicationHistoryServlet?scope=subordinate&filter=<%= "incomplete".equals(currentStatusFilter) ? "unapproved" : "all" %>" 
               class="tab <%= "subordinate".equals(currentScope) ? "active" : "" %>">配下</a>
        <% } %>
        
        <% if ("D100".equals(dptId)) { // 管理部特権 %>
            <a href="ApplicationHistoryServlet?scope=management&filter=<%= "incomplete".equals(currentStatusFilter) ? "unapproved" : "all" %>" 
               class="tab <%= "management".equals(currentScope) ? "active" : "" %>">管理</a>
        <% } %>
    </div>
    <% } %>

    <div class="tab-group">
        <a href="ApplicationHistoryServlet?scope=<%= currentScope %>&filter=unapproved" 
           class="tab <%= "incomplete".equals(currentStatusFilter) ? "active" : "" %>">未完了</a>
        <a href="ApplicationHistoryServlet?scope=<%= currentScope %>&filter=all" 
           class="tab <%= "all".equals(currentStatusFilter) ? "active" : "" %>">全て表示</a>
    </div>

    <table>
        <thead>
            <tr>
                <th>申請ID</th>
                <th>申請者名</th>
                <th>申請種別</th>
                <th>支払方法</th>
                <th>金額</th>
                <th>申請内容</th>
                <th>緊急度</th>
                <th>ステータス</th>
                <% if (showActionColumn) { %>
                    <th>操作</th>
                <% } %>
            </tr>
        </thead>
        <tbody>
            <% 
                if (historyList != null && !historyList.isEmpty()) { 
                    for (ApplicationBean app : historyList) {
                        int sid = app.getStatus_id();
                        boolean isOwnApplication = empId.equals(app.getEmployeeId());
                        
                        // 【判定ルール】
                        // 修正・削除：未承認(1) かつ 自身の申請であること
                        boolean canEditOrDelete = (sid == 1 && isOwnApplication);
                        
                        // 【修正ルール】
                        // 管理部削除特権：管理スコープでの全社表示時、ステータスが完了(5)であれば削除可能
                        boolean isManagementDelete = ("management".equals(currentScope) && "D100".equals(dptId) && sid == 5);
            %>
                <tr>
                    <td><%= app.getApctId() %></td>
                    <td><%= app.getEmployeeName() %></td>
                    <td><%= app.getType() %></td>
                    <td><%= app.getPaymentMethod() %></td>
                    <td><%= String.format("%,d円", app.getAmount()) %></td>
                    <td><%= app.getContent() %></td>
                    <td><%= app.getUrgent() %></td>
                    <td>
                        <%
                            // 【修正】新ステータスマスタのマッピング
                            if (sid == 1) out.print("未承認");
                            else if (sid == 2) out.print("上長承認");
                            else if (sid == 3) out.print("管理部承認");
                            else if (sid == 4) out.print("社長承認");
                            else if (sid == 5) out.print("完了");
                            else if (sid == 6) out.print("却下");
                            else if (sid == 7) out.print("削除");
                            else out.print("不明(" + sid + ")");
                        %>
                    </td>
                    <% if (showActionColumn) { %>
                        <td>
                            <% if (canEditOrDelete) { %>
                                <form action="ApplicationEdit" method="post" style="display:inline;">
                                    <input type="hidden" name="apct_id" value="<%= app.getApctId() %>">
                                    <input type="submit" value="修正" class="btn btn-edit">
                                </form>
                            <% } %>
                            
                            <% if (canEditOrDelete || isManagementDelete) { %>
                                <form action="ApplicationDelete" method="post" style="display:inline;" onsubmit="return confirmDelete();">
                                    <input type="hidden" name="apct_id" value="<%= app.getApctId() %>">
                                    <input type="submit" value="削除" class="btn btn-delete">
                                </form>
                            <% } %>
                        </td>
                    <% } %>
                </tr>
            <% 
                    } 
                } else { 
            %>
                <tr>
                    <td colspan="<%= showActionColumn ? 9 : 8 %>" style="text-align:center;">該当する申請履歴がありません。</td>
                </tr>
            <% } %>
        </tbody>
    </table>

    <%
        // 各サーブレットからエラーメッセージ（errorMessage）が転送されてきた場合、その場でブラウザのアラートで表示する
        String errorMsg = (String) request.getAttribute("errorMessage");
        if (errorMsg != null && !errorMsg.isEmpty()) {
    %>
        <script type="text/javascript">
            alert("<%= errorMsg %>");
        </script>
    <%
        }
    %>
</body>
</html>