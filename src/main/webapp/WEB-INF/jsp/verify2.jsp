<%@ page language="java" contentType="text/html; charset=UTF-8" 
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title>Password Reset</title>      
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/verify2.css"> 
</head>
<body>
    <div class="reset-card">
        <h2>秘密の質問の認証</h2>
        <div class="step-indicator">ステップ 2 / 3</div>
        <div class="card-body">
            <form action="Verify2" method="post">

                <input type="hidden" name="q1" value="${q1}">
                <input type="hidden" name="q2" value="${q2}">
                <input type="hidden" name="q3" value="${q3}">
                <input type="hidden" name="empId" value="${empId}">

                <div class="qa-group">
                    <label class="question-label" for="selectQ1">質問 1</label>
                    	<select id="selectQ1" name="q1" required>  
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
                    
                    <label for="answer1">回答</label>
                    <input type="text" id="answer1" name="answer1" placeholder="回答を入力してください" required>
                </div>
                
                <div class="qa-group">
                   <label class="question-label" for="selectQ2">質問 2</label>
                    	<select id="selectQ2" name="q2" required>  
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
                    
                    <label for="answer2">回答</label>
                    <input type="text" id="answer2" name="answer2" placeholder="回答を入力してください" required>
                </div>
                
                <div class="qa-group">
                    <label class="question-label" for="selectQ3">質問 3</label>
                    	<select id="selectQ3" name="q3" required>  
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
                    
                    <label for="answer3">回答</label>
                    <input type="text" id="answer3" name="answer3" placeholder="回答を入力してください" required>
                </div>
                <c:if test="${not empty errorMessage}">
                    <div class="error-message">
                        ${errorMessage}
                    </div>
                </c:if>
                <button type="submit" class="btn-submit">回答を認証する</button>
            </form>
            <a href="index.jsp" class="back-link">ログイン画面に戻る</a>
        </div> 
     </div>
</body>
</html>