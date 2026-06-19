<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- JSTLタグライブラリの宣言（c:条件分岐など、fmt:フォーマット用） --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>新規申請提出 - AppApp システム</title>
    <%-- 共通ナビゲーションCSSと新規申請画面専用CSSの読み込み --%>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/navbar.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/application.css">
    <%-- ポップアップ用の高機能ライブラリ SweetAlert2 --%>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script> 
    <%-- Font Awesome（アイコン）の読み込み --%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    
</head>
<body>
    <%-- 共通ヘッダー（ナビゲーションメニュー・通知ベル）を読み込み --%>
    <%@ include file="/WEB-INF/jsp/header.jsp" %>

    <div class="container">
        <div class="application-card">
            
            <div class="card-header">          	
                <h2><i class="fa-solid fa-pen-to-square"></i>&nbsp;&nbsp;新規申請提出</h2>
            </div>
            
            <div class="card-body">
                <%-- バックエンド（Servlet）から渡されたエラーメッセージがある場合に表示 --%>
                <c:if test="${not empty eMsg}">
                    <div class="error-display">⚠️ ${eMsg}</div>
                </c:if>
             
                <%-- ログインユーザー（申請者）の基本情報を表示 --%>
                <c:if test="${not empty employeeInfo}">
                    <div class="info-display">
                        <strong>【 申請者情報 】</strong><br>
                        社員ID: ${employeeInfo.emp_id} &nbsp;|&nbsp; 
                        氏名: ${employeeInfo.emp_name} &nbsp;|&nbsp; 
                        所属部署: ${not empty departmentName ? departmentName : '未所属'}
                    </div>
                </c:if>
    
                <%-- 申請フォーム（送信処理は下のJavaScriptで制御するため id="appForm" を付与） --%>
                <form action="${pageContext.request.contextPath}/ApplicationServlet" method="post" id="appForm">
                    
                    <%-- 申請種別の選択（必須項目） --%>
                    <div class="form-group">
                        <label for="appType">申請種別<span class="required-mark">※必須</span></label>
                        <select id="appType" name="applicationType" required>
                            <option value="" disabled selected>種別を選択してください</option>
                            <option value="備品購入申請">備品購入申請</option>
                            <option value="研修参加申請">研修参加申請</option>
                            <option value="出張申請">出張申請</option>
                            <option value="業務委託・外注費申請">業務委託・外注費申請</option>
                            <option value="その他">その他</option>
                        </select>
                    </div>
                    
                    <%-- 精算方法の選択（必須項目） --%>
                    <div class="form-group">
                        <label for="paymentMethod">精算方法<span class="required-mark">※必須</span></label>
                        <select id="paymentMethod" name="paymentMethod" required>
                            <option value="" disabled selected>精算方法を選択してください</option>
                            <option value="立替払い（現金手渡し）">立替払い（現金手渡し）</option>
                            <option value="立替払い (給与振込)">立替払い (給与振込)</option>
                            <option value="会社直接支払い">会社直接支払い</option>
                        </select>
                    </div>
                    
                    <%-- 申請金額の入力（必須項目、半角数字のみ） --%>
                    <div class="form-group">
                        <label for="amount">申請金額 (円)<span class="required-mark">※必須</span></label>
                        <input type="number" id="amount" name="amount" min="0" placeholder="金額を半角数字で入力してください" required>
                    </div>
                    
                    <%-- 申請内容の入力（必須項目） --%>
                    <div class="form-group">
                        <label for="content">申請内容<span class="required-mark">※必須</span></label>
                        <textarea id="content" name="content" placeholder="具体的な申請内容を入力してください（例：〇〇プロジェクト用PC購入）" required></textarea>
                    </div>
                    
                    <%-- 申請理由の入力（必須項目） --%>
                    <div class="form-group">
                        <label for="reason">申請理由<span class="required-mark">※必須</span></label>
                        <textarea id="reason" name="reason" placeholder="この申請が必要な理由を記入してください" required></textarea>
                    </div>
                    
                    <%-- 備考の入力（任意項目）※name属性は "remark" になっています --%>
                    <div class="form-group">
                        <label for="remark">備考</label>
                        <textarea id="note" name="remark" placeholder="特記事項があれば記入してください"></textarea>
                    </div>
                    
                    <%-- 緊急フラグ（チェックボックス） --%>
                    <div class="checkbox-group">
                        <input type="checkbox" id="urgent" name="urgentFlag" value="true">
                        <label for="urgent">⚠️ この申請は緊急を要する</label>
                    </div>
                    
                    <%-- 送信ボタン（クリックするとJSのsubmitイベントが発火します） --%>
                    <button type="submit" class="btn-submit">申請を提出する</button>
                </form>
                
                <a href="${pageContext.request.contextPath}/TopPageServlet" class="back-link">⬅ メインメニューに戻る</a>
            </div> 
        </div> 
    </div> 

    <%--フロントエンドのJavaScript処理（確認ポップアップと非同期送信） --%>
    <script>
    const form = document.getElementById('appForm');
    
    // フォーム送信（submit）イベントをフックして独自の処理を行う
    form.addEventListener('submit', function(event) {
        // 1. ブラウザ標準の画面遷移（フォーム送信）を一旦キャンセルする
        event.preventDefault(); 
        
        // 2. 画面に入力された各項目の値を取得する
        const appTypeSelect = document.getElementById('appType');
        const appType = appTypeSelect.options[appTypeSelect.selectedIndex]?.text || '';
        
        const paymentSelect = document.getElementById('paymentMethod');
        const paymentMethod = paymentSelect.options[paymentSelect.selectedIndex]?.text || '';
        
        const amount = document.getElementById('amount').value;
        const content = document.getElementById('content').value;
        const reason = document.getElementById('reason').value;
        const note = document.getElementById('note').value || 'なし';
        
        // 緊急フラグの判定と表示テキストの作成
        const isUrgent = document.getElementById('urgent').checked;
        const urgentText = isUrgent ? '<span class="swal-urgent-text">至急</span>' : '通常';
        
        // 金額を3桁区切りにフォーマット
        const formattedAmount = Number(amount).toLocaleString();
        
        // 3. 確認ポップアップに表示するためのHTML（サマリー）を動的に組み立てる
        // （入力された改行コード \n をHTMLの <br> タグに変換して表示を整える）
        const summaryHtml = `
            <div class="swal-summary-container">
                <div class="swal-summary-section">
                    <strong>申請種別:</strong> ` + appType + `<br>
                    <strong>精算方法:</strong> ` + paymentMethod + `<br>
                    <strong>申請金額:</strong> ¥` + formattedAmount + `
                </div>
                <div class="swal-summary-section">
                    <strong>申請内容:</strong><br><div class="swal-summary-text">` + content.replace(/\n/g, '<br>') + `</div>
                    <strong style="display:block; margin-top:10px;">申請理由:</strong><div class="swal-summary-text">` + reason.replace(/\n/g, '<br>') + `</div>
                    <strong style="display:block; margin-top:10px;">備考:</strong><div class="swal-summary-text">` + note.replace(/\n/g, '<br>') + `</div>
                </div>
                <div>
                    <strong>緊急度:</strong> ` + urgentText + `
                </div>
            </div>
        `;

        // 4. SweetAlert2を使って、入力内容の最終確認ダイアログを表示する
        Swal.fire({
            title: '<span style="font-size: 22px; color: #0d47a1;">以下の内容で提出しますか？</span>',
            html: summaryHtml,
            width: '700px', // 内容が多いので幅を広めに設定
            icon: 'question',
            customClass: { icon: 'my-custom-icon-size' },
            showCancelButton: true,
            confirmButtonColor: '#1976d2', // ビジネスブルー
            cancelButtonColor: '#6c757d',  
            confirmButtonText: '提出を確定',
            cancelButtonText: 'キャンセル'
        }).then((result) => {
            // ユーザーが「提出を確定」をクリックした場合
            if (result.isConfirmed) {
                
                // 多重送信防止のためのローディングアニメーションを表示
                Swal.fire({
                    title: '送信中...',
                    text: 'しばらくお待ちください',
                    allowOutsideClick: false,
                    didOpen: () => { Swal.showLoading(); }
                });

                // フォームの入力データを非同期送信用の形式に変換
                const formData = new URLSearchParams(new FormData(form));
                
                // 5. Fetch API を使用して、画面をリロードせずにサーバー(Servlet)へPOST送信
                fetch(form.action, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: formData
                })
                .then(async response => {  
                    // サーバーからのレスポンスが正常（HTTP 200 OK等）な場合
                    if(response.ok) {
                        Swal.fire({
                            title: '提出完了',
                            text: '申請が正常に提出されました。',
                            icon: 'success',
                            customClass: { icon: 'my-custom-icon-size' },
                            confirmButtonText: 'メインメニューへ',
                            confirmButtonColor: '#1976d2',
                        }).then(() => {
                            // 完了後、TopPageへ自動遷移させる
                            window.location.href = "${pageContext.request.contextPath}/TopPageServlet"; 
                        });
                    } else {
                        // サーバー側でバリデーションエラー等が起きた場合（HTTP 4xx/5xx）
                        const errorMsg = await response.text();
                        Swal.fire({
                            title: 'エラー', 
                            html: '<span style="color:#dc3545;">' + errorMsg + '</span>', 
                            icon: 'error',
                            confirmButtonColor: '#dc3545'
                        });
                    }
                })
                .catch(error => {
                    // ネットワーク切断など、通信自体に失敗した場合の処理
                    console.error('Error:', error);
                    Swal.fire('通信エラー', 'サーバーに接続できませんでした。', 'error');
                });
            }
        });
    });
    </script>
</body>
</html>