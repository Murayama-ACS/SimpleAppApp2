package servlet;

import java.io.IOException;
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

@WebServlet("/ApplicationStatus")
public class ApplicationStatusServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. ログインチェック
        HttpSession session = request.getSession();
        EmployeeBean employee = (EmployeeBean) session.getAttribute("loginEmployee");

        if (employee == null) {
            response.sendRedirect(request.getContextPath() + "/login_mock.jsp");
            return;
        }

        // 2. 権限チェック（役職は不問、経理部のみアクセス許可）
        String dptId = employee.getDpt_id();
        if (!"D200".equals(dptId)) {
            request.setAttribute("eMsg", "アクセス権限がありません。");
            request.getRequestDispatcher("/login_mock.jsp").forward(request, response);
            return;
        }

        try {
            ApplicationDAO appDao = new ApplicationDAO();
            List<ApplicationBean> applications = appDao.getAccountingApplications();
            request.setAttribute("applications", applications);

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

        } catch (Exception e) {
            log("ApplicationStatusServlet GET error", e);
            request.setAttribute("errorMessage", "データ取得中に例外が発生しました。");
            RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/jsp/app_list.jsp");
            rd.forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // 1. ログインチェック
        HttpSession session = request.getSession();
        EmployeeBean employee = (EmployeeBean) session.getAttribute("loginEmployee");

        if (employee == null) {
            response.sendRedirect(request.getContextPath() + "/login_mock.jsp");
            return;
        }

        // 2. 権限チェック（役職は不問、経理部のみアクセス許可）
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