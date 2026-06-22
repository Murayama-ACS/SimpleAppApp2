<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>通知センター - AppApp システム</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/navbar.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/notification_list.css">
    <style>
    /* 通知画面専用のカスタムスタイル */
        .unread-row { background-color: #fff8e1; font-weight: bold; }
        .read-row { background-color: #ffffff; color: #6c757d; }
        .notification-card { max-width: 1000px; margin: 40px auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
    </style>
</head>
<body>
	<%-- 動的な未読カウント計算を含む共通ヘッダーを読み込み --%>
    <%@ include file="/WEB-INF/jsp/header.jsp" %>

    <div class="container">
        <div class="notification-card">
            <h2 style="margin-bottom: 20px; color: #343a40;">通知センター</h2>
            
            <p style="color: #6c757d; margin-bottom: 20px;">※このページを開くと、すべての新しい通知が「既読」になります。</p>

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
                            <th style="text-align: center;">詳細</th>
                        </tr>
                    </thead>
                    <tbody>
                    	<%-- 通知データの有無で表示を分岐 --%>
                        <c:choose>
                        	<%-- パターンA：通知が1件もない場合 --%>
                            <c:when test="${empty notifications}">
                                <tr>
                                    <td colspan="7" style="text-align:center; padding: 40px;">現在、新しい通知はありません。</td>
                                </tr>
                            </c:when>
                            <%-- パターンB：通知データが存在する場合 --%>
                            <c:otherwise>
                                <c:forEach var="noti" items="${notifications}">
                                	<%-- 三項演算子で既読フラグ（noti.read）を判定し、適用するCSSクラスを動的に切り替え --%>
                                    <tr class="${noti.read ? 'read-row' : 'unread-row'}">
                                        <td style="white-space: nowrap;">${noti.timeStr}</td>
                                        <td style="color: #3f51b5;">${noti.apctId}</td>
                                        <td>${noti.content}</td>
                                        <td>
                                        	<%-- ステータスIDによってバッジの色を動的に変更 --%>
                                             <span class="status-badge ${noti.statusId == 6 ? 'status-rejected' : (noti.statusId == 1 || noti.statusId == 2 || noti.statusId == 3 || noti.statusId == 4 ? 'status-pending' : 'status-approved')}">
                                                    ${noti.statusName}
                                             </span>
                                        </td>
                                        <td>${noti.approverName} <span style="font-size:11px;">(${noti.approverPosName})</span></td>
                                        <%-- コメントが長すぎる場合は省略記号（...）で表示。空の場合は「なし」と表示 --%>
                                        <td style="max-width: 250px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
                                            ${not empty noti.comment ? noti.comment : '<span style="color:#adb5bd;">なし</span>'}
                                        </td>
                                        <%-- 該当の申請ID（apct_id）をURLパラメータとして渡し、詳細画面へリンク --%>
                                        <td style="text-align: center;">
                                            <a href="${pageContext.request.contextPath}/ApplicationDetail?apct_id=${noti.apctId}&from=noti" class="btn-sm btn-detail" style="text-decoration: none;">確認</a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>

            <a href="${pageContext.request.contextPath}/TopPageServlet" class="back-link" style="display: block; margin-top: 20px;">⬅ メインメニューに戻る</a>
        </div>
    </div>
</body>
</html>