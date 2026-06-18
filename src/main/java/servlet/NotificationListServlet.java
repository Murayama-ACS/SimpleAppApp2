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
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        ApprovalDAO approvalDao = new ApprovalDAO();
        
        // 1. まず現在の通知一覧（未読が含まれる状態）を取得してリクエストに格納
        List<NotificationBean> notifications = approvalDao.selectNotificationsByApplicant(employee.getEmp_id());
        request.setAttribute("notifications", notifications);

        // 2. 画面を表示する前に、このユーザーの未読通知をすべて既読に更新する
        // （これにより、このリクエストの表示は未読デザインのままになり、次回トップに戻ると既読扱いになります）
        approvalDao.updateAllNotificationsAsRead(employee.getEmp_id());

        // 3. 通知一覧ページへフォワード
        request.getRequestDispatcher("/WEB-INF/jsp/notification_list.jsp").forward(request, response);
    }
}