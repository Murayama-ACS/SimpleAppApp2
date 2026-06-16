package servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import bean.ApplicationBean;
import bean.EmployeeBean;
import dao.ApplicationDAO;
import dao.ApplicationDAO.PageResult;

@WebServlet("/ApplicationStatus")
public class ApplicationStatusServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final int LIMIT = 20;

    // =========================================================================
    // 【GETリクエスト処理ブロック】緊急度検索パラメータを含めた一覧表示制御
    // =========================================================================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 【1-1. ログイン検証】
        HttpSession session = request.getSession();
        EmployeeBean employee = (EmployeeBean) session.getAttribute("loginEmployee");

        if (employee == null) {
            response.sendRedirect(request.getContextPath() + "/login_mock.jsp");
            return;
        }

        // 【1-2. 経理部権限検証】
        String dptId = employee.getDpt_id();
        if (!"D200".equals(dptId)) {
            request.setAttribute("eMsg", "アクセス権限がありません。");
            request.getRequestDispatcher("/login_mock.jsp").forward(request, response);
            return;
        }

        // 【1-3. 検索パラメータの取得と型変換】
        String qStatus = trimToNull(request.getParameter("q_status"));
        String qName = trimToNull(request.getParameter("q_name"));
        String qType = trimToNull(request.getParameter("q_type"));
        String qUrgent = trimToNull(request.getParameter("q_urgent")); // 【新規追加】

        String qAmountMinStr = trimToNull(request.getParameter("q_amount_min"));
        String qAmountMaxStr = trimToNull(request.getParameter("q_amount_max"));

        Integer qAmountMin = null;
        Integer qAmountMax = null;
        try {
            if (qAmountMinStr != null) qAmountMin = Integer.valueOf(qAmountMinStr);
        } catch (NumberFormatException e) {}
        try {
            if (qAmountMaxStr != null) qAmountMax = Integer.valueOf(qAmountMaxStr);
        } catch (NumberFormatException e) {}

        // 【1-4. ソート・ページングパラメータの制御】
        String sortKey = request.getParameter("sort");
        String sortDir = request.getParameter("dir");
        
        int page = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            try { page = Integer.parseInt(pageParam); } catch (NumberFormatException ex) { page = 1; }
        }
        if (request.getParameter("search") != null) { 
            page = 1;
        }
        if (page < 1) page = 1;
        int offset = (page - 1) * LIMIT;

        // 【1-5. 経理専用DAOメソッドによるデータ取得処理】
        try {
            ApplicationDAO dao = new ApplicationDAO();
            
            // 引数に緊急度の検索パラメータ「qUrgent」を連動
            PageResult<ApplicationBean> pageRes = dao.searchAccountingApplications(
                    qStatus, qName, qType, qAmountMin, qAmountMax, qUrgent,
                    sortKey, sortDir, LIMIT, offset);
            
            List<ApplicationBean> list = pageRes.getItems();
            boolean hasNext = pageRes.hasNext();

            // リクエストスコープへの設定と状態維持データの格納
            request.setAttribute("appList", list);
            request.setAttribute("q_status", qStatus);
            request.setAttribute("q_name", qName);
            request.setAttribute("q_type", qType);
            request.setAttribute("q_urgent", qUrgent); // 【新規追加】
            request.setAttribute("q_amount_min", qAmountMinStr);
            request.setAttribute("q_amount_max", qAmountMaxStr);
            request.setAttribute("sort", sortKey);
            request.setAttribute("dir", sortDir);
            request.setAttribute("page", page);
            request.setAttribute("hasNext", hasNext);

            String success = request.getParameter("success");
            if ("true".equals(success)) {
                request.setAttribute("showSuccessPopup", true);
            }

            String errorMsg = request.getParameter("errorMessage");
            if (errorMsg != null) {
                request.setAttribute("errorMessage", errorMsg);
            }

            RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/jsp/app_list.jsp");
            rd.forward(request, response);

        } catch (SQLException e) {
            log("SQLエラーが発生しました", e);
            request.setAttribute("errorMessage", "データベース情報の取得中にエラーが発生しました。");
            request.getRequestDispatcher("WEB-INF/jsp/app_list.jsp").forward(request, response);
        } catch (Exception e) {
            log("システムエラーが発生しました", e);
            request.setAttribute("errorMessage", "情報の取得中にシステムエラーが発生しました。");
            request.getRequestDispatcher("WEB-INF/jsp/app_list.jsp").forward(request, response);
        }
    }

    private String trimToNull(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    // =========================================================================
    // 【POSTリクエスト処理ブロック】詳細表示制御（維持）
    // =========================================================================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        EmployeeBean employee = (EmployeeBean) session.getAttribute("loginEmployee");

        if (employee == null) {
            response.sendRedirect(request.getContextPath() + "/login_mock.jsp");
            return;
        }

        String dptId = employee.getDpt_id();
        if (!"D200".equals(dptId)) {
            request.setAttribute("eMsg", "アクセス権限がありません。経理部専用の機能です。");
            request.getRequestDispatcher("/login_mock.jsp").forward(request, response);
            return;
        }

        String apctId = request.getParameter("apct_id");
        if (apctId == null || apctId.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/ApplicationStatus?errorMessage=" 
                    + java.net.URLEncoder.encode("申請IDが指定されていません。", "UTF-8"));
            return;
        }

        try {
            ApplicationDAO appDao = new ApplicationDAO();
            ApplicationBean application = appDao.findById(apctId);
            
            if (application == null) {
                response.sendRedirect(request.getContextPath() + "/ApplicationStatus?errorMessage=" 
                        + java.net.URLEncoder.encode("指定された申請が見つかりません。", "UTF-8"));
                return;
            }

            request.setAttribute("application", application);
            RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/jsp/app_status.jsp");
            rd.forward(request, response);

        } catch (Exception e) {
            log("ApplicationStatusServlet POST error", e);
            response.sendRedirect(request.getContextPath() + "/ApplicationStatus?errorMessage=" 
                    + java.net.URLEncoder.encode("詳細画面の処理中にエラーが発生しました。", "UTF-8"));
        }
    }
}