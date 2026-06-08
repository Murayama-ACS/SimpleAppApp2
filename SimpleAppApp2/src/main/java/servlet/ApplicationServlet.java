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
import model.TodaysDateTime;

@WebServlet("/Application")
public class ApplicationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String employeeId = (String) session.getAttribute("emp_id");
        
        if (employeeId == null) {
            response.sendRedirect(request.getContextPath() + "/login_mock.jsp");
            return;
        }

        ApplicationDAO dao = new ApplicationDAO();
        

        EmployeeBean employee = dao.selectEmployee(employeeId);
        String dptName = "未所属";

        if (employee != null) {
            dptName = dao.selectDepartmentName(employee.getDpt_id());
        }

        // リクエストスコープへそれぞれ格納
        request.setAttribute("employeeInfo", employee);
        request.setAttribute("departmentName", dptName);

        request.getRequestDispatcher("/application.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        try {
            HttpSession session = request.getSession();
            String employeeId = (String) session.getAttribute("emp_id");
            
            if (employeeId == null) {
                response.sendRedirect(request.getContextPath() + "/login_mock.jsp");
                return;
            }

            String applicationType = request.getParameter("applicationType");
            String paymentMethod   = request.getParameter("paymentMethod");
            String amountParam     = request.getParameter("amount");
            String content         = request.getParameter("content");
            String reason          = request.getParameter("reason");
            String note            = request.getParameter("note");
            String urgentFlagParam = request.getParameter("urgentFlag");

            int amount = 0;
            if (amountParam != null && !amountParam.trim().isEmpty()) {
                amount = Integer.parseInt(amountParam.trim());
            }

            ApplicationBean bean = new ApplicationBean();
            bean.setType(applicationType);
            bean.setAmount(amount);
            bean.setPaymentMethod(paymentMethod);
            bean.setEmployeeId(employeeId);
            bean.setContent(content);
            bean.setReason(reason);
            bean.setNote(note);

            boolean urgent = false;
            if (urgentFlagParam != null) {
                String u = urgentFlagParam.trim().toLowerCase();
                urgent = "on".equals(u) || "true".equals(u) || "1".equals(u);
            }
            bean.setUrgentFlag(urgent);
            bean.setCreateDate(TodaysDateTime.getNow());

            ApplicationDAO dao = new ApplicationDAO();
            dao.insert(bean);

            request.getRequestDispatcher("/app_submit.jsp").forward(request, response);

        } catch (Exception e) {
            log("ApplicationServlet error", e);
            
            try {
                HttpSession session = request.getSession();
                String employeeId = (String) session.getAttribute("emp_id");
                
                if (employeeId != null) {
                	ApplicationDAO dao = new ApplicationDAO();
                    EmployeeBean employee = dao.selectEmployee(employeeId);
                    String dptName = "未所属";
                    if (employee != null) {
                        dptName = dao.selectDepartmentName(employee.getDpt_id());
                    }
                    request.setAttribute("employeeInfo", employee);
                    request.setAttribute("departmentName", dptName);
                }
                
                request.setAttribute("errorMessage", "申請の登録中にエラーが発生しました。");
                request.getRequestDispatcher("/application.jsp").forward(request, response);
                
            } catch (Exception ex) {
                log("Fatal error in catch block", ex);
            }
        }
    }
}