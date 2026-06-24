<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>通知センター - AppApp システム</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/navbar.css">
    <%-- 刷新された専用CSSのみを読み込み（内部のstyleタグは廃止） --%>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/notification_list.css">
</head>
<body>
    <%-- 動的な未読カウント計算を含む共通ヘッダーを読み込み --%>
    <%@ include file="/WEB-INF/jsp/header.jsp" %>

    <div class="container">
        <div class="notification-card">
            <h2 class="notification-title">通知センター</h2>
            <div class="notification-desc">※このページを開くと、すべての新しい通知が「既読」になります。</div>

            <div class="table-scroll-container">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>日時</th>
                            <th>申請ID</th>
                            <th>申請内容</th>
                            <th>結果</th>
                            <th>処理者</th>
                            <th>コメント</th>
                            <th class="col-action">詳細</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%-- 通知データの有無で表示を分岐 --%>
                        <c:choose>
                            <c:when test="${empty notifications}">
                                <tr class="empty-row">
                                    <td colspan="7">現在、新しい通知はありません。</td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="noti" items="${notifications}">
                                    <%-- 既読フラグでCSSクラスを切り替え --%>
                                    <tr class="${noti.read ? 'read-row' : 'unread-row'}">
                                        
                                        <td class="col-time">${noti.timeStr}</td>
                                        
                                        <td class="col-id">${noti.apctId}</td>
                                        
                                        <td>${noti.content}</td>
                                        
                                        <td>
                                            <span class="status-badge ${noti.statusId == 6 ? 'status-rejected' : (noti.statusId == 1 || noti.statusId == 2 || noti.statusId == 3 || noti.statusId == 4 ? 'status-pending' : 'status-approved')}">
                                                ${noti.statusName}
                                            </span>
                                        </td>
                                        
                                        <td>
                                            ${noti.approverName} 
                                            <span class="col-approver-pos">(${noti.approverPosName})</span>
                                        </td>
                                        
                                        <td class="col-comment">
                                            <c:choose>
                                                <c:when test="${not empty noti.comment}">
                                                    ${noti.comment}
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="empty-comment">なし</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        
                                        <td class="col-action">
                                            <a href="${pageContext.request.contextPath}/ApplicationDetail?apct_id=${noti.apctId}&from=noti" class="btn-sm btn-detail">確認</a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>

            <a href="${pageContext.request.contextPath}/TopPageServlet" class="back-link">⬅ メインメニューに戻る</a>
        </div>
    </div>
</body>
</html>