package servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

@WebServlet("/ApplicationWaitList")
public class ApplicationWaitListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * 一覧画面の表示（GET）
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		EmployeeBean employee = (EmployeeBean) session.getAttribute("loginEmployee"); 
		
		if (employee == null) {
			response.sendRedirect(request.getContextPath() + "/login_mock.jsp");
			return;
		}

		String pendingStatus = request.getParameter("pendingStatus");
		if (pendingStatus == null || pendingStatus.trim().isEmpty()) {
			pendingStatus = "1"; 
		}

		try {
			ApplicationDAO dao = new ApplicationDAO();
			List<ApplicationBean> list = dao.getPendingApplications(pendingStatus);

			request.setAttribute("applications", list);
			request.setAttribute("currentStatus", pendingStatus);

			RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/jsp/app_wait.jsp");
			rd.forward(request, response);

		} catch (Exception e) {
			log("ApplicationWaitListServlet doGet error", e);
			forwardToWaitListWithError(request, response, pendingStatus, "申請一覧の読み込み中にエラーが発生しました。");
		}
	}

	/**
	 * 一覧画面のポップアップからの承認・却下処理（POST）
	 */
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

		String apctId = request.getParameter("apct_id");
		String nextStatusStr = request.getParameter("next_status_id");
		String comment = request.getParameter("comment");

		String pendingStatus = request.getParameter("pendingStatus");
		if (pendingStatus == null || pendingStatus.trim().isEmpty()) {
			pendingStatus = "1";
		}

		if (apctId == null || nextStatusStr == null) {
			forwardToWaitListWithError(request, response, pendingStatus, "不正なリクエストです。処理を中断しました。");
			return;
		}

		try {
			int nextStatusId = Integer.parseInt(nextStatusStr.trim());

			// 1. 履歴ID (approval_id) の生成
			String timeStamp = new SimpleDateFormat("yyMMddHHmmssSSS").format(new Date());
			String approvalId = "APV" + timeStamp;

			// 2. ApprovalBean の作成
			ApprovalBean approval = new ApprovalBean();
			approval.setApprovalId(approvalId);
			approval.setApctId(apctId);
			approval.setEmployeeId(employee.getEmp_id());
			approval.setStatusId(nextStatusId);
			approval.setComment(comment);
			approval.setCreateDate(LocalDateTime.now());

			ApprovalDAO approvalDao = new ApprovalDAO();
			ApplicationDAO applicationDao = new ApplicationDAO();

			// 3. データの登録・更新
			int insertResult = approvalDao.insert(approval);

			if (insertResult > 0) {
				ApplicationBean appBean = applicationDao.findById(apctId);
				if (appBean != null) {
					appBean.setStatus_id(nextStatusId);
					appBean.setUpdateDate(LocalDateTime.now());
					applicationDao.insert(appBean); // 上書き仕様流用
				}
			} else {
				forwardToWaitListWithError(request, response, pendingStatus, "承認データの登録に失敗しました。");
				return;
			}

			// 成功したら一覧の再読込
			doGet(request, response);

		} catch (Exception e) {
			log("ApplicationWaitListServlet doPost error", e);
			forwardToWaitListWithError(request, response, pendingStatus, "承認処理中にエラーが発生しました。");
		}
	}

	private void forwardToWaitListWithError(HttpServletRequest request, HttpServletResponse response, 
			String pendingStatus, String errorMessage) throws ServletException, IOException {
		try {
			List<ApplicationBean> emptyList = new ArrayList<>();
			request.setAttribute("applications", emptyList);
			request.setAttribute("currentStatus", pendingStatus);
			request.setAttribute("errorMessage", errorMessage);
			
			RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/jsp/app_wait.jsp");
			rd.forward(request, response);
		} catch (Exception ex) {
			log("Fatal error in forwardToWaitListWithError", ex);
		}
	}
}