package servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import bean.EmployeeBean;
import bean.NotificationBean;
import dao.ApprovalDAO;

@WebServlet("/TopPageServlet")
public class TopPageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. ログインチェック
        HttpSession session = request.getSession();
        EmployeeBean employee = (EmployeeBean) session.getAttribute("empBean");

        if (employee == null) {
        	session.setAttribute("eMsg", "error : session timeout");
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        // 2. 通知の未読件数を取得
        try {
            ApprovalDAO approvalDao = new ApprovalDAO();
            //【修正】（引数に「10」と「0」を追加します）
            List<NotificationBean> notifications = approvalDao.selectNotificationsByApplicant(employee.getEmp_id(), 10, 0);
            // 未読（is_readがfalse）の件数をカウント
            long unreadCount = notifications.stream().filter(n -> !n.isRead()).count();
            request.setAttribute("unreadCount", unreadCount);

        } catch (Exception e) {
            log("TopPageServlet 通知取得エラー", e);
            // 万が一DBエラーが起きても、TopPage自体は表示できるように 0件 として続行
            request.setAttribute("unreadCount", 0); 
        }

        // 3. TopPage画面へフォワード
        request.getRequestDispatcher("/WEB-INF/jsp/top_page.jsp").forward(request, response);
    }
}