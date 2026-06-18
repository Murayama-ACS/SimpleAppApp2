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

// 💡 修正：あなたの他のページのリンクに合わせて "/TopPageServlet" に統一しました
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
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        // 2. 通知の未読件数を取得
        try {
            ApprovalDAO approvalDao = new ApprovalDAO();
            List<NotificationBean> notifications = approvalDao.selectNotificationsByApplicant(employee.getEmp_id());

            // 未読（is_readがfalse）の件数をカウント
            long unreadCount = notifications.stream().filter(n -> !n.isRead()).count();
            request.setAttribute("unreadCount", unreadCount);

            // --- デバッグ用ログ（本番環境では消してもOKです） ---
            System.out.println("====== TopPageServlet デバッグ ======");
            System.out.println("ログイン社員: " + employee.getEmp_name() + " (ID: " + employee.getEmp_id() + ")");
            System.out.println("未読通知件数: " + unreadCount);
            System.out.println("====================================");

        } catch (Exception e) {
            log("TopPageServlet 通知取得エラー", e);
            // 万が一DBエラーが起きても、TopPage自体は表示できるように 0件 として続行
            request.setAttribute("unreadCount", 0); 
        }

        // 3. TopPage画面へフォワード
        request.getRequestDispatcher("/WEB-INF/jsp/top_page.jsp").forward(request, response);
    }
}