<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>SimpleAppApp2</title>
<style>
     /*popup template design*/
    .popup-background{
        /*position size*/
        position: fixed;
        z-index : 100;
        top : 0;
        left : 0;
        height : 100vh;
        width : 100vw;

        /*design*/
        background-color: gray;
        opacity : 0.5
    }
    .popup{
        /*position size*/
        --height : 300px;
        --width : 300px;
        z-index : 101;
        position: fixed;
        top : calc(50vh - calc(var(--height) / 2));
        left : calc(50vw - calc(var(--width) / 2));
        height : var(--height);
        width : var(--width);

        /*design*/
        overflow: hidden;
        background-color: white;
        border-radius: 10px;
        box-shadow: 0px 0px 3px 1px gray;
    }
    .popup > .close-button{
        /*position size*/
        --size : 20px;
        position:absolute;
        top : calc(var(--size)/4);
        left : calc(var(--width) - var(--size) * 5/4);/*css variables are available in children*/
        height : var(--size);
        width : var(--size);

        /*design*/
        display: flex;
        justify-content: center;
        align-items: center;
        background-color: transparent;
        color : gray;
        font-size: var(--size);
        border-radius: calc(var(--size)/5);
    }
    .popup > .close-button:hover{
        /*design*/
        background-color: red;
        color : white;
    }
    .popup > .content{
        padding: 10px;
        text-align: center;
    }
    

    /*hide popup*/
    .popup-flag{
        display:none;
    }
    .popup-flag:not(:checked) + .popup-background{
        display:none;
    }
    .popup-flag:not(:checked) + * + .popup{
        display:none;
    }
    <%-- ==========================================
         【追加】CSVフォーム用の簡易スタイル
         ========================================== --%>
    .form-section {
        margin-bottom: 30px;
        padding: 15px;
        border: 1px solid #ccc;
        border-radius: 5px;
    }
</style>
</head>
<body>

<%-- ==========================================
     【変更】画面全体をセクション分けし、隠しパラメータを追加
     ========================================== --%>
<div class="form-section">
    <h3>画面から個別登録</h3>
    <form action="EmployeeAdd" method="post">
        <%-- 【追加】個別登録であることを示す識別キー --%>
        <input type="hidden" name="mode" value="manual">

        String emp_id;<br> 社員ID<input type="text" name="emp_id"><br>
        社員名<input type="text" name="emp_name"><br>
        ふりがな<input type="text" name="emp_furigana"><br>
        Email<input type="text" name="email"><br>
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
        <input type="submit" value="新規登録"><br>
    </form>
</div>

<%-- ==========================================
     【追加】CSV一括登録用のフォームセクション
     ========================================== --%>
<div class="form-section">
    <h3>CSVファイルから一括登録</h3>
    <form action="EmployeeAdd" method="post" enctype="multipart/form-data">
        <%-- CSV登録であることを示す識別キー --%>
        <input type="hidden" name="mode" value="csv">
        
        <label for="csvFile">CSVファイルを選択してください：</label><br>
        <input type="file" id="csvFile" name="csvFile" accept=".csv"><br><br>
        <input type="submit" value="CSV一括登録を実行">
    </form>
</div>
<%-- ========================================== --%>

<a href="EmployeeInfo">戻る</a><br>

<%-- ==========================================
     【追加】CSV処理の結果メッセージ・詳細エラー表示
     ========================================== --%>
<c:if test="${not empty sMsg}">
    <p style="color: green;"><c:out value="${sMsg}" /></p>
</c:if>
<%-- ========================================== --%>

<c:out value="${eMsg }" /><br>

<%-- ==========================================
     【追加】CSV行別のバリデーションエラー箇所のループ表示
     ========================================== --%>
<c:if test="${not empty errorList}">
    <ul style="color: red;">
        <c:forEach var="err" items="${errorList}">
            <li><c:out value="${err}" /></li>
        </c:forEach>
    </ul>
</c:if>
<%-- ========================================== --%>

</body>
</html>