<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>AppApp System</title>
<link rel="stylesheet" type="text/css" href="css/index.css">
</head>
<body>
    <div class="login-card">
        <div class="card-header">
            <div class="brand-banner">
                <img src="${pageContext.request.contextPath}/images/logo.png" alt="AppApp System" class="sys-logo-hero">
            </div>
            <div class="subtitle">申請管理システム</div>
        </div>

        <div class="card-body">
            <h2>ログイン</h2>    
            
            <!-- ロック中の表示（サーバが lockedUntilMillis をセットしている前提） -->
			<c:if test="${not empty lockedUntilMillis}">
			    <div class="error-message" role="alert" aria-live="polite">
			        <c:out value="${eMsg}" /><br>
			        <big id="lockCountdownText" aria-live="polite"></big>
			    </div>
			</c:if>
			
			<!-- 通常のエラーメッセージ（ロック中は表示しない） -->
			<c:if test="${empty lockedUntilMillis and not empty eMsg}">
			    <div class="error-message" role="alert" aria-live="polite">
			        <c:out value="${eMsg}" />
			    </div>
			</c:if>

            <!-- 成功メッセージ -->
            <c:if test="${param.changed == 'true'}">
                <div class="success-message">
                    初期設定が完了しました。新しいパスワードでログインしてください。
                </div>
            </c:if>
      
            <form action="Login" method="post" id="loginForm">
                <div class="form-group">
                    <label for="empId">社員ID または Email</label>
                    <input type="text" id="empId" name="empId" placeholder="IDかEmailを入力" required autofocus
                </div>
                
                <div class="form-group">
                    <label for="password">パスワード</label>
                    <input type="password" id="password" name="password" placeholder="パスワードを入力" required
                </div>
                
                <div class="forgot-password">
                    <a href="Verify1">パスワードをお忘れですか？</a>
                </div>
                
                <button type="submit" class="btn-submit" id="loginSubmit">ログイン</button>
            </form>
        </div>
    </div>

    <!-- カウントダウン用スクリプト: lockedUntilMillis がセットされている場合のみ動作 -->
    <c:if test="${not empty lockedUntilMillis}">
    <script>
    (function(){
        // サーバから渡されたミリ秒を取得（JSTLで数値を埋め込む）
        var lockedUntil = parseInt('${lockedUntilMillis}', 10);
        if (isNaN(lockedUntil)) return;

        var textEl = document.getElementById('lockCountdownText');
        var empIdInput = document.getElementById('empId');
        var passwordInput = document.getElementById('password');
        var submitBtn = document.getElementById('loginSubmit');

        function formatRemaining(seconds) {
            if (seconds <= 0) return 'まもなく解除されます。';
            var hours = Math.floor(seconds / 3600);
            var minutes = Math.floor((seconds % 3600) / 60);
            var secs = seconds % 60;
            if (hours > 0) {
                return 'あと ' + hours + '時間' + minutes + '分で解除されます。';
            } else if (minutes > 0) {
                return 'あと ' + minutes + '分' + secs + '秒で解除されます。';
            } else {
                return 'あと ' + secs + '秒で解除されます。';
            }
        }

        function tick(){
            var now = Date.now();
            var remSec = Math.max(0, Math.floor((lockedUntil - now) / 1000));
            if (textEl) textEl.textContent = formatRemaining(remSec);

            if (remSec <= 0) {
                // ロック解除: フォームを有効化
                if (empIdInput) empIdInput.disabled = false;
                if (passwordInput) passwordInput.disabled = false;
                if (submitBtn) submitBtn.disabled = false;
                // 文言を上書きして終了
                if (textEl) textEl.textContent = 'アカウントのロックは解除されました。再度ログインしてください。';
            } else {
                setTimeout(tick, 1000);
            }
        }

        // 初回実行
        tick();
    })();
    </script>
    </c:if>

</body>
</html>