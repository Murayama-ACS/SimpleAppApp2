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

@WebServlet("/TopPage")
public class TopPageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
    	// --- TopPageServlet.java の doGet メソッド内 ---

    	HttpSession session = request.getSession();
    	EmployeeBean employee = (EmployeeBean) session.getAttribute("loginEmployee");

    	if (employee == null) {
    	    response.sendRedirect(request.getContextPath() + "/login_mock.jsp");
    	    return;
    	}

    	ApprovalDAO approvalDao = new ApprovalDAO();
    	List<NotificationBean> notifications = approvalDao.selectNotificationsByApplicant(employee.getEmp_id());

    	// ★【デバッグログ追加】コンソールで確認するための記述
    	System.out.println("====== TopPageServlet デバッグ ======");
    	System.out.println("ログイン中の社員ID: " + employee.getEmp_id());
    	System.out.println("DBから取得した全体の通知件数: " + notifications.size());

    	long unreadCount = notifications.stream().filter(n -> !n.isRead()).count();
    	System.out.println("そのうち未読（バッジ対象）の件数: " + unreadCount);
    	System.out.println("====================================");

    	request.setAttribute("unreadCount", unreadCount);
    	request.getRequestDispatcher("/WEB-INF/jsp/toppage.jsp").forward(request, response);

    	// 未読の通知が何件あるかをカウントする

    	// リクエストスコープに格納
    	request.setAttribute("unreadCount", unreadCount);

    	request.getRequestDispatcher("/WEB-INF/jsp/toppage.jsp").forward(request, response);
    }
}