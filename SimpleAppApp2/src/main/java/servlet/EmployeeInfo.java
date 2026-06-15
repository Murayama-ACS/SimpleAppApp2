package servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import bean.EmployeeBean;
import dao.EmployeeDAO;
import dao.EmployeeDAO.PageResult;

/**
 * Servlet implementation class EmployeeInfo
 */
@WebServlet("/EmployeeInfo")
public class EmployeeInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_LIMIT = 20;
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    request.setCharacterEncoding("UTF-8");

	    // 認証／権限チェック（既存のまま）
	    String loginUrl = "/login.jsp";
	    HttpSession session = request.getSession();
	    EmployeeBean employee = (EmployeeBean) session.getAttribute("empBean");
	    if (employee == null) {
	        response.sendRedirect(request.getContextPath() + loginUrl);
	        return;
	    }
	    String ldptId = employee.getDpt_id();
	    if (ldptId == null || !ldptId.matches("^D4.*$")) {
	        request.setAttribute("eMsg", "アクセス権限がありません。");
	        request.getRequestDispatcher(loginUrl).forward(request, response);
	        return;
	    }

	    // 検索パラメータを正規化
	    String empId   = trimToNull(request.getParameter("q_emp_id"));
	    String empName = trimToNull(request.getParameter("q_emp_name"));
	    String dptId   = trimToNull(request.getParameter("q_dpt_id"));
	    String posId   = trimToNull(request.getParameter("q_pos_id"));

	    // ソート・ページ
	    String sortKey = request.getParameter("sort");
	    String sortDir = request.getParameter("dir");

	    // page の扱い（検索ボタンで page をリセット）
	    int page = 1;
	    String pageParam = request.getParameter("page");
	    if (pageParam != null) {
	        try { page = Integer.parseInt(pageParam); } catch (NumberFormatException ex) { page = 1; }
	    }
	    if (request.getParameter("search") != null) { // 新しい検索なら1ページ目
	        page = 1;
	    }
	    if (page < 1) page = 1;

	    final int LIMIT = 20;
	    int offset = (page - 1) * LIMIT;

	    EmployeeDAO dao = new EmployeeDAO();
	    List<EmployeeBean> empList = new ArrayList<>();
	    boolean hasNext = false;
	    try {
	        // DAO側で PageResult を返すように実装すること
	        PageResult<EmployeeBean> pageRes = dao.searchEmployees(empId, empName, dptId, posId, sortKey, sortDir, LIMIT, offset);
	        empList = pageRes.getItems();
	        hasNext = pageRes.hasNext();
	    } catch (SQLException e) {
	        e.printStackTrace();
	        request.setAttribute("eMsg", "社員情報の取得中にエラーが発生しました");
	    }

	    // 検索条件・ページング情報を戻す
	    request.setAttribute("q_emp_id", empId);
	    request.setAttribute("q_emp_name", empName);
	    request.setAttribute("q_dpt_id", dptId);
	    request.setAttribute("q_pos_id", posId);
	    request.setAttribute("empList", empList);
	    request.setAttribute("page", page);
	    request.setAttribute("sort", sortKey);
	    request.setAttribute("dir", sortDir);
	    request.setAttribute("hasNext", hasNext);

	    request.getRequestDispatcher("/WEB-INF/jsp/user_info2.jsp").forward(request, response);
	}

	private String trimToNull(String s) {
	    if (s == null) return null;
	    s = s.trim();
	    return s.isEmpty() ? null : s;
	}
	/*protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    // 1. ログインチェック
	    String loginUrl = "/login.jsp";
	    HttpSession session = request.getSession();
	    EmployeeBean employee = (EmployeeBean) session.getAttribute("empBean");
	
	    if (employee == null) {
	        response.sendRedirect(request.getContextPath() + loginUrl);
	        return;
	    }
	
	    // 2. 権限チェック
	    String ldptId = employee.getDpt_id();
	    if (ldptId == null || !ldptId.matches("^D4.*$")) {
	        request.setAttribute("eMsg", "アクセス権限がありません。");
	        request.getRequestDispatcher(loginUrl).forward(request, response);
	        return;
	    }
	
	    String sortKey = request.getParameter("sort"); // 例: emp_id, emp_name, email, dpt_id, pos_id
	    String sortDir = request.getParameter("dir");  // asc or desc
	    String pageParam = request.getParameter("page");
	
	    int page = 1;
	    if (pageParam != null) {
	        try {
	            page = Integer.parseInt(pageParam);
	        } catch (NumberFormatException e) {
	            page = 1;
	        }
	    }
	    if (page < 1) page = 1;
	
	    int offset = (page - 1) * DEFAULT_LIMIT;
	
	    // debug ログ（本番では logger を使ってください）
	    System.out.println("EmployeeInfo.doGet: page=" + page + ", offset=" + offset + ", sort=" + sortKey + ", dir=" + sortDir);
	
	    EmployeeDAO empDAO = new EmployeeDAO();
	    ArrayList<EmployeeBean> empList = null;
	    try {
	        empList = empDAO.empInfo(sortKey, sortDir, offset); // DAO に offset を渡す
	        if (empList == null) empList = new ArrayList<>();
	    } catch (SQLException e) {
	        e.printStackTrace();
	        request.setAttribute("eMsg", "社員情報の取得でエラーが発生しました。");
	        empList = new ArrayList<>();
	    }
	
	    // request スコープにセット（ServletContext ではなく request）
	    request.setAttribute("empList", empList);
	    request.setAttribute("page", page);
	    request.setAttribute("sort", sortKey);
	    request.setAttribute("dir", sortDir);
	
	    RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/user_info2.jsp");
	    rd.forward(request, response);
	}*/

	/*protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 1. ログインチェック
		String loginUrl = "/login.jsp";
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
		EmployeeDAO empDAO = new EmployeeDAO();
		ArrayList<EmployeeBean> empList = empDAO.empInfo();
		if(empList == null) {
			empList = new ArrayList<EmployeeBean>();
			System.out.println("empListが空です");
		}
	
		ServletContext app = this.getServletContext();
		app.setAttribute("empList", empList);
		RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/jsp/user_info.jsp");
		rd.forward(request, response);
	}*/
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

}
