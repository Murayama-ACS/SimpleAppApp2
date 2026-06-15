package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import bean.ApplicationBean;
import bean.EmployeeBean;
import dao.ApplicationDAO;

@WebServlet("/ApplicationDetail")
public class ApplicationDetailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		// 1. ログインチェック（セッション検証）ブロック
		HttpSession session = request.getSession();
		EmployeeBean employee = (EmployeeBean) session.getAttribute("loginEmployee");
		
		if (employee == null) {
			response.sendRedirect(request.getContextPath() + "/login_mock.jsp");
			return;
		}

		// 2. パラメータ取得およびデータ取得ブロック
		String apctId = request.getParameter("apct_id");
		ApplicationDAO dao = new ApplicationDAO();
		
		// 申請IDをキーに、部署名・氏名・ステータス名を含んだ申請データを取得
		ApplicationBean app = dao.findById(apctId);

		// 3. 画面遷移（データフロー）ブロック
		if (app != null) {
			// データが存在すれば、リクエストスコープに格納して詳細画面へ
			request.setAttribute("application", app);
			request.getRequestDispatcher("/app_detail.jsp").forward(request, response);
		} else {
			// 万が一データが見つからない場合はエラーメッセージを戻して履歴一覧へ
			request.setAttribute("errorMessage", "指定された申請データが見つからないか、既に削除されています。");
			request.getRequestDispatcher("/ApplicationHistoryServlet").forward(request, response);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		doGet(request, response);
	}
}