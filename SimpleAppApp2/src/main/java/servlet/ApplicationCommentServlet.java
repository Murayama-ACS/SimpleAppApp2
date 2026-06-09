package servlet;

import java.io.IOException;
import java.util.ArrayList;
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8");

		// セッションからEmployeeBeanオブジェクトを取得してログインチェック
		HttpSession session = request.getSession();
		EmployeeBean employee = (EmployeeBean) session.getAttribute("EmployeeBean"); 
		
		if (employee == null) {
			response.sendRedirect(request.getContextPath() + "/login_mock.jsp");
			return;
		}

		// リクエストから申請IDを取得
		String apctId = request.getParameter("apct_id"); 
		if (apctId == null || apctId.trim().isEmpty()) {
			// 申請IDがない場合も、一覧画面（app_wait.jsp）へ戻す
			request.setAttribute("errorMessage", "申請IDが指定されていません。");
			request.setAttribute("applications", new ArrayList<ApplicationBean>());
			request.setAttribute("currentStatus", "1");
			RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/jsp/app_wait.jsp");
			rd.forward(request, response);
			return;
		}

		try {
			// プロジェクト共通のインスタンス化形式
			ApplicationDAO appDao = new ApplicationDAO();
			ApprovalDAO approvalDao = new ApprovalDAO();

			// 1. 申請データの詳細を取得
			ApplicationBean application = appDao.findById(apctId);
			if (application == null) {
				// 申請データが見つからない場合も、一覧画面（app_wait.jsp）へ戻す
				request.setAttribute("errorMessage", "指定された申請が見つかりません。");
				request.setAttribute("applications", new ArrayList<ApplicationBean>());
				request.setAttribute("currentStatus", "1");
				RequestDispatcher rd = request.getRequestDispatcher("app_wait.jsp");
				rd.forward(request, response);
				return;
			}

			// 2. 確定済みのApprovalDAOの仕様に合わせて承認データを取得
			ApprovalBean approval = approvalDao.selectByApctId(apctId);

			// 正常に取得できた場合は詳細画面（app_comment.jsp）へフォワード
			request.setAttribute("application", application);
			request.setAttribute("approvalData", approval);

			RequestDispatcher rd = request.getRequestDispatcher("app_comment.jsp");
			rd.forward(request, response);

		} catch (Exception e) {
			log("ApplicationCommentServlet error", e);
			
			try {
				// エラーが起きた場合は一覧画面（app_wait.jsp）にエラーメッセージと空リストを渡して表示
				List<ApplicationBean> emptyList = new ArrayList<>();
				request.setAttribute("applications", emptyList);
				request.setAttribute("currentStatus", "1"); // デフォルトの未承認ステータスを想定
				
				request.setAttribute("errorMessage", "申請詳細の読み込み中にエラーが発生しました。");
				RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/jsp/app_wait.jsp");
				rd.forward(request, response);
				
			} catch (Exception ex) {
				log("Fatal error in catch block", ex);
			}
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