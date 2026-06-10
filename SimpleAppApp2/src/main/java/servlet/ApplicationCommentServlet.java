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

	/**
	 * 詳細画面フォームからの承認・却下登録処理（POST）
	 * ※一覧画面の「詳細」ボタン、および詳細画面自身の「承認」「却下」ボタンの両方を受け付けます。
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

		if (apctId == null || apctId.trim().isEmpty()) {
			forwardToWaitListWithError(request, response, "申請IDが指定されていません。");
			return;
		}

		try {
			ApplicationDAO appDao = new ApplicationDAO();
			ApprovalDAO approvalDao = new ApprovalDAO();

			// 【分岐処理】next_status_id が存在する場合のみ登録処理を実行（詳細表示ボタンからの遷移時はスキップ）
			if (nextStatusStr != null && !nextStatusStr.trim().isEmpty()) {
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

				// 3. データの登録・更新
				int insertResult = approvalDao.insert(approval);

				if (insertResult > 0) {
					ApplicationBean appBean = appDao.findById(apctId);
					if (appBean != null) {
						appBean.setStatus_id(nextStatusId);
						appBean.setUpdateDate(LocalDateTime.now());
						appDao.insert(appBean); // 上書き仕様流用
					}
				} else {
					forwardToWaitListWithError(request, response, "承認データの登録に失敗しました。");
					return;
				}
			}

			// 最新の状態をデータベースから再読込して詳細画面を表示
			ApplicationBean application = appDao.findById(apctId);
			if (application == null) {
				forwardToWaitListWithError(request, response, "指定された申請が見つかりません。");
				return;
			}

			ApprovalBean approvalData = approvalDao.selectByApctId(apctId);

			request.setAttribute("application", application);
			request.setAttribute("approvalData", approvalData);

			RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/jsp/app_comment.jsp");
			rd.forward(request, response);

		} catch (Exception e) {
			log("ApplicationCommentServlet error", e);
			forwardToWaitListWithError(request, response, "申請詳細の処理中にエラーが発生しました。");
		}
	}

	/**
	 * 共通エラー処理：不具合発生時は一覧画面へ安全に戻す
	 */
	private void forwardToWaitListWithError(HttpServletRequest request, HttpServletResponse response, String errorMessage) 
			throws ServletException, IOException {
		try {
			List<ApplicationBean> emptyList = new ArrayList<>();
			request.setAttribute("applications", emptyList);
			request.setAttribute("currentStatus", "1");
			request.setAttribute("errorMessage", errorMessage);
			
			RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/jsp/app_wait.jsp");
			rd.forward(request, response);
		} catch (Exception ex) {
			log("Fatal error in forwardToWaitListWithError", ex);
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		doPost(request, response);
	}
}