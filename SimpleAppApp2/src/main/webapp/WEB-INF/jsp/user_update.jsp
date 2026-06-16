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
ふりがな<input type="text" name="emp_furigana"><br>
Email<input type="email" name="email"><br>
部署
<select name="dpt_id">
	<option value=""><c:out value="--指定なし--" /></option>
  	<c:forEach var="d" items="${dptList}">
 		<option value="${d.dpt_id}" <c:if test="${dpt_id != null && dpt_id eq d.dpt_id}">selected</c:if>>
    	<c:out value="${d.dpt_name}" />
  		</option>
	</c:forEach>
</select>
<br>
役職
<select name="pos_id">
	<option value=""><c:out value="--指定なし--" /></option>
    <c:forEach var="p" items="${posList}">
 		<option value="${p.pos_id}" <c:if test="${pos_id != null && pos_id eq p.pos_id}">selected</c:if>>
    	<c:out value="${p.pos_name}" />
  		</option>
	</c:forEach>
</select>
<br>
<input type="submit" value="更新">
<c:out value="${eMsg }" />
</form>
<a href="EmployeeInfo">戻る</a>
</body>
</html>