<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>申請入力画面</title>
</head>
<body>
    <h2>テスト用 申請入力フォーム</h2>
    
    <form action="${pageContext.request.contextPath}/Application" method="post">
        <table border="1">
            <tr>
                <td><label for="applicationType">申請種別 (type):</label></td>
                <td><input type="text" id="applicationType" name="applicationType" required></td>
            </tr>
            <tr>
                <td><label for="paymentMethod">精算方法 (paymentMethod):</label></td>
                <td><input type="text" id="paymentMethod" name="paymentMethod"></td>
            </tr>
            <tr>
                <td><label for="employeeId">社員ID (employeeId / content※):</label></td>
                <td><input type="text" id="employeeId" name="employeeId"></td>
            </tr>
            <tr>
                <td><label for="content">申請内容 (content / amount※):</label></td>
                <td><textarea id="content" name="content" rows="4" cols="30"></textarea></td>
            </tr>
            <tr>
                <td><label for="reason">申請理由 (reason):</label></td>
                <td><textarea id="reason" name="reason" rows="4" cols="30"></textarea></td>
            </tr>
            <tr>
                <td><label for="note">備考 (note / remark※):</label></td>
                <td><input type="text" id="note" name="note"></td>
            </tr>
            <tr>
                <td><label for="urgentFlag">緊急フラグ (urgent):</label></td>
                <td>
                    <input type="checkbox" id="urgentFlag" name="urgentFlag" value="true"> 緊急の場合はチェック
                </td>
            </tr>
        </table>
        <br>
        <input type="submit" value="申請を送信する">
    </form>
</body>
</html>