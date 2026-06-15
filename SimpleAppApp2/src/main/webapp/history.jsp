<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page import="java.util.List" %>
<%@ page import="bean.ApplicationBean" %>
<%@ page import="bean.EmployeeBean" %>
<%
    // セッションからログインユーザー情報を取得
    EmployeeBean employee = (EmployeeBean) session.getAttribute("loginEmployee");
    String empId = (employee != null) ? employee.getEmp_id() : "";
    String dptId = (employee != null) ? employee.getDpt_id() : "";
    String posId = (employee != null) ? employee.getPos_id() : "";

    // サーブレットから渡された各種属性を取得
    List<ApplicationBean> historyList = (List<ApplicationBean>) request.getAttribute("appList");
    String dptName = (String) request.getAttribute("dpt_name");
    String currentScope = (String) request.getAttribute("currentScope");
    String currentStatusFilter = (String) request.getAttribute("currentStatusFilter");

    // デフォルト値の安全な補填
    if (currentScope == null) currentScope = "self";
    if (currentStatusFilter == null) currentStatusFilter = "incomplete";

    // 操作列（詳細・修正・削除）は、レイアウト崩れを防ぐため常に表示
    boolean showActionColumn = true;
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>申請履歴一覧</title>
<style>
    body { font-family: sans-serif; margin: 20px; }
    .header-info { margin-bottom: 20px; padding: 10px; background-color: #f0f0f0; }
    .search-box { background-color: #fcfcfc; border: 1px solid #ddd; padding: 15px; margin-bottom: 15px; border-radius: 4px; }
    .tab-group { margin-bottom: 15px; }
    .tab { display: inline-block; padding: 10px 20px; margin-right: 5px; background-color: #e0e0e0; text-decoration: none; color: #333; border-radius: 4px; }
    .tab.active { background-color: #007bff; color: white; font-weight: bold; }
    table { width: 100%; border-collapse: collapse; margin-top: 10px; }
    th, td { border: 1px solid #ccc; padding: 10px; text-align: left; }
    th { background-color: #f5f5f5; }
    th a { text-decoration: none; color: #000; display: block; width: 100%; }
    .btn { padding: 5px 10px; border: none; border-radius: 3px; cursor: pointer; text-decoration: none; color: white; font-size: 13px; }
    .btn-detail { background-color: #007bff; margin-right: 5px; }
    .btn-edit { background-color: #28a745; margin-right: 5px; }
    .btn-delete { background-color: #dc3545; }
    .paging-group { margin-top: 15px; font-size: 14px; }
</style>
<script type="text/javascript">
    function confirmDelete() { 
        return confirm("この申請を削除してもよろしいですか？"); 
    }
</script>
</head>
<body>

    <h2>申請履歴一覧</h2>

    <div class="header-info">
        ログイン社員: <%= (employee != null) ? employee.getEmp_name() : "" %> 
        (部署: <%= dptName != null ? dptName : "未設定" %> / 
         役職: <% 
            if("E04".equals(posId)) out.print("社長");
            else if("E03".equals(posId)) out.print("本部長");
            else if("E02".equals(posId)) out.print("部長");
            else if("E01".equals(posId)) out.print("課長");
            else out.print("一般社員");
         %>)
    </div>

    <div class="search-box">
        <form action="${pageContext.request.contextPath}/ApplicationHistoryServlet" method="get">
            <input type="hidden" name="scope" value="<c:out value='${currentScope}'/>" />
            <input type="hidden" name="filter" value="<c:out value='${currentStatusFilter == \"incomplete\" ? \"unapproved\" : \"all\"}'/>" />
            
            申請状況:
            <select name="q_status">
                <option value="">--指定なし--</option>
                <option value="1" <c:if test="${q_status eq '1'}">selected</c:if>>未承認</option>
                <option value="2" <c:if test="${q_status eq '2'}">selected</c:if>>上長承認</option>
                <option value="3" <c:if test="${q_status eq '3'}">selected</c:if>>管理部承認</option>
                <option value="4" <c:if test="${q_status eq '4'}">selected</c:if>>社長承認</option>
                <option value="5" <c:if test="${q_status eq '5'}">selected</c:if>>完了</option>
                <option value="6" <c:if test="${q_status eq '6'}">selected</c:if>>却下</option>
            </select>

            <c:if test="${empBean.pos_id ne 'E00'}">
                &nbsp;&nbsp;名前:
                <input type="text" name="q_name" value="<c:out value='${q_name}'/>" style="width:100px;" />
            </c:if>

            <c:if test="${empBean.pos_id eq 'E03' || empBean.pos_id eq 'E04'}">
                &nbsp;&nbsp;部門:
                <select name="q_department">
                    <option value="">--指定なし--</option>
                    <c:forEach var="d" items="${dptList}">
                        <option value="${d.dpt_id}" <c:if test="${q_department eq d.dpt_id}">selected</c:if>>
                            <c:out value="${d.dpt_name}" />
                        </option>
                    </c:forEach>
                </select>
            </c:if>

            &nbsp;&nbsp;種別:
            <select name="q_type">
                <option value="">--指定なし--</option>
                <option value="経費精算" <c:if test="${q_type eq '経費精算'}">selected</c:if>>経費精算</option>
                <option value="旅費精算" <c:if test="${q_type eq '旅費精算'}">selected</c:if>>旅費精算</option>
            </select>

            &nbsp;&nbsp;金額（円以上）:
            <input type="text" name="q_amount" value="<c:out value='${q_amount}'/>" style="width:80px;" />

            &nbsp;&nbsp;
            <button type="submit" name="search" value="1">検索</button>

            <input type="hidden" name="sort" value="<c:out value='${sort}'/>" />
            <input type="hidden" name="dir"  value="<c:out value='${dir}'/>" />
            <input type="hidden" name="page" value="<c:out value='${page}'/>" />
        </form>
    </div>

    <% if (!"E00".equals(posId) || "D100".equals(dptId)) { %>
    <div class="tab-group">
        <a href="ApplicationHistoryServlet?scope=self&filter=<%= "incomplete".equals(currentStatusFilter) ? "unapproved" : "all" %>" 
           class="tab <%= "self".equals(currentScope) ? "active" : "" %>">自身</a>
        
        <% if (!"E00".equals(posId)) { %>
            <a href="ApplicationHistoryServlet?scope=subordinate&filter=<%= "incomplete".equals(currentStatusFilter) ? "unapproved" : "all" %>" 
               class="tab <%= "subordinate".equals(currentScope) ? "active" : "" %>">配下</a>
        <% } %>
        
        <% if ("D100".equals(dptId)) { %>
            <a href="ApplicationHistoryServlet?scope=management&filter=<%= "incomplete".equals(currentStatusFilter) ? "unapproved" : "all" %>" 
               class="tab <%= "management".equals(currentScope) ? "active" : "" %>">管理</a>
        <% } %>
    </div>
    <% } %>

    <div class="tab-group">
        <a href="ApplicationHistoryServlet?scope=<%= currentScope %>&filter=unapproved" 
           class="tab <%= "incomplete".equals(currentStatusFilter) ? "active" : "" %>">未完了</a>
        <a href="ApplicationHistoryServlet?scope=<%= currentScope %>&filter=all" 
           class="tab <%= "all".equals(currentStatusFilter) ? "active" : "" %>">全て表示</a>
    </div>

    <table>
        <thead>
            <tr>
                <th>申請ID</th>
                
                <c:url var="sortName" value="/ApplicationHistoryServlet">
                    <c:param name="scope" value="${currentScope}"/>
                    <c:param name="filter" value="${currentStatusFilter == 'incomplete' ? 'unapproved' : 'all'}"/>
                    <c:param name="sort" value="name"/>
                    <c:param name="dir" value="${sort == 'name' && dir == 'asc' ? 'desc' : 'asc'}"/>
                    <c:param name="page" value="1"/>
                    <c:param name="q_status" value="${q_status}"/><c:param name="q_name" value="${q_name}"/><c:param name="q_department" value="${q_department}"/><c:param name="q_type" value="${q_type}"/><c:param name="q_amount" value="${q_amount}"/>
                </c:url>
                <th><a href="${sortName}">申請者名<c:if test="${sort == 'name'}"><c:out value="${dir == 'asc' ? ' ▲' : ' ▼'}" /></c:if></a></th>
                
                <th>部門</th>
                <th>申請種別</th>
                <th>支払方法</th>
                <th>金額</th>
                <th>申請内容</th>
                
                <c:url var="sortStatus" value="/ApplicationHistoryServlet">
                    <c:param name="scope" value="${currentScope}"/>
                    <c:param name="filter" value="${currentStatusFilter == 'incomplete' ? 'unapproved' : 'all'}"/>
                    <c:param name="sort" value="status"/>
                    <c:param name="dir" value="${sort == 'status' && dir == 'asc' ? 'desc' : 'asc'}"/>
                    <c:param name="page" value="1"/>
                    <c:param name="q_status" value="${q_status}"/><c:param name="q_name" value="${q_name}"/><c:param name="q_department" value="${q_department}"/><c:param name="q_type" value="${q_type}"/><c:param name="q_amount" value="${q_amount}"/>
                </c:url>
                <th><a href="${sortStatus}">申請状況<c:if test="${sort == 'status'}"><c:out value="${dir == 'asc' ? ' ▲' : ' ▼'}" /></c:if></a></th>

                <c:url var="sortDate" value="/ApplicationHistoryServlet">
                    <c:param name="scope" value="${currentScope}"/>
                    <c:param name="filter" value="${currentStatusFilter == 'incomplete' ? 'unapproved' : 'all'}"/>
                    <c:param name="sort" value="date"/>
                    <c:param name="dir" value="${sort == 'date' && dir == 'asc' ? 'desc' : 'asc'}"/>
                    <c:param name="page" value="1"/>
                    <c:param name="q_status" value="${q_status}"/><c:param name="q_name" value="${q_name}"/><c:param name="q_department" value="${q_department}"/><c:param name="q_type" value="${q_type}"/><c:param name="q_amount" value="${q_amount}"/>
                </c:url>
                <th><a href="${sortDate}">申請日<c:if test="${sort == 'date'}"><c:out value="${dir == 'asc' ? ' ▲' : ' ▼'}" /></c:if></a></th>
                
                <th>操作</th>
            </tr>
        </thead>
        <tbody>
            <% 
                if (historyList != null && !historyList.isEmpty()) { 
                    for (ApplicationBean app : historyList) {
                        int sid = app.getStatus_id();
                        boolean isOwnApplication = empId.equals(app.getEmployeeId());
                        
                        // 修正・削除ボタン：未承認(1) かつ 自身の申請であること
                        boolean canEditOrDelete = (sid == 1 && isOwnApplication);
                        
                        // 管理部削除特権：管理スコープでの全社表示時、ステータスが完了(5)であれば削除可能
                        boolean isManagementDelete = ("management".equals(currentScope) && "D100".equals(dptId) && sid == 5);
            %>
                <tr>
                    <td><%= app.getApctId() %></td>
                    <td><%= app.getEmployeeName() %></td>
                    <td><%= app.getDepartmentName() != null ? app.getDepartmentName() : "未設定" %></td>
                    <td><%= app.getType() %></td>
                    <td><%= app.getPaymentMethod() %></td>
                    <td><%= String.format("%,d円", app.getAmount()) %></td>
                    <td><%= app.getContent() %></td>
                    <td>
                        <%
                            if (sid == 1) out.print("未承認");
                            else if (sid == 2) out.print("上長承認");
                            else if (sid == 3) out.print("管理部承認");
                            else if (sid == 4) out.print("社長承認");
                            else if (sid == 5) out.print("完了");
                            else if (sid == 6) out.print("却下");
                            else if (sid == 7) out.print("削除");
                        %>
                    </td>
                    <td>
                        <%= app.getCreateDate() != null ? app.getCreateDate().format(java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")) : "---" %>
                    </td>
                    <td>
                        <form action="ApplicationDetail" method="get" style="display:inline;">
                            <input type="hidden" name="apct_id" value="<%= app.getApctId() %>">
                            <input type="submit" value="詳細" class="btn btn-detail">
                        </form>

                        <% if (canEditOrDelete) { %>
                            <form action="ApplicationEdit" method="post" style="display:inline;">
                                <input type="hidden" name="apct_id" value="<%= app.getApctId() %>">
                                <input type="submit" value="修正" class="btn btn-edit">
                            </form>
                        <% } %>
                        
                        <% if (canEditOrDelete || isManagementDelete) { %>
                            <form action="ApplicationDelete" method="post" style="display:inline;" onsubmit="return confirmDelete();">
                                <input type="hidden" name="apct_id" value="<%= app.getApctId() %>">
                                <input type="submit" value="削除" class="btn btn-delete">
                            </form>
                        <% } %>
                    </td>
                </tr>
            <% 
                    } 
                } else { 
            %>
                <tr><td colspan="10" style="text-align:center;">該当する申請履歴がありません。</td></tr>
            <% } %>
        </tbody>
    </table>

    <div class="paging-group">
        <c:url var="urlPrev" value="ApplicationHistoryServlet">
            <c:param name="scope" value="${currentScope}"/><c:param name="filter" value="${currentStatusFilter == 'incomplete' ? 'unapproved' : 'all'}"/>
            <c:param name="sort" value="${sort}"/><c:param name="dir" value="${dir}"/><c:param name="page" value="${page - 1}"/>
            <c:param name="q_status" value="${q_status}"/><c:param name="q_name" value="${q_name}"/><c:param name="q_department" value="${q_department}"/><c:param name="q_type" value="${q_type}"/><c:param name="q_amount" value="${q_amount}"/>
        </c:url>
        <c:choose>
            <c:when test="${page > 1}"><a href="${urlPrev}">前へ</a></c:when>
            <c:otherwise>前へ</c:otherwise>
        </c:choose>
        
        &nbsp;|&nbsp;
        
        <c:choose>
            <c:when test="${hasNext}">
                <c:url var="urlNext" value="ApplicationHistoryServlet">
                    <c:param name="scope" value="${currentScope}"/><c:param name="filter" value="${currentStatusFilter == 'incomplete' ? 'unapproved' : 'all'}"/>
                    <c:param name="sort" value="${sort}"/><c:param name="dir" value="${dir}"/><c:param name="page" value="${page + 1}"/>
                    <c:param name="q_status" value="${q_status}"/><c:param name="q_name" value="${q_name}"/><c:param name="q_department" value="${q_department}"/><c:param name="q_type" value="${q_type}"/><c:param name="q_amount" value="${q_amount}"/>
                </c:url>
                <a href="${urlNext}">次へ</a>
            </c:when>
            <c:otherwise>次へ</c:otherwise>
        </c:choose>
    </div>

    <%
        String errorMsg = (String) request.getAttribute("errorMessage");
        if (errorMsg != null && !errorMsg.isEmpty()) {
    %>
        <script type="text/javascript">alert("<%= errorMsg %>");</script>
    <% } %>
</body>
</html>