<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- JSTLタグライブラリの宣言（c:条件分岐などの制御構文、fmt:金額フォーマット用） --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>経理処理詳細 - AppApp システム</title>
    <%-- ポップアップ用ライブラリ（SweetAlert2）と共通・専用CSSの読み込み --%>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script> 
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/navbar.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/app_status.css">
</head>
<body>

    <%-- 未読通知カウントなどの機能を含む共通ヘッダーを読み込み --%>
    <%@ include file="/WEB-INF/jsp/header.jsp" %>

    <div class="container">
        <div class="detail-card">
            <h2>経理処理（最終確認）</h2>

            <%-- 緊急フラグの判定：複数の表記（緊急、true、1）に対応し、該当する場合は警告バッジを表示 --%>
            <c:if test="${application.urgent == '緊急' || application.urgent == 'true' || application.urgent == '1'}">
                <div class="badge-urgent-box">⚠️ 至急対応案件</div>
            </c:if>

            <%-- 申請者の基本情報と現在のステータス表示 --%>
            <div class="info-box">
                <div class="info-item"><strong>申請者:</strong> ${application.employeeName} (${application.departmentName})</div>
                <div class="info-item mt-5"><strong>現在の状態:</strong> <span class="status-highlight">${application.statusName}</span></div>
            </div>

            <%-- 申請内容の詳細データテーブル --%>
            <table class="data-table">
                <tr><th>申請ID</th><td class="bold-text">${application.apctId}</td></tr>
                <tr><th>申請種別</th><td>${application.type}</td></tr>
                <tr><th>精算方法</th><td><strong>${application.paymentMethod}</strong></td></tr>
                <%-- 金額を「¥」マーク付きの3桁区切り（小数点なし）にフォーマットして強調表示 --%>
                <tr><th>申請金額</th><td class="amount-highlight"><fmt:formatNumber value="${application.amount}" type="currency" currencySymbol="¥" pattern="¥#,##0"/></td></tr>
                <tr><th>申請内容</th><td>${application.content}</td></tr>
                <tr><th>申請理由</th><td>${application.reason}</td></tr>
                <tr><th>備考</th><td>${empty application.note ? 'なし' : application.note}</td></tr>
            </table>

            <%-- 処理完了ボタンの表示制御 --%>
            <%-- ステータスが「5（経理完了）」ではない場合のみ、完了ボタンを表示する --%>
            <c:if test="${application.status_id != 5}">
                <div class="action-area">
                    <h3 class="action-title">承認済みデータの経理処理を完了しますか？</h3>
                    <%-- 経理処理を実行するサーブレットへのPOST送信フォーム --%>
                    <form id="completeForm" action="${pageContext.request.contextPath}/ApplicationStatusEdit" method="post" class="inline-form">
                        <input type="hidden" name="apct_id" value="${application.apctId}">
                        <%-- 誤操作防止のため type="button" にし、JSの確認ポップアップを経由させる --%>
                        <button type="button" class="btn-complete" onclick="confirmComplete()">経理処理を完了する</button>
                    </form>
                </div>
            </c:if>

            <div class="nav-links">
                <%-- 戻るボタン：一覧画面の検索条件（q_name等）を維持したURL（lastAccountingListUrl）がセッションにあればそこに戻る --%>
                <a href="${not empty sessionScope.lastAccountingListUrl ? sessionScope.lastAccountingListUrl : pageContext.request.contextPath += '/ApplicationStatus'}" class="btn-back">⬅ ステータス変更一覧に戻る</a>
            </div>
        </div>
    </div>

    <%-- フロントエンドのJavaScript処理 --%>
    <script>
        // 経理処理完了前の確認ポップアップ (SweetAlert2を使用)
        function confirmComplete() {
            Swal.fire({
                title: '経理処理を完了しますか？',
                text: "この操作により、申請ステータスが「完了」になり、申請者に通知されます。",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#28a745', // 完了・承認を表す緑色
                cancelButtonColor: '#6c757d',
                confirmButtonText: '完了する',
                cancelButtonText: 'キャンセル'
            }).then((result) => {
                // ユーザーが「完了する」をクリックした場合の処理
                if (result.isConfirmed) {
                    // 多重送信防止のためのローディングアニメーションを表示
                    Swal.fire({
                        title: '処理中...',
                        text: 'データベースを更新しています',
                        allowOutsideClick: false,
                        didOpen: () => { Swal.showLoading(); }
                    });
                    // フォームを送信
                    document.getElementById('completeForm').submit();
                }
            });
        }
    </script>

    <%-- サーバー側の処理完了後のサクセスポップアップ表示制御 --%>
    <%-- ApplicationStatusEditサーブレットが showSuccessPopup=true をセットして画面を返した場合に発動 --%>
    <c:if test="${showSuccessPopup}">
        <script>
            // 画面のHTMLがすべて読み込まれた直後に実行される
            document.addEventListener('DOMContentLoaded', function() {
                Swal.fire({
                    title: '処理完了',
                    text: '経理処理が正常に完了しました。',
                    icon: 'success',
                    confirmButtonColor: '#971717', // 経理部のテーマカラー（深紅）に変更
                    confirmButtonText: '一覧へ戻る',
                    allowOutsideClick: false // 外側をクリックして閉じられないようにする
                }).then(() => {
                    // 「一覧へ戻る」ボタンが押されたら、ステータス変更一覧画面へ自動遷移
                    window.location.href = "${pageContext.request.contextPath}/ApplicationStatus";
                });
            });
        </script>
    </c:if>

</body>
</html>