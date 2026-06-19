<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- JSTLタグライブラリの宣言（c:条件分岐やループ、fmt:金額や日付のフォーマット用） --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>申請ステータス変更一覧 - AppApp システム</title>   
    <%-- 外部ライブラリ（SweetAlert2）と、共通ナビ・専用CSSの読み込み --%>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script> 
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/navbar.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/app_list.css">
    <%-- Font Awesome（アイコン）の読み込み --%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
    <%-- 通知機能・メニューを含む共通ヘッダーをインクルード --%>
    <%@ include file="/WEB-INF/jsp/header.jsp" %>

    <div class="container">
        <div class="card">
            <div class="card-header">
                <h2><i class="fa-solid fa-file-invoice-dollar"></i>&nbsp;&nbsp;申請ステータス変更一覧 (最終経理処理)</h2>
            </div>
            
            <div class="card-body">
                <%-- サーブレットから渡されたエラーメッセージがある場合のみ表示 --%>
                <c:if test="${not empty errorMessage}">
                    <div class="error-msg">⚠️ ${errorMessage}</div>
                </c:if>

                <%-- 詳細絞り込み検索フォーム領域 --%>
                <div class="search-form-container" style="background-color: #fafafa; padding: 20px; border-radius: 8px; border: 1px solid #e0e0e0; margin-bottom: 25px; border-left: 4px solid #b71c1c;">
                    <%-- GETメソッドでApplicationStatusサーブレットへ検索条件を送信 --%>
                    <form action="${pageContext.request.contextPath}/ApplicationStatus" method="get">
                        <div style="font-weight: bold; color: #731111; margin-bottom: 10px;">詳細絞り込み検索</div>
                        
                        <div class="search-inputs" style="display: flex; flex-wrap: wrap; gap: 15px; align-items: center;">
                            <%-- 各入力項目は、検索後も入力した値を保持できるように EL式（${q_dept} など）を value に設定 --%>
                            <input type="text" name="q_dept" value="${q_dept}" placeholder="部署名（一部入力可）" style="padding:8px; border:1px solid #ccc; border-radius:4px;">
                            <input type="text" name="q_name" value="${q_name}" placeholder="申請者名（一部入力可）" style="padding:8px; border:1px solid #ccc; border-radius:4px;">
                            
                            <select name="q_type" style="padding:8px; border:1px solid #ccc; border-radius:4px;">
                                <option value="">すべての種別</option>
                                <option value="備品購入申請" ${q_type == '備品購入申請' ? 'selected' : ''}>備品購入申請</option>
                                <option value="研修参加申請" ${q_type == '研修参加申請' ? 'selected' : ''}>研修参加申請</option>
                                <option value="出張申請" ${q_type == '出張申請' ? 'selected' : ''}>出張申請</option>
                                <option value="業務委託・外注費申請" ${q_type == '業務委託・外注費申請' ? 'selected' : ''}>業務委託・外注費申請</option>
                                <option value="その他" ${q_type == 'その他' ? 'selected' : ''}>その他</option>
                            </select>

                            <%-- 金額の範囲検索（最小〜最大） --%>
                            <input type="number" name="q_amount_min" value="${q_amount_min}" placeholder="最小金額 (円)" min="0" style="padding:8px; border:1px solid #ccc; border-radius:4px; width: 120px;">
                            <span style="color:#6c757d;">〜</span>
                            <input type="number" name="q_amount_max" value="${q_amount_max}" placeholder="最大金額 (円)" min="0" style="padding:8px; border:1px solid #ccc; border-radius:4px; width: 120px;">
                            
                            <select name="q_urgent" style="padding:8px; border:1px solid #ccc; border-radius:4px;">
                                <option value="">緊急度（すべて）</option>
                                <option value="緊急" ${q_urgent == '緊急' ? 'selected' : ''}>至急</option>
                                <option value="通常" ${q_urgent == '通常' ? 'selected' : ''}>通常</option>
                            </select>

                            <button type="submit" class="btn-search" style="background-color:#b71c1c; color:white; border:none; padding:8px 20px; border-radius:4px; cursor:pointer; font-weight:bold;">検索</button>
                            <%-- 条件をクリアする場合はパラメータなしで一覧へリンク --%>
                            <a href="${pageContext.request.contextPath}/ApplicationStatus" class="btn-clear" style="color:#b71c1c; text-decoration:none; font-size:14px; font-weight:bold;">条件クリア</a>
                        </div>
                    </form>
                </div>

                <%-- データテーブル表示領域 --%>
                <div class="table-scroll-container">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <%-- 動的ソートヘッダー：クリック時にJS関数を呼び出し。現在ソート中の列は背景色と矢印(▲/▼)を変更 --%>
                                <th class="sortable" onclick="doSort('id')" style="cursor:pointer; ${sort == 'id' ? 'background-color:#f0caca; color:#731111;' : ''}">
                                    申請ID <span style="color:${sort == 'id' ? '#731111' : '#d98282'};">${sort == 'id' ? (dir == 'ASC' ? '▲' : '▼') : '⇅'}</span>
                                </th>
                                <th class="sortable" onclick="doSort('dpt')" style="cursor:pointer; ${sort == 'dpt' ? 'background-color:#f0caca; color:#731111;' : ''}">
                                    部署名 <span style="color:${sort == 'dpt' ? '#731111' : '#d98282'};">${sort == 'dpt' ? (dir == 'ASC' ? '▲' : '▼') : '⇅'}</span>
                                </th>
                                <th class="sortable" onclick="doSort('name')" style="cursor:pointer; ${sort == 'name' ? 'background-color:#f0caca; color:#731111;' : ''}">
                                    申請者 <span style="color:${sort == 'name' ? '#731111' : '#d98282'};">${sort == 'name' ? (dir == 'ASC' ? '▲' : '▼') : '⇅'}</span>
                                </th>
                                <th class="sortable" onclick="doSort('type')" style="cursor:pointer; ${sort == 'type' ? 'background-color:#f0caca; color:#731111;' : ''}">
                                    申請種別 <span style="color:${sort == 'type' ? '#731111' : '#d98282'};">${sort == 'type' ? (dir == 'ASC' ? '▲' : '▼') : '⇅'}</span>
                                </th>
                                <th class="sortable" onclick="doSort('amount')" style="cursor:pointer; ${sort == 'amount' ? 'background-color:#f0caca; color:#731111;' : ''}">
                                    金額 <span style="color:${sort == 'amount' ? '#731111' : '#d98282'};">${sort == 'amount' ? (dir == 'ASC' ? '▲' : '▼') : '⇅'}</span>
                                </th>
                                <th class="sortable" onclick="doSort('urgent')" style="cursor:pointer; ${sort == 'urgent' ? 'background-color:#f0caca; color:#731111;' : ''}">
                                    緊急度 <span style="color:${sort == 'urgent' ? '#731111' : '#d98282'};">${sort == 'urgent' ? (dir == 'ASC' ? '▲' : '▼') : '⇅'}</span>
                                </th>
                                <th class="sortable" onclick="doSort('status')" style="cursor:pointer; ${sort == 'status' ? 'background-color:#f0caca; color:#731111;' : ''}">
                                    現在状態 <span style="color:${sort == 'status' ? '#731111' : '#d98282'};">${sort == 'status' ? (dir == 'ASC' ? '▲' : '▼') : '⇅'}</span>
                                </th>
                                <th class="sortable" onclick="doSort('date')" style="cursor:pointer; ${sort == 'date' ? 'background-color:#f0caca; color:#731111;' : ''}">
                                    申請日時 <span style="color:${sort == 'date' ? '#731111' : '#d98282'};">${sort == 'date' ? (dir == 'ASC' ? '▲' : '▼') : '⇅'}</span>
                                </th>
                                <th style="width: 100px; text-align: center;">操作</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <%-- 経理処理待ちのデータがない場合 --%>
                                <c:when test="${empty applications}">
                                    <tr>
                                        <td colspan="9" style="text-align:center; padding: 40px; color: #6c757d;">現在、処理待ちの申請はありません。</td>
                                    </tr>
                                </c:when>
                                
                                <%-- データが存在する場合のループ --%>
                                <c:otherwise>
                                    <c:forEach var="app" items="${applications}">
                                        <tr>
                                            <td style="font-weight: bold; color: #b71c1c;">${app.apctId}</td>
                                            <td>${app.departmentName}</td>
                                            <td>${app.employeeName}</td>
                                            <td>${app.type}</td>
                                            <%-- 金額をカンマ区切り（小数点なし）で表示 --%>
                                            <td style="font-weight: bold;"><fmt:formatNumber value="${app.amount}" type="currency" currencySymbol="¥" pattern="¥#,##0"/></td>
                                            <td>
                                                <%-- 緊急フラグの判定 --%>
                                                <c:if test="${app.urgent == '緊急' || app.urgent == 'true' || app.urgent == '1'}">
                                                    <span class="badge-urgent">至急</span>
                                                </c:if>
                                                <c:if test="${!(app.urgent == '緊急' || app.urgent == 'true' || app.urgent == '1')}">
                                                    <span style="color:#6c757d; font-size:12px;">通常</span>
                                                </c:if>
                                            </td>
                                            <td><span class="status-badge">${app.statusName}</span></td>
                                            <td>
                                                <%-- 日時フォーマットの変換（yyyy-MM-dd'T'HH:mm -> yyyy/MM/dd HH:mm） --%>
                                                <fmt:parseDate value="${app.createDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" type="both" />
                                                <fmt:formatDate value="${parsedDate}" pattern="yyyy/MM/dd HH:mm" />
                                            </td>
                                            <td style="text-align: center;">
                                                <%-- 完了処理画面（app_status.jsp）へ遷移するためのPOSTフォーム --%>
                                                <form action="${pageContext.request.contextPath}/ApplicationStatus" method="post" style="margin: 0;">
                                                    <input type="hidden" name="apct_id" value="${app.apctId}">
                                                    <button type="submit" class="btn-detail">完了処理</button>
                                                </form>
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
                    <%-- 現在が1ページ目の場合は「前のページ」ボタンを無効化（disabled） --%>
                    <button class="page-btn" onclick="doPage(${page - 1})" ${page <= 1 ? 'disabled' : ''}>◀ 前のページ</button>
                    <span style="font-weight: bold; color: rgb(151, 23, 23);">ページ ${page}</span>
                    <%-- 次のページがない場合（hasNext=false）は「次のページ」ボタンを無効化 --%>
                    <button class="page-btn" onclick="doPage(${page + 1})" ${!hasNext ? 'disabled' : ''}>次のページ ▶</button>
                </div>

                <a href="${pageContext.request.contextPath}/TopPageServlet" class="back-link" style="margin-top: 30px;">⬅ メインメニューに戻る</a>
            </div> 
        </div> 
    </div>

    <%-- フロントエンドのJavaScript処理 --%>
    <script>
        // ソートボタン押下時の処理
        function doSort(key) {
            const currentSort = '${sort}';
            const currentDir = '${dir}';
            let newDir = 'ASC'; // デフォルトは昇順
            
            // 同じ列をクリックした場合は昇順・降順を反転
            if(currentSort === key && currentDir === 'ASC') { newDir = 'DESC'; }
            
            // 現在のURLパラメータにソート条件を追加してリロード
            const url = new URL(window.location.href);
            url.searchParams.set('sort', key);
            url.searchParams.set('dir', newDir);
            url.searchParams.set('page', '1'); // ソート条件変更時は1ページ目に戻す
            window.location.href = url.toString();
        }

        // ページネーション押下時の処理
        function doPage(pageNum) {
            // URLパラメータの page を指定された番号に書き換えてリロード
            const url = new URL(window.location.href);
            url.searchParams.set('page', pageNum);
            window.location.href = url.toString();
        }
    </script>
</body>
</html>