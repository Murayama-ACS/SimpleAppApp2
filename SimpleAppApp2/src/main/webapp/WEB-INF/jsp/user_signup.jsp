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
  </style>
</head>
<body>
<form action="EmployeeAdd" method="post">
社員ID<input type="text" name="emp_id"><br>
社員名<input type="text" name="emp_name"><br>
Email<input type="text" name="email"><br>
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
<input type="submit" value="新規登録"><br>
</form>
<a href="EmployeeInfo">戻る</a><br>
<c:out value="${eMsg }" /><br>
</body>
</html>