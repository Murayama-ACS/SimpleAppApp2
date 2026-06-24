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

@WebServlet("/NotificationList")
public class NotificationListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        EmployeeBean employee = (EmployeeBean) session.getAttribute("empBean");
        
        if (employee == null) {
        	session.setAttribute("eMsg", "error : session timeout");
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        ApprovalDAO approvalDao = new ApprovalDAO();
        
        //ページネーション処理
        int page = 1; // デフォルトは1ページ目
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        
        int limit = 10; // 1ページあたりの表示件数
        int offset = (page - 1) * limit; // データ取得の開始位置
        
        // 1. 総件数を取得し、次のページがあるか（hasNext）を判定
        int totalCount = approvalDao.countNotificationsByApplicant(employee.getEmp_id());
        boolean hasNext = (page * limit) < totalCount;
        
        // 2. 指定したページ範囲の通知一覧を取得
        List<NotificationBean> notifications = approvalDao.selectNotificationsByApplicant(employee.getEmp_id(), limit, offset);
        
        // 3. JSPへデータを渡す
        request.setAttribute("notifications", notifications);
        request.setAttribute("page", page);
        request.setAttribute("hasNext", hasNext);

        // 4. このユーザーの未読通知をすべて既読に更新する
        approvalDao.updateAllNotificationsAsRead(employee.getEmp_id());

        // 5. 通知一覧ページへフォワード
        request.getRequestDispatcher("/WEB-INF/jsp/notification_list.jsp").forward(request, response);
    }
}