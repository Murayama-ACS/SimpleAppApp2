package servlet;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import bean.EmployeeBean;
import dao.EmployeeDAO;
import dao.FailedLoginDAO;

@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String url = "login.jsp";
		String identifier = request.getParameter("identifier");
		String pass = request.getParameter("pass");
		String eMsg = "社員IDもしくはメールアドレスを入力してください。";

		if (identifier == null || identifier.trim().isEmpty()) {
			request.setAttribute("eMsg", eMsg);
		} else {
			EmployeeDAO empDAO = new EmployeeDAO();
			String input = identifier.trim();
			boolean isEmail = input.contains("@");
			String baseMsg = isEmail ? "メールアドレスかパスワードの入力が間違っています。" : "社員IDかパスワードの入力が間違っています。";

			// リクエスト情報（監査やログ用、DAOに渡す）
			String remoteAddr = request.getRemoteAddr();
			String userAgent = request.getHeader("User-Agent");

			// 追加：登録時と同じパスワードポリシー
			final String PASS_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";

			try {
				if (pass == null ||!pass.matches(PASS_PATTERN)) {
					// ポリシー違反の入力は認証処理しない（failed_login は増やさない）
					// 表示は汎用のエラーメッセージ（ユーザ存在を漏らさない）
					request.setAttribute("eMsg", baseMsg);
				}else {
					EmployeeBean empBean = empDAO.authenticateAndGetEmployee(input, pass, isEmail, remoteAddr, userAgent);

					if(empBean == null) {
						// 従来のメッセージ
						// 残り回数を取得して表示（存在しない場合は null が返るので汎用メッセージ）
						FailedLoginDAO flDao = new FailedLoginDAO();
						try {
							// empId が直接わかる場合はそれを使う。email の場合は先に emp_id を取得する
							String empIdForCheck = null;
							if (isEmail) {
								// email から emp_id を取得（EmployeeDAO にメソッドを追加しても良い）
								empIdForCheck = new EmployeeDAO().findEmpIdByEmail(input); // 実装例を次に示します
							} else {
								empIdForCheck = input;
							}

							if (empIdForCheck != null) {
								Integer remaining = flDao.getRemainingPasswordAttemptsByEmpId(empIdForCheck);
								if (remaining != null) {
									if (remaining == -1) {
										// ロック中
										eMsg = "アカウントは一時的にロックされています。時間を置いて再試行してください。";
									} else {
										eMsg = baseMsg + " あと " + remaining + " 回でアカウントがロックされます。";
									}
								} else {
									// ユーザが存在しないか記録なし → 汎用メッセージのまま
									eMsg = baseMsg;
								}
							} else {
								eMsg = baseMsg;
							}
						} catch (SQLException ex) {
							// DBエラーは汎用メッセージで
							ex.printStackTrace();
							eMsg = baseMsg;
						}
						request.setAttribute("eMsg", eMsg);
					} else {
						// 認証成功
						HttpSession session = request.getSession();
						session.setAttribute("empBean", empBean);
						if (pass != null && pass.equals("Abcd1234")) {
							url = "WEB-INF/jsp/pass_reset1.jsp";
						} else {
							url = "WEB-INF/jsp/toppage.jsp";
						}
					}
				}
			} catch (SQLException ex) {
				// DBエラー等：内部用ログのみ出す（平文は出さない）
				ex.printStackTrace();
				request.setAttribute("eMsg", "システムエラーが発生しました。時間を置いて再度お試しください。");
			}

		}

		RequestDispatcher rd = request.getRequestDispatcher(url);
		rd.forward(request, response);
	}
	//	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	//		request.setCharacterEncoding("UTF-8");
	//		String url = "login.jsp";
	//		String identifier = request.getParameter("identifier");
	//		String pass = request.getParameter("pass");
	//		String eMsg = "社員IDもしくはメールアドレスを入力してください。";
	//		
	//		if(identifier.equals("")) {
	//			//社員ID or メールアドレスが入力されていない場合はエラーメッセージをセットしてlogin.jspに戻る
	//			request.setAttribute("eMsg",eMsg);
	//		}else {
	//			//
	//			EmployeeDAO empDAO = new EmployeeDAO(); 
	//			String input = identifier.trim();
	//			boolean isEmail = input.contains("@");
	//			System.out.println(input);
	//			System.out.println(pass);
	//
	//			System.out.println(isEmail);
	//			EmployeeBean empBean = empDAO.empInfo(identifier, pass, isEmail);
	//			//System.out.println("employee:" + empBean);
	//			if(empBean == null) {
	//				//従業員情報が取得できなかった場合はエラーメッセージをセットしてlogin.jspに戻る
	//				if(isEmail) {
	//					eMsg = "メールアドレスかパスワードの入力が間違っています。";
	//				}else {
	//					eMsg = "社員IDかパスワードの入力が間違っています。";
	//				}
	//				request.setAttribute("eMsg",eMsg);
	//			}else {
	//				//従業員情報を取得できたらセッションスコープにセットしてtoppage.jspにフォワードする
	//				HttpSession session = request.getSession();
	//				session.setAttribute("empBean", empBean);
	//				if(pass.equals("1234")) {
	//					url = "WEB-INF/jsp/pass_reset1.jsp";
	//				}else {
	//					url = "WEB-INF/jsp/toppage.jsp";
	//				}
	//			}
	//		}
	//		System.out.println(url + "+" + pass);
	//		RequestDispatcher rd = request.getRequestDispatcher(url);
	//		rd.forward(request, response);
	//	}

}
