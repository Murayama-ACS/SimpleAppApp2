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
社員ID<c:out value="${insertEmpBean.emp_id }" /><br>
社員名<c:out value="${insertEmpBean.emp_name }" /><br>
ふりがな<c:out value="${insertEmpBean.emp_furigana }" /><br>
Email<c:out value="${insertEmpBean.email }" /><br>
部署<c:out value="${insertEmpBean.dpt_id }" /><br>
役職<c:out value="${insertEmpBean.pos_id }" /><br>
<form action="EmployeeUpdate" method="post">
<input type="submit" value="確定"><br>
</form>
<a href="EmployeeAdd">戻る</a>
<c:out value="${eMsg }" /><br>
</body>
</html>