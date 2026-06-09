<%--
モックのため必ず削除すること 
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="bean.EmployeeBean" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>申請入力画面</title>
<script>
    function confirmSubmission() {
        var type = document.getElementById("applicationType").value;
        var method = document.getElementById("paymentMethod").value;
        var amount = document.getElementById("amount").value;
        var content = document.getElementById("content").value;
        var reason = document.getElementById("reason").value;
        var note = document.getElementById("note").value;
        var urgent = document.getElementById("urgentFlag").checked ? "緊急" : "通常";

        var message = "この内容で申請を送信してもよろしいですか？\n\n"
                    + "【申請種別】: " + type + "\n"
                    + "【精算方法】: " + method + "\n"
                    + "【金  額】: " + amount + " 円\n"
                    + "【申請内容】: " + content + "\n"
                    + "【申請理由】: " + reason + "\n"
                    + "【備  考】: " + note + "\n"
                    + "【優先度等】: " + urgent;

        return confirm(message);
    }
</script>
</head>
<body>
    <h2>申請入力フォーム</h2>

    <%
        // サーブレットがリクエストスコープに入れてくれたオブジェクトと文字列を取り出す
        EmployeeBean empBean = (EmployeeBean) request.getAttribute("employeeInfo");
        String dptName = (String) request.getAttribute("departmentName");
        
        // ★【修正】セッションからEmployeeBean型としてログイン情報を取得
        EmployeeBean sessionEmp = (EmployeeBean) session.getAttribute("loginEmployee");
        
        String empId = "未ログイン";
        String empName = "未ログイン";

        // 最優先でリクエストスコープ（サーブレット経由）のデータを使用し、
        // 取得できない場合はセッションのデータを使用する安全設計
        if (empBean != null) {
            empId = empBean.getEmp_id();
            empName = empBean.getEmp_name();
        } else if (sessionEmp != null) {
            empId = sessionEmp.getEmp_id();
            empName = sessionEmp.getEmp_name();
        }
        
        if (dptName == null) {
            dptName = "未所属";
        }
    %>

    <div style="background-color: #f0f0f0; padding: 10px; margin-bottom: 20px; border: 1px solid #ccc;">
        <strong>現在のログインユーザー情報:</strong><br>
        社員ID: <%= empId %><br>
        氏名: <%= empName %><br>
        部署: <%= dptName %>
    </div>

    <form action="${pageContext.request.contextPath}/Application" method="post" onsubmit="return confirmSubmission()">
        <table border="1">
            <tr>
                <td><label for="applicationType">申請種別 (type):</label></td>
                <td><input type="text" id="applicationType" name="applicationType" required value="経費精算"></td>
            </tr>
            <tr>
                <td><label for="paymentMethod">精算方法 (method):</label></td>
                <td><input type="text" id="paymentMethod" name="paymentMethod" value="現金"></td>
            </tr>
            <tr>
                <td><label for="amount">金額 (amount):</label></td>
                <td><input type="number" id="amount" name="amount" value="5000" required></td>
            </tr>
            <tr>
                <td><label for="content">申請内容 (content):</label></td>
                <td><textarea id="content" name="content" rows="4" cols="30" required>消耗品費として文房具を購入</textarea></td>
            </tr>
            <tr>
                <td><label for="reason">申請理由 (reason):</label></td>
                <td><textarea id="reason" name="reason" rows="4" cols="30" required>業務使用のため</textarea></td>
            </tr>
            <tr>
                <td><label for="note">備考 (remark):</label></td>
                <td><input type="text" id="note" name="note" value="領収書あり"></td>
            </tr>
            <tr>
                <td><label for="urgentFlag">緊急フラグ (urgent):</label></td>
                <td>
                    <input type="checkbox" id="urgentFlag" name="urgentFlag" value="true"> 緊急
                </td>
            </tr>
        </table>
        <br>
        <input type="submit" value="申請を送信する" <%= ("未ログイン".equals(empId)) ? "disabled" : "" %>>
    </form>

    <%
        // サーブレットからエラーメッセージが送られてきているか確認し、ポップアップを表示する
        String errorMsg = (String) request.getAttribute("errorMessage");
        if (errorMsg != null && !errorMsg.isEmpty()) {
    %>
        <script type="text/javascript">
            alert("<%= errorMsg %>");
        </script>
    <%
        }
    %>
</body>
</html>