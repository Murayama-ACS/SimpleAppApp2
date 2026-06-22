<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- JSTLタグライブラリの宣言（c:条件分岐などの制御構文、fmt:日付や金額のフォーマット） --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>申請詳細（閲覧） - AppApp システム</title>
    <%-- 共通ナビゲーションと閲覧専用詳細画面のCSSを読み込み --%>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/navbar.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/app_detail.css"> 
</head>
<body>

    <%-- 通知の未読カウントなどの機能を持つ共通ヘッダーをインクルード --%>
    <%@ include file="/WEB-INF/jsp/header.jsp" %>

    <div class="detail-card">
        <h2>申請内容詳細</h2>

        <%-- 緊急フラグの判定：複数の表記揺れ（true, 1, 緊急）に対応して警告バッジを表示 --%>
        <c:if test="${application.urgent == 'true' || application.urgent == '1' || application.urgent == '緊急'}">
            <div class="badge-urgent-box">⚠️ 至急案件</div>
        </c:if>

        <%-- 申請者の基本情報表示エリア --%>
        <div class="applicant-info-box">
            <div class="info-item"><strong>申請者:</strong> ${application.employeeName} (${application.departmentName})</div>
            <div class="info-item"><strong>申請日時:</strong> 
                <%-- DBの文字列日時を解析(parse)し、スラッシュ区切りの見やすい形式にフォーマット(format) --%>
                <fmt:parseDate value="${application.createDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" type="both" />
                <fmt:formatDate value="${parsedDate}" pattern="yyyy/MM/dd HH:mm" />
            </div>
        </div>

        <%-- 申請内容の詳細データテーブル --%>
        <table class="detail-data-table">
            <tr><th>申請ID</th><td>${application.apctId}</td></tr>
            <tr><th>カテゴリ</th><td>${application.type}</td></tr>
            <tr><th>精算方法</th><td>${application.paymentMethod}</td></tr>
            <%-- 金額を「¥」マーク付き、小数点以下のない3桁区切りにフォーマット --%>
            <tr><th>金額</th><td><fmt:formatNumber value="${application.amount}" type="currency" currencySymbol="¥" pattern="¥#,##0"/></td></tr>
            <tr><th>申請内容</th><td>${application.content}</td></tr>
            <tr><th>申請理由</th><td>${application.reason}</td></tr>
            <tr>
                <th>申請状態</th>
                <td>
                    <%-- 状態名に応じて文字色を動的に変更（完了:緑, 却下:赤, その他:黄色） --%>
                    <span style="font-weight: bold; color: ${application.statusName == '完了' ? '#28a745' : (application.statusName == '却下' ? '#dc3545' : '#ffc107')};">
                        ${application.statusName}
                    </span>
                </td>
            </tr>
            <%-- 上司のコメントと備考：値が空(empty)の場合はデフォルトで「なし」と表示 --%>
            <tr><th>上司のコメント</th><td style="color: rgb(255, 128, 64);">${empty approval.comment ? 'なし' : approval.comment}</td></tr>
            <tr><th>備考</th><td>${empty application.note ? 'なし' : application.note}</td></tr>
        </table>
        <div class="dual-nav-container">
            <c:choose>
                <%-- URLパラメータに from=noti がある場合（通知センターから来た場合） --%>
                <c:when test="${param.from == 'noti'}">
                    <%-- Sessionを無視して、確実に通知センターへ戻る --%>
                    <a href="${pageContext.request.contextPath}/NotificationList" class="back-link">⬅ 通知センターに戻る</a>
                </c:when>
                
                <%-- それ以外（申請一欄から来た場合） --%>
                <c:otherwise>
                    <%-- 検索条件を維持するために、Sessionに保存されたURLへ戻る --%>
                    <a href="${not empty sessionScope.lastListUrl ? sessionScope.lastListUrl : pageContext.request.contextPath += '/ApplicationHistoryServlet'}" class="back-link">⬅ 申請履歴一覧に戻る</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    
</body>
</html>