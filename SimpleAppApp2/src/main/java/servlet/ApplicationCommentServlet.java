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

		// 1. ログインチェックブロック
		HttpSession session = request.getSession();
		EmployeeBean employee = (EmployeeBean) session.getAttribute("loginEmployee"); 

		if (employee == null) {
			response.sendRedirect(request.getContextPath() + "/login_mock.jsp");
			return;
		}

		// 2. 画面アクセス権限チェックブロック
		String posId = employee.getPos_id();
		if ("E00".equals(posId)) {
			request.setAttribute("eMsg", "アクセス権限がありません。");
			request.getRequestDispatcher("/login_mock.jsp").forward(request, response);
			return;
		}

		// 3. リクエストパラメータの取得および入力情報の検証ブロック
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

			// 4. アクション実行（承認・却下の確定処理）ブロック
			if (actionType != null && !actionType.trim().isEmpty()) {
				
				// 現在の申請データをDBから取得して最新のstatus_idを確認
				ApplicationBean application = appDao.findById(apctId);
				if (application == null) {
					forwardToWaitListWithError(request, response, "対象の申請データが見つかりません。");
					return;
				}
				int currentStatusId = application.getStatus_id();

				int nextStatusId = 0;

				// 却下アクション、または承認者属性（管理部・社長・その他上長）に応じた遷移先ステータスIDの算出
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

				// 登録用エンティティ（Bean）の生成およびデータセット
				String timeStamp = new SimpleDateFormat("yyMMddHHmmssSSS").format(new Date());
				String approvalId = "APV" + timeStamp;

				ApprovalBean approval = new ApprovalBean();
				approval.setApprovalId(approvalId);
				approval.setApctId(apctId);
				approval.setEmployeeId(employee.getEmp_id()); 
				approval.setStatusId(nextStatusId);
				approval.setComment(comment);
				approval.setCreateDate(LocalDateTime.now());

				// データベースへのトランザクション（登録・更新）実行
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

				// 処理成功時の画面リダイレクト制御
				response.sendRedirect(request.getContextPath() + "/ApplicationWaitList?success=true");
				return; 
			}

			// 5. 初期表示（詳細表示処理）ブロック
			ApplicationBean application = appDao.findById(apctId);
			if (application == null) {
				forwardToWaitListWithError(request, response, "指定された申請が見つかりません。");
				return;
			}

			request.setAttribute("application", application);

			// 詳細画面へフォワード
			RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/jsp/app_comment.jsp");
			rd.forward(request, response);

		} catch (Exception e) {
			// 例外発生時のログ出力および共通エラーハンドリングへの委譲
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