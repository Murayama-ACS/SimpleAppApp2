<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ page import="java.util.List"%>
<%@ page import="bean.ApplicationBean"%>
<%@ page import="bean.EmployeeBean"%>
<%@ page import="java.time.format.DateTimeFormatter"%>
<%
    EmployeeBean employee = (session.getAttribute("loginEmployee") != null) ? (EmployeeBean) session.getAttribute("loginEmployee") : null;
    List<ApplicationBean> historyList = (List<ApplicationBean>) request.getAttribute("appList");
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
%>
<!DOCTYPE html>
<html>
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

.header-info {
	margin-bottom: 20px;
	padding: 10px;
	background-color: #e2e8f0;
	border-radius: 4px;
	font-size: 14px;
}

.search-box {
	background-color: #fff;
	border: 1px solid #cbd5e0;
	padding: 15px;
	margin-bottom: 15px;
	border-radius: 4px;
	box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

table {
	width: 100%;
	border-collapse: collapse;
	background-color: #fff;
	box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

th, td {
	border: 1px solid #e2e8f0;
	padding: 10px;
	text-align: left;
	font-size: 14px;
}

th {
	background-color: #edf2f7;
}

th a {
	text-decoration: none;
	color: #2d3748;
	font-weight: bold;
	display: block;
	width: 100%;
}

th a:hover {
	text-decoration: underline;
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

.btn-primary {
	background-color: #3182ce;
	color: #fff;
}

.btn-primary:hover {
	background-color: #2b6cb0;
}

.btn-default {
	background-color: #e2e8f0;
	color: #4a5568;
	border-color: #cbd5e0;
}

.btn-default:hover {
	background-color: #cbd5e0;
}

.badge-urgent {
	background-color: #fed7d7;
	color: #9b2c2c;
	padding: 2px 6px;
	border-radius: 4px;
	font-size: 12px;
	font-weight: bold;
}

.paging-group {
	margin-top: 15px;
	font-size: 14px;
}
</style>
<script type="text/javascript">
    function clearSearch() {
        var currentSort = "<c:out value='${sort}'/>";
        var currentDir = "<c:out value='${dir}'/>";
        location.href = "${pageContext.request.contextPath}/ApplicationStatus"
            + "?sort=" + currentSort
            + "&dir=" + currentDir
            + "&page=1";
    }
</script>
</head>
<body>

	<h2>申請ステータス変更一覧</h2>

	<div class="header-info">
		ログイン経理ユーザー: <%= (employee != null) ? employee.getEmp_name() : "" %> さん
	</div>

	<div class="search-box">
		<form action="${pageContext.request.contextPath}/ApplicationStatus" method="get">

			申請状況: <select name="q_status">
				<option value="">--指定なし--</option>
				<option value="3" <c:if test="${q_status eq '3'}">selected</c:if>>管理部承認</option>
				<option value="4" <c:if test="${q_status eq '4'}">selected</c:if>>社長承認</option>
			</select> &nbsp;&nbsp;名前: <input type="text" name="q_name"
				value="<c:out value='${q_name}'/>" style="width: 100px;" />

			&nbsp;&nbsp;種別: <select name="q_type">
				<option value="">--指定なし--</option>
				<option value="経費精算" <c:if test="${q_type eq '経費精算'}">selected</c:if>>経費精算</option>
				<option value="旅費精算" <c:if test="${q_type eq '旅費精算'}">selected</c:if>>旅費精算</option>
			</select> &nbsp;&nbsp;金額範囲: <input type="number" name="q_amount_min"
				value="<c:out value='${q_amount_min}'/>" style="width: 90px;" />
			円以上 〜 <input type="number" name="q_amount_max"
				value="<c:out value='${q_amount_max}'/>" style="width: 90px;" />
			円以下 &nbsp;&nbsp;

			緊急度: <select name="q_urgent">
				<option value="">すべて</option>
				<option value="通常" <c:if test="${q_urgent eq '通常'}">selected</c:if>>通常</option>
				<option value="緊急" <c:if test="${q_urgent eq '緊急'}">selected</c:if>>緊急</option>
			</select> &nbsp;&nbsp;

			<button type="submit" name="search" value="1" class="btn btn-primary" style="padding: 4px 12px;">検索</button>
			<button type="button" onclick="clearSearch()" class="btn btn-default" style="padding: 4px 12px;">クリア</button>

			<input type="hidden" name="sort" value="<c:out value='${sort}'/>" />
			<input type="hidden" name="dir" value="<c:out value='${dir}'/>" /> 
			<input type="hidden" name="page" value="<c:out value='${page}'/>" />
		</form>
	</div>

	<table>
		<thead>
			<tr>
				<th>申請ID</th>
				
				<c:url var="sortName" value="/ApplicationStatus">
					<c:param name="sort" value="name" />
					<c:param name="dir" value="${sort == 'name' && dir == 'asc' ? 'desc' : 'asc'}" />
					<c:param name="page" value="1" />
					<c:param name="q_status" value="${q_status}" />
					<c:param name="q_name" value="${q_name}" />
					<c:param name="q_type" value="${q_type}" />
					<c:param name="q_amount_min" value="${q_amount_min}" />
					<c:param name="q_amount_max" value="${q_amount_max}" />
					<c:param name="q_urgent" value="${q_urgent}" />
				</c:url>
				<th><a href="${sortName}">申請者氏名<c:if test="${sort == 'name'}"><c:out value="${dir == 'asc' ? ' ▲' : ' ▼'}" /></c:if></a></th>

				<c:url var="sortDpt" value="/ApplicationStatus">
					<c:param name="sort" value="dpt" />
					<c:param name="dir" value="${sort == 'dpt' && dir == 'asc' ? 'desc' : 'asc'}" />
					<c:param name="page" value="1" />
					<c:param name="q_status" value="${q_status}" />
					<c:param name="q_name" value="${q_name}" />
					<c:param name="q_type" value="${q_type}" />
					<c:param name="q_amount_min" value="${q_amount_min}" />
					<c:param name="q_amount_max" value="${q_amount_max}" />
					<c:param name="q_urgent" value="${q_urgent}" />
				</c:url>
				<th><a href="${sortDpt}">部署名<c:if test="${sort == 'dpt'}"><c:out value="${dir == 'asc' ? ' ▲' : ' ▼'}" /></c:if></a></th>

				<th>申請種別</th>
				
				<c:url var="sortAmount" value="/ApplicationStatus">
					<c:param name="sort" value="amount" />
					<c:param name="dir" value="${sort == 'amount' && dir == 'asc' ? 'desc' : 'asc'}" />
					<c:param name="page" value="1" />
					<c:param name="q_status" value="${q_status}" />
					<c:param name="q_name" value="${q_name}" />
					<c:param name="q_type" value="${q_type}" />
					<c:param name="q_amount_min" value="${q_amount_min}" />
					<c:param name="q_amount_max" value="${q_amount_max}" />
					<c:param name="q_urgent" value="${q_urgent}" />
				</c:url>
				<th><a href="${sortAmount}">金額<c:if test="${sort == 'amount'}"><c:out value="${dir == 'asc' ? ' ▲' : ' ▼'}" /></c:if></a></th>

				<c:url var="sortUrgent" value="/ApplicationStatus">
					<c:param name="sort" value="urgent" />
					<c:param name="dir" value="${sort == 'urgent' && dir == 'asc' ? 'desc' : 'asc'}" />
					<c:param name="page" value="1" />
					<c:param name="q_status" value="${q_status}" />
					<c:param name="q_name" value="${q_name}" />
					<c:param name="q_type" value="${q_type}" />
					<c:param name="q_amount_min" value="${q_amount_min}" />
					<c:param name="q_amount_max" value="${q_amount_max}" />
					<c:param name="q_urgent" value="${q_urgent}" />
				</c:url>
				<th><a href="${sortUrgent}">緊急度<c:if test="${sort == 'urgent'}"><c:out value="${dir == 'asc' ? ' ▲' : ' ▼'}" /></c:if></a></th>
				
				<c:url var="sortStatus" value="/ApplicationStatus">
					<c:param name="sort" value="status" />
					<c:param name="dir" value="${sort == 'status' && dir == 'asc' ? 'desc' : 'asc'}" />
					<c:param name="page" value="1" />
					<c:param name="q_status" value="${q_status}" />
					<c:param name="q_name" value="${q_name}" />
					<c:param name="q_type" value="${q_type}" />
					<c:param name="q_amount_min" value="${q_amount_min}" />
					<c:param name="q_amount_max" value="${q_amount_max}" />
					<c:param name="q_urgent" value="${q_urgent}" />
				</c:url>
				<th><a href="${sortStatus}">現在の状態<c:if test="${sort == 'status'}"><c:out value="${dir == 'asc' ? ' ▲' : ' ▼'}" /></c:if></a></th>

				<c:url var="sortDate" value="/ApplicationStatus">
					<c:param name="sort" value="date" />
					<c:param name="dir" value="${sort == 'date' && dir == 'asc' ? 'desc' : 'asc'}" />
					<c:param name="page" value="1" />
					<c:param name="q_status" value="${q_status}" />
					<c:param name="q_name" value="${q_name}" />
					<c:param name="q_type" value="${q_type}" />
					<c:param name="q_amount_min" value="${q_amount_min}" />
					<c:param name="q_amount_max" value="${q_amount_max}" />
					<c:param name="q_urgent" value="${q_urgent}" />
				</c:url>
				<th><a href="${sortDate}">申請日時<c:if test="${sort == 'date'}"><c:out value="${dir == 'asc' ? ' ▲' : ' ▼'}" /></c:if></a></th>

				<th>操作</th>
			</tr>
		</thead>
		<tbody>
			<% 
                if (historyList != null && !historyList.isEmpty()) { 
                    for (ApplicationBean app : historyList) {
                        int sid = app.getStatus_id();
            %>
			<tr>
				<td><%= app.getApctId() %></td>
				<td><%= app.getEmployeeName() != null ? app.getEmployeeName() : "" %></td>
				<td><%= app.getDepartmentName() != null ? app.getDepartmentName() : "未設定" %></td>
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
				<td><%= app.getCreateDate() != null ? app.getCreateDate().format(formatter) : "---" %></td>
				<td>
					<form action="ApplicationStatus" method="post" style="margin: 0;">
						<input type="hidden" name="apct_id" value="<%= app.getApctId() %>">
						<button type="submit" class="btn btn-primary" style="padding: 4px 8px;">ステータス変更</button>
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

	<div class="paging-group">
		<c:url var="urlPrev" value="ApplicationStatus">
			<c:param name="sort" value="${sort}" />
			<c:param name="dir" value="${dir}" />
			<c:param name="page" value="${page - 1}" />
			<c:param name="q_status" value="${q_status}" />
			<c:param name="q_name" value="${q_name}" />
			<c:param name="q_type" value="${q_type}" />
			<c:param name="q_amount_min" value="${q_amount_min}" />
			<c:param name="q_amount_max" value="${q_amount_max}" />
			<c:param name="q_urgent" value="${q_urgent}" />
		</c:url>
		<c:choose>
			<c:when test="${page > 1}">
				<a href="${urlPrev}">前へ</a>
			</c:when>
			<c:otherwise>前へ</c:otherwise>
		</c:choose>

		&nbsp;|&nbsp;

		<c:choose>
			<c:when test="${hasNext}">
				<c:url var="urlNext" value="ApplicationStatus">
					<c:param name="sort" value="${sort}" />
					<c:param name="dir" value="${dir}" />
					<c:param name="page" value="${page + 1}" />
					<c:param name="q_status" value="${q_status}" />
					<c:param name="q_name" value="${q_name}" />
					<c:param name="q_type" value="${q_type}" />
					<c:param name="q_amount_min" value="${q_amount_min}" />
					<c:param name="q_amount_max" value="${q_amount_max}" />
					<c:param name="q_urgent" value="${q_urgent}" />
				</c:url>
				<a href="${urlNext}">次へ</a>
			</c:when>
			<c:otherwise>次へ</c:otherwise>
		</c:choose>
	</div>

	<%
        String errorMsg = (String) request.getAttribute("errorMessage");
        if (errorMsg != null && !errorMsg.isEmpty()) {
    %>
	<script type="text/javascript">alert("<%= errorMsg %>");</script>
	<% } %>

	<%
        Boolean showPopup = (Boolean) request.getAttribute("showSuccessPopup");
        if (showPopup != null && showPopup) {
    %>
	<script type="text/javascript">alert("変更完了しました");</script>
	<% } %>
</body>
</html>