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

<!-- 検索フォーム -->
<form action="${pageContext.request.contextPath}/EmployeeInfo" method="get">
  社員ID:
  <input type="text" name="q_emp_id" value="<c:out value='${param.q_emp_id != null ? param.q_emp_id : q_emp_id}'/>" />

  名前:
  <input type="text" name="q_emp_name" value="<c:out value='${param.q_emp_name != null ? param.q_emp_name : q_emp_name}'/>" />

  部署:
  <select name="q_dpt_id">
    <option value=""><c:out value="--指定なし--" /></option>
  	<c:forEach var="d" items="${dptList}">
 		<option value="${d.dpt_id}" <c:if test="${q_dpt_id != null && q_dpt_id eq d.dpt_id}">selected</c:if>>
    	<c:out value="${d.dpt_name}" />
  		</option>
	</c:forEach>
  </select>

  役職:  
  <select name="q_pos_id">
    <option value=""><c:out value="--指定なし--" /></option>
    <c:forEach var="p" items="${posList}">
 		<option value="${p.pos_id}" <c:if test="${q_pos_id != null && q_pos_id eq p.pos_id}">selected</c:if>>
    	<c:out value="${p.pos_name}" />
  		</option>
	</c:forEach>
  </select>

  <!-- 検索ボタン（押したら page をリセットする処理はサーブレット側で search パラメータを見て行ってください） -->
  <button type="submit" name="search" value="1">検索</button>

  <!-- 現在のソート・ページ情報を維持する hidden -->
  <input type="hidden" name="sort" value="<c:out value='${sort}'/>" />
  <input type="hidden" name="dir"  value="<c:out value='${dir}'/>" />
  <input type="hidden" name="page" value="<c:out value='${page}'/>" />
</form>

<!-- ここから既存のテーブル表示（省略せずにそのまま置いてください） -->

<table border="1">
<tr>
<%-- テーブルヘッダ（ソートリンク） --%>

	<c:url var="urlSortEmpId" value="/EmployeeInfo">
	  <c:param name="sort" value="emp_id"/>
	  <c:param name="dir" value="${sort == 'emp_id' && dir == 'asc' ? 'desc' : 'asc'}"/>
	  <c:param name="page" value="1"/> <!-- ソート時は先頭ページへ戻す -->
	  <c:param name="q_emp_id" value="${param.q_emp_id != null ? param.q_emp_id : q_emp_id}"/>
	  <c:param name="q_emp_name" value="${param.q_emp_name != null ? param.q_emp_name : q_emp_name}"/>
	  <c:param name="q_dpt_id" value="${param.q_dpt_id != null ? param.q_dpt_id : q_dpt_id}"/>
	  <c:param name="q_pos_id" value="${param.q_pos_id != null ? param.q_pos_id : q_pos_id}"/>
	</c:url>
	<th>
	  <a href="${urlSortEmpId}">社員ID
	    <c:if test="${sort == 'emp_id'}">
	      <c:out value="${dir == 'asc' ? ' ▲' : ' ▼'}" />
	    </c:if>
	  </a>
	</th>
	
	<c:url var="urlSortName" value="/EmployeeInfo">
	  <c:param name="sort" value="emp_name"/>
	  <c:param name="dir" value="${sort == 'emp_name' && dir == 'asc' ? 'desc' : 'asc'}"/>
	  <c:param name="page" value="1"/>
	  <c:param name="q_emp_id" value="${param.q_emp_id != null ? param.q_emp_id : q_emp_id}"/>
	  <c:param name="q_emp_name" value="${param.q_emp_name != null ? param.q_emp_name : q_emp_name}"/>
	  <c:param name="q_dpt_id" value="${param.q_dpt_id != null ? param.q_dpt_id : q_dpt_id}"/>
	  <c:param name="q_pos_id" value="${param.q_pos_id != null ? param.q_pos_id : q_pos_id}"/>
	</c:url>
	<th>
	  <a href="${urlSortName}">名前
	    <c:if test="${sort == 'emp_name'}">
	      <c:out value="${dir == 'asc' ? ' ▲' : ' ▼'}" />
	    </c:if>
	  </a>
	</th>
	
	<c:url var="urlSortEmail" value="/EmployeeInfo">
	  <c:param name="sort" value="email"/>
	  <c:param name="dir" value="${sort == 'email' && dir == 'asc' ? 'desc' : 'asc'}"/>
	  <c:param name="page" value="1"/>
	  <c:param name="q_emp_id" value="${param.q_emp_id != null ? param.q_emp_id : q_emp_id}"/>
	  <c:param name="q_emp_name" value="${param.q_emp_name != null ? param.q_emp_name : q_emp_name}"/>
	  <c:param name="q_dpt_id" value="${param.q_dpt_id != null ? param.q_dpt_id : q_dpt_id}"/>
	  <c:param name="q_pos_id" value="${param.q_pos_id != null ? param.q_pos_id : q_pos_id}"/>
	</c:url>
	<th>
	  <a href="${urlSortEmail}">Email
	    <c:if test="${sort == 'email'}">
	      <c:out value="${dir == 'asc' ? ' ▲' : ' ▼'}" />
	    </c:if>
	  </a>
	</th>
	
	<c:url var="urlSortDpt" value="/EmployeeInfo">
	  <c:param name="sort" value="dpt_id"/>
	  <c:param name="dir" value="${sort == 'dpt_id' && dir == 'asc' ? 'desc' : 'asc'}"/>
	  <c:param name="page" value="1"/>
	  <c:param name="q_emp_id" value="${param.q_emp_id != null ? param.q_emp_id : q_emp_id}"/>
	  <c:param name="q_emp_name" value="${param.q_emp_name != null ? param.q_emp_name : q_emp_name}"/>
	  <c:param name="q_dpt_id" value="${param.q_dpt_id != null ? param.q_dpt_id : q_dpt_id}"/>
	  <c:param name="q_pos_id" value="${param.q_pos_id != null ? param.q_pos_id : q_pos_id}"/>
	</c:url>
	<th>
	  <a href="${urlSortDpt}">部署
	    <c:if test="${sort == 'dpt_id'}">
	      <c:out value="${dir == 'asc' ? ' ▲' : ' ▼'}" />
	    </c:if>
	  </a>
	</th>
	
	<c:url var="urlSortPos" value="/EmployeeInfo">
	  <c:param name="sort" value="pos_id"/>
	  <c:param name="dir" value="${sort == 'pos_id' && dir == 'asc' ? 'desc' : 'asc'}"/>
	  <c:param name="page" value="1"/>
	  <c:param name="q_emp_id" value="${param.q_emp_id != null ? param.q_emp_id : q_emp_id}"/>
	  <c:param name="q_emp_name" value="${param.q_emp_name != null ? param.q_emp_name : q_emp_name}"/>
	  <c:param name="q_dpt_id" value="${param.q_dpt_id != null ? param.q_dpt_id : q_dpt_id}"/>
	  <c:param name="q_pos_id" value="${param.q_pos_id != null ? param.q_pos_id : q_pos_id}"/>
	</c:url>
	<th>
	  <a href="${urlSortPos}">役職
	    <c:if test="${sort == 'pos_id'}">
	      <c:out value="${dir == 'asc' ? ' ▲' : ' ▼'}" />
	    </c:if>
	  </a>
	</th>
</tr>

<c:forEach var="employee" items="${empList}">
  <tr>
    <td><c:out value="${employee.emp_id}" /></td>
    <td><c:out value="${employee.emp_name}" /></td>
    <td><c:out value="${employee.email}" /></td>
    <td><c:out value="${employee.dpt_name}" /></td>
	<td><c:out value="${employee.pos_name}" /></td>

    <!-- 削除 -->
    <td>
      <form action="${pageContext.request.contextPath}/EmployeeRemove" method="post">
        <input type="hidden" name="removeEmp_id" value="<c:out value='${employee.emp_id}'/>" />
        <label for="popupFlag${employee.emp_id}">削除</label>
        <input type="checkbox" class="popup-flag" id="popupFlag${employee.emp_id}" />
        <label class="popup-background" for="popupFlag${employee.emp_id}"></label>
        <div class="popup">
          <label class="close-button" for="popupFlag${employee.emp_id}">×</label>
          <div class="content">
            <h3><c:out value="${employee.emp_id}" /></h3>
            <h5><c:out value="${employee.emp_name}" /></h5>
            <p>上記の社員を削除しますか？</p>
            <input type="submit" value="削除" />
          </div>
        </div>
      </form>
    </td>

    <!-- 更新 -->
    <td>
      <form action="${pageContext.request.contextPath}/EmployeeUpdate?action=updateform" method="post">
        <input type="hidden" name="updateEmp_id" value="<c:out value='${employee.emp_id}'/>" />
        <input type="hidden" name="updateEmp_name" value="<c:out value='${employee.emp_name}'/>" />
        <input type="hidden" name="updateEmp_furigana" value="<c:out value='${employee.emp_furigana}'/>" />
        <input type="hidden" name="updateEmail" value="<c:out value='${employee.email}'/>" />
        <input type="hidden" name="updateDpt_id" value="<c:out value='${employee.dpt_id}'/>" />
        <input type="hidden" name="updatePos_id" value="<c:out value='${employee.pos_id}'/>" />
        <label for="updatePopupFlag${employee.emp_id}">更新</label>
        <input type="checkbox" class="popup-flag" id="updatePopupFlag${employee.emp_id}" />
        <label class="popup-background" for="updatePopupFlag${employee.emp_id}"></label>
        <div class="popup">
          <label class="close-button" for="updatePopupFlag${employee.emp_id}">×</label>
          <div class="content">
            <h3><c:out value="${employee.emp_id}" /></h3>
            <h5><c:out value="${employee.emp_name}" /></h5>
            <p>上記社員のデータを更新しますか？</p>
            <input type="submit" value="更新" />
          </div>
        </div>
      </form>
    </td>
  </tr>
</c:forEach>
</table>

<!-- ページング（前へ／次へ） -->

<c:url var="urlPrev" value="EmployeeInfo">
  <c:param name="sort" value="${sort}" />
  <c:param name="dir" value="${dir}" />
  <c:param name="page" value="${page - 1}" />
  <c:param name="q_emp_id" value="${q_emp_id}" />
  <c:param name="q_emp_name" value="${q_emp_name}" />
  <c:param name="q_dpt_id" value="${q_dpt_id}" />
  <c:param name="q_pos_id" value="${q_pos_id}" />
</c:url>

<c:choose>
  <c:when test="${page > 1}">
    <a href="${urlPrev}">前へ</a>
  </c:when>
  <c:otherwise>前へ</c:otherwise>
</c:choose>

&nbsp;|&nbsp;

<c:choose>
  <c:when test="${hasNext}">
    <c:url var="urlNext" value="EmployeeInfo">
      <c:param name="sort" value="${sort}" />
      <c:param name="dir" value="${dir}" />
      <c:param name="page" value="${page + 1}" />
      <c:param name="q_emp_id" value="${q_emp_id}" />
      <c:param name="q_emp_name" value="${q_emp_name}" />
      <c:param name="q_dpt_id" value="${q_dpt_id}" />
      <c:param name="q_pos_id" value="${q_pos_id}" />
    </c:url>
    <a href="${urlNext}">次へ</a>
  </c:when>
  <c:otherwise>次へ</c:otherwise>
</c:choose>

<br>
<a href="EmployeeAdd">新規登録</a><br>
</body>
</html>