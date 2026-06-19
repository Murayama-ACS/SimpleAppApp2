<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- JSTLタグライブラリの宣言（c:条件分岐やループなどの制御構文、fmt:フォーマット用） --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>社員管理 - AppApp システム</title>
    <%-- 共通ナビゲーションCSS、社員管理一覧専用CSS、およびポップアップ用ライブラリ（SweetAlert2）の読み込み --%>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/user_info.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/navbar.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <%-- Font Awesome（アイコン）の読み込み --%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
    <%-- 通知の未読カウントなどの機能を含む共通ヘッダーをインクルード --%>
    <%@ include file="/WEB-INF/jsp/header.jsp" %>

    <div class="container container-wide">
        <div class="application-card">
            
            <div class="card-header" style="display: flex; justify-content: space-between; align-items: center;">
                <h2><i class="fa-solid fa-address-card"></i>&nbsp;&nbsp;社員管理・一覧</h2>
                <%-- 新規社員登録画面への遷移ボタン --%>
                <a href="${pageContext.request.contextPath}/EmployeeAdd" class="btn-add-new">＋ 新規社員登録</a>
            </div>
            
            <div class="card-body">
                <%-- サーブレット側でバリデーションエラーなどが発生した場合のメッセージ表示領域 --%>
                <c:if test="${not empty eMsg}">
                    <div class="error-display" style="background:#f8d7da; color:#721c24; padding:15px; border-radius:4px; margin-bottom:20px; font-weight:bold;">⚠️ ${eMsg}</div>
                </c:if>

                <%-- 社員絞り込み検索フォーム領域 --%>
                <div class="search-form-container">
                    <form action="${pageContext.request.contextPath}/EmployeeInfo" method="get" id="searchForm">
                        <input type="hidden" name="search" value="true">
						<input type="hidden" name="page" id="pageInput" value="${page}">
					    <input type="hidden" name="sort" id="sortInput" value="${param.sort}">
					    <input type="hidden" name="dir" id="dirInput" value="${param.dir}">
                        <div style="font-weight: bold; color: #495057;">社員検索</div>
                        <div class="search-inputs">
                            <%-- 各項目は、検索実行後も入力した条件を維持できるように EL式（${q_emp_id}等）を value に設定 --%>
                            <input type="text" name="q_emp_id" value="${q_emp_id}" placeholder="社員ID (前方一致可)" style="width: 150px;">
                            <input type="text" name="q_emp_name" value="${q_emp_name}" placeholder="氏名・ふりがな (一部入力可)" style="width: 200px;">

                            <%-- 部署マスターリスト（dptList）を展開してプルダウンを作成 --%>
                            <select name="q_dpt_id">
                                <option value="">すべての部署</option>
                                <c:forEach var="dpt" items="${dptList}">
                                    <option value="${dpt.dpt_id}" ${q_dpt_id == dpt.dpt_id ? 'selected' : ''}>${dpt.dpt_name}</option>
                                </c:forEach>
                            </select>

                            <%-- 役職マスターリスト（posList）を展開してプルダウンを作成 --%>
                            <select name="q_pos_id">
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
                
                <%-- データテーブル表示領域 --%>
                <div class="table-scroll-container">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <%-- 動的ソートヘッダー：現在ソートの対象になっている列は、背景色(ミントグリーン系)と矢印(▲/▼)を動的に変更 --%>
                                <th class="sortable" onclick="doSort('emp_id')" style="cursor:pointer; ${param.sort == 'emp_id' ? 'background-color:#b2dfdb; color:#00332a;' : ''}">
                                    社員ID <span style="color:${param.sort == 'emp_id' ? '#00332a' : '#8a9c98'};">${param.sort == 'emp_id' ? (param.dir == 'ASC' ? '▲' : '▼') : '⇅'}</span>
                                </th>
                                <th class="sortable" onclick="doSort('emp_name')" style="cursor:pointer; ${param.sort == 'emp_name' ? 'background-color:#b2dfdb; color:#00332a;' : ''}">
                                    氏名 (ふりがな) <span style="color:${param.sort == 'emp_name' ? '#00332a' : '#8a9c98'};">${param.sort == 'emp_name' ? (param.dir == 'ASC' ? '▲' : '▼') : '⇅'}</span>
                                </th>
                                <th class="sortable" onclick="doSort('email')" style="cursor:pointer; ${param.sort == 'email' ? 'background-color:#b2dfdb; color:#00332a;' : ''}">
                                    メールアドレス <span style="color:${param.sort == 'email' ? '#00332a' : '#8a9c98'};">${param.sort == 'email' ? (param.dir == 'ASC' ? '▲' : '▼') : '⇅'}</span>
                                </th>
                                <th class="sortable" onclick="doSort('dpt_id')" style="cursor:pointer; ${param.sort == 'dpt_id' ? 'background-color:#b2dfdb; color:#00332a;' : ''}">
                                    部署 <span style="color:${param.sort == 'dpt_id' ? '#00332a' : '#8a9c98'};">${param.sort == 'dpt_id' ? (param.dir == 'ASC' ? '▲' : '▼') : '⇅'}</span>
                                </th>
                                <th class="sortable" onclick="doSort('pos_id')" style="cursor:pointer; ${param.sort == 'pos_id' ? 'background-color:#b2dfdb; color:#00332a;' : ''}">
                                    役職 <span style="color:${param.sort == 'pos_id' ? '#00332a' : '#8a9c98'};">${param.sort == 'pos_id' ? (param.dir == 'ASC' ? '▲' : '▼') : '⇅'}</span>
                                </th>
                                <th style="width: 120px; text-align: center;">操作</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <%-- 検索結果が0件（またはデータなし）の場合のプレースホルダー --%>
                                <c:when test="${empty empList}">
                                    <tr>
                                        <td colspan="6" class="empty-state" style="text-align: center; padding: 40px; color: #6c757d;">該当する社員が見つかりません。</td>
                                    </tr>
                                </c:when>
                                
                                <%-- データが存在する場合のループ処理 --%>
                                <c:otherwise>
                                    <c:forEach var="emp" items="${empList}">
                                        <tr>
                                            <td style="font-weight: bold; color: #0056b3;">${emp.emp_id}</td>
                                            <td>
                                                ${emp.emp_name} <br>
                                                <%-- ふりがなは少し小さめの文字でグレー表示 --%>
                                                <span style="font-size: 11px; color: #6c757d;">${emp.furigana}</span>
                                            </td>
                                            <td>${emp.email}</td>
                                            <td>${emp.dpt_name}</td>
                                            <td>${emp.pos_name}</td>
                                            
                                            <%-- アクションボタン領域 --%>
                                            <td>
                                                <div class="action-flex">
                                                    <%-- 編集ボタン：対象社員の現在のデータを隠しフィールド(hidden)にセットして更新画面へPOST送信 --%>
                                                    <form action="${pageContext.request.contextPath}/EmployeeUpdate" method="post" style="margin: 0;">
                                                        <input type="hidden" name="action" value="updateform">
                                                        <input type="hidden" name="updateEmp_id" value="${emp.emp_id}">
                                                        <input type="hidden" name="updateEmp_name" value="${emp.emp_name}">
                                                        <input type="hidden" name="updateEmp_furigana" value="${emp.furigana}">
                                                        <input type="hidden" name="updateEmail" value="${emp.email}">
                                                        <input type="hidden" name="updateDpt_id" value="${emp.dpt_id}">
                                                        <input type="hidden" name="updatePos_id" value="${emp.pos_id}">
                                                        <button type="submit" class="btn-sm btn-edit">編集</button>
                                                    </form>
    
                                                    <%-- 削除ボタン：システムエラーを防ぐため、「自分自身（現在ログインしているID）」は削除できないように表示を制御 --%>
                                                    <c:if test="${empBean.emp_id != emp.emp_id}">
                                                        <form action="${pageContext.request.contextPath}/EmployeeRemove" method="post" id="deleteForm_${emp.emp_id}" style="margin: 0;">
                                                            <input type="hidden" name="removeEmp_id" value="${emp.emp_id}">
                                                            <%-- ボタン押下時にJS関数を呼び出し、SweetAlert2で確認ポップアップを出す --%>
                                                            <button type="button" class="btn-sm btn-delete" onclick="confirmDelete('${emp.emp_id}', '${emp.emp_name}')">削除</button>
                                                        </form>
                                                    </c:if>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
               
                <%-- ページネーション機能 --%>
                <div class="pagination">
                    <button class="page-btn" onclick="doPage(${page - 1})" ${page <= 1 ? 'disabled' : ''}>◀ 前のページ</button>
                    <span style="font-weight: bold; color: #495057;">ページ ${page}</span>
                    <button class="page-btn" onclick="doPage(${page + 1})" ${!hasNext ? 'disabled' : ''}>次のページ ▶</button>
                </div>

                <a href="${pageContext.request.contextPath}/TopPageServlet" style="display: block; margin-top: 30px; color: #495057; text-decoration: none; font-weight: bold;">⬅ メインメニューに戻る</a>
            </div> 
        </div> 
    </div>

    <%-- フロントエンドのJavaScript処理 --%>
    <script>
        // テーブルのヘッダーをクリックしたときの動的ソート処理
		function doPage(pageNum) {
		    document.getElementById('pageInput').value = pageNum;
		    document.getElementById('searchForm').submit();
		}
		
		function doSort(key) {
		    const currentSort = document.getElementById('sortInput').value;
		    const currentDir = document.getElementById('dirInput').value;
		    let newDir = 'ASC';
		    
		    if(currentSort === key && currentDir === 'ASC') {
		        newDir = 'DESC';
		    }
		    
		    document.getElementById('sortInput').value = key;
		    document.getElementById('dirInput').value = newDir;
		    document.getElementById('pageInput').value = 1;
		    
		    document.getElementById('searchForm').submit();
		}

        // 削除ボタン押下時の確認ポップアップ (SweetAlert2を使用)
        function confirmDelete(empId, empName) {
            Swal.fire({
                title: '本当に削除しますか？',
                html: "社員 <strong>" + empName + "</strong> (ID: " + empId + ") を削除します。<br>この操作は元に戻せません！",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#dc3545', // 危険な操作であることを示す赤色
                cancelButtonColor: '#6c757d',
                confirmButtonText: '削除する',
                cancelButtonText: 'キャンセル'
            }).then((result) => {
                // ユーザーが「削除する」をクリックした場合
                if (result.isConfirmed) {
                    // 多重クリックによるエラーを防ぐため、通信中はローディングアニメーションを表示
                    Swal.fire({
                        title: '削除中...',
                        allowOutsideClick: false,
                        didOpen: () => { Swal.showLoading(); }
                    });
                    // 対象社員用の隠しフォームをSubmit（送信）する
                    document.getElementById('deleteForm_' + empId).submit();
                }
            });
        }
    </script>
</body>
</html>