<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>SimpleAppApp2</title>
</head>
<body>
<form action="InitPassReset" method="post">
<p>新しいパスワード<input type="password" name="pass"></p>
<p>確認<input type="password" name="retype"></p>
<p>秘密の質問
<select name="quiz">
	<option value="0">初めて飼ったペットの名前は？</option>
	<option value="1">子どものころに一番よく遊んだ路地や通りの名前は？</option>
	<option value="2">初めて一人で泊まった旅館やホテルの名前は？</option>
	<option value="3">学生時代に仲間から呼ばれていたあだ名は？</option>
	<option value="4">自分で最初に作った料理の名前は？</option>
	<option value="5">初めて自分で買った雑誌やコミックのタイトルは？</option>
	<option value="6">最初に参加したライブやイベントの会場名は？</option>
	<option value="7">幼少期によく遊んだ公園の呼び名や特徴（例：「大きな松のある公園」など）は？</option>
	<option value="8">人生で最初に成し遂げた印象的な出来事を一言で表すと？</option>
	<option value="9">家族やごく親しい友人だけが使う自分の呼び名（愛称）は？</option>
</select>
</p>
<p>秘密の解答<input type="text" name="answer"></p>
<input type="submit" value="提出">
</form>
<c:if test="${not empty eMsg }">
			<c:out value="${eMsg }" />
			<br>
		</c:if>
</body>
</html>