<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- JSTLタグライブラリの宣言（c:条件分岐、fn:文字列検索関数） --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%-- Javaのクラスをインポート（スクリプトレット内で使用するため） --%>
<%@ page import="bean.EmployeeBean, dao.ApprovalDAO" %>

<%-- 全画面共通のリアルタイム未読通知計算ロジック --%>
<%
    // セッションからログイン中のユーザー情報を取得
    EmployeeBean loginEmp = (EmployeeBean) session.getAttribute("empBean");
    
    // ログインしている場合のみ通知を計算する
    if (loginEmp != null) {
        ApprovalDAO headerDao = new ApprovalDAO();
        
        // DB側で直接「未読数」だけをカウントする
        int globalUnreadCount = headerDao.countUnreadNotifications(loginEmp.getEmp_id());
        
        // 計算した未読件数をリクエストスコープにセットし、下部のHTML（JSP）へ渡す
        request.setAttribute("globalUnreadCount", globalUnreadCount);
    }
%>

<nav class="navbar">
    <%-- システムロゴ・タイトル（クリックでTopPageへ戻る） --%>
    <div class="navbar-brand">
        <%-- インラインスタイルを排除し、専用のクラスを付与 --%>
        <a href="${pageContext.request.contextPath}/TopPageServlet" class="brand-link">
            <img src="${pageContext.request.contextPath}/images/logo2.png" alt="Logo" class="brand-logo">
            <span class="brand-name">AppApp システム</span>
        </a>    
    </div>
    
    <%-- メニューリンク領域（権限に応じた出し分け） --%>
    <div class="navbar-menu">
        <%-- 「申請作成」: 社長(E04)は申請を作成しないため非表示、それ以外の全員に表示 --%>
        <c:if test="${empBean.pos_id != 'E04'}">
            <a href="${pageContext.request.contextPath}/ApplicationServlet" class="nav-link">申請作成</a>
        </c:if>
        
        <%-- 「申請履歴」: 全ユーザーに表示（閲覧できる範囲はリンク先の画面内で制御） --%>
        <a href="${pageContext.request.contextPath}/ApplicationHistoryServlet" class="nav-link">申請履歴</a>
        
        <%-- 「未承認一覧」: 一般社員(E00)は部下を持たないため非表示、役職者(E01〜E04)にのみ表示 --%>
        <c:if test="${empBean.pos_id != 'E00'}">
            <a href="${pageContext.request.contextPath}/ApplicationWaitList" class="nav-link">未承認一覧</a>
        </c:if>
        
        <%-- 「社員管理」: 部署IDに 'D4' (人事部系) が含まれているユーザーにのみ表示 --%>
        <c:if test="${fn:contains(empBean.dpt_id, 'D4')}">
            <a href="${pageContext.request.contextPath}/EmployeeInfo" class="nav-link">社員管理</a>
        </c:if>

        <%-- 「経理処理」: 部署IDが 'D200' (経理部) に完全一致するユーザーにのみ表示 --%>
        <c:if test="${empBean.dpt_id == 'D200'}">
            <a href="${pageContext.request.contextPath}/ApplicationStatus" class="nav-link">経理処理</a>
        </c:if>
    </div>
    
    <%-- 通知ベルアイコン領域 --%>
    <div class="navbar-actions">
	    <a href="${pageContext.request.contextPath}/NotificationList" class="nav-bell-container">
	        <i class="fa-solid fa-bell"></i>	        
	        <%-- globalUnreadCount が1件以上ある場合のみ、赤いバッジを表示 --%>
	        <c:if test="${globalUnreadCount > 0}">
	            <span class="nav-badge">${globalUnreadCount}</span>
	        </c:if>
	    </a>
	</div>

    <%-- ログインユーザー情報とログアウトボタン --%>
    <div class="navbar-user">
        ようこそ、${not empty empBean.emp_name ? empBean.emp_name : 'ゲスト'} さん
        <a href="${pageContext.request.contextPath}/Logout" class="logout-btn">ログアウト</a>
    </div>
</nav>