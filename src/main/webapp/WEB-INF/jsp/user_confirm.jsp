<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- JSTLコアタグの宣言（c:条件分岐など） --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>登録内容確認 - AppApp システム</title>
    <%-- 確認画面専用CSS、共通ナビゲーションCSS、およびポップアップ用ライブラリ（SweetAlert2）の読み込み --%>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/user_confirm.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/navbar.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script> 
    
</head>
<body>
    <%-- 通知の未読カウントなどの機能を含む共通ヘッダーをインクルード --%>
    <%@ include file="/WEB-INF/jsp/header.jsp" %>

    <div class="container"> 
        <div class="form-container">
            
            <h2 style="margin-top: 0; margin-bottom: 20px; border-bottom: 2px solid #00796b; padding-bottom: 12px; color: #004d40;">
                登録内容の確認
            </h2>
            
            <p style="color: #546e7a; margin-bottom: 25px; font-weight: bold;">
                以下の内容で新しい社員情報をデータベースに登録します。<br>内容に間違いがないかご確認ください。
            </p>
            
            <%-- 入力内容の確認テーブル --%>
            <%-- 前の画面(EmployeeAddサーブレット)でセッションに保存された insertEmpBean の内容を表示します --%>
            <table class="confirm-table">
                <tr>
                    <th>社員ID</th>
                    <td style="color: #00796b;">${insertEmpBean.emp_id}</td>
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
                    <th>部署ID</th>
                    <td>${insertEmpBean.dpt_id}</td>
                </tr>
                <tr>
                    <th>役職ID</th>
                    <td>${insertEmpBean.pos_id}</td>
                </tr>
            </table>

            <%--  確定・登録フォーム --%>
            <%-- データ自体はすでにサーバー側(セッション)にあるため、ここでは「登録処理を実行せよ」という合図(action=insert)だけをPOST送信します --%>
            <form action="${pageContext.request.contextPath}/EmployeeUpdate" method="post" id="confirmForm">
                <input type="hidden" name="action" value="insert">
                <button type="submit" class="btn-update">この内容で確定・登録する</button>
            </form>
            
            <%--  戻るボタン --%>
            <div class="btn-back-layout">
                <%-- JavaScriptの history.back() を使用してブラウザの履歴を1つ戻る。
                     これにより、前の入力フォームに戻った際に、ユーザーが入力したテキストが消えずに残った状態になります --%>
                <button type="button" class="btn-back-action" onclick="history.back()">入力画面に戻って修正</button>
            </div>
            
        </div> 
    </div>

    <%-- フロントエンドのJavaScript処理 --%>
    <script>
        // 「確定・登録する」ボタンが押され、フォームが送信される瞬間の処理をフック
        document.getElementById('confirmForm').addEventListener('submit', function() {
            // データベースへの登録処理（通信）中に、ユーザーがボタンを連打して
            // 同じデータが2重に登録されてしまうバグ（二重送信）を防ぐため、ローディングポップアップを表示します。
            Swal.fire({
                title: '登録処理中...',
                text: 'データベースに保存しています',
                allowOutsideClick: false, // ポップアップの外側をクリックしても閉じられないように固定
                didOpen: () => { Swal.showLoading(); } // くるくる回るローディングアニメーションを開始
            });
        });
    </script>
</body>
</html>