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
import bean.EmployeeBean; // 追加：EmployeeBeanのインポート
import dao.ApplicationDAO;

@WebServlet("/ApplicationWaitList")
public class ApplicationWaitListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		// セッションからEmployeeBeanオブジェクトを取得する形式に変更
		HttpSession session = request.getSession();
		EmployeeBean employee = (EmployeeBean) session.getAttribute("EmployeeBean"); 
		
		// ログイン情報の存在チェック
		if (employee == null) {
			response.sendRedirect(request.getContextPath() + "/login_mock.jsp");
			return;
		}

		// 表示対象とするステータスを取得（指定がない場合のデフォルト値は "1"）
		String pendingStatus = request.getParameter("pendingStatus");
		if (pendingStatus == null || pendingStatus.trim().isEmpty()) {
			pendingStatus = "1"; 
		}

		try {
			// プロジェクト共通のインスタンス化形式
			ApplicationDAO dao = new ApplicationDAO();
			
			// 申請DAOのメソッドを呼び出し
			List<ApplicationBean> list = dao.getPendingApplications(pendingStatus);

			// 取得した申請一覧と現在のステータスをリクエストに設定
			request.setAttribute("applications", list);
			request.setAttribute("currentStatus", pendingStatus);

			// 既存のMainサーブレットに合わせたWEB-INFへのフォワード形式
			RequestDispatcher rd = request.getRequestDispatcher("app_wait.jsp");
			rd.forward(request, response);

		} catch (Exception e) {
			log("ApplicationWaitListServlet error", e);
			
			try {
				List<ApplicationBean> emptyList = new ArrayList<>();
				request.setAttribute("applications", emptyList);
				request.setAttribute("currentStatus", pendingStatus);
				
				request.setAttribute("errorMessage", "申請一覧の読み込み中にエラーが発生しました。");
				RequestDispatcher rd = request.getRequestDispatcher("app_wait.jsp");
				rd.forward(request, response);
				
			} catch (Exception ex) {
				log("Fatal error in catch block", ex);
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		doGet(request, response);
	}
}