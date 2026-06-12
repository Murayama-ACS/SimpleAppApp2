package servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import bean.EmployeeBean;
import dao.EmployeeDAO;

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
		final String JSP_CONFIRM  = "WEB-INF/jsp/user_confirm.jsp";
		final String JSP_UPDATE   = "WEB-INF/jsp/user_update.jsp";
		final String JSP_CONFIRM2 = "WEB-INF/jsp/user_confirm2.jsp";
		final String JSP_SIGNUP   = "WEB-INF/jsp/user_signup.jsp";

		String action = safe(request.getParameter("action"));
		HttpSession session = request.getSession();
		String url = JSP_CONFIRM;

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
			String email = safe(request.getParameter("email"));
			String dptId = request.getParameter("dpt_id");
			String posId = request.getParameter("pos_id");

			if (isNullOrEmpty(empId) || isNullOrEmpty(empName) || isNullOrEmpty(email) || dptId == null || posId == null) {
				request.setAttribute("eMsg", "社員ID、名前、Email、部署、役職のいずれかが入力されていません。");
				break;
			}

			boolean changed = false;
			if (!empName.equals(updateBean.getEmp_name())) {
				updateBean.setEmp_name(empName); // 元コードの誤りを修正（emp_id を上書きしていた）
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
					url = JSP_CONFIRM2;
				}
			} else {
				// 変更なし → 確認ページへ遷移（必要に応じてメッセージを表示）
				url = JSP_CONFIRM2;
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
					url = JSP_CONFIRM2;
				}
			}
			break;
		}

		RequestDispatcher rd = request.getRequestDispatcher(url);
		rd.forward(request, response);
	}
	//		String action = request.getParameter("action");
	//		String url = "WEB-INF/jsp/user_confirm.jsp";
	//		HttpSession session = request.getSession();
	//
	//		if(action.equals("updateform")) {//user_info→user_updateへの遷移
	//			url = "WEB-INF/jsp/user_update.jsp";
	//			String emp_id = request.getParameter("updateEmp_id");
	//			String emp_name = request.getParameter("updateEmp_name");
	//			String email = request.getParameter("updateEmail");
	//			String dpt_id = request.getParameter("updateDpt_id");
	//			String pos_id = request.getParameter("updatePos_id");
	//			EmployeeBean empBean = new EmployeeBean(emp_id, emp_name, email, dpt_id, pos_id);
	//			session.setAttribute("updateEmpBean", empBean);
	//		}else if(action.equals("update")){//user_update→user_confirmへの遷移
	//			url = "WEB-INF/jsp/user_update.jsp";
	//			EmployeeBean empBean = (EmployeeBean)session.getAttribute("updateEmpBean");
	//			String emp_id = empBean.getEmp_id();
	//			String emp_name = request.getParameter("emp_name");
	//			String email = request.getParameter("email");
	//			String dpt_id = request.getParameter("dpt_id");
	//			String pos_id = request.getParameter("pos_id");
	//
	//			if(emp_id.isEmpty() || emp_name.isEmpty() || email.isEmpty() || dpt_id == null || pos_id == null) {
	//				request.setAttribute("eMsg", "社員ID、名前、Email、部署、役職のいずれかが入力されていません。");
	//			}else{
	//				if(!emp_name.equals(empBean.getEmp_name())) {
	//					empBean.setEmp_id(emp_id);
	//				}
	//				if(!email.equals(empBean.getEmail())) {
	//					empBean.setEmail(email);
	//				}
	//				if(!dpt_id.equals(empBean.getDpt_id())) {
	//					empBean.setDpt_id(dpt_id);
	//				}
	//				if(!pos_id.equals(empBean.getPos_id())) {
	//					empBean.setPos_id(pos_id);
	//				}
	//				EmployeeDAO empDAO = new EmployeeDAO();
	//				int updateResult = empDAO.updateEmpInfo(empBean);
	//				if(updateResult == 0) {
	//					request.setAttribute("eMsg", "更新に失敗しました。");
	//				}else if(updateResult == -1){
	//					request.setAttribute("eMsg", "既に登録されているメールアドレスのため、更新できません。");
	//				}else {
	//					session.removeAttribute("updateEmpBean");
	//					url = "WEB-INF/jsp/user_confirm2.jsp";
	//				}
	//			}
	//		}else {//user_signup→user_confirm2への遷移
	//			EmployeeBean insertEmpBean = (EmployeeBean)session.getAttribute("insertEmpBean");
	//			if(insertEmpBean == null) {
	//				request.setAttribute("eMsg", "データが登録できていません");
	//			}else {
	//				EmployeeDAO empDAO = new EmployeeDAO();
	//				int result = empDAO.insertEmployee(insertEmpBean);
	//				if(result == 0) {
	//					request.setAttribute("eMsg", "社員の新規登録に失敗しました。");
	//					url = "WEB-INF/jsp/user_signup.jsp";
	//				}else if(result == -1) {
	//					request.setAttribute("eMsg", "社員IDもしくはメールアドレスが重複している社員が存在するため、登録に失敗しました。");
	//					url = "WEB-INF/jsp/user_signup.jsp";
	//				}else {
	//					session.removeAttribute("insertEmpBean");
	//					url = "WEB-INF/jsp/user_confirm2.jsp";
	//				}
	//			}
	//		}
	//		RequestDispatcher rd = request.getRequestDispatcher(url);
	//		rd.forward(request, response);
	//	}

	private static boolean isNullOrEmpty(String s) {
		return s == null || s.isEmpty();
	}

	private static String safe(String s) {
		return s == null ? "" : s;
	}

	private EmployeeBean buildBeanFromRequest(HttpServletRequest req, String prefix) {
		// prefix 例: "update" → パラメータ名 "updateEmp_id", "updateEmp_name", ...
		String empId = safe(req.getParameter(prefix + "Emp_id"));
		String empName = safe(req.getParameter(prefix + "Emp_name"));
		String email = safe(req.getParameter(prefix + "Email"));
		String dptId = safe(req.getParameter(prefix + "Dpt_id"));
		String posId = safe(req.getParameter(prefix + "Pos_id"));
		return new EmployeeBean(empId, empName, email, dptId, posId);
	}
}



