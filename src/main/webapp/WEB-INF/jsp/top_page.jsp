<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>AppApp TopPage</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/navbar.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/top_page.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
    <%@ include file="/WEB-INF/jsp/header.jsp" %>
    
    <div class="container">
        <h2 class="page-title">メインメニュー</h2>
        
        <div class="menu-grid">
            
            <%-- 申請作成 --%>
            <c:if test="${empBean.pos_id !='E04' }">
                <a href="ApplicationServlet" class="menu-card card-creation">
                    <div class="menu-icon"><i class="fa-solid fa-pen-to-square"></i></div>
                    <div class="menu-title">申請作成</div>
                    <div class="menu-desc">新しい申請書を作成・提出します。</div>
                </a>
            </c:if>
			
            <%-- 申請履歴 --%>
            <a href="ApplicationHistoryServlet" class="menu-card card-history">
                <div class="menu-icon"><i class="fa-solid fa-file-lines"></i></div>
                <div class="menu-title">申請履歴</div>
                <div class="menu-desc">過去の申請履歴を確認します。</div>
            </a>
            
            <%-- 未承認一覧 --%>
            <c:if test="${empBean.pos_id !='E00' }">
                <a href="ApplicationWaitList" class="menu-card card-approval">
                    <div class="menu-icon"><i class="fa-solid fa-list-check"></i></div>
                    <div class="menu-title">未承認一覧</div>
                    <div class="menu-desc">部下からの申請を確認、または承認します。</div>
                </a>
            </c:if>
            
            <%-- ユーザー情報一覧 --%>
            <c:if test="${fn:contains(empBean.dpt_id, 'D4')}">
                <a href="EmployeeInfo" class="menu-card card-hr">
                    <div class="menu-icon"><i class="fa-solid fa-address-card"></i></div>
                    <div class="menu-title">ユーザー情報一覧</div>
                    <div class="menu-desc">社員情報を確認・編集、または追加します。</div>
                </a>
            </c:if>
			
            <%-- 申請ステータス変更 --%>
            <c:if test="${empBean.dpt_id =='D200' }">
                <a href="ApplicationStatus" class="menu-card card-admin">
					<div class="menu-icon"><i class="fa-solid fa-file-invoice-dollar"></i></div>                    
					<div class="menu-title">申請ステータス変更</div>
                    <div class="menu-desc">経理部が申請ステータスを確認します。</div>
                </a>
            </c:if>

        </div>
    </div>
</body>
</html>