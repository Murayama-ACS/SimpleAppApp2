<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>社員管理 - AppApp システム</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/navbar.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/user_info.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
    <%@ include file="/WEB-INF/jsp/header.jsp" %>

    <div class="container container-wide">
        <div class="application-card">
            
            <div class="card-header">
                <h2><i class="fa-solid fa-address-card"></i>&nbsp;&nbsp;社員管理・一覧</h2>
                <a href="${pageContext.request.contextPath}/EmployeeAdd" class="btn-add-new">＋ 新規社員登録</a>
            </div>
            
            <div class="card-body">
                <c:if test="${not empty eMsg}">
                    <div class="error-display">⚠️ ${eMsg}</div>
                </c:if>

                <div class="search-form-container">
                    <form action="${pageContext.request.contextPath}/EmployeeInfo" method="get" id="searchForm">
                        <input type="hidden" name="search" value="true">
                        <input type="hidden" name="page" id="pageInput" value="${page}">
                        <input type="hidden" name="sort" id="sortInput" value="${param.sort}">
                        <input type="hidden" name="dir" id="dirInput" value="${param.dir}">
                        
                        <div class="search-title">社員検索</div>
                        <div class="search-inputs">
                            <input type="text" name="q_emp_id" value="${q_emp_id}" placeholder="社員ID (前方一致可)" class="search-input-text">
                            <input type="text" name="q_emp_name" value="${q_emp_name}" placeholder="氏名・ふりがな" class="search-input-text">

                            <select name="q_dpt_id" class="search-select">
                                <option value="">すべての部署</option>
                                <c:forEach var="dpt" items="${dptList}">
                                    <option value="${dpt.dpt_id}" ${q_dpt_id == dpt.dpt_id ? 'selected' : ''}>${dpt.dpt_name}</option>
                                </c:forEach>
                            </select>

                            <select name="q_pos_id" class="search-select">
                                <option value="">すべての役職</option>
                                <c:forEach var="pos" items="${posList}">
                                    <option value="${pos.pos_id}" ${q_pos_id == pos.pos_id ? 'selected' : ''}>${pos.pos_name}</option>
                                </c:forEach>
                            </select>

                            <button type="submit" class="btn-search">検索</button>
                            <a href="${pageContext.request.contextPath}/EmployeeInfo" class="btn-clear">クリア</a>
                        </div>
                    </form>
                </div>
                
                <div class="table-scroll-container">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th class="sortable ${param.sort == 'emp_id' ? 'active-sort' : ''}" onclick="doSort('emp_id')">
                                    社員ID <span class="sort-icon">${param.sort == 'emp_id' ? (param.dir == 'ASC' ? '▲' : '▼') : '⇅'}</span>
                                </th>
                                <th class="sortable ${param.sort == 'emp_name' ? 'active-sort' : ''}" onclick="doSort('emp_name')">
                                    氏名 (ふりがな) <span class="sort-icon">${param.sort == 'emp_name' ? (param.dir == 'ASC' ? '▲' : '▼') : '⇅'}</span>
                                </th>
                                <th class="sortable ${param.sort == 'email' ? 'active-sort' : ''}" onclick="doSort('email')">
                                    メールアドレス <span class="sort-icon">${param.sort == 'email' ? (param.dir == 'ASC' ? '▲' : '▼') : '⇅'}</span>
                                </th>
                                <th class="sortable ${param.sort == 'dpt_id' ? 'active-sort' : ''}" onclick="doSort('dpt_id')">
                                    部署 <span class="sort-icon">${param.sort == 'dpt_id' ? (param.dir == 'ASC' ? '▲' : '▼') : '⇅'}</span>
                                </th>
                                <th class="sortable ${param.sort == 'pos_id' ? 'active-sort' : ''}" onclick="doSort('pos_id')">
                                    役職 <span class="sort-icon">${param.sort == 'pos_id' ? (param.dir == 'ASC' ? '▲' : '▼') : '⇅'}</span>
                                </th>
                                <th class="col-action">操作</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty empList}">
                                    <tr><td colspan="6" class="empty-state">該当する社員が見つかりません。</td></tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="emp" items="${empList}">
                                        <tr>
                                            <td class="col-emp-id">${emp.emp_id}</td>
                                            <td>
                                                <div class="emp-name">${emp.emp_name}</div>
                                                <div class="emp-furigana">${emp.furigana}</div>
                                            </td>
                                            <td>${emp.email}</td>
                                            <td>${emp.dpt_name}</td>
                                            <td>${emp.pos_name}</td>
                                            <td class="action-flex">
                                                <form action="${pageContext.request.contextPath}/EmployeeUpdate" method="post" class="inline-form">
                                                    <input type="hidden" name="action" value="updateform">
                                                    <input type="hidden" name="updateEmp_id" value="${emp.emp_id}">
                                                    <input type="hidden" name="updateEmp_name" value="${emp.emp_name}">
                                                    <input type="hidden" name="updateEmp_furigana" value="${emp.furigana}">
                                                    <input type="hidden" name="updateEmail" value="${emp.email}">
                                                    <input type="hidden" name="updateDpt_id" value="${emp.dpt_id}">
                                                    <input type="hidden" name="updatePos_id" value="${emp.pos_id}">
                                                    <button type="submit" class="btn-sm btn-edit">編集</button>
                                                </form>
    
                                                <c:if test="${empBean.emp_id != emp.emp_id}">
                                                    <form action="${pageContext.request.contextPath}/EmployeeRemove" method="post" id="deleteForm_${emp.emp_id}" class="inline-form">
                                                        <input type="hidden" name="removeEmp_id" value="${emp.emp_id}">
                                                        <button type="button" class="btn-sm btn-delete" onclick="confirmDelete('${emp.emp_id}', '${emp.emp_name}')">削除</button>
                                                    </form>
                                                </c:if>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
               
                <div class="pagination">
                    <button class="page-btn" onclick="doPage(${page - 1})" ${page <= 1 ? 'disabled' : ''}>◀ 前のページ</button>
                    <span class="page-count">ページ ${page}</span>
                    <button class="page-btn" onclick="doPage(${page + 1})" ${!hasNext ? 'disabled' : ''}>次のページ ▶</button>
                </div>

                <a href="${pageContext.request.contextPath}/TopPageServlet" class="back-link">⬅ メインメニューに戻る</a>
            </div> 
        </div> 
    </div>

    <script>
        function doPage(pageNum) {
            document.getElementById('pageInput').value = pageNum;
            document.getElementById('searchForm').submit();
        }
        
        function doSort(key) {
            const currentSort = document.getElementById('sortInput').value;
            const currentDir = document.getElementById('dirInput').value;
            let newDir = 'ASC';
            if(currentSort === key && currentDir === 'ASC') newDir = 'DESC';
            
            document.getElementById('sortInput').value = key;
            document.getElementById('dirInput').value = newDir;
            document.getElementById('pageInput').value = 1;
            document.getElementById('searchForm').submit();
        }

        function confirmDelete(empId, empName) {
            Swal.fire({
                title: '本当に削除しますか？',
                html: "社員 <strong>" + empName + "</strong> (ID: " + empId + ") を削除します。<br>この操作は元に戻せません！",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#dc3545',
                cancelButtonColor: '#6c757d',
                confirmButtonText: '削除する',
                cancelButtonText: 'キャンセル'
            }).then((result) => {
                if (result.isConfirmed) {
                    Swal.fire({ title: '削除中...', allowOutsideClick: false, didOpen: () => { Swal.showLoading(); } });
                    document.getElementById('deleteForm_' + empId).submit();
                }
            });
        }
    </script>
</body>
</html>