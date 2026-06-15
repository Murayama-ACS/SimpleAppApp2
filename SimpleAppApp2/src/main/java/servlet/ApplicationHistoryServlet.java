package servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import bean.ApplicationBean;
import bean.EmployeeBean;
import dao.ApplicationDAO;

@WebServlet("/ApplicationHistoryServlet")
public class ApplicationHistoryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		// 1. ログインチェック（セッション検証）ブロック
		HttpSession session = request.getSession();
		EmployeeBean employee = (EmployeeBean) session.getAttribute("loginEmployee");
		
		// 未ログイン状態（セッション切れ含む）の場合は、ログイン画面へ強制リダイレクト
		if (employee == null) {
			response.sendRedirect(request.getContextPath() + "/login_mock.jsp");
			return;
		}

		// ログインユーザーの属性（役職ID、部署ID）を取得
		String posId = employee.getPos_id();
		String dptId = employee.getDpt_id();

		// 2. 表示対象範囲（scope）の初期化とガード処理ブロック
		// リクエストからスコープパラメータ（"self", "subordinate", "management"）を取得
		String scope = request.getParameter("scope");
		if (scope == null || scope.isEmpty()) {
			scope = "self"; // 指定がない場合のデフォルトは「自身」
		}

		// ガード処理①：管理部(D100)以外の一般社員(E00)は、強制的に「自身」のみに制限
		if ("E00".equals(posId) && !"D100".equals(dptId)) {
			scope = "self";
		}
		
		// ガード処理②：「管理」データを要求できるのは管理部(D100)のみとし、それ以外は「自身」に制限
		if ("management".equals(scope) && !"D100".equals(dptId)) {
			scope = "self";
		}

		// 3. ステータスフィルターの変換ブロック
		// 画面の切り替えパラメータ（"unapproved", "all"）を取得
		String filter = request.getParameter("filter");
		if (filter == null || filter.isEmpty()) {
			filter = "unapproved"; // 指定がない場合のデフォルトは「未完了」
		}

		// DAO（SQL）での絞り込み条件に適合する内部キーワード（"incomplete", "all"）に変換
		String statusFilter = "all";
		if ("unapproved".equals(filter)) {
			statusFilter = "incomplete"; // "unapproved" の時は "incomplete"（1〜4）として扱う
		}

		// 4. データ取得および画面遷移（メイン処理）ブロック
		try {
			ApplicationDAO dao = new ApplicationDAO();
			
			// 表示用マスタデータの取得（部署名）
			String dptName = dao.selectDepartmentName(dptId);
			
			// 決定した権限（scope）とフィルター状態（statusFilter）を基に、該当する申請履歴リストを取得
			List<ApplicationBean> list = dao.getHistoryApplications(employee, scope, statusFilter);

			// 画面（JSP）へ引き渡す各種データをリクエストスコープに格納
			request.setAttribute("empBean", employee);
			request.setAttribute("dpt_name", dptName);
			request.setAttribute("appList", list);
			request.setAttribute("currentScope", scope);
			request.setAttribute("currentStatusFilter", statusFilter);

			// 申請履歴一覧画面（history.jsp）へフォワード（内部遷移）
			RequestDispatcher rd = request.getRequestDispatcher("/history.jsp");
			rd.forward(request, response);

		// 5. 例外発生時の自画面エラーハンドリングブロック
		} catch (Exception e) {
			e.printStackTrace(); 
			
			// エラーメッセージをリクエストスコープに格納（JSP最下部でアラート表示される）
			request.setAttribute("errorMessage", "履歴情報の取得中にシステムエラーが発生しました。詳細: " + e.getMessage());
			
			// history.jspのヘッダーやスクリプトレットでNullPointerExceptionを起こさないよう、最低限必要なデータを補填
			request.setAttribute("empBean", employee);
			request.setAttribute("currentScope", scope);
			request.setAttribute("currentStatusFilter", statusFilter);
			
			// ログイン画面へ戻さず、自身のページ（history.jsp）へフォワードしてその場でエラーを通知
			request.getRequestDispatcher("/history.jsp").forward(request, response);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		// POSTリクエスト受信時も、同様にdoGetメソッドを呼び出して一元処理する
		doGet(request, response);
	}
}