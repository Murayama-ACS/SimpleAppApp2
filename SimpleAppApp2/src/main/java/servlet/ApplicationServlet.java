package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import bean.ApplicationBean;
import dao.ApplicationDAO;
import model.TodaysDateTime;

@WebServlet("/Application")
public class ApplicationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/application.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        try {
            // セッションからログイン中の社員IDを取得
            HttpSession session = request.getSession();
            String employeeId = (String) session.getAttribute("emp_id");
            
            if (employeeId == null) {
                response.sendRedirect(request.getContextPath() + "/login_mock.jsp");
                return;
            }

            // リクエストパラメータ取得
            String applicationType = request.getParameter("applicationType");
            String paymentMethod   = request.getParameter("paymentMethod");
            String amountParam     = request.getParameter("amount");
            String content         = request.getParameter("content");
            String reason          = request.getParameter("reason");
            String note            = request.getParameter("note");
            String urgentFlagParam = request.getParameter("urgentFlag");


            // 金額（文字列）を int 型に変換する処理
            int amount = 0;
            if (amountParam != null && !amountParam.trim().isEmpty()) {
                amount = Integer.parseInt(amountParam.trim());
            }
            
            // Bean にデータ格納
            ApplicationBean bean = new ApplicationBean();
            bean.setType(applicationType);
            bean.setAmount(amount);
            bean.setPaymentMethod(paymentMethod);
            bean.setEmployeeId(employeeId);
            bean.setContent(content);
            bean.setReason(reason);
            bean.setNote(note);

            boolean urgent = false;
            if (urgentFlagParam != null) {
                String u = urgentFlagParam.trim().toLowerCase();
                urgent = "true".equals(u) || "1".equals(u);
            }
            bean.setUrgentFlag(urgent);
            bean.setCreateDate(TodaysDateTime.getNow());

            // DB登録
            ApplicationDAO dao = new ApplicationDAO();
            dao.insert(bean);

            // 完了画面へフォワード
            request.getRequestDispatcher("/app_submit.jsp").forward(request, response);

        } catch (Exception e) {
            log("ApplicationServlet error", e);
            request.setAttribute("errorMessage", "申請の登録中にエラーが発生しました。");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
}