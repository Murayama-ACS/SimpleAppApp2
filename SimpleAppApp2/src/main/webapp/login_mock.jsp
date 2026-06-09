<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="bean.EmployeeBean" %>
<%
    // テスト用のログイン処理（送信ボタンが押されたらセッションにEmployeeBeanをセット）
    String action = request.getParameter("action");
    if ("login".equals(action)) {
        String empId = request.getParameter("emp_id");
        String empName = "test";
        String email = "test@test.jp";
        String dptId = "D400";
        String posId = "EY1";
        
        if (empId != null && !empId.trim().isEmpty()) {
            EmployeeBean mockEmployee = new EmployeeBean(empId, empName, email, dptId, posId);
            session.setAttribute("EmployeeBean", mockEmployee);
            
            // 未承認一覧サーブレットへ遷移
            response.sendRedirect(request.getContextPath() + "/ApplicationWaitList");
            return;
        }
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>ログイン モック画面</title>
</head>
<body>
    <h2>テスト用ログイン画面</h2>
    <p style="color: red;">※テスト用の社員IDを入力して「ログインして一覧へ」を押してください。</p>
    
    <form action="login_mock.jsp" method="post">
        <input type="hidden" name="action" value="login">
        <table border="1">
            <tr>
                <th>社員ID (emp_id)</th>
                <td><input type="text" name="emp_id" value="1111" required></td>
            </tr>
        </table>
        <br>
        <button type="submit">ログインして一覧へ</button>
    </form>
</body>
</html>