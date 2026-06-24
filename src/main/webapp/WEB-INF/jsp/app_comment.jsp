<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- JSTLタグライブラリの宣言（c:条件分岐などの制御構文、fmt:日付や金額のフォーマット） --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>申請詳細 - AppApp システム</title>
    <%-- 専用CSSと共通ナビゲーションCSSの読み込み --%>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/navbar.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/app_comment.css"> 
    <%-- ポップアップダイアログ表示用のライブラリ（SweetAlert2） --%>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>
    <%-- 通知の未読カウントロジックを含む共通ヘッダーをインクルード --%>
    <%@ include file="/WEB-INF/jsp/header.jsp" %>

    <div class="detail-card">
        <h2>申請内容詳細</h2>

        <%-- 緊急フラグが設定されている場合のみ、目立つ警告バッジを表示 --%>
        <c:if test="${application.urgent == 'true' || application.urgent == '1' || application.urgent == '緊急'}">
            <div class="badge-urgent-box">⚠️ 至急案件</div>
        </c:if>

        <%-- 申請者の基本情報表示エリア --%>
        <div class="applicant-info-box">
            <div class="info-item"><strong>申請者:</strong> ${application.employeeName} (${application.departmentName})</div>
            <div class="info-item"><strong>申請日時:</strong> 
                <%-- DBから取得した文字列の日時を解析し、見やすい形式（yyyy/MM/dd HH:mm）に変換 --%>
                <fmt:parseDate value="${application.createDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" type="both" />
                <fmt:formatDate value="${parsedDate}" pattern="yyyy/MM/dd HH:mm" />
            </div>
        </div>

        <%-- 申請内容の詳細データテーブル --%>
        <table class="detail-data-table">
            <tr><th>申請ID</th><td>${application.apctId}</td></tr>
            <tr><th>カテゴリ</th><td>${application.type}</td></tr>
            <tr><th>精算方法</th><td>${application.paymentMethod}</td></tr>
            <%-- 金額を「¥」マーク付きの3桁区切りにフォーマット（小数点は表示しない） --%>
            <tr><th>金額</th><td><fmt:formatNumber value="${application.amount}" type="currency" currencySymbol="¥" pattern="¥#,##0"/></td></tr>
            <tr><th>申請内容</th><td>${application.content}</td></tr>
            <tr><th>申請理由</th><td>${application.reason}</td></tr>
            <tr>
                <th>申請状態</th>
                <td>
                    <%-- 状態名に応じて文字色を動的に変更するクラスを付与 --%>
                    <span class="status-text ${application.statusName == '承認' ? 'text-approved' : (application.statusName == '却下' ? 'text-rejected' : 'text-pending')}">
                        ${application.statusName}
                    </span>
                </td>
            </tr>
            <%-- 前回のコメントが存在しない場合は「なし」と表示 --%>
            <tr><th>コメント</th><td class="comment-data">${empty approval.comment ? 'なし' : approval.comment}</td></tr>
            <tr><th>備考</th><td>${empty application.note ? 'なし' : application.note}</td></tr>
        </table>

        <%-- 承認アクションボックスの表示制御 --%>
        <%-- サーブレット側で設定された「承認権限(canApprove)」があり、かつ対象が「未承認（ステータス1）」の場合のみ表示 --%>
        <c:if test="${canApprove && (application.statusName == '未承認' || application.statusName == '承認待ち' || application.status_id == 1)}">
            <div class="approval-action-box">
                <h3 class="action-title">この申請に対するアクション</h3>
                
                <form id="approvalForm" action="${pageContext.request.contextPath}/ApplicationComment" method="post">
                    <%-- バックエンドに送信するための隠しフィールド（申請IDと、JSでセットするアクション種別） --%>
                    <input type="hidden" name="apct_id" value="${application.apctId}">
                    <input type="hidden" name="action_type" id="hiddenActionType" value="">
                    
                    <div class="comment-input-area">
                        <label for="commentInput" class="comment-label">コメント (任意):</label>
                        <textarea id="commentInput" name="comment" rows="4" placeholder="上司としてのコメントやフィードバックを入力してください"></textarea>
                    </div>
                    
                    <%-- 承認・却下ボタン（クリック時にJSのconfirmAction関数を呼び出す） --%>
                    <div class="btn-container">
                        <button class="btn-sm btn-approve" type="button" onclick="confirmAction('approve', '承認')" >承認する</button>
                        <button class="btn-sm btn-reject" type="button" onclick="confirmAction('reject', '却下')" >却下する</button>
                    </div>
                </form>
            </div>
        </c:if>

        <div class="dual-nav-container">
            <%--戻るボタン：セッションに検索条件付きの一覧URL（lastListUrl）が保存されていればそこに戻り、なければデフォルトの未承認一覧に戻る --%>
            <a class="nav-back-btn" href="${not empty sessionScope.lastListUrl ? sessionScope.lastListUrl : pageContext.request.contextPath += '/ApplicationWaitList'}">⬅ 未承認一覧へ</a>
        </div>
    </div>

    <%-- アクションボックスが表示されている場合のみ、関連するJavaScriptもレンダリングする --%>
    <c:if test="${canApprove && (application.statusName == '未承認' || application.statusName == '承認待ち' || application.status_id == 1)}">
        <script>
            // アクション確認用のポップアップ処理
            function confirmAction(actionType, actionName) {
                // 押されたボタンが「却下」なら赤色、それ以外（承認）なら緑色に設定
                const btnColor = (actionType === 'reject') ? '#dc3545' : '#28a745';
                const commentValue = document.getElementById('commentInput').value.trim();
                
                // 確認メッセージの組み立て（CSSでスタイル分離済み）
                let confirmText = "この申請を「" + actionName + "」しますか？";
                if (commentValue !== "") {
                    confirmText += "<br><span class='confirm-notice'>※コメントも一緒に送信されます</span>";
                }

                // SweetAlert2を使用したリッチな確認ダイアログ
                Swal.fire({
                    title: '確認',
                    html: confirmText,
                    icon: 'warning',
                    showCancelButton: true,
                    confirmButtonText: actionName + 'を確定',
                    cancelButtonText: 'キャンセル',
                    confirmButtonColor: btnColor,
                    cancelButtonColor: '#6c757d'
                }).then((result) => {
                    // ユーザーが「確定」を押した場合の処理
                    if (result.isConfirmed) {
                        // 隠しフィールドにアクション種別（approve または reject）をセット
                        document.getElementById('hiddenActionType').value = actionType;
                        
                        // 多重送信防止とUX向上のためのローディングスピナーを表示
                        Swal.fire({
                            title: '処理中...',
                            text: 'データベースを更新しています',
                            allowOutsideClick: false,
                            didOpen: () => { Swal.showLoading(); }
                        });
                        
                        // フォームを送信
                        document.getElementById('approvalForm').submit();
                    }
                });
            }
        </script>
    </c:if>
</body>
</html>