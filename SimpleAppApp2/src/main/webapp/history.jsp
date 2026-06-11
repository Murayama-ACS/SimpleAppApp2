<%--
モックのため必ず削除すること 
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="bean.ApplicationBean" %>
<%@ page import="bean.EmployeeBean" %>
<%
    EmployeeBean employee = (EmployeeBean) session.getAttribute("loginEmployee");
    String empName = (employee != null) ? employee.getEmp_name() : "ゲスト";
    String empId = (employee != null) ? employee.getEmp_id() : "未ログイン";
    String posId = (employee != null) ? employee.getPos_id() : "";
    String dptId = (employee != null) ? employee.getDpt_id() : "";
    
    String dptName = (String) request.getAttribute("dpt_name");
    if (dptName == null || dptName.trim().isEmpty()) {
        dptName = "未所属";
    }

    List<ApplicationBean> historyList = (List<ApplicationBean>) request.getAttribute("appList");
    String currentScope = (String) request.getAttribute("currentScope");
    String currentStatusFilter = (String) request.getAttribute("currentStatusFilter");
    String errorMessage = (String) request.getAttribute("errorMessage");

    // 画面に「操作」列を表示するかどうかの判定（表示しているリスト内にstatus_idが1〜3のデータが1つでもある、または未完了フィルタ時）
    boolean showActionColumn = "incomplete".equals(currentStatusFilter);
    if (!showActionColumn && historyList != null) {
        for (ApplicationBean app : historyList) {
            if (app.getStatus_id() >= 1 && app.getStatus_id() <= 3) {
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
    <title>申請履歴一覧 モック</title>
    <style>
        .tab-group { margin-bottom: 10px; }
        .tab { padding: 5px 10px; border: 1px solid #ccc; background: #e0e0e0; text-decoration: none; color: #000; }
        .active { background: #007bff; color: white; font-weight: bold; }
    </style>
</head>
<body>
    <h2>申請履歴一覧画面（モック）</h2>
    
    <div style="background-color: #f0f0f0; padding: 10px; margin-bottom: 20px; border: 1px solid #ccc;">
        <strong>申請者情報:</strong><br>
        社員ID: <%= empId %> | 氏名: <%= empName %> | 所属部署: <%= dptName %>
    </div>

    <% if (errorMessage != null) { %>
        <p style="color: red;"><%= errorMessage %></p>
    <% } %>

    <form action="<%= request.getContextPath() %>/ApplicationHistoryServlet" method="get" style="background:#f9f9f9; padding:10px; border:1px solid #ccc;">
        
        <% if (!"E00".equals(posId)) { %>
            <div class="tab-group">
                <label>【対象範囲】</label>
                <a href="ApplicationHistoryServlet?scope=self&filter=<%= "incomplete".equals(currentStatusFilter) ? "unapproved" : "all" %>" class="tab <%= "self".equals(currentScope) ? "active" : "" %>">自身</a>
                <a href="ApplicationHistoryServlet?scope=subordinate&filter=<%= "incomplete".equals(currentStatusFilter) ? "unapproved" : "all" %>" class="tab <%= "subordinate".equals(currentScope) ? "active" : "" %>">配下</a>
                <% if ("D100".equals(dptId)) { %>
                    <a href="ApplicationHistoryServlet?scope=management&filter=<%= "incomplete".equals(currentStatusFilter) ? "unapproved" : "all" %>" class="tab <%= "management".equals(currentScope) ? "active" : "" %>">管理</a>
                <% } %>
            </div>
        <% } %>
        
        <div class="tab-group">
            <label>【状態切替】</label>
            <a href="ApplicationHistoryServlet?scope=<%= currentScope %>&filter=unapproved" class="tab <%= "incomplete".equals(currentStatusFilter) ? "active" : "" %>">未完了</a>
            <a href="ApplicationHistoryServlet?scope=<%= currentScope %>&filter=all" class="tab <%= "all".equals(currentStatusFilter) ? "active" : "" %>">全て表示</a>
        </div>
    </form>
    <br>

    <table border="1" cellpadding="5" cellspacing="0" style="width:100%; text-align: left;">
        <thead>
            <tr style="background-color: #f2f2f2;">
                <th>申請ID</th>
                <th>申請者</th>
                <th>申請種別</th>
                <th>申請金額</th>
                <th>精算方法</th>
                <th>緊急度</th>
                <th>申請状態</th>
                <th>申請時間</th>
                <th>更新時間</th>
                <% if (showActionColumn) { %>
                    <th>操作</th>
                <% } %>
            </tr>
        </thead>
        <tbody>
            <% if (historyList != null && !historyList.isEmpty()) { %>
                <% for (ApplicationBean app : historyList) { %>
                    <tr>
                        <td><%= app.getApctId() %></td>
                        <td><%= app.getEmployeeName() %></td>
                        <td><%= app.getType() %></td>
                        <td>￥<%= app.getAmount() %></td>
                        <td><%= app.getPaymentMethod() %></td>
                        <td><%= app.getUrgent() %></td>
                        <td><%= app.getStatusName() %></td>
                        <td><%= app.getCreateDate() %></td>
                        <td><%= app.getUpdateDate() %></td>
                        
                        <% if (showActionColumn) { %>
                            <td>
                                <div style="display: flex; gap: 5px;">
                                    <form action="<%= request.getContextPath() %>/ApplicationComment" method="post" style="margin:0;">
                                        <input type="hidden" name="apct_id" value="<%= app.getApctId() %>">
                                        <button type="submit">詳細</button>
                                    </form>

                                    <% 
                                        int sid = app.getStatus_id();
                                        boolean isOwnApplication = empId.equals(app.getEmployeeId());
                                        
                                        // 通常の修正・削除判定（未承認(1) かつ 自身の申請であること）
                                        boolean canEditOrDelete = (sid == 1 && isOwnApplication);
                                        
                                        // 管理部が「管理」スコープで全社員を表示している時の特別ルール（完了(4)であれば削除可能）
                                        boolean isManagementDelete = ("management".equals(currentScope) && "D100".equals(dptId) && sid == 4);
                                    %>

                                    <% if (canEditOrDelete) { %>
                                        <form action="<%= request.getContextPath() %>/ApplicationEdit" method="post" style="margin:0;">
                                            <input type="hidden" name="apct_id" value="<%= app.getApctId() %>">
                                            <button type="submit">修正</button>
                                        </form>
                                    <% } %>

                                    <% if (canEditOrDelete || isManagementDelete) { %>
                                        <form action="<%= request.getContextPath() %>/ApplicationDelete" method="post" style="margin:0;" onsubmit="return confirm('本当にこの申請を削除しますか？');">
                                            <input type="hidden" name="apct_id" value="<%= app.getApctId() %>">
                                            <button type="submit">削除</button>
                                        </form>
                                    <% } %>
                                </div>
                            </td>
                        <% } %>
                    </tr>
                <% } %>
            <% } else { %>
                <tr>
                    <td colspan="<%= showActionColumn ? 10 : 9 %>" style="text-align: center;">該当する申請履歴がありません。</td>
                </tr>
            <% } %>
        </tbody>
    </table>
    
    <br>
    <a href="<%= request.getContextPath() %>/login_mock.jsp">ログイン模擬画面へ戻る</a>
</body>
</html>