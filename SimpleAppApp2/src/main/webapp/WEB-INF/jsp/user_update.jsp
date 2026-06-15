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
社員ID<c:out value="${updateEmpBean.emp_id }" />
<form action="EmployeeUpdate?action=update" method="post">
社員名<input type="text" name="emp_name"><br>
Email<input type="email" name="email"><br>
部署
<select name="dpt_id">
	<option value="D000">経営企画部</option>
	<option value="D100">管理部</option>
	<option value="D200">経理部</option>
	<option value="D300">総務部</option>
	<option value="D400">人事部</option>
	<option value="D410">人事部A課</option>
	<option value="D420">人事部B課</option>
	<option value="D500">開発部</option>
	<option value="D510">開発部A課</option>
	<option value="D520">開発部B課</option>
	<option value="D530">開発部C課</option>
	<option value="D540">開発部D課</option>
	<option value="D600">営業部</option>
	<option value="D610">営業部A課</option>
	<option value="D620">営業部B課</option>
	<option value="D630">営業部C課</option>
	<option value="D700">情報システム部</option>
	<option value="D710">情報システム部A課</option>
	<option value="D712">情報システム部A課B課</option>
	<option value="D720">情報システム部B課</option>
	<option value="D730">情報システム部C課</option>
	<option value="D734">情報システム部C課D課</option>
	<option value="D740">情報システム部D課</option>	
</select>
<br>
役職
<select name="pos_id">
	<option value="E00">一般社員</option>
	<option value="E01">課長</option>
	<option value="E02">部長</option>
	<option value="E03">本部長</option>
	<option value="E04">社長</option>
</select>
<br>
<input type="submit" value="更新">
<c:out value="${eMsg }" />
</form>
<a href="EmployeeInfo">戻る</a>
</body>
</html>