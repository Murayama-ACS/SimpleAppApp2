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
                    
                    // 申請一覧（app_list.jsp）を制御するサーブレットへと遷移させます
                    response.sendRedirect(request.getContextPath() + "/ApplicationStatus");
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
            
            <%-- 追加された経理部のアカウント --%>
            <option value="A20160111">田中 太郎（役職: 部長 / 部署: 経理部）</option>
            <option value="A20211116">田中 真央（役職: 一般社員 / 部署: 経理部）</option>
            
            <%-- 既存のアカウント --%>
            <option value="A00000001">山田 太一（役職: 社長 / 部署: 経営企画部）</option>
            <option value="A20180926">鈴木 健（役職: 本部長 / 部署: 情報システム部）</option>
            <option value="A20221226">渡辺 大輔（役職: 部長 / 部署: 情報システム部C課D課）</option>
            <option value="A20240411">佐藤 健（役職: 部長 / 部署: 情報システム部A課B課）</option>
            <option value="A20160108">山田 真央（役職: 課長 / 部署: 情報システム部A課）</option>
            <option value="A20190103">渡辺 一郎（役職: 部長 / 部署: 管理部）</option>
            <option value="A20250307">佐藤 大輔（役職: 一般社員 / 部署: 管理部）</option>
            <option value="A20260112">高橋 美咲（役職: 課長 / 部署: 開発部A課）</option>
            <option value="A20171208">山田 大輔（役職: 一般社員 / 部署: 開発部A課）</option>
            <option value="A20200923">伊藤 美咲（役職: 一般社員 / 部署: 開発部B課）</option>
            <option value="A20240409">佐藤 美咲（役職: 一般社員 / 部署: 情報システム部A課）</option>
            <option value="A20231208">鈴木 大輔（役職: 一般社員 / 部署: 情報システム部B課）</option>
            <option value="A20230324">高橋 大輔（役職: 一般社員 / 部署: 情報システム部D課）</option>
            <option value="A20230905">加藤 直樹（役職: 課長 / 部署: 情報システム部D課）</option>
        </select>
        <br><br>
        
        <input type="submit" value="模擬ログインして申請ステータス変更一覧画面へ" style="padding: 5px 15px;">
    </form>
</body>
</html>