<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- JSTLタグライブラリの宣言（c:条件分岐やループ、fmt:日付や金額のフォーマット） --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>未承認申請一覧 - AppApp システム</title>
    <%-- 共通ナビゲーションCSS、一覧画面専用CSS、およびポップアップ用ライブラリ（SweetAlert2）の読み込み --%>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/navbar.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/approval_list.css">
    <%-- Font Awesome（アイコン）の読み込み --%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script> 
</head>
<body>
    <%-- 通知の未読カウントなどの機能を含む共通ヘッダーをインクルード --%>
    <%@ include file="/WEB-INF/jsp/header.jsp" %>
    
    <div class="container">
        <div class="application-card">
            
            <div class="card-header">
                <h2><i class="fa-solid fa-list-check"></i>&nbsp;&nbsp;未承認申請一覧</h2>
            </div>
            
            <div class="card-body">
                <%-- メッセージ表示エリア --%>
                <%-- エラーメッセージがある場合 --%>
                <c:if test="${not empty errorMessage}">
                    <div class="error-display">⚠️ ${errorMessage}</div>
                </c:if>
                
                <%-- 承認・却下などの処理が成功した後のサクセスメッセージ --%>
                <c:if test="${not empty successMessage}">
                    <div class="success-display">✔ ${successMessage}</div>
                </c:if>
                
                <%-- ログインユーザーの役職判定 --%>
                <%-- 役職ID(pos_id)を元に、画面表示用の役職名(posName)を変数として定義 --%>
                <c:choose>
                    <c:when test="${empBean.pos_id == 'E04'}"><c:set var="posName" value="社長" /></c:when>
                    <c:when test="${empBean.pos_id == 'E03'}"><c:set var="posName" value="本部長" /></c:when>
                    <c:when test="${empBean.pos_id == 'E02'}"><c:set var="posName" value="部長" /></c:when>
                    <c:when test="${empBean.pos_id == 'E01'}"><c:set var="posName" value="課長" /></c:when>
                    <c:otherwise><c:set var="posName" value="一般社員" /></c:otherwise>
                </c:choose>

                <%-- ログイン情報の表示 --%>
                <div class="info-display">
                    <strong>【 ログイン情報 】</strong><br>
                    社員ID: ${empBean.emp_id} &nbsp;|&nbsp; 
                    氏名: ${empBean.emp_name} &nbsp;|&nbsp; 
                    所属部署: ${not empty dpt_name ? dpt_name : '未所属'} &nbsp;|&nbsp; 
                    役職: <span class="user-position">${posName}</span>
                </div>

                <%-- 絞り込み検索フォーム領域 --%>
                <div class="search-form-container">
                    <form action="${pageContext.request.contextPath}/ApplicationWaitList" method="get">
                        <div class="search-title">絞り込み検索</div>
                        <div class="search-inputs">
                            <%-- 各項目は、検索実行後も入力値を保持できるように EL式（${q_dept}等）を value に設定 --%>
                            <input type="text" name="q_dept" value="${q_dept}" placeholder="部署名（一部入力可）">
                            <input type="text" name="q_name" value="${q_name}" placeholder="申請者名（一部入力可）">
                            <input type="number" name="q_amount_min" value="${q_amount_min}" placeholder="最小金額 (円)" min="0" class="search-input-num">
                            <span class="search-separator">〜</span>
                            <input type="number" name="q_amount_max" value="${q_amount_max}" placeholder="最大金額 (円)" min="0" class="search-input-num">

                            <select name="q_urgent">
                                <option value="">緊急度（すべて）</option>
                                <option value="緊急" ${q_urgent == '緊急' ? 'selected' : ''}>至急</option>
                                <option value="通常" ${q_urgent == '通常' ? 'selected' : ''}>通常</option>
                            </select>
                            <button type="submit" class="btn-search">検索</button>
                            <a href="${pageContext.request.contextPath}/ApplicationWaitList" class="btn-clear">クリア</a>
                        </div>
                    </form>
                </div>

                <%-- データテーブル表示領域 --%>
                <div class="table-scroll-container">
                    <table>
                        <thead>
                            <tr>
                                <%-- 動的ソートヘッダー：現在ソート中の列には active-sort クラスを付与 --%>
                                <th class="sortable ${sort == 'date' ? 'active-sort' : ''}" onclick="doSort('date')">
                                    申請日 <span class="sort-arrow ${sort == 'date' ? 'active-arrow' : ''}">${sort == 'date' ? (dir == 'ASC' ? '▲' : '▼') : '⇅'}</span>
                                </th>
                                <th class="sortable ${sort == 'id' ? 'active-sort' : ''}" onclick="doSort('id')">
                                    申請ID <span class="sort-arrow ${sort == 'id' ? 'active-arrow' : ''}">${sort == 'id' ? (dir == 'ASC' ? '▲' : '▼') : '⇅'}</span>
                                </th>
                                <th class="sortable ${sort == 'dept' ? 'active-sort' : ''}" onclick="doSort('dept')">
                                    部署 <span class="sort-arrow ${sort == 'dept' ? 'active-arrow' : ''}">${sort == 'dept' ? (dir == 'ASC' ? '▲' : '▼') : '⇅'}</span>
                                </th>
                                <th class="sortable ${sort == 'name' ? 'active-sort' : ''}" onclick="doSort('name')">
                                    氏名 <span class="sort-arrow ${sort == 'name' ? 'active-arrow' : ''}">${sort == 'name' ? (dir == 'ASC' ? '▲' : '▼') : '⇅'}</span>
                                </th>
                                <th class="sortable ${sort == 'type' ? 'active-sort' : ''}" onclick="doSort('type')">
                                    申請種別 <span class="sort-arrow ${sort == 'type' ? 'active-arrow' : ''}">${sort == 'type' ? (dir == 'ASC' ? '▲' : '▼') : '⇅'}</span>
                                </th>
                                <th class="sortable ${sort == 'amount' ? 'active-sort' : ''}" onclick="doSort('amount')">
                                    金額 <span class="sort-arrow ${sort == 'amount' ? 'active-arrow' : ''}">${sort == 'amount' ? (dir == 'ASC' ? '▲' : '▼') : '⇅'}</span>
                                </th>
                                <th>内容</th>
                                <th class="sortable ${sort == 'urgent' ? 'active-sort' : ''}" onclick="doSort('urgent')">
                                    緊急度 <span class="sort-arrow ${sort == 'urgent' ? 'active-arrow' : ''}">${sort == 'urgent' ? (dir == 'ASC' ? '▲' : '▼') : '⇅'}</span>
                                </th>
                                <th class="col-action-header">操作</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <%-- 自分に回ってきている承認待ちデータがない場合 --%>
                                <c:when test="${empty applications}">
                                    <tr class="empty-row">
                                        <td colspan="9">
                                            現在、あなたに回ってきている未承認の申請はありません。
                                        </td>
                                    </tr>
                                </c:when>
                                
                                <%-- データが存在する場合のループ処理 --%>
                                <c:otherwise>
                                    <c:forEach var="app" items="${applications}">
                                        <tr>
                                            <td>
                                                <%-- 日時フォーマットの変換（yyyy-MM-dd'T'HH:mm -> yyyy/MM/dd HH:mm） --%>
                                                <fmt:parseDate value="${app.createDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" type="both" />
                                                <fmt:formatDate value="${parsedDate}" pattern="yyyy/MM/dd HH:mm" />
                                            </td>
                                            <td class="col-id">${app.apctId}</td>
                                            <td>${app.departmentName}</td>
                                            <td>${app.employeeName}</td>
                                            <td>${app.type}</td>
                                            <%-- 金額をカンマ区切りでフォーマット（小数点なし） --%>
                                            <td><fmt:formatNumber value="${app.amount}" type="currency" currencySymbol="¥" pattern="¥#,##0"/></td>
                                            <td>${app.content}</td>
                                            <td>
                                                <%-- 緊急フラグの判定（表記揺れ対応） --%>
                                                <c:choose>
                                                    <c:when test="${app.urgent == '緊急' || app.urgent == 'true' || app.urgent == '1'}">
                                                        <span class="badge-urgent">至急</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge-normal">通常</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            
                                           <%-- アクションボタン領域 --%>
                                           <td class="action-btns">
                                                <a href="${pageContext.request.contextPath}/ApplicationComment?apct_id=${app.apctId}" class="btn btn-detail">詳細</a>                                    
                                                
                                                <%-- 現在のステータスが「2(部承認待ち)」なら次は「3(管理部承認)」、そうでなければ「2」をセット --%>
                                                <c:set var="nextApproveStatus" value="${app.status_id == 2 ? 3 : 2}" />                                
                                                
                                                <%-- 承認・却下ボタン（クリック時にJSのポップアップ関数を呼び出し） --%>
                                                <button class="btn btn-approve" onclick="openSweetAlert('${app.apctId}', ${nextApproveStatus}, '承認')">承認</button>                                    
                                                <button class="btn btn-reject" onclick="openSweetAlert('${app.apctId}', 6, '却下')">却下</button>
                                           </td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>

                <a href="${pageContext.request.contextPath}/TopPageServlet" class="back-link">⬅ メインメニューに戻る</a>
            </div> 
        </div> 
    </div>

    <%-- 隠しフォーム（Hidden Form） --%>
    <%-- SweetAlert2のポップアップで入力されたコメントとアクション内容を、JS経由でここにセットしてPOST送信する --%>
    <form id="hiddenSubmitForm" action="${pageContext.request.contextPath}/ApplicationWaitList" method="post" class="hidden-form">
        <input type="hidden" name="apct_id" id="hiddenApctId">
        <input type="hidden" name="next_status_id" id="hiddenStatusId">
        <input type="hidden" name="comment" id="hiddenComment">
    </form>

    <%-- フロントエンドのJavaScript処理 --%>
    <script>
        // テーブルヘッダーのソート処理
        function doSort(key) {
            const currentSort = '${sort}';
            const currentDir = '${dir}';
            let newDir = 'ASC'; // デフォルトは昇順
            
            // 同じ列をクリックしたら昇順・降順を反転させる
            if(currentSort === key && currentDir === 'ASC') {
                newDir = 'DESC';
            }
            
            // 統合されたナビゲーション関数を呼び出す
            buildAndNavigate(key, newDir);
        }

        // すべてのパラメータを維持して遷移する関数
        function buildAndNavigate(sortKey, sortDir) {
            const url = new URL(window.location.href.split('?')[0]); // ベースURLのみ取得
            
            // 1. 検索フォームの入力値を取得
            const qDept = document.querySelector('input[name="q_dept"]').value.trim();
            const qName = document.querySelector('input[name="q_name"]').value.trim();
            const qAmountMin = document.querySelector('input[name="q_amount_min"]').value.trim();
            const qAmountMax = document.querySelector('input[name="q_amount_max"]').value.trim();
            const qUrgent = document.querySelector('select[name="q_urgent"]').value;
            
            // 検索条件が1つでも入力されていればパラメータを設定
            if (qDept !== "") url.searchParams.set('q_dept', qDept);
            if (qName !== "") url.searchParams.set('q_name', qName);
            if (qAmountMin !== "") url.searchParams.set('q_amount_min', qAmountMin);
            if (qAmountMax !== "") url.searchParams.set('q_amount_max', qAmountMax);
            if (qUrgent !== "") url.searchParams.set('q_urgent', qUrgent);
            
            // 2. ソート情報をセット
            if (sortKey) url.searchParams.set('sort', sortKey);
            if (sortDir) url.searchParams.set('dir', sortDir);
            
            // 3. 画面遷移
            window.location.href = url.toString();
        }

        // 承認・却下時のコメント入力ポップアップ処理 (SweetAlert2)
        function openSweetAlert(apctId, statusId, actionName) {
            const btnColor = (statusId === 6) ? '#dc3545' : '#28a745'; 
            
            Swal.fire({
                title: '申請を「' + actionName + '」します',
                input: 'textarea',
                inputLabel: 'コメント (任意):',
                inputPlaceholder: '申請者へのコメントやフィードバックを入力してください',
                showCancelButton: true,
                confirmButtonText: actionName + 'を確定',
                cancelButtonText: 'キャンセル',
                confirmButtonColor: btnColor,
                cancelButtonColor: '#6c757d',
                customClass: {
                    input: 'swal-textarea-custom'
                }
            }).then((result) => {
                if (result.isConfirmed) {
                    document.getElementById('hiddenApctId').value = apctId;
                    document.getElementById('hiddenStatusId').value = statusId;
                    document.getElementById('hiddenComment').value = result.value || ""; 
                    document.getElementById('hiddenSubmitForm').submit();
                }
            });
        }
    </script>
    
    <%-- URLに success=true が含まれている場合、完了ポップアップを表示 --%>
    <c:if test="${param.success == 'true'}">
        <script>
            // ページが完全に読み込まれた後にポップアップを実行
            document.addEventListener('DOMContentLoaded', function() {
                // URLからアクション種別（approve または reject）を取得
                const action = '${param.action}';
                
                // アクションに応じてタイトルとアイコンを切り替え
                const titleStr = (action === 'reject') ? '却下完了' : '承認完了';
                const iconType = (action === 'reject') ? 'success' : 'success';
                const msgStr = (action === 'reject') ? '対象の申請を却下しました。' : '対象の申請を承認しました。';
                
                // CSSクラス「swal-comment-notice」を適用（<br><br>も削除してCSSのmarginに任せる）
                const comment = '<div class="swal-comment-notice">※コメントを入力された場合、送信されています。</div>';

                // SweetAlert2 で完了メッセージを表示
                Swal.fire({
                    title: titleStr,
                    html: msgStr + comment,
                    icon: iconType,
                    showConfirmButton: true,         
                    confirmButtonText: '確認する',       
                    confirmButtonColor: '#0047A5'   
                }).then((result) => {
                    const cleanUrl = window.location.protocol + "//" + window.location.host + window.location.pathname;
                    window.history.replaceState({path: cleanUrl}, '', cleanUrl);
                });
            });
        </script>
    </c:if>
</body>
</html>