<%--
モックのため必ず削除すること 
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="bean.EmployeeBean" %>
<%@ page import="dao.ApplicationDAO" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>テスト用ログイン模擬画面</title>
</head>
<body>
    <h2>テスト用ログイン模擬画面</h2>
    <p>テストを実行する前に、セッションにユーザー情報を登録します。</p>
    
    <%
        if ("true".equals(request.getParameter("login"))) {
            String testEmpId = "A20190524"; // テスト用の社員ID
            
            // DAOを呼び出してDBから社員情報をオブジェクト(Bean)として取得
            ApplicationDAO dao = new ApplicationDAO();
            EmployeeBean employee = dao.selectEmployee(testEmpId);
            
            if (employee != null) {
                // セッションにEmployeeBeanオブジェクトを格納
                session.setAttribute("loginEmployee", employee);
                response.sendRedirect(request.getContextPath() + "/Application");
                return;
            } else {
                out.println("<p style='color:red;'>エラー: ID「" + testEmpId + "」の社員データがDBに見つかりません。先にemployeesテーブルへデータを登録してください。</p>");
            }
        }
    %>

    <form action="login_mock.jsp" method="get">
        <input type="hidden" name="login" value="true">
        <input type="submit" value="加藤 健として模擬ログインして申請画面へ">
    </form>
</body>
</html>