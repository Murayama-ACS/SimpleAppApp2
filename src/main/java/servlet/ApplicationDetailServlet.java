package servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import bean.ApplicationBean;
import bean.ApprovalBean;
import bean.EmployeeBean;
import dao.ApplicationDAO;
import dao.ApprovalDAO;

@WebServlet("/ApplicationDetail")
public class ApplicationDetailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8");
		
		// 1. ログインチェック (登录检查)
		HttpSession session = request.getSession();
		EmployeeBean employee = (EmployeeBean) session.getAttribute("empBean"); 
		if (employee == null) {
			employee = (EmployeeBean) session.getAttribute("loginEmployee"); 
		}

		if (employee == null) {
			response.sendRedirect(request.getContextPath() + "/index.jsp");
			return;
		}

		// 2. リクエストから申請IDを取得 (获取申请ID)
		String apctId = request.getParameter("apct_id");
		if (apctId == null || apctId.trim().isEmpty()) {
			request.setAttribute("eMsg", "申請IDが指定されていません。");
			request.getRequestDispatcher("/ApplicationHistoryServlet").forward(request, response);
			return;
		}

		try {
			ApplicationDAO appDao = new ApplicationDAO();
			ApprovalDAO approvalDao = new ApprovalDAO();

			// 3. 申請の基本データを取得 (获取申请基础数据)
			ApplicationBean application = appDao.findById(apctId);
			if (application == null) {
				request.setAttribute("eMsg", "指定された申請が見つかりません。");
				request.getRequestDispatcher("/ApplicationHistoryServlet").forward(request, response);
				return;
			}

			// 4. 過去の承認履歴（コメントなど）を取得 (获取上司的审批评论)
			ApprovalBean approval = approvalDao.selectByApctId(apctId);

			// 5. データをリクエストスコープにセットし、JSPへ渡す (传递给纯净版JSP)
			request.setAttribute("application", application);
			request.setAttribute("approval", approval); 

			RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/jsp/app_detail.jsp");
			rd.forward(request, response);

		} catch (Exception e) {
			log("ApplicationDetailServlet doGet error", e);
			request.setAttribute("eMsg", "申請詳細の表示中にエラーが発生しました。");
			request.getRequestDispatcher("/ApplicationHistoryServlet").forward(request, response);
		}
	}
}