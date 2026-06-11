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

    List<ApplicationBean> list = (List<ApplicationBean>) request.getAttribute("applications");
    String currentStatus = (String) request.getAttribute("currentStatus");
    String errorMessage = (String) request.getAttribute("errorMessage");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>申請一覧（未承認） モック</title>
    <style>
        .modal {
            display: none;
            position: fixed;
            z-index: 100;
            left: 0; top: 0; width: 100%; height: 100%;
            background-color: rgba(0,0,0,0.4);
        }
        .modal-content {
            background-color: #fff;
            margin: 15% auto; padding: 20px;
            border: 1px solid #888; width: 400px;
        }
    </style>
</head>
<body>
    <h2>申請一覧画面（ステータスID: <%= currentStatus != null ? currentStatus : "1" %>）</h2>
    <p>ログインユーザー: <%= empName %> さん</p>

    <% if (errorMessage != null) { %>
        <p style="color: red;"><%= errorMessage %></p>
    <% } %>

    <form action="<%= request.getContextPath() %>/ApplicationWaitList" method="get">
        <label>表示ステータスID: </label>
        <input type="text" name="pendingStatus" value="<%= currentStatus != null ? currentStatus : "1" %>" size="5">
        <button type="submit">切り替え</button>
    </form>
    <br>

    <table border="1" cellpadding="5" cellspacing="0">
        <thead>
            <tr style="background-color: #f2f2f2;">
                <th>申請日</th>
                <th>申請ID</th>
                <th>申請者の部署</th>
                <th>申請者の氏名</th>
                <th>申請種別</th>
                <th>申請金額</th>
                <th>申請内容</th>
                <th>緊急度</th>
                <th>操作</th>
            </tr>
        </thead>
        <tbody>
            <% if (list != null && !list.isEmpty()) { %>
                <% for (ApplicationBean app : list) { %>
                    <tr>
                        <td><%= app.getCreateDate() %></td>
                        <td><%= app.getApctId() %></td>
                        <td><%= app.getDepartmentName() %></td>
                        <td><%= app.getEmployeeName() %></td>
                        <td><%= app.getType() %></td>
                        <td><%= app.getAmount() %> 円</td>
                        <td><%= app.getContent() %></td>
                        <td><%= app.getUrgent() %></td>
                        <td>
                            <div style="display: flex; gap: 5px;">
                                <form action="<%= request.getContextPath() %>/ApplicationComment" method="post" style="margin:0;">
                                    <input type="hidden" name="apct_id" value="<%= app.getApctId() %>">
                                    <button type="submit">詳細</button>
                                </form>
                                
                                <button type="button" onclick="openApprovalModal('<%= app.getApctId() %>', <%= app.getStatus_id() %>)">承認</button>
                                
                                <button type="button" onclick="openRejectModal('<%= app.getApctId() %>')">却下</button>
                            </div>
                        </td>
                    </tr>
                <% } %>
            <% } else { %>
                <tr>
                    <td colspan="9" style="text-align: center;">対象の申請データが存在しません。</td>
                </tr>
            <% } %>
        </tbody>
    </table>
    
    <div id="actionModal" class="modal">
        <div class="modal-content">
            <h3 id="modalTitle">申請処理</h3>
            <form action="<%= request.getContextPath() %>/ApplicationWaitList" method="post">
                <input type="hidden" id="modalApctId" name="apct_id">
                <input type="hidden" id="modalNextStatus" name="next_status_id">
                <input type="hidden" name="pendingStatus" value="<%= currentStatus != null ? currentStatus : "1" %>">
                
                <p>コメントを入力してください：</p>
                <textarea name="comment" rows="4" style="width: 100%;" required></textarea>
                <br><br>
                
                <div style="display: flex; justify-content: space-between;">
                    <button type="button" onclick="closeModal()">戻る</button>
                    <button type="submit" id="modalSubmitBtn">確定</button>
                </div>
            </form>
        </div>
    </div>

    <br>
    <a href="<%= request.getContextPath() %>/login_mock.jsp">ログイン画面へ戻る</a>

    <script>
        var modal = document.getElementById("actionModal");
        var modalApctId = document.getElementById("modalApctId");
        var modalNextStatus = document.getElementById("modalNextStatus");
        var modalTitle = document.getElementById("modalTitle");
        var modalSubmitBtn = document.getElementById("modalSubmitBtn");

        function openApprovalModal(apctId, currentStatusId) {
            modalApctId.value = apctId;
            modalNextStatus.value = currentStatusId + 1;
            modalTitle.innerText = "申請承認確認";
            modalSubmitBtn.innerText = "承認する";
            modal.style.display = "block";
        }

        function openRejectModal(apctId) {
            modalApctId.value = apctId;
            modalNextStatus.value = 5;
            modalTitle.innerText = "申請却下確認";
            modalSubmitBtn.innerText = "却下する";
            modal.style.display = "block";
        }

        function closeModal() {
            modal.style.display = "none";
        }
    </script>
</body>
</html>