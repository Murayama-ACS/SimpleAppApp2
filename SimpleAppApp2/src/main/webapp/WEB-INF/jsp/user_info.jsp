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
<c:out value="${eMsg}" /><br>

<table border="1">
<tr>
	<th>社員ID</th><th>名前</th><th>Email</th><th>部署</th><th>役職</th><th>操作</th>
</tr>

<c:forEach var="employee" items="${empList}">
	<tr>
			<td><c:out value="${employee.emp_id}" /></td>
			<td><c:out value="${employee.emp_name }" /></td>
			<td><c:out value="${employee.email }" /></td>
			<td><c:out value="${employee.dpt_id }" /></td>
			<td><c:out value="${employee.pos_id }" /></td>
			<td>
				<form action="EmployeeRemove" method="post">
					<input type="hidden" name="removeEmp_id" value='${employee.emp_id }'>
					<label for="popupFlag${employee.emp_id }">削除</label>
			    	<!--popup-template-->
				    <input type="checkbox" class="popup-flag" id="popupFlag${employee.emp_id }">
				    <label class="popup-background" for="popupFlag${employee.emp_id }"></label>
				    <div class="popup">
				        <label class="close-button" for="popupFlag${employee.emp_id }">×</label>
				        <div class="content">
				            <h3><c:out value="${employee.emp_id  }"/></h3>
				            <h5><c:out value="${employee.emp_name  }"/></h5>
				            <p>上記の社員を削除しますか？</p>
				            <input type="submit" value="削除">
				        </div>
				    </div> 
				</form>	
			</td>
			<td>
				<form action="EmployeeUpdate?action=updateform" method="post">
					<input type="hidden" name="updateEmp_id" value='${employee.emp_id }'>
					<input type="hidden" name="updateEmp_name" value='${employee.emp_name }'>
					<input type="hidden" name="updateEmail" value='${employee.email }'>
					<input type="hidden" name="updateDpt_id" value='${employee.dpt_id }'>
					<input type="hidden" name="updatePos_id" value='${employee.pos_id }'>
					
					<label for="updatePopupFlag${employee.emp_id }">更新</label>
			    	<!--popup-template-->
				    <input type="checkbox" class="popup-flag" id="updatePopupFlag${employee.emp_id }">
				    <label class="popup-background" for="updatePopupFlag${employee.emp_id }"></label>
				    <div class="popup">
				        <label class="close-button" for="updatePopupFlag${employee.emp_id }">×</label>
				        <div class="content">
				            <h3><c:out value="${employee.emp_id  }"/></h3>
				            <h5><c:out value="${employee.emp_name  }"/></h5>
				            <p>上記社員のデータを更新しますか？</p>
				            <input type="submit" value="更新">
				        </div>
				    </div> 
				</form>	
			</td>
	</tr>
</c:forEach>
</table>

<a href="EmployeeAdd">新規登録</a><br>

</body>
</html>