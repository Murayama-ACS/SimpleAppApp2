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

/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String url = "/index.jsp";
		String identifier = request.getParameter("empId");
		String pass = request.getParameter("password");
		
		if (identifier == null || identifier.trim().isEmpty() || pass == null || pass.isEmpty()) {
            request.setAttribute("eMsg", "社員IDもしくはメールアドレスと、パスワードを入力してください。");
            RequestDispatcher rd = request.getRequestDispatcher(url);
    		rd.forward(request, response);
            return;
        }
		
		String input = identifier.trim();
        boolean isEmail = input.contains("@");
        String baseMsg = isEmail ? "メールアドレスかパスワードの入力が間違っています。" : "社員IDかパスワードの入力が間違っています。";
        
        String remoteAddr = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        final String PASS_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";

        if (!pass.matches(PASS_PATTERN)) {
            request.setAttribute("eMsg", baseMsg);
            RequestDispatcher rd = request.getRequestDispatcher(url);
    		rd.forward(request, response);
            return;
        }

        EmployeeDAO empDAO = new EmployeeDAO();
        
        try {
            EmployeeBean empBean = empDAO.authenticateAndGetEmployee(input, pass, isEmail, remoteAddr, userAgent);

            if (empBean == null) {
                FailedLoginDAO flDao = new FailedLoginDAO();
                String empIdForCheck = isEmail ? empDAO.findEmpIdByEmail(input) : input;

                if (empIdForCheck != null) {
                    Integer remaining = flDao.getRemainingPasswordAttemptsByEmpId(empIdForCheck);
                    
                    if (remaining != null) {
                        if (remaining == -1) {
                            request.setAttribute("eMsg", "アカウントは一時的にロックされています。時間を置いて再試行してください。");
                        } else {
                            request.setAttribute("eMsg", baseMsg + "（あと " + remaining + " 回でアカウントがロックされます）");
                        }
                    } else {
                        request.setAttribute("eMsg", baseMsg);
                    }
                } else {
                    request.setAttribute("eMsg", baseMsg);
                }
                RequestDispatcher rd = request.getRequestDispatcher(url);
        		rd.forward(request, response);
            } else {
                HttpSession session = request.getSession();
                session.setAttribute("empBean", empBean);

                if (pass.equals("Abcd1234")) {
                	RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/pass_reset1.jsp");
            		rd.forward(request, response);               
                } else {
                    RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/top_page.jsp");
            		rd.forward(request, response);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            request.setAttribute("eMsg", "システムエラーが発生しました。時間を置いて再度お試しください。");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }

	}


