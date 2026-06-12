<%--
モックのため必ず削除すること 
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="bean.ApplicationBean"%>
<%@ page import="java.time.format.DateTimeFormatter"%>
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
	left: 0;
	top: 0;
	width: 100%;
	height: 100%;
	background-color: rgba(0, 0, 0, 0.4);
}

.modal-content {
	background-color: #fff;
	margin: 15% auto;
	padding: 20px;
	border: 1px solid #888;
	width: 400px;
}
</style>
</head>
<body>
	<h2>申請詳細情報</h2>

	<% if (app != null) { %>
	<h3>【申請内容】</h3>
	<table border="1" cellpadding="5" cellspacing="0">
		<tr>
			<th style="background-color: #f2f2f2; width: 150px;">申請日</th>
			<td>
				<% 
					if (app.getCreateDate() != null) {
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年M月d日 HH:mm:ss");
						out.print(app.getCreateDate().format(formatter));
					} else {
						out.print("-");
					}
				%>
			</td>
		</tr>
		<tr>
			<th style="background-color: #f2f2f2;">申請ID</th>
			<td><%= app.getApctId() %></td>
		</tr>
		<tr>
			<th style="background-color: #f2f2f2;">申請者の部署</th>
			<td><%= app.getDepartmentName() %></td>
		</tr>
		<tr>
			<th style="background-color: #f2f2f2;">申請者の氏名</th>
			<td><%= app.getEmployeeName() %></td>
		</tr>
		<tr>
			<th style="background-color: #f2f2f2;">申請種別</th>
			<td><%= app.getType() %></td>
		</tr>
		<tr>
			<th style="background-color: #f2f2f2;">申請金額</th>
			<td><%= app.getAmount() %> 円</td>
		</tr>
		<tr>
			<th style="background-color: #f2f2f2;">申請内容</th>
			<td><%= app.getContent() %></td>
		</tr>
		<tr>
			<th style="background-color: #f2f2f2;">申請理由</th>
			<td><%= app.getReason() %></td>
		</tr>
		<tr>
			<th style="background-color: #f2f2f2;">精算方法</th>
			<td><%= app.getPaymentMethod() %></td>
		</tr>
		<tr>
			<th style="background-color: #f2f2f2;">備考</th>
			<td><%= app.getNote() != null ? app.getNote() : "" %></td>
		</tr>
		<tr>
			<th style="background-color: #f2f2f2;">緊急度</th>
			<td><%= app.getUrgent() %></td>
		</tr>
		<tr>
			<th style="background-color: #f2f2f2;">申請状態</th>
			<td><%= app.getStatusName() %></td>
		</tr>
	</table>

	<h3>【承認・コメントの入力】</h3>
	<table border="1" cellpadding="5" cellspacing="0">
		<tr>
			<th style="background-color: #f2f2f2; width: 150px;">コメント
				(comment)</th>
			<td><textarea id="commentInput" rows="4" style="width: 400px;"
					required></textarea></td>
		</tr>
	</table>
	<br>

	<div style="display: flex; gap: 10px;">
		<%-- 社長直行ルート（現在のステータスが1）なら4（社長承認）を渡し、それ以外の通常ルートなら+1する --%>
		<button type="button"
			onclick="openConfirmModal(<%= currentStatusId == 1 ? 4 : currentStatusId + 1 %>, '承認')">承認する</button>
		<button type="button" onclick="submitReject()">却下する</button>
	</div>

	<% } else { %>
	<p style="color: red;">申請データが正常に読み込めませんでした。</p>
	<% } %>

	<%-- 処理確認用ポップアップモーダル --%>
	<%-- 【変更なし】画面上部での EmployeeBean や ApplicationBean の取得はそのまま --%>

	<div style="display: flex; gap: 10px;">
		<%-- ボタン送信時の数値を廃止し、操作名（'approve' や 'reject'）を渡します --%>
		<button type="button" onclick="openConfirmModal('approve', '承認')">承認する</button>
		<button type="button" onclick="submitReject()">却下する</button>
	</div>

	<div id="confirmModal" class="modal">
		<div class="modal-content">
			<h3 id="modalTitle">処理確認</h3>
			<p>この内容でよろしいですか？</p>

			<form action="<%= request.getContextPath() %>/ApplicationComment"
				method="post" id="commentForm"
				onsubmit="return handleFormSubmit(this)">
				<input type="hidden" name="apct_id"
					value="<%= app != null ? app.getApctId() : "" %>">

				<%-- 【変更】next_status_id を廃止し、操作タイプを送るパラメータに変更 --%>
				<input type="hidden" name="action_type" id="modalActionType">
				<input type="hidden" name="comment" id="modalCommentHidden">

				<div style="display: flex; justify-content: space-between;">
					<button type="button" onclick="closeModal()">戻る</button>
					<button type="submit" id="modalSubmitBtn">確定</button>
				</div>
			</form>
		</div>
	</div>

	<script>
    var modal = document.getElementById("confirmModal");
    var commentInput = document.getElementById("commentInput");
    var modalActionType = document.getElementById("modalActionType"); // 変更
    var modalCommentHidden = document.getElementById("modalCommentHidden");
    var modalTitle = document.getElementById("modalTitle");
    var modalSubmitBtn = document.getElementById("modalSubmitBtn");
    var isSubmitting = false;

    // 【変更】第1引数を actionType に変更
    function openConfirmModal(actionType, actionName) {
        if (!commentInput.value.trim()) {
            alert("コメントを入力してください。");
            return;
        }
        
        isSubmitting = false;
        if (modalSubmitBtn) {
            modalSubmitBtn.innerText = actionName + "する";
        }
        
        modalActionType.value = actionType; // 'approve' または 'reject' が入る
        modalCommentHidden.value = commentInput.value;
        
        modalTitle.innerText = "申請" + actionName + "確認";
        modal.style.display = "block";
    }

    function submitReject() {
        openConfirmModal('reject', '却下'); // 変更
    }

    function closeModal() {
        modal.style.display = "none";
    }

    function handleFormSubmit(form) {
        if (isSubmitting) return false;
        isSubmitting = true;
        var submitBtn = form.querySelector('button[type="submit"]');
        if (submitBtn) submitBtn.innerText = "処理中...";
        return true;
    }
</script>
</body>
</html>