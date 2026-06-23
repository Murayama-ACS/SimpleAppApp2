<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- JSTLタグライブラリの宣言（c:制御構文、fmt:日付/金額フォーマット、fn:文字列操作） --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>申請履歴一覧 - AppApp システム</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/application_history.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/navbar.css">
    <%-- Font Awesome（アイコン）の読み込み --%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script> 
</head>
<body>
    <%-- 通知の未読カウントロジックを含む共通ナビゲーションバーを読み込み --%>
    <%@ include file="/WEB-INF/jsp/header.jsp" %>
    
    <div class="container container-wide"> 
        <div class="application-card">
            
            <div class="card-header">
                <h2><i class="fa-solid fa-file-lines"></i>&nbsp;&nbsp;申請履歴・状況確認</h2>
            </div>
            
            <div class="card-body">
                <%-- エラーメッセージが存在する場合のみ表示 --%>
                <c:if test="${not empty errorMessage}">
                    <div class="error-display" style="background:#f8d7da; color:#721c24; padding:15px; border-radius:4px; margin-bottom:20px;">⚠️ ${errorMessage}</div>
                </c:if>

                <%-- ログインユーザーの役職ID(pos_id)を判定し、画面表示用の役職名(posName)を変数としてセット --%>
                <c:choose>
                    <c:when test="${empBean.pos_id == 'E04'}"><c:set var="posName" value="社長" /></c:when>
                    <c:when test="${empBean.pos_id == 'E03'}"><c:set var="posName" value="本部長" /></c:when>
                    <c:when test="${empBean.pos_id == 'E02'}"><c:set var="posName" value="部長" /></c:when>
                    <c:when test="${empBean.pos_id == 'E01'}"><c:set var="posName" value="課長" /></c:when>
                    <c:otherwise><c:set var="posName" value="一般社員" /></c:otherwise>
                </c:choose>

                <%-- ログイン情報のヘッダー表示領域 --%>
                <div class="info-display">
                    <strong>【 ログイン情報 】</strong><br>
                    社員ID: ${empBean.emp_id} &nbsp;|&nbsp; 
                    氏名: ${empBean.emp_name} &nbsp;|&nbsp; 
                    所属部署: ${not empty dpt_name ? dpt_name : '未所属'} &nbsp;|&nbsp; 
                    役職: <span class="user-position">${posName}</span>
                </div>

                <%-- フィルタリングパネル（権限に応じた表示範囲の切り替えタブ） --%>
                <div class="filter-panel">
                    <%-- 一般社員(E00)以外、または管理部(D100)の場合に表示範囲の切り替えボタンを表示 --%>
                    <c:if test="${empBean.pos_id != 'E00' || empBean.dpt_id == 'D100'}">
                        <div class="filter-row">
                            <div class="filter-label">対象範囲 :</div>
                            <c:set var="statusParam" value="${currentStatusFilter == 'incomplete' ? 'unapproved' : 'all'}" />
                            
                            <%-- 社長(E04)以外は「自身の申請」タブを表示 --%>
                            <c:if test="${empBean.pos_id != 'E04'}">
                                <a href="ApplicationHistoryServlet?scope=self&filter=${statusParam}" class="filter-btn ${currentScope == 'self' ? 'active' : ''}">自身の申請</a>
                            </c:if>
                            
                            <%-- 役職持ち(E00以外)は「配下の申請」タブを表示 --%>
                            <c:if test="${empBean.pos_id != 'E00'}">
                                <a href="ApplicationHistoryServlet?scope=subordinate&filter=${statusParam}" class="filter-btn ${currentScope == 'subordinate' ? 'active' : ''}">配下の申請</a>
                            </c:if>
                            
                            <%-- 管理部(D100)かつ役職持ちの場合は「全社管理」タブを表示 --%>
                            <c:if test="${empBean.dpt_id == 'D100' && empBean.pos_id != 'E00'}">
                                <a href="ApplicationHistoryServlet?scope=management&filter=${statusParam}" class="filter-btn ${currentScope == 'management' ? 'active' : ''}">全社管理</a>
                            </c:if>
                        </div>
                    </c:if>

                    <%-- 状態によるフィルタリング（未完了のみ / 全て表示） --%>
                    <div class="filter-row">
                        <div class="filter-label">状態切替 :</div>
                        <a href="ApplicationHistoryServlet?scope=${currentScope}&filter=unapproved" class="filter-btn ${currentStatusFilter == 'incomplete' ? 'active' : ''}">未完了のみ</a>
                        <a href="ApplicationHistoryServlet?scope=${currentScope}&filter=all" class="filter-btn ${currentStatusFilter == 'all' ? 'active' : ''}">全て表示</a>
                    </div>
                </div>

                <%-- 詳細絞り込み検索フォーム --%>
                <div class="search-form-container">
                    <form action="ApplicationHistoryServlet" method="get">
                        <%-- 現在のタブ状態（scopeとfilter）を維持したまま検索を行うための隠しフィールド --%>
                        <input type="hidden" name="scope" value="${currentScope}">
                        <input type="hidden" name="filter" value="${currentStatusFilter == 'incomplete' ? 'unapproved' : 'all'}">
                        <input type="hidden" name="search" value="true">

                        <div style="font-weight: bold; color: #495057;">詳細絞り込み</div>
                        <div class="search-inputs">
                            <%-- 各項目はEL式（${q_status == '1' ? 'selected' : ''}など）を使って、検索後も入力値を保持する --%>
                            <select name="q_status">
                                <option value="">すべての状態</option>
                                <option value="1" ${q_status == '1' ? 'selected' : ''}>申請中</option>
                                <option value="2" ${q_status == '2' ? 'selected' : ''}>部承認待ち</option>
                                <option value="3" ${q_status == '3' ? 'selected' : ''}>管理部承認</option>
                                <option value="4" ${q_status == '4' ? 'selected' : ''}>社長承認</option>
                                <option value="5" ${q_status == '5' ? 'selected' : ''}>完了</option>
                                <option value="6" ${q_status == '6' ? 'selected' : ''}>却下</option>
                            </select>

                            <select name="q_type">
                                <option value="">すべての種別</option>
                                <option value="備品購入申請" ${q_type == '備品購入申請' ? 'selected' : ''}>備品購入申請</option>
                                <option value="研修参加申請" ${q_type == '研修参加申請' ? 'selected' : ''}>研修参加申請</option>
                                <option value="出張申請" ${q_type == '出張申請' ? 'selected' : ''}>出張申請</option>
                                <option value="業務委託・外注費申請" ${q_type == '業務委託・外注費申請' ? 'selected' : ''}>業務委託・外注費申請</option>
                                <option value="その他" ${q_type == 'その他' ? 'selected' : ''}>その他</option>
                            </select>

                            <%-- 申請者名検索：役職持ち(E00以外)にのみ表示 --%>
                            <c:if test="${empBean.pos_id != 'E00'}">
                                <input type="text" name="q_name" value="${q_name}" placeholder="申請者名（一部入力可）">
                            </c:if>

                            <%-- 部署検索：本部長(E03)と社長(E04)にのみ表示 --%>
                            <c:if test="${empBean.pos_id == 'E03' || empBean.pos_id == 'E04'}">
                                <select name="q_department">
                                    <option value="">すべての部署</option>
                                    <c:forEach var="dpt" items="${dptList}">
                                        <option value="${dpt.dpt_id}" ${q_department == dpt.dpt_id ? 'selected' : ''}>${dpt.dpt_name}</option>
                                    </c:forEach>
                                </select>
                            </c:if>

                            <input type="number" name="q_amount_min" value="${q_amount_min}" placeholder="最小金額 (円)" min="0" style="padding:8px; border:1px solid #ccc; border-radius:4px; width: 120px;">
                            <span style="color:#6c757d;">〜</span>
                            <input type="number" name="q_amount_max" value="${q_amount_max}" placeholder="最大金額 (円)" min="0" style="padding:8px; border:1px solid #ccc; border-radius:4px; width: 120px;">

                            <button type="submit" class="btn-search">検索</button>
                            <a href="ApplicationHistoryServlet?scope=${currentScope}&filter=${currentStatusFilter == 'incomplete' ? 'unapproved' : 'all'}" class="btn-clear">クリア</a>
                        </div>
                    </form>
                </div>

                <%-- データテーブル領域 --%>
                <div class="table-scroll-container">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <%-- 動的ソートヘッダー：現在ソートされている列の背景色と矢印の向きをEL式で変更 --%>
                                <th class="sortable" onclick="doSort('id')" style="cursor:pointer; ${sort == 'id' ? 'background-color:#d0d4f5; color:#1a237e;' : ''}">
                                    申請ID <span style="color:${sort == 'id' ? '#1a237e' : '#a0a0a0'};">${sort == 'id' ? (dir == 'ASC' ? '▲' : '▼') : '⇅'}</span>
                                </th>
                                <th class="sortable" onclick="doSort('name')" style="cursor:pointer; ${sort == 'name' ? 'background-color:#d0d4f5; color:#1a237e;' : ''}">
                                    申請者 <span style="color:${sort == 'name' ? '#1a237e' : '#a0a0a0'};">${sort == 'name' ? (dir == 'ASC' ? '▲' : '▼') : '⇅'}</span>
                                </th>
                                <th class="sortable" onclick="doSort('type')" style="cursor:pointer; ${sort == 'type' ? 'background-color:#d0d4f5; color:#1a237e;' : ''}">
                                    種別 <span style="color:${sort == 'type' ? '#1a237e' : '#a0a0a0'};">${sort == 'type' ? (dir == 'ASC' ? '▲' : '▼') : '⇅'}</span>
                                </th>
                                <th class="sortable" onclick="doSort('amount')" style="cursor:pointer; ${sort == 'amount' ? 'background-color:#d0d4f5; color:#1a237e;' : ''}">
                                    金額 <span style="color:${sort == 'amount' ? '#1a237e' : '#a0a0a0'};">${sort == 'amount' ? (dir == 'ASC' ? '▲' : '▼') : '⇅'}</span>
                                </th>
                                <th class="sortable" onclick="doSort('method')" style="cursor:pointer; ${sort == 'method' ? 'background-color:#d0d4f5; color:#1a237e;' : ''}">
                                    精算 <span style="color:${sort == 'method' ? '#1a237e' : '#a0a0a0'};">${sort == 'method' ? (dir == 'ASC' ? '▲' : '▼') : '⇅'}</span>
                                </th>
                                <th class="sortable" onclick="doSort('urgent')" style="cursor:pointer; ${sort == 'urgent' ? 'background-color:#d0d4f5; color:#1a237e;' : ''}">
                                    緊急度 <span style="color:${sort == 'urgent' ? '#1a237e' : '#a0a0a0'};">${sort == 'urgent' ? (dir == 'ASC' ? '▲' : '▼') : '⇅'}</span>
                                </th>
                                <th class="sortable" onclick="doSort('status')" style="cursor:pointer; ${sort == 'status' ? 'background-color:#d0d4f5; color:#1a237e;' : ''}">
                                    状態 <span style="color:${sort == 'status' ? '#1a237e' : '#a0a0a0'};">${sort == 'status' ? (dir == 'ASC' ? '▲' : '▼') : '⇅'}</span>
                                </th>
                                <th class="sortable" onclick="doSort('date')" style="cursor:pointer; ${sort == 'date' ? 'background-color:#d0d4f5; color:#1a237e;' : ''}">
                                    申請日時 <span style="color:${sort == 'date' ? '#1a237e' : '#a0a0a0'};">${sort == 'date' ? (dir == 'ASC' ? '▲' : '▼') : '⇅'}</span>
                                </th>
                                <th>操作</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <%-- データが0件の場合のプレースホルダー表示 --%>
                                <c:when test="${empty appList}">
                                    <tr><td colspan="9" style="text-align:center; padding: 40px; color: #6c757d;">該当する申請履歴がありません。</td></tr>
                                </c:when>
                                
                                <%-- データが存在する場合のループ処理 --%>
                                <c:otherwise>
                                    <c:forEach var="app" items="${appList}">
                                        <tr>
                                            <td style="font-weight: bold; color: #3f51b5;">${app.apctId}</td>
                                            <td>${app.employeeName}</td>
                                            <td>${app.type}</td>
                                            <%-- 金額フォーマット：少数点以下を切り捨てて3桁区切り（¥1,000） --%>
                                            <td><fmt:formatNumber value="${app.amount}" type="currency" currencySymbol="¥" pattern="¥#,##0"/></td>
                                            <td>${app.paymentMethod}</td>
                                            <td>
                                                <%-- 緊急フラグの判定表示 --%>
                                                <c:if test="${app.urgent == '緊急' || app.urgent == 'true' || app.urgent == '1'}">
                                                    <span class="badge-urgent">至急</span>
                                                </c:if>
                                            </td>
                                            <td>
                                                <%-- ステータスIDに応じてバッジのCSSクラス（赤・緑・グレー）を動的に付与 --%>
                                                <span class="status-badge ${app.status_id == 6 ? 'status-rejected' : (app.status_id == 1 || app.status_id == 2 || app.status_id == 3 || app.status_id == 4 ? 'status-pending' : 'status-approved')}">
                                                    ${app.statusName}
                                                </span>
                                            </td>
                                            <td>
                                                <%-- String型の日付をLocalDateTimeとして解析し、指定フォーマットで出力 --%>
                                                <fmt:parseDate value="${app.createDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" type="both" />
                                                <fmt:formatDate value="${parsedDate}" pattern="yyyy/MM/dd HH:mm" />
                                            </td>
                                            
                                            <td>
                                                <div class="action-flex">
                                                    <%-- 詳細確認ボタン（全員表示） --%>
                                                    <a href="${pageContext.request.contextPath}/ApplicationDetail?apct_id=${app.apctId}" class="btn-sm btn-detail" style="text-decoration: none;">詳細</a>

                                                    <%-- アクション権限の判定フラグを作成 --%>
                                                    <%-- 自分の申請かどうか --%>
                                                    <c:set var="isOwnApp" value="${empBean.emp_id == app.employeeId}" />
                                                    <%-- 修正・削除可能か（ステータスが「申請中(1)」で、かつ自分の申請の場合） --%>
                                                    <c:set var="canEditOrDelete" value="${app.status_id == 1 && isOwnApp}" />
                                                    <%-- 管理部による強制削除権限（全社管理タブ、管理部所属、ステータスが「部承認待ち(2)」の場合） --%>
                                                    <c:set var="isManagementDelete" value="${currentScope == 'management' && empBean.dpt_id == 'D100' && app.status_id !=5 && app.status_id != 6}" />

                                                    <%-- 修正ボタンの表示制御 --%>
                                                    <c:if test="${canEditOrDelete}">
                                                        <form action="${pageContext.request.contextPath}/ApplicationEdit" method="post" style="margin: 0;">
                                                            <input type="hidden" name="apct_id" value="${app.apctId}">
                                                            <input type="hidden" name="isSubmit" value="false">
                                                            <button type="submit" class="btn-sm btn-edit">修正</button>
                                                        </form>
                                                    </c:if>

                                                    <%-- 削除ボタンの表示制御（本人 または 管理部） --%>
                                                    <c:if test="${canEditOrDelete || isManagementDelete}">
                                                        <form action="${pageContext.request.contextPath}/ApplicationDelete" method="post" id="deleteForm_${app.apctId}" style="margin: 0;">
                                                            <input type="hidden" name="apct_id" value="${app.apctId}">
                                                            <%-- javascriptの関数を呼び出し、SweetAlertで確認ポップアップを出す --%>
                                                            <button type="button" class="btn-sm btn-delete" onclick="confirmDelete('${app.apctId}')">削除</button>
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

                <%-- ページネーション --%>
                <div class="pagination">
                    <button class="page-btn" onclick="doPage(${page - 1})" ${page <= 1 ? 'disabled' : ''}>◀ 前のページ</button>
                    <span style="font-weight: bold; color: #495057;">ページ ${page}</span>
                    <button class="page-btn" onclick="doPage(${page + 1})" ${!hasNext ? 'disabled' : ''}>次のページ ▶</button>
                </div>

                <a href="${pageContext.request.contextPath}/TopPageServlet" class="back-link">⬅ メインメニューに戻る</a>
            </div> 
        </div> 
    </div>

    <%--フロントエンドのJavaScript処理 --%>
    <script>
        // テーブルヘッダーのソート処理
		function doSort(key) {
		    const currentSort = '${sort}';
		    const currentDir = '${dir}';
		    let newDir = 'ASC'; 
		    if(currentSort === key && currentDir === 'ASC') { newDir = 'DESC'; }
		    
		    // ソート条件が変わったら1ページ目に戻す
		    buildAndNavigate(key, newDir, 1);
		}

        // ページネーション処理
        function doPage(pageNum) {
		    const currentSort = '${sort}';
		    const currentDir = '${dir}';
		    buildAndNavigate(currentSort, currentDir, pageNum);
		}
       // 【追加】共通のURL構築・遷移関数（すべてのパラメータをかき集める）
        function buildAndNavigate(sortKey, sortDir, pageNum) {
            const url = new URL(window.location.href.split('?')[0]); // ベースURLのみ取得
            
            // 1. タブの状態を取得
            url.searchParams.set('scope', '${currentScope}');
            url.searchParams.set('filter', '${currentStatusFilter == "incomplete" ? "unapproved" : "all"}');
            
            // 2. 検索フォームの入力値を取得
            const qStatus = document.querySelector('select[name="q_status"]').value;
            const qType = document.querySelector('select[name="q_type"]').value;
            const qAmountMin = document.querySelector('input[name="q_amount_min"]').value.trim();
            const qAmountMax = document.querySelector('input[name="q_amount_max"]').value.trim();
            const nameInput = document.querySelector('input[name="q_name"]');
            const deptSelect = document.querySelector('select[name="q_department"]');
            
            let hasSearchData = (qStatus !== "" || qType !== "" || qAmountMin !== "" || qAmountMax !== "");
            if (nameInput && nameInput.value.trim() !== "") hasSearchData = true;
            if (deptSelect && deptSelect.value !== "") hasSearchData = true;
            
            if (hasSearchData) {
                url.searchParams.set('search', 'true');
                url.searchParams.set('q_status', qStatus);
                url.searchParams.set('q_type', qType);
                url.searchParams.set('q_amount_min', qAmountMin);
                url.searchParams.set('q_amount_max', qAmountMax);
                if (nameInput) url.searchParams.set('q_name', nameInput.value.trim());
                if (deptSelect) url.searchParams.set('q_department', deptSelect.value);
            }
            
            // 3. ソートとページング情報をセット
            if (sortKey) url.searchParams.set('sort', sortKey);
            if (sortDir) url.searchParams.set('dir', sortDir);
            url.searchParams.set('page', pageNum);
            
            // 4. 画面遷移
            window.location.href = url.toString();
        }
        // 削除ボタン押下時の確認ポップアップ (SweetAlert2を使用)
        function confirmDelete(apctId) {
            Swal.fire({
                title: '本当に削除しますか？',
                text: "この申請データ（ID: " + apctId + "）を削除します。元に戻すことはできません！",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#dc3545', // 危険な操作のため赤色を指定
                cancelButtonColor: '#6c757d',
                confirmButtonText: '削除する',
                cancelButtonText: 'キャンセル'
            }).then((result) => {
                // ユーザーが「削除」をクリックした場合のみ、該当の隠しフォームを送信する
                if (result.isConfirmed) {
                    document.getElementById('deleteForm_' + apctId).submit();
                }
            });
        }
    </script>
    <c:if test="${param.deleteSuccess == 'true'}">
        <script>
            document.addEventListener('DOMContentLoaded', function() {
                Swal.fire({
                    title: '削除完了',
                    text: '指定された申請データを正常に削除しました。',
                    icon: 'success',
                    showConfirmButton: true,
                    confirmButtonText: '確認する',
                    confirmButtonColor: '#0047A5'
                }).then(() => {
                    //ポップアップを閉じた後、URLから「deleteSuccess=true」を消去
                    const url = new URL(window.location.href);
                    url.searchParams.delete('deleteSuccess');
                    window.history.replaceState({}, '', url.toString());
                });
            });
        </script>
    </c:if>
</body>
</html>