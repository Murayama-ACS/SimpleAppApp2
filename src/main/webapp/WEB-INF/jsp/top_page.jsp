<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- JSTLタグライブラリの宣言（c:条件分岐、fmt:フォーマット、fn:文字列操作） --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>AppApp TopPage</title>
    <%-- 共通ナビゲーションCSSとトップページ専用（グリッドレイアウト等）のCSSを読み込み --%>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/navbar.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/top_page.css">
</head>
<body>
    <%-- 通知の未読カウントなどの機能を含む共通ヘッダーをインクルード --%>
    <%@ include file="/WEB-INF/jsp/header.jsp" %>
    
    <div class="container">
        <h2 class="page-title">メインメニュー</h2>
        
        <%-- メニューカードを並べるグリッドコンテナ --%>
        <div class="menu-grid">
            
            <%-- 申請作成メニュー --%>
            <%-- 社長(E04)は自ら申請を作成しないため、E04以外の全ユーザーに表示 --%>
            <c:if test="${empBean.pos_id !='E04' }">
                <a href="ApplicationServlet" class="menu-card card-creation">
                    <div class="menu-icon">📝</div>
                    <div class="menu-title">申請作成</div>
                    <div class="menu-desc">新しい申請書を作成・提出します。</div>
                </a>
            </c:if>

            <%-- 申請履歴メニュー --%>
            <%-- 自身の履歴確認や全社状況の確認に使うため、権限に関わらず全員に表示 --%>
            <a href="ApplicationHistoryServlet" class="menu-card card-history">
                <div class="menu-icon">📜</div>
                <div class="menu-title">申請履歴</div>
                <div class="menu-desc">過去の申請履歴を確認します。</div>
            </a>
            
            <%-- 未承認一覧（承認）メニュー --%>
            <%-- 一般社員(E00)は部下を持たず承認権限がないため、役職者(E00以外)にのみ表示 --%>
            <c:if test="${empBean.pos_id !='E00' }">
                <a href="ApplicationWaitList" class="menu-card card-approval">
                    <div class="menu-icon">🔍</div>
                    <div class="menu-title">未承認一覧</div>
                    <div class="menu-desc">部下からの申請を確認、または承認します。</div>
                </a>
            </c:if>
            
            <%-- ユーザー情報（社員管理）メニュー --%>
            <%-- 部署IDに 'D4' (人事・総務系) が含まれているユーザーにのみ表示 --%>
            <c:if test="${fn:contains(empBean.dpt_id, 'D4')}">
                <a href="EmployeeInfo" class="menu-card card-hr">
                    <div class="menu-icon">🪪</div>
                    <div class="menu-title">ユーザー情報一覧</div>
                    <div class="menu-desc">ユーザー情報を確認・編集、または追加します。</div>
                </a>
            </c:if>

            <%-- 申請ステータス変更（経理処理）メニュー --%>
            <%-- 部署IDが 'D200' (経理部) に完全一致するユーザーにのみ表示 --%>
            <c:if test="${empBean.dpt_id =='D200' }">
                <a href="ApplicationStatus" class="menu-card card-admin">
                    <div class="menu-icon">🔏</div>
                    <div class="menu-title">申請ステータス変更</div>
                    <div class="menu-desc">管理者が申請ステータスを確認します。</div>
                </a>
            </c:if>

        </div>
    </div>
</body>
</html>