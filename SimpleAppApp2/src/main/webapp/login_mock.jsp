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
    <p>テストを実行する前に、プルダウンからユーザーを選択してセッションに登録します。</p>
    
    <%
        if ("true".equals(request.getParameter("login"))) {
            // プルダウンから選択された社員IDを取得
            String testEmpId = request.getParameter("emp_id");
            
            if (testEmpId != null && !testEmpId.trim().isEmpty()) {
                // DAOを呼び出してDBから社員情報をオブジェクト(Bean)として取得
                ApplicationDAO dao = new ApplicationDAO();
                EmployeeBean employee = dao.selectEmployee(testEmpId);
                
                if (employee != null) {
                    // セッションにEmployeeBeanオブジェクトを格納
                    session.setAttribute("loginEmployee", employee);
                    response.sendRedirect(request.getContextPath() + "/ApplicationWaitList");
                    return;
                } else {
                    out.println("<p style='color:red;'>エラー: ID「" + testEmpId + "」の社員データがDBに見つかりません。先にemployeesテーブルへデータを登録するか、SQLが反映されているか確認してください。</p>");
                }
            } else {
                out.println("<p style='color:red;'>エラー: 社員が選択されていません。</p>");
            }
        }
    %>

    <form action="login_mock.jsp" method="get">
        <input type="hidden" name="login" value="true">
        
        <label for="emp_select">ログインユーザー選択: </label>
        <select name="emp_id" id="emp_select" style="padding: 5px; width: 450px;">
            <option value="">-- アカウントを選択してください --</option>
            <option value="A20230905">加藤 直樹（役職: E01 / 部署: D740）</option>
            <option value="A20190524">加藤 健（役職: E00 / 部署: D740）</option>
            <option value="A20160108">山田 真 Mao（役職: E01 / 部署: D710）</option>
            <option value="A20180926">鈴木 健（役職: E03 / 部署: D700）</option>
            <option value="A20200313">田中 太郎（役職: E01 / 部署: D420）</option>
            <option value="A20221203">佐藤 直樹（役職: E00 / 部署: D410）</option>
            <option value="A20220613">伊藤 太郎（役職: E00 / 部署: D400）</option>
            <option value="A20250314">渡辺 彩（役職: E03 / 部署: D200）</option>
            <option value="A99999999">テストアカウント（役職: E02 / 部署: D200）</option>
            <option value="A20190103">渡辺 一郎（役職: E02 / 部署: D100）</option>
            <option value="A00000001">山田 太一［社長］（役職: E04 / 部署: D000）</option>
        </select>
        <br><br>
        
        <input type="submit" value="模擬ログインして申請画面へ" style="padding: 5px 15px;">
    </form>
</body>
</html>