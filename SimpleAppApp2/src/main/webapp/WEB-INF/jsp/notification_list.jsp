<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>通知一覧</title>
<style>
body {
	font-family: Arial, sans-serif;
	margin: 20px;
	background-color: #f5f7fa;
}

.container {
	max-width: 800px;
	margin: 0 auto;
	background-color: #fff;
	border: 1px solid #cbd5e0;
	padding: 20px;
	border-radius: 4px;
	box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.back-link {
	margin-bottom: 20px;
}

.back-link a {
	color: #3182ce;
	text-decoration: none;
	font-weight: bold;
}

.notification-list {
	list-style-type: none;
	padding: 0;
	margin: 0;
}

.notification-item {
	padding: 15px;
	border-bottom: 1px solid #e2e8f0;
	color: #333; /* 文字色を固定 */
}

.notification-item:last-child {
	border-bottom: none;
}
/* 今回初めて開いた未読の通知アイテムのスタイル */
.unread {
	font-weight: bold;
	background-color: #ebf8ff;
	border-left: 4px solid #3182ce;
}

.badge-new {
	color: #e53e3e;
	font-size: 0.85em;
	margin-right: 5px;
}

.date-text {
	font-size: 0.85em;
	color: #718096;
	margin-top: 5px;
	display: block;
}

.comment-box {
	background-color: #f7fafc;
	padding: 8px 12px;
	margin-top: 8px;
	border-radius: 4px;
	font-size: 0.9em;
	color: #4a5568;
	font-weight: normal;
}
</style>
</head>
<body>

	<div class="container">
		<div class="back-link">
			<a href="${pageContext.request.contextPath}/TopPage">← トップページへ戻る</a>
		</div>

		<h2>🔔 通知一覧</h2>
		<p style="color: #718096; font-size: 0.9em;">※この画面を開いたことで、表示されたすべての新着通知は既読扱いになりました。</p>
		<hr>

<ul class="notification-list">
    <c:choose>
        <c:when test="${not empty notifications}">
            <c:forEach var="notif" items="${notifications}">
                <li class="notification-item ${notif.read ? '' : 'unread'}">
                    
                    <c:if test="${!notif.read}">
                        <span class="badge-new">[新着]</span>
                    </c:if>
                    
                    <c:choose>
                        <%-- 1. 経理完了(status_id: 5)の判定 --%>
                        <c:when test="${notif.statusId == 5}">
                            申請「<c:out value="${notif.content}" />」の精算処理が完了しました。
                        </c:when>

                        <%-- 2. 【新規追加】却下(status_id: 6)の判定 --%>
                        <c:when test="${notif.statusId == 6}">
                            組織の役職：<c:out value="${notif.approverPosName}" />（<c:out value="${notif.approverName}" />）によって、申請「<c:out value="${notif.content}" />」が<span style="color: #e53e3e;">却下</span>されました。
                            <c:if test="${not empty notif.comment}">
                                <div class="comment-box" style="border-left: 3px solid #e53e3e;">
                                    <strong>却下理由:</strong> <c:out value="${notif.comment}" />
                                </div>
                            </c:if>
                        </c:when>
                        
                        <%-- 3. 管理部承認(status_id: 3)の判定 --%>
                        <c:when test="${notif.statusId == 3}">
                            管理部によって、申請「<c:out value="${notif.content}" />」が<c:out value="${notif.statusName}" />されました。
                            <c:if test="${not empty notif.comment}">
                                <div class="comment-box">
                                    コメント: <c:out value="${notif.comment}" />
                                </div>
                            </c:if>
                        </c:when>
                        
                        <%-- 4. それ以外の承認（課長・部長・社長など） --%>
                        <c:otherwise>
                            組織の役職：<c:out value="${notif.approverPosName}" />（<c:out value="${notif.approverName}" />）によって、申請「<c:out value="${notif.content}" />」が<c:out value="${notif.statusName}" />されました。
                            <c:if test="${not empty notif.comment}">
                                <div class="comment-box">
                                    コメント: <c:out value="${notif.comment}" />
                                </div>
                            </c:if>
                        </c:otherwise>
                    </c:choose>
                    
                    <span class="date-text">
                        日時: <c:out value="${notif.timeStr}" />
                    </span>

                </li>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <li class="notification-item" style="text-align: center; color: #718096;">通知はありません。</li>
        </c:otherwise>
    </c:choose>
</ul>
	</div>

</body>
</html>