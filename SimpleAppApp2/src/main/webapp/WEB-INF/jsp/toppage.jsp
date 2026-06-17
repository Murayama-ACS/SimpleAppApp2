<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ page import="bean.EmployeeBean"%>
<%@ page import="java.time.format.DateTimeFormatter"%>
<%
    EmployeeBean employee = (EmployeeBean) session.getAttribute("loginEmployee");
    String empName = (employee != null) ? employee.getEmp_name() : "ゲスト";
    String empDpt = (employee != null) ? employee.getDpt_id() : "未所属";
    String empPos = (employee != null) ? employee.getPos_id() : "未設定";
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>トップページ</title>
<style>
body {
	font-family: Arial, sans-serif;
	margin: 20px;
	background-color: #f5f7fa;
}

.user-info {
	background-color: #e2e8f0;
	padding: 10px;
	border-radius: 4px;
	margin-bottom: 20px;
}

.menu-area, .notification-area {
	background-color: #fff;
	border: 1px solid #cbd5e0;
	padding: 15px;
	margin-bottom: 20px;
	border-radius: 4px;
	box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.menu-area ul {
	list-style-type: none;
	padding: 0;
}

.menu-area li {
	margin-bottom: 10px;
}

.menu-area a {
	text-decoration: none;
	color: #3182ce;
	font-weight: bold;
}

.menu-area a:hover {
	text-decoration: underline;
}

.notification-list {
	list-style-type: none;
	padding: 0;
}

.notification-list li {
	padding: 10px;
	border-bottom: 1px solid #e2e8f0;
}

.notification-list li:last-child {
	border-bottom: none;
}

.notification-link {
	text-decoration: none;
	color: #333;
	display: block;
}

.notification-link:hover {
	background-color: #f7fafc;
}

.unread {
	font-weight: bold;
	background-color: #ebf8ff;
}

.badge-new {
	color: #e53e3e;
	font-size: 0.85em;
	margin-right: 5px;
}

.date-text {
	font-size: 0.85em;
	color: #718096;
	margin-left: 10px;
}
.notification-btn-container {
        margin-bottom: 20px;
    }
    .notification-btn {
        display: inline-block;
        position: relative;
        padding: 10px 20px;
        background-color: #4a5568;
        color: white;
        text-decoration: none;
        font-weight: bold;
        border-radius: 4px;
    }
    .notification-btn:hover {
        background-color: #2d3748;
    }
    .badge {
        position: absolute;
        top: -8px;
        right: -8px;
        background-color: #e53e3e;
        color: white;
        border-radius: 50%;
        padding: 2px 7px;
        font-size: 12px;
        font-weight: bold;
        box-shadow: 0 1px 3px rgba(0,0,0,0.2);
    }
</style>
</head>
<body>

	<h2>トップページ</h2>
	
	<div class="user-info">
		ログインユーザー:
		<%= empName %>
		さん （部署:
		<%= empDpt %>
		/ 役職:
		<%= empPos %>）
		<div style="margin-top: 10px;">
			<a href="${pageContext.request.contextPath}/login_mock.jsp">別のアカウントでログインし直す</a>
		</div>
	</div>
	
	<div class="menu-area">
		<h3>メインメニュー</h3>
		<ul>
			<li><a href="${pageContext.request.contextPath}/Application">📝
					申請入力画面</a></li>
			<li><a
				href="${pageContext.request.contextPath}/ApplicationWaitList">✅
					未承認申請一覧（上長・管理部向け）</a></li>
			<li><a
				href="${pageContext.request.contextPath}/ApplicationStatus">💰
					申請ステータス変更一覧（経理部向け）</a></li>
		</ul>
	</div>

<div class="notification-btn-container">
    <a href="${pageContext.request.contextPath}/NotificationList" class="notification-btn">
        🔔 通知を確認する
        <%-- 未読件数が 0 より大きい場合のみバッジを表示 --%>
        <c:if test="${unreadCount > 0}">
            <span class="badge"><c:out value="${unreadCount}" /></span>
        </c:if>
    </a>
</div>

</body>
</html>