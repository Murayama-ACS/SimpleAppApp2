<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>登録内容確認 - AppApp システム</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/navbar.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/user_confirm.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script> 
</head>
<body>
    <%@ include file="/WEB-INF/jsp/header.jsp" %>

    <div class="container"> 
        <div class="form-container">
            
            <h2 class="confirm-title">登録内容の確認</h2>
            
            <p class="confirm-desc">
                以下の内容で新しい社員情報をデータベースに登録します。<br>内容に間違いがないかご確認ください。
            </p>
            
            <%-- 入力内容の確認テーブル --%>
            <table class="confirm-table">
                <tr>
                    <th>社員ID</th>
                    <td class="emp-id-text">${insertEmpBean.emp_id}</td>
                </tr>
                <tr>
                    <th>氏名</th>
                    <td>${insertEmpBean.emp_name}</td>
                </tr>
                <tr>
                    <th>ふりがな</th>
                    <td>${insertEmpBean.furigana}</td>
                </tr>
                <tr>
                    <th>メールアドレス</th>
                    <td>${insertEmpBean.email}</td>
                </tr>
                <tr>
                    <th>部署名</th>
                    <td>
                        <c:forEach var="dpt" items="${dptList}">
                            <c:if test="${dpt.dpt_id == insertEmpBean.dpt_id}">
                                ${dpt.dpt_name}
                            </c:if>
                        </c:forEach>
                    </td>
                </tr>
                <tr>
                    <th>役職名</th>
                    <td>
                        <c:forEach var="pos" items="${posList}">
                            <c:if test="${pos.pos_id == insertEmpBean.pos_id}">
                                ${pos.pos_name}
                            </c:if>
                        </c:forEach>
                    </td>
                </tr>
            </table>

            <%-- 確定・登録フォーム --%>
            <form action="${pageContext.request.contextPath}/EmployeeUpdate" method="post" id="confirmForm">
                <input type="hidden" name="action" value="insert">
                <button type="submit" class="btn-update">この内容で確定・登録する</button>
            </form>
            
            <%-- 戻るボタン --%>
            <div class="btn-back-layout">
                <button type="button" class="btn-back-action" onclick="history.back()">入力画面に戻って修正</button>
            </div>
            
        </div> 
    </div>

    <script>
        document.getElementById('confirmForm').addEventListener('submit', function() {
            Swal.fire({
                title: '登録処理中...',
                text: 'データベースに保存しています',
                allowOutsideClick: false,
                didOpen: () => { Swal.showLoading(); }
            });
        });
    </script>
</body>
</html>