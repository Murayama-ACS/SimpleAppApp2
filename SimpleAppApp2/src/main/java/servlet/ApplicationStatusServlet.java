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

    // =========================================================================
    // 【GETリクエスト処理ブロック】経理部用 申請一覧画面(app_list.jsp)の表示制御
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

        // 【1-3. データ取得および画面遷移】
        try {
            ApplicationDAO appDao = new ApplicationDAO();
            
            // 経理対象データ(status_id: 3, 4)のリストを取得
            List<ApplicationBean> applications = appDao.getAccountingApplications();
            request.setAttribute("applications", applications);

            // 前処理(編集完了)からの成功通知パラメータのハンドリング
            String success = request.getParameter("success");
            if ("true".equals(success)) {
                request.setAttribute("showSuccessPopup", true);
            }

            // エラーメッセージ引き継ぎ用パラメータのハンドリング
            String errorMsg = request.getParameter("errorMessage");
            if (errorMsg != null) {
                request.setAttribute("errorMessage", errorMsg);
            }

            // 申請一覧画面(app_list.jsp)へ遷移
            RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/jsp/app_list.jsp");
            rd.forward(request, response);

        } catch (Exception e) {
            // 例外発生時のログ出力およびエラー画面転送
            log("ApplicationStatusServlet GET error", e);
            request.setAttribute("errorMessage", "データ取得中に例外が発生しました。");
            RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/jsp/app_list.jsp");
            rd.forward(request, response);
        }
    }

    // =========================================================================
    // 【POSTリクエスト処理ブロック】選択された申請の詳細画面(app_status.jsp)の表示制御
    // =========================================================================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // 【2-1. ログイン検証】
        HttpSession session = request.getSession();
        EmployeeBean employee = (EmployeeBean) session.getAttribute("loginEmployee");

        if (employee == null) {
            response.sendRedirect(request.getContextPath() + "/login_mock.jsp");
            return;
        }

        // 【2-2. 経理部権限検証】
        String dptId = employee.getDpt_id();
        if (!"D200".equals(dptId)) {
            request.setAttribute("eMsg", "アクセス権限がありません。経理部専用の機能です。");
            request.getRequestDispatcher("/login_mock.jsp").forward(request, response);
            return;
        }

        // 【2-3. リクエストパラメータ検証】
        String apctId = request.getParameter("apct_id");

        if (apctId == null || apctId.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/ApplicationStatus?errorMessage=" 
                    + java.net.URLEncoder.encode("申請IDが指定されていません。", "UTF-8"));
            return;
        }

        // 【2-4. 詳細データ取得および画面遷移】
        try {
            ApplicationDAO appDao = new ApplicationDAO();
            
            // 申請IDをキーに対象データを取得
            ApplicationBean application = appDao.findById(apctId);
            
            if (application == null) {
                response.sendRedirect(request.getContextPath() + "/ApplicationStatus?errorMessage=" 
                        + java.net.URLEncoder.encode("指定された申請が見つかりません。", "UTF-8"));
                return;
            }

            request.setAttribute("application", application);

            // 詳細画面(app_status.jsp)へ遷移
            RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/jsp/app_status.jsp");
            rd.forward(request, response);

        } catch (Exception e) {
            // 例外発生時のログ出力および一覧画面へのリダイレクト復旧
            log("ApplicationStatusServlet POST error", e);
            response.sendRedirect(request.getContextPath() + "/ApplicationStatus?errorMessage=" 
                    + java.net.URLEncoder.encode("詳細画面の処理中にエラーが発生しました。", "UTF-8"));
        }
    }
}