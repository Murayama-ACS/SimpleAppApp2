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

import bean.DepartmentBean;
import bean.EmployeeBean;
import bean.PositionBean;
import dao.DepartmentDAO;
import dao.EmployeeDAO;
import dao.PositionDAO;

/**
 * Servlet implementation class EmployeeUpdate
 */
@WebServlet("/EmployeeUpdate")
public class EmployeeUpdate extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 1. ログインチェック
		String loginUrl = "/index.jsp";
		HttpSession session = request.getSession();
		EmployeeBean employee = (EmployeeBean) session.getAttribute("empBean"); 

		if (employee == null) {
			response.sendRedirect(request.getContextPath() + loginUrl);
			return;
		}

		// 2. 権限チェック
		String ldptId = employee.getDpt_id();
		if (!ldptId.matches("^D4.*$")) {
			request.setAttribute("eMsg", "アクセス権限がありません。");
			request.getRequestDispatcher(loginUrl).forward(request, response);
			return;
		}
		final String JSP_CONFIRM  = "WEB-INF/jsp/user_confirm.jsp";
		final String JSP_UPDATE   = "WEB-INF/jsp/user_update.jsp";
		final String JSP_COMPLETE = "WEB-INF/jsp/user_complete.jsp";
		final String JSP_SIGNUP   = "WEB-INF/jsp/user_signup.jsp";

		String action = safe(request.getParameter("action"));
		String url = JSP_CONFIRM;
		
		
		DepartmentDAO deptDao = new DepartmentDAO();
	    List<DepartmentBean> dptList = deptDao.findAll(); // DB から全部署を取得するメソッド
	    request.setAttribute("dptList", dptList);
	    
	    PositionDAO posDao = new PositionDAO();
	    List<PositionBean> posList = posDao.findAll();
	    request.setAttribute("posList", posList);
	    
		switch (action) {
		case "updateform":
			url = JSP_UPDATE;
			// update 用パラメータから EmployeeBean を作成してセッションに保存
			
			session.setAttribute("updateEmpBean", buildBeanFromRequest(request, "update"));
			break;

		case "update":
			url = JSP_UPDATE;
			EmployeeBean updateBean = (EmployeeBean) session.getAttribute("updateEmpBean");
			if (updateBean == null) {
				request.setAttribute("eMsg", "更新対象のデータが見つかりません。セッションが切れている可能性があります。");
				break;
			}

			// パラメータ取得
			String empId = updateBean.getEmp_id(); // ID はセッションのものを採用
			String empName = safe(request.getParameter("emp_name"));
			String empFurigana = safe(request.getParameter("furigana"));
			String email = safe(request.getParameter("email"));
			String dptId = request.getParameter("dpt_id");
			String posId = request.getParameter("pos_id");
			
			
			
			if (isNullOrEmpty(empId) || isNullOrEmpty(empName) || isNullOrEmpty(empFurigana) || isNullOrEmpty(email) || dptId == null || posId == null) {
				request.setAttribute("eMsg", "社員ID、名前、ふりがな、Email、部署、役職のいずれかが入力されていません。");
				break;
			}

			boolean changed = false;
			if (!empName.equals(updateBean.getEmp_name())) {
				updateBean.setEmp_name(empName); // 元コードの誤りを修正（emp_id を上書きしていた）
				changed = true;
			}
			if (!empFurigana.equals(updateBean.getFurigana())) {
				updateBean.setFurigana(empFurigana); // 元コードの誤りを修正（emp_id を上書きしていた）
				changed = true;
			}
			if (!email.equals(updateBean.getEmail())) {
				updateBean.setEmail(email);
				changed = true;
			}
			if (!dptId.equals(updateBean.getDpt_id())) {
				updateBean.setDpt_id(dptId);
				changed = true;
			}
			if (!posId.equals(updateBean.getPos_id())) {
				updateBean.setPos_id(posId);
				changed = true;
			}
			if (changed) {
				EmployeeDAO empDAO = new EmployeeDAO();
				int updateResult = empDAO.updateEmpInfo(updateBean);
				if (updateResult == 0) {
					request.setAttribute("eMsg", "更新に失敗しました。");
				} else if (updateResult == -1) {
					request.setAttribute("eMsg", "既に登録されているメールアドレスのため、更新できません。");
				} else {
					session.removeAttribute("updateEmpBean");
					url = JSP_COMPLETE;
				}
			} else {
				// 変更なし → 確認ページへ遷移（必要に応じてメッセージを表示）
				url = JSP_COMPLETE;
			}
			break;

		default: // user_signup→user_confirm2 相当
			EmployeeBean insertBean = (EmployeeBean) session.getAttribute("insertEmpBean");
			if (insertBean == null) {
				request.setAttribute("eMsg", "データが登録できていません");
			} else {
				EmployeeDAO empDAO = new EmployeeDAO();
				int result = empDAO.insertEmployee(insertBean);
				if (result == 0) {
					request.setAttribute("eMsg", "社員の新規登録に失敗しました。");
					url = JSP_SIGNUP;
				} else if (result == -1) {
					request.setAttribute("eMsg", "社員IDもしくはメールアドレスが重複している社員が存在するため、登録に失敗しました。");
					url = JSP_SIGNUP;
				} else {
					session.removeAttribute("insertEmpBean");
					url = JSP_COMPLETE;
				}
			}
			break;
		}

		RequestDispatcher rd = request.getRequestDispatcher(url);
		rd.forward(request, response);
	}


	private static boolean isNullOrEmpty(String s) {
		return s == null || s.isEmpty();
	}

	private static String safe(String s) {
		return s == null ? "" : s;
	}

	private EmployeeBean buildBeanFromRequest(HttpServletRequest req, String prefix) {
		String empId = safe(req.getParameter(prefix + "Emp_id"));
		String empName = safe(req.getParameter(prefix + "Emp_name"));
		String empFurigana = safe(req.getParameter(prefix + "Emp_furigana"));
		String email = safe(req.getParameter(prefix + "Email"));
		String dptId = safe(req.getParameter(prefix + "Dpt_id"));
		String posId = safe(req.getParameter(prefix + "Pos_id"));
		return new EmployeeBean(empId, empName, empFurigana, email, dptId, posId);
	}
}


