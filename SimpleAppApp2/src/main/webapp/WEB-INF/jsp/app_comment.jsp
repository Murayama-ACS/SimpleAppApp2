<%--
モックのため必ず削除すること 
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="bean.ApplicationBean" %>
<%
    ApplicationBean app = (ApplicationBean) request.getAttribute("application");
    int currentStatusId = (app != null) ? app.getStatus_id() : 1;
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>申請詳細・コメント モック</title>
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
    <h2>申請詳細情報</h2>

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
                <td><%= app.getStatusName() %></td> <%-- 数値から名称表示へ修正 --%>
            </tr>
            <tr>
                <th style="background-color: #f2f2f2;">作成日時 (create_date)</th>
                <td><%= app.getCreateDate() %></td>
            </tr>
        </table>

        <h3>【承認・コメントの入力】</h3>
        <table border="1" cellpadding="5" cellspacing="0">
            <tr>
                <th style="background-color: #f2f2f2; width: 150px;">コメント (comment)</th>
                <td>
                    <textarea id="commentInput" rows="4" style="width: 400px;" required></textarea>
                </td>
            </tr>
        </table>
        <br>
        
        <div style="display: flex; gap: 10px;">
            <button type="button" onclick="openConfirmModal(<%= currentStatusId + 1 %>, '承認')">承認する</button>
            <button type="button" onclick="submitReject(<%= currentStatusId %>)">却下する</button>
        </div>

    <% } else { %>
        <p style="color: red;">申請データが正常に読み込めませんでした。</p>
    <% } %>

    <div id="confirmModal" class="modal">
        <div class="modal-content">
            <h3 id="modalTitle">処理確認</h3>
            <p>この内容でよろしいですか？</p>
            
            <form action="<%= request.getContextPath() %>/ApplicationComment" method="post" id="commentForm">
                <input type="hidden" name="apct_id" value="<%= app != null ? app.getApctId() : "" %>">
                <input type="hidden" name="next_status_id" id="commentNextStatus">
                <input type="hidden" name="comment" id="modalCommentHidden">
                
                <div style="display: flex; justify-content: space-between;">
                    <button type="button" onclick="closeModal()">戻る</button>
                    <button type="submit" id="modalSubmitBtn">確定</button>
                </div>
            </form>
        </div>
    </div>

    <br>
    <form action="<%= request.getContextPath() %>/ApplicationWaitList" method="get">
        <input type="hidden" name="pendingStatus" value="<%= currentStatusId %>">
        <button type="submit">一覧画面へ戻る</button>
    </form>

    <script>
        var modal = document.getElementById("confirmModal");
        var commentInput = document.getElementById("commentInput");
        var commentNextStatus = document.getElementById("commentNextStatus");
        var modalCommentHidden = document.getElementById("modalCommentHidden");
        var modalTitle = document.getElementById("modalTitle");
        var modalSubmitBtn = document.getElementById("modalSubmitBtn");

        function openConfirmModal(nextStatusId, actionName) {
            if (!commentInput.value.trim()) {
                alert("コメントを入力してください。");
                return;
            }
            
            commentNextStatus.value = nextStatusId;
            modalCommentHidden.value = commentInput.value;
            
            modalTitle.innerText = "申請" + actionName + "確認";
            modalSubmitBtn.innerText = actionName + "する";
            modal.style.display = "block";
        }

        function submitReject(currentStatusId) {
            openConfirmModal(5, '却下');
        }

        function closeModal() {
            modal.style.display = "none";
        }
    </script>
</body>
</html>