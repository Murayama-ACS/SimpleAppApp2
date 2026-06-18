<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- JSTLコアタグ（c:条件分岐など）の宣言 --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>申請内容修正 - AppApp システム</title>
    <%-- 共通ナビゲーションCSSと修正画面専用CSS、およびSweetAlert2の読み込み --%>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/navbar.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/app_edit.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>
    <%-- 動的な未読通知カウントの計算ロジックを含む共通ヘッダーを読み込み --%>
    <%@ include file="/WEB-INF/jsp/header.jsp" %>

    <div class="container">
        <div class="application-card">
            
            <div class="card-header-edit">
                <h2>申請内容の修正</h2>
            </div>
            
            <div class="card-body">
                <%-- サーブレット側でバリデーションエラーなどが発生した場合にメッセージを表示 --%>
                <c:if test="${not empty errorMessage}">
                    <div class="error-display">⚠️ ${errorMessage}</div>
                </c:if>

                <%-- 修正対象となる申請の基本情報表示領域 --%>
                <div class="info-display-edit">
                    <strong>【 申請情報 】</strong><br>
                    対象申請ID: <strong>${application.apctId}</strong> &nbsp;|&nbsp; 
                    現在の状態: <span style="color:#d32f2f; font-weight:bold;">${application.statusName}</span>
                </div>

                <%-- 修正データを更新用サーブレット（ApplicationEdit）へPOST送信するフォーム --%>
                <form action="${pageContext.request.contextPath}/ApplicationEdit" method="post" id="editForm">
                    <%-- どのデータを更新するかを識別する申請IDと、送信フラグ（isSubmit=true）を隠しパラメータで伝送 --%>
                    <input type="hidden" name="apct_id" value="${application.apctId}">
                    <input type="hidden" name="isSubmit" value="true">

                    <%-- 申請種別：三項演算子を使用して、元々登録されていたカテゴリを初期選択（selected）状態にする --%>
                    <div class="form-group">
                        <label for="appType">申請種別<span class="required-mark">※必須</span></label>
                        <select id="appType" name="applicationType" required>
                            <option value="備品購入申請" ${application.type == '備品購入申請' ? 'selected' : ''}>備品購入申請</option>
                            <option value="研修参加申請" ${application.type == '研修参加申請' ? 'selected' : ''}>研修参加申請</option>
                            <option value="出張申請" ${application.type == '出張申請' ? 'selected' : ''}>出張申請</option>
                            <option value="業務委託・外注費申請" ${application.type == '業務委託・外注費申請' ? 'selected' : ''}>業務委託・外注費申請</option>
                            <option value="その他" ${application.type == 'その他' ? 'selected' : ''}>その他</option>
                        </select>
                    </div>
                    
                    <%-- 精算方法：元の精算方法を初期選択状態にする --%>
                    <div class="form-group">
                        <label for="paymentMethod">精算方法<span class="required-mark">※必須</span></label>
                        <select id="paymentMethod" name="paymentMethod" required>
                            <option value="立替払い（現金手渡し）" ${application.paymentMethod == '立替払い（現金手渡し）' ? 'selected' : ''}>立替払い（現金手渡し）</option>
                            <option value="立替払い (給与振込)" ${application.paymentMethod == '立替払い (給与振込)' ? 'selected' : ''}>立替払い (給与振込)</option>
                            <option value="会社直接支払い" ${application.paymentMethod == '会社直接支払い' ? 'selected' : ''}>会社直接支払い</option>
                        </select>
                    </div>
                    
                    <%-- 金額、内容、理由、備考：value属性やタグの間に元のデータを埋め込んで初期表示 --%>
                    <div class="form-group">
                        <label for="amount">申請金額 (円)<span class="required-mark">※必須</span></label>
                        <input type="number" id="amount" name="amount" min="0" max="99999999" value="${application.amount}" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="content">申請内容<span class="required-mark">※必須</span></label>
                        <textarea id="content" name="content" required>${application.content}</textarea>
                    </div>
                    
                    <div class="form-group">
                        <label for="reason">申請理由<span class="required-mark">※必須</span></label>
                        <textarea id="reason" name="reason" required>${application.reason}</textarea>
                    </div>
                    
                    <div class="form-group">
                        <label for="note">備考</label>
                        <textarea id="note" name="note">${application.note}</textarea>
                    </div>
                    
                    <%-- 緊急フラグ：表記（緊急、true、1）を考慮した判定を行い、該当すればチェック（checked）状態にする --%>
                    <div class="checkbox-group">
                        <input type="checkbox" id="urgent" name="urgentFlag" value="true" 
                            ${application.urgent == '緊急' || application.urgent == 'true' || application.urgent == '1' ? 'checked' : ''}>
                        <label for="urgent">⚠️ この申請は緊急を要する</label>
                    </div>
                    
                    <%-- ボタン誤操作による直接送信を防ぐため type="button" にし、JSの関数を呼び出す --%>
                    <button type="button" class="btn-edit-submit" onclick="confirmEdit()">修正内容を上書き保存する</button>
                </form>
                
                <a href="${pageContext.request.contextPath}/ApplicationHistoryServlet" class="back-link">⬅ キャンセルして履歴一覧に戻る</a>
            </div> 
        </div> 
    </div> 

    <%-- フロントエンドのJavaScriptバリデーション＆ポップアップ制御 --%>
    <script>
        function confirmEdit() {
            const form = document.getElementById('editForm');
            
            // 1. HTML5標準のバリデーションチェック（requiredの未入力など）を実行
            if (!form.checkValidity()) {
                // 未入力エラーがある場合はブラウザ標準の警告ツールチップを表示して処理を中断
                form.reportValidity();
                return;
            }

            // 2. 入力内容に問題がない場合、SweetAlert2の確認ダイアログを表示
            Swal.fire({
                title: '修正を確認',
                text: 'この内容で申請を上書き保存します。よろしいですか？',
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#ffc107', // 警告・修正を表す黄色系のカラー
                cancelButtonColor: '#6c757d',
                confirmButtonText: '<span style="color:#333;font-weight:bold;">はい、保存します</span>',
                cancelButtonText: 'キャンセル'
            }).then((result) => {
                // ユーザーが「はい」を選択した場合の処理
                if (result.isConfirmed) {
                    // 通信中・処理中であることを明示し、ユーザーの連打による二重送信（多重登録防バグ）を防止
                    Swal.fire({
                        title: '保存中...',
                        text: 'しばらくお待ちください',
                        allowOutsideClick: false, // ポップアップ外クリックでのクローズを禁止
                        didOpen: () => { Swal.showLoading(); } // ローディングアニメーションを開始
                    });
                    
                    // フォームを実際に送信してサーバー側にデータを送る
                    form.submit();
                }
            });
        }
    </script>
</body>
</html>