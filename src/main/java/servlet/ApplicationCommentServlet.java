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

@WebServlet("/ApplicationComment")
public class ApplicationCommentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8");
		
		// 1. ログインチェック
		HttpSession session = request.getSession();
		EmployeeBean employee = (EmployeeBean) session.getAttribute("empBean"); 
		if (employee == null) {
			employee = (EmployeeBean) session.getAttribute("loginEmployee"); 
		}

		if (employee == null) {
			response.sendRedirect(request.getContextPath() + "/index.jsp");
			return;
		}

		// 2. リクエストから申請IDを取得
		String apctId = request.getParameter("apct_id");
		if (apctId == null || apctId.trim().isEmpty()) {
			forwardToWaitListWithError(request, response, "申請IDが指定されていません。");
			return;
		}

		try {
			ApplicationDAO appDao = new ApplicationDAO();
			ApprovalDAO approvalDao = new ApprovalDAO();

			// 3. 申請の基本データを取得
			ApplicationBean application = appDao.findById(apctId);
			if (application == null) {
				forwardToWaitListWithError(request, response, "指定された申請が見つかりません。");
				return;
			}
			String posId = employee.getPos_id();
			if ("E00".equals(posId) && !application.getEmployeeId().equals(employee.getEmp_id())) {
				forwardToWaitListWithError(request, response, "他人の申請詳細を見る権限がありません。");
				return;
			}

			// 4. セキュリティ対策：このユーザーが現在この申請を承認する権限があるか判定
			List<ApplicationBean> pendingList = appDao.getPendingApplications(employee);
			boolean canApprove = false;
			for (ApplicationBean pendingApp : pendingList) {
				if (pendingApp.getApctId().equals(apctId)) {
					canApprove = true; // 未承認一覧に存在すれば操作可能フラグを立てる
					break;
				}
			}

			// 5. 過去の承認履歴（コメントなど）を取得し、画面に表示させる
			ApprovalBean approval = approvalDao.selectByApctId(apctId);

			// すべてのデータをリクエストスコープにセットし、JSPへ渡す
			request.setAttribute("application", application);
			request.setAttribute("canApprove", canApprove); 
			request.setAttribute("approval", approval); 

			RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/jsp/app_comment.jsp");
			rd.forward(request, response);

		} catch (Exception e) {
			log("ApplicationCommentServlet doGet error", e);
			forwardToWaitListWithError(request, response, "申請詳細の表示中にエラーが発生しました。");
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8");
		
		// 1. ログインチェック
		HttpSession session = request.getSession();
		EmployeeBean employee = (EmployeeBean) session.getAttribute("empBean"); 
		if (employee == null) {
			employee = (EmployeeBean) session.getAttribute("loginEmployee"); 
		}

		if (employee == null) {
			response.sendRedirect(request.getContextPath() + "/index.jsp");
			return;
		}

		String posId = employee.getPos_id();
		if ("E00".equals(posId)) {
			request.setAttribute("eMsg", "アクセス権限がありません。");
			request.getRequestDispatcher("/index.jsp").forward(request, response);
			return;
		}

		// 3. パラメータの取得
		String apctId = request.getParameter("apct_id"); 
		String actionType = request.getParameter("action_type"); 
		String comment = request.getParameter("comment");

		if (apctId == null || apctId.trim().isEmpty()) {
			forwardToWaitListWithError(request, response, "申請IDが指定されていません。");
			return;
		}

		try {
			ApplicationDAO appDao = new ApplicationDAO();
			ApprovalDAO approvalDao = new ApprovalDAO();
			
			// バックエンド防衛線：POST時も再度権限をチェック
			List<ApplicationBean> pendingList = appDao.getPendingApplications(employee);
			boolean canApprove = false;
			for (ApplicationBean pendingApp : pendingList) {
				if (pendingApp.getApctId().equals(apctId)) {
					canApprove = true;
					break;
				}
			}

			// 確定処理
			if (actionType != null && !actionType.trim().isEmpty()) {
				
				if (!canApprove) {
					forwardToWaitListWithError(request, response, "この申請を処理する権限がありません、または既に処理済みです。");
					return;
				}
				
				ApplicationBean application = appDao.findById(apctId);
				if (application == null) {
					forwardToWaitListWithError(request, response, "対象の申請データが見つかりません。");
					return;
				}
				int currentStatusId = application.getStatus_id();
				int nextStatusId = 0;

				if ("reject".equals(actionType)) {
					nextStatusId = 6; 
				} else if ("approve".equals(actionType)) {
					String userDpt = employee.getDpt_id(); 
					String userPos = employee.getPos_id(); 

					if ("D100".equals(userDpt)) {
						nextStatusId = 3;
					} else if (currentStatusId == 1 && "E04".equals(userPos)) {
						nextStatusId = 4;
					} else {
						nextStatusId = currentStatusId + 1;
					}
				} else {
					forwardToWaitListWithError(request, response, "不正な操作リクエストです。");
					return;
				}

				String timeStamp = new SimpleDateFormat("yyMMddHHmmssSSS").format(new Date());
				String approvalId = "APV" + timeStamp;

				ApprovalBean approval = new ApprovalBean();
				approval.setApprovalId(approvalId);
				approval.setApctId(apctId);
				approval.setEmployeeId(employee.getEmp_id()); 
				approval.setStatusId(nextStatusId);
				approval.setComment(comment);
				approval.setCreateDate(LocalDateTime.now());

				int insertResult = approvalDao.insert(approval);
				if (insertResult > 0) {
					int updateResult = appDao.updateStatus(apctId, nextStatusId, LocalDateTime.now());
					if (updateResult == 0) {
						forwardToWaitListWithError(request, response, "申請データの状態更新に失敗しました。");
						return;
					}
				} else {
					forwardToWaitListWithError(request, response, "承認データの登録に失敗しました。");
					return;
				}

				response.sendRedirect(request.getContextPath() + "/ApplicationWaitList?success=true");
				return; 
			}

			doGet(request, response);

		} catch (Exception e) {
			log("ApplicationCommentServlet doPost error", e);
			forwardToWaitListWithError(request, response, "申請詳細の処理中にエラーが発生しました。");
		}
	}

	private void forwardToWaitListWithError(HttpServletRequest request, HttpServletResponse response, String errorMessage) 
			throws ServletException, IOException {
		try {
			List<ApplicationBean> emptyList = new ArrayList<>();
			request.setAttribute("applications", emptyList);
			request.setAttribute("currentStatus", "1");
			request.setAttribute("errorMessage", errorMessage);
			
			RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/approval_list.jsp");
			rd.forward(request, response);
		} catch (Exception ex) {
			log("Fatal error in forwardToWaitListWithError", ex);
		}
	}
}