<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="bean.ApplicationBean" %>
<%@ page import="bean.EmployeeBean" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
    EmployeeBean employee = (EmployeeBean) session.getAttribute("loginEmployee");
    String empName = (employee != null) ? employee.getEmp_name() : "ゲスト";
    String empPos = (employee != null) ? employee.getPos_id() : ""; 
    String empDpt = (employee != null) ? employee.getDpt_id() : "";

    List<ApplicationBean> list = (List<ApplicationBean>) request.getAttribute("applications");
    String currentStatus = (String) request.getAttribute("currentStatus");
    String errorMessage = (String) request.getAttribute("errorMessage");

    // 検索条件の取得
    String searchDept = (String) request.getAttribute("searchDept");
    String searchName = (String) request.getAttribute("searchName");
    String searchAmountMin = (String) request.getAttribute("searchAmountMin"); // Min（以上）
    String searchAmountMax = (String) request.getAttribute("searchAmountMax"); // Max（以下）
    String searchUrgent = (String) request.getAttribute("searchUrgent"); 
    if (searchUrgent == null) searchUrgent = "";

    // ソート条件の取得
    String sortColumn = (String) request.getAttribute("sortColumn");
    String sortOrder = (String) request.getAttribute("sortOrder");
    if (sortColumn == null) sortColumn = "date";
    if (sortOrder == null) sortOrder = "DESC";
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>申請一覧（未承認）</title>
    <style>
        .modal {
            display: none;
            position: fixed;
            z-index: 100;
            left: 0; top: 0; width: 100%; height: 100%;
            background-color: rgba(0,0,0,0.4);
        }
        .modal-content {
            background-color: #fff;
            margin: 15% auto; padding: 20px;
            border: 1px solid #888; width: 400px;
        }
        fieldset {
            border: 1px solid #ccc;
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 4px;
        }
        legend {
            font-weight: bold;
            padding: 0 5px;
        }
        .sort-link {
            text-decoration: none;
            color: #333;
        }
        .sort-link:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <h2>申請一覧画面（ステータスID: <%= currentStatus != null ? currentStatus : "1" %>）</h2>
    <p>ログインユーザー: <%= empName %> さん</p>

    <% if (errorMessage != null) { %>
        <p style="color: red;"><%= errorMessage %></p>
    <% } %>

    <fieldset>
        <legend>検索条件</legend>
        <form action="<%= request.getContextPath() %>/ApplicationWaitList" method="get" id="searchForm">
            <input type="hidden" name="pendingStatus" value="<%= currentStatus != null ? currentStatus : "1" %>">
            <input type="hidden" name="sortColumn" value="<%= sortColumn %>">
            <input type="hidden" name="sortOrder" value="<%= sortOrder %>">

            <label>部署名: </label>
            <input type="text" name="searchDept" value="<%= searchDept != null ? searchDept : "" %>" style="width: 120px;">&nbsp;
            
            <label>氏名: </label>
            <input type="text" name="searchName" value="<%= searchName != null ? searchName : "" %>" style="width: 100px;">&nbsp;
            
            <label>金額範囲: </label>
            <input type="number" name="searchAmountMin" value="<%= searchAmountMin != null ? searchAmountMin : "" %>" style="width: 90px;"> 円以上 〜 
            <input type="number" name="searchAmountMax" value="<%= searchAmountMax != null ? searchAmountMax : "" %>" style="width: 90px;"> 円以下&nbsp;
            
            <label>緊急度: </label>
            <select name="searchUrgent">
                <option value="" <%= "".equals(searchUrgent) ? "selected" : "" %>>すべて</option>
                <option value="通常" <%= "通常".equals(searchUrgent) ? "selected" : "" %>>通常</option>
                <option value="緊急" <%= "緊急".equals(searchUrgent) ? "selected" : "" %>>緊急</option>
            </select>
            &nbsp;&nbsp;
            <button type="submit">検索</button>
            <button type="button" onclick="location.href='<%= request.getContextPath() %>/ApplicationWaitList'">クリア</button>
        </form>
    </fieldset>

    <table border="1" cellpadding="5" cellspacing="0">
        <thead>
            <tr style="background-color: #f2f2f2;">
                <th>
                    <a href="javascript:void(0);" onclick="doSort('date')" class="sort-link">
                        申請日 <%= "date".equals(sortColumn) ? ("ASC".equals(sortOrder) ? "▲" : "▼") : "" %>
                    </a>
                </th>
                <th>
                    <a href="javascript:void(0);" onclick="doSort('id')" class="sort-link">
                        申請ID <%= "id".equals(sortColumn) ? ("ASC".equals(sortOrder) ? "▲" : "▼") : "" %>
                    </a>
                </th>
                <th>
                    <a href="javascript:void(0);" onclick="doSort('dept')" class="sort-link">
                        申請者の部署 <%= "dept".equals(sortColumn) ? ("ASC".equals(sortOrder) ? "▲" : "▼") : "" %>
                    </a>
                </th>
                <th>
                    <a href="javascript:void(0);" onclick="doSort('name')" class="sort-link">
                        申請者の氏名 <%= "name".equals(sortColumn) ? ("ASC".equals(sortOrder) ? "▲" : "▼") : "" %>
                    </a>
                </th>
                <th>
                    <a href="javascript:void(0);" onclick="doSort('type')" class="sort-link">
                        申請種別 <%= "type".equals(sortColumn) ? ("ASC".equals(sortOrder) ? "▲" : "▼") : "" %>
                    </a>
                </th>
                <th>
                    <a href="javascript:void(0);" onclick="doSort('amount')" class="sort-link">
                        申請金額 <%= "amount".equals(sortColumn) ? ("ASC".equals(sortOrder) ? "▲" : "▼") : "" %>
                    </a>
                </th>
                <th>申請内容</th>
                <th>
                    <a href="javascript:void(0);" onclick="doSort('urgent')" class="sort-link">
                        緊急度 <%= "urgent".equals(sortColumn) ? ("ASC".equals(sortOrder) ? "▲" : "▼") : "" %>
                    </a>
                </th>
                <th>操作</th>
            </tr>
        </thead>
        <tbody>
            <% if (list != null && !list.isEmpty()) { %>
                <% for (ApplicationBean app : list) { %>
                    <tr>
                        <td>
                            <% 
                                if (app.getCreateDate() != null) {
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年M月d日 HH:mm:ss");
                                    out.print(app.getCreateDate().format(formatter));
                                } else {
                                    out.print("-");
                                }
                            %>
                        </td>
                        <td><%= app.getApctId() %></td>
                        <td><%= app.getDepartmentName() %></td>
                        <td><%= app.getEmployeeName() %></td>
                        <td><%= app.getType() %></td>
                        <td><%= app.getAmount() %> 円</td>
                        <td><%= app.getContent() %></td>
                        <td><%= app.getUrgent() %></td>
                        <td>
                            <div style="display: flex; gap: 5px;">
                                <form action="<%= request.getContextPath() %>/ApplicationComment" method="post" style="margin:0;">
                                    <input type="hidden" name="apct_id" value="<%= app.getApctId() %>">
                                    <button type="submit">詳細</button>
                                </form>
                                <button type="button" onclick="openApprovalModal('<%= app.getApctId() %>', <%= app.getStatus_id() %>)">承認</button>
                                <button type="button" onclick="openRejectModal('<%= app.getApctId() %>')">却下</button>
                            </div>
                        </td>
                    </tr>
                <% } %>
            <% } else { %>
                <tr>
                    <td colspan="9" style="text-align: center;">対象の申請データが存在しません。</td>
                </tr>
            <% } %>
        </tbody>
    </table>
    
    <div id="actionModal" class="modal">
        <div class="modal-content">
            <h3 id="modalTitle">申請処理</h3>
            <form action="<%= request.getContextPath() %>/ApplicationWaitList" method="post" onsubmit="return handleFormSubmit(this)">
                <input type="hidden" id="modalApctId" name="apct_id">
                <input type="hidden" id="modalNextStatus" name="next_status_id">
                <input type="hidden" name="pendingStatus" value="<%= currentStatus != null ? currentStatus : "1" %>">
                
                <p>コメントを入力してください：</p>
                <textarea name="comment" rows="4" style="width: 100%;" required></textarea>
                <br><br>
                
                <div style="display: flex; justify-content: space-between;">
                    <button type="button" onclick="closeModal()">戻る</button>
                    <button type="submit" id="modalSubmitBtn">確定</button>
                </div>
            </form>
        </div>
    </div>

    <br>
    <a href="<%= request.getContextPath() %>/login_mock.jsp">ログイン画面へ戻る</a>

    <script>
        var modal = document.getElementById("actionModal");
        var modalApctId = document.getElementById("modalApctId");
        var modalNextStatus = document.getElementById("modalNextStatus");
        var modalTitle = document.getElementById("modalTitle");
        var modalSubmitBtn = document.getElementById("modalSubmitBtn");
        
        var currentUserPos = "<%= empPos %>"; 
        var currentUserDpt = "<%= empDpt %>"; 
        var isSubmitting = false;

        function doSort(column) {
            var form = document.getElementById("searchForm");
            var currentColumn = form.sortColumn.value;
            var currentOrder = form.sortOrder.value;
            
            if (currentColumn === column) {
                form.sortOrder.value = (currentOrder === "ASC") ? "DESC" : "ASC";
            } else {
                form.sortColumn.value = column;
                form.sortOrder.value = "DESC"; 
            }
            form.submit();
        }

        function openApprovalModal(apctId, currentStatusId) {
            isSubmitting = false;
            if (modalSubmitBtn) modalSubmitBtn.innerText = "承認する";

            modalApctId.value = apctId;
            
            if (currentUserDpt === "D100") {
                modalNextStatus.value = 3;
            } else if (currentStatusId === 1 && currentUserPos === "E04") {
                modalNextStatus.value = 4; 
            } else {
                modalNextStatus.value = currentStatusId + 1; 
            }
            
            modalTitle.innerText = "申請承認確認";
            modal.style.display = "block";
        }

        function openRejectModal(apctId) {
            isSubmitting = false;
            if (modalSubmitBtn) modalSubmitBtn.innerText = "却下する";

            modalApctId.value = apctId;
            modalNextStatus.value = 6; 
            modalTitle.innerText = "申請却下確認";
            modal.style.display = "block";
        }

        function closeModal() {
            modal.style.display = "none";
        }

        function handleFormSubmit(form) {
            if (isSubmitting) return false; 
            isSubmitting = true; 

            var submitBtn = form.querySelector('button[type="submit"]');
            if (submitBtn) submitBtn.innerText = "処理中..."; 
            return true;
        }

        window.addEventListener('load', function() {
            var urlParams = new URLSearchParams(window.location.search);
            if (urlParams.get('success') === 'true') {
                alert('処理が完了しました。');
                var cleanUrl = window.location.protocol + "//" + window.location.host + window.location.pathname;
                if (urlParams.get('pendingStatus')) {
                    cleanUrl += "?pendingStatus=" + urlParams.get('pendingStatus');
                }
                window.history.replaceState({}, document.title, cleanUrl);
            }
        });
    </script>
</body>
</html>