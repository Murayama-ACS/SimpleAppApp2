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
	 * 詳細画面の表示、および詳細画面からの承認・却下登録処理（POST）
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8");

		// 1. ログインチェック
		HttpSession session = request.getSession();
		EmployeeBean employee = (EmployeeBean) session.getAttribute("loginEmployee"); 

		if (employee == null) {
			response.sendRedirect(request.getContextPath() + "/login_mock.jsp");
			return;
		}

		// 2. 権限チェック
		String posId = employee.getPos_id();
		if ("E00".equals(posId)) {
			request.setAttribute("errorMessage", "アクセス権限がありません。この画面は各部上長、または管理部上長専用です。");
			request.getRequestDispatcher("/login_mock.jsp").forward(request, response);
			return;
		}

		String apctId = request.getParameter("apct_id"); 
		String actionType = request.getParameter("action_type"); // 'approve' または 'reject' を取得
		String comment = request.getParameter("comment");

		if (apctId == null || apctId.trim().isEmpty()) {
			forwardToWaitListWithError(request, response, "申請IDが指定されていません。");
			return;
		}

		try {
			ApplicationDAO appDao = new ApplicationDAO();
			ApprovalDAO approvalDao = new ApprovalDAO();

			// 【確定処理】JSPからアクションタイプ（approve/reject）が送られてきた場合
			if (actionType != null && !actionType.trim().isEmpty()) {
				
				// 1. 現在の申請データをDBから取得して最新のstatus_idを確認
				ApplicationBean application = appDao.findById(apctId);
				if (application == null) {
					forwardToWaitListWithError(request, response, "対象の申請データが見つかりません。");
					return;
				}
				int currentStatusId = application.getStatus_id();

				// 2. 役職と操作内容から次のステータスIDをサーブレット側で安全に決定
				int nextStatusId = 0;

				if ("reject".equals(actionType)) {
					// 却下の場合は一律「6: 却下」
					nextStatusId = 6;
				} else if ("approve".equals(actionType)) {
					String userDpt = employee.getDpt_id(); // ログインユーザーの部署ID
					String userPos = employee.getPos_id(); // ログインユーザーの役職コード

					if ("D100".equals(userDpt)) {
						// 【追加】管理部の上長（課長・部長）が承認した場合は、
						// status_idが1（自部署内の申請）であっても2（他部署からの申請）であっても、次は一律「3: 管理部承認」となる
						nextStatusId = 3;
					} else if (currentStatusId == 1 && "E04".equals(userPos)) {
						// 現在が未承認(1)で、承認者が社長(E04)の場合のみ「4: 社長承認」へジャンプ
						nextStatusId = 4;
					} else {
						// それ以外の本部長、部長、課長などの通常承認はステップを1つ進める
						nextStatusId = currentStatusId + 1;
					}
				} else {
					forwardToWaitListWithError(request, response, "不正な操作リクエストです。");
					return;
				}

				// 3. 履歴ID (approval_id) の生成
				String timeStamp = new SimpleDateFormat("yyMMddHHmmssSSS").format(new Date());
				String approvalId = "APV" + timeStamp;

				// 4. ApprovalBean の作成とデータ設定
				ApprovalBean approval = new ApprovalBean();
				approval.setApprovalId(approvalId);
				approval.setApctId(apctId);
				approval.setEmployeeId(employee.getEmp_id()); // 処理を行ったログインユーザーのID
				approval.setStatusId(nextStatusId);
				approval.setComment(comment);
				approval.setCreateDate(LocalDateTime.now());

				// 5. 承認履歴(approvals)テーブルへのデータ登録
				int insertResult = approvalDao.insert(approval);

				if (insertResult > 0) {
					// 6. applicationsテーブルのstatus_idを更新
					int updateResult = appDao.updateStatus(apctId, nextStatusId, LocalDateTime.now());
					if (updateResult == 0) {
						forwardToWaitListWithError(request, response, "申請データの状態更新に失敗しました。");
						return;
					}
				} else {
					forwardToWaitListWithError(request, response, "承認データの登録に失敗しました。");
					return;
				}

				// 処理成功時：完了画面を挟まず、一覧画面へ成功フラグを付与してリダイレクト
				response.sendRedirect(request.getContextPath() + "/ApplicationWaitList?success=true");
				return; 
			}

			// --- action_type が無い場合（一覧画面から詳細画面へ通常遷移してきた時） ---
			ApplicationBean application = appDao.findById(apctId);
			if (application == null) {
				forwardToWaitListWithError(request, response, "指定された申請が見つかりません。");
				return;
			}

			request.setAttribute("application", application);

			// 詳細画面（app_comment.jsp）へフォワード
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
	 * GETリクエスト時もPOST処理（doPost）に委譲する
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		doPost(request, response);
	}
}