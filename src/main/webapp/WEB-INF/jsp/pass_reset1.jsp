<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Password Reset</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/pass_reset1.css">
<script>
function goToTopPage() {
    window.location.href = "${pageContext.request.contextPath}/TopPageServlet";
}
</script>
</head>
<body>
	<div class="setup-card">
        <div class="card-header">
            <h2>アカウント初期設定</h2>
            <div class="subtitle">
                初回ログインです。<br>新しいパスワードと秘密の質問を設定してください。
            </div>
        </div>
        
        <div class="card-body">
            <c:if test="${not empty errorMessage}">
                <div class="error-message">
                    ${errorMessage}
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/InitPassReset" method="post">
     
                <div class="section-title">🔒 パスワードの設定</div>
                <div class="form-group">
                    <label for="newPassword">新しいパスワード</label>
                    <input type="password" id="newPassword" name="newPassword" placeholder="8文字以上の英数字" required minlength="8">
                </div>
                <div class="form-group">
                    <label for="confirmPassword">確認</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" placeholder="もう一度入力してください" required minlength="8">
                </div>

                <div class="section-title">🛡️ 秘密の質問の設定</div>
                
                <div class="qa-group">
                    <div class="form-group">
                        <label for="question1">質問 1</label>
                        <select id="question1" name="question1" required>
                            <option value="" disabled selected>質問を選択してください</option>
                            <option value="初めて飼ったペットの名前は？">初めて飼ったペットの名前は？</option>
                            <option value="子どものころに一番よく遊んだ路地や通りの名前は？">子どものころに一番よく遊んだ路地や通りの名前は？</option>
                            <option value="初めて一人で泊まった旅館やホテルの名前は？">初めて一人で泊まった旅館やホテルの名前は？</option>
                            <option value="学生時代に仲間から呼ばれていたあだ名は？">学生時代に仲間から呼ばれていたあだ名は？</option>
                            <option value="自分で最初に作った料理の名前は？">自分で最初に作った料理の名前は？</option>
                            <option value="初めて自分で買った雑誌やコミックのタイトルは？">初めて自分で買った雑誌やコミックのタイトルは？</option>
                            <option value="最初に参加したライブやイベントの会場名は？">最初に参加したライブやイベントの会場名は？</option>
                            <option value="幼少期によく遊んだ公園の呼び名や特徴は？">幼少期によく遊んだ公園の呼び名や特徴（例：「大きな松のある公園」など）は？</option>
                            <option value="人生で最初に成し遂げた印象的な出来事を一言で表すと？">人生で最初に成し遂げた印象的な出来事を一言で表すと？</option>
                            <option value="家族やごく親しい友人だけが使う自分の呼び名（愛称）は？">家族やごく親しい友人だけが使う自分の呼び名（愛称）は？</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="answer1">回答 1</label>
                        <input type="text" id="answer1" name="answer1" required>
                    </div>
                </div>

                <div class="qa-group">
                    <div class="form-group">
                        <label for="question2">質問 2</label>
                        <select id="question2" name="question2" required>
                            <option value="" disabled selected>質問を選択してください</option>
                            <option value="初めて飼ったペットの名前は？">初めて飼ったペットの名前は？</option>
                            <option value="子どものころに一番よく遊んだ路地や通りの名前は？">子どものころに一番よく遊んだ路地や通りの名前は？</option>
                            <option value="初めて一人で泊まった旅館やホテルの名前は？">初めて一人で泊まった旅館やホテルの名前は？</option>
                            <option value="学生時代に仲間から呼ばれていたあだ名は？">学生時代に仲間から呼ばれていたあだ名は？</option>
                            <option value="自分で最初に作った料理の名前は？">自分で最初に作った料理の名前は？</option>
                            <option value="初めて自分で買った雑誌やコミックのタイトルは？">初めて自分で買った雑誌やコミックのタイトルは？</option>
                            <option value="最初に参加したライブやイベントの会場名は？">最初に参加したライブやイベントの会場名は？</option>
                            <option value="幼少期によく遊んだ公園の呼び名や特徴は？">幼少期によく遊んだ公園の呼び名や特徴（例：「大きな松のある公園」など）は？</option>
                            <option value="人生で最初に成し遂げた印象的な出来事を一言で表すと？">人生で最初に成し遂げた印象的な出来事を一言で表すと？</option>
                            <option value="家族やごく親しい友人だけが使う自分の呼び名（愛称）は？">家族やごく親しい友人だけが使う自分の呼び名（愛称）は？</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="answer2">回答 2</label>
                        <input type="text" id="answer2" name="answer2" required>
                    </div>
                </div>

                <div class="qa-group">
                    <div class="form-group">
                        <label for="question3">質問 3</label>
                        <select id="question3" name="question3" required>
                            <option value="" disabled selected>質問を選択してください</option>
                            <option value="初めて飼ったペットの名前は？">初めて飼ったペットの名前は？</option>
                            <option value="子どものころに一番よく遊んだ路地や通りの名前は？">子どものころに一番よく遊んだ路地や通りの名前は？</option>
                            <option value="初めて一人で泊まった旅館やホテルの名前は？">初めて一人で泊まった旅館やホテルの名前は？</option>
                            <option value="学生時代に仲間から呼ばれていたあだ名は？">学生時代に仲間から呼ばれていたあだ名は？</option>
                            <option value="自分で最初に作った料理の名前は？">自分で最初に作った料理の名前は？</option>
                            <option value="初めて自分で買った雑誌やコミックのタイトルは？">初めて自分で買った雑誌やコミックのタイトルは？</option>
                            <option value="最初に参加したライブやイベントの会場名は？">最初に参加したライブやイベントの会場名は？</option>
                            <option value="幼少期によく遊んだ公園の呼び名や特徴は？">幼少期によく遊んだ公園の呼び名や特徴（例：「大きな松のある公園」など）は？</option>
                            <option value="人生で最初に成し遂げた印象的な出来事を一言で表すと？">人生で最初に成し遂げた印象的な出来事を一言で表すと？</option>
                            <option value="家族やごく親しい友人だけが使う自分の呼び名（愛称）は？">家族やごく親しい友人だけが使う自分の呼び名（愛称）は？</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="answer3">回答 3</label>
                        <input type="text" id="answer3" name="answer3" required>
                    </div>
                </div>
                <button type="submit" class="btn-submit">設定を完了します</button>
            </form>
            <c:if test="${isSuccess}">
		    	<div id="successPopup" class="popup-overlay">
		        	<div class="popup-content">
		            <div class="popup-icon">✅</div>
		            <h3 class="popup-title">設定完了</h3>
		            <p class="popup-text">パスワードと秘密の質問の設定が完了しました。<br>トップページへ移動します。</p>
		            <button class="btn-primary" onclick="goToTopPage()">トップページへ</button>
		        	</div>
    			</div>
			</c:if>
        </div>
    </div>
</body>
</html>