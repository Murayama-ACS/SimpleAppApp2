package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bean.ApplicationBean;
import dao.ApplicationDAO;
import model.TodaysDateTime;

// 送信先のパスは form の action と合わせる
@WebServlet("/Application")
public class ApplicationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // GET はフォームにリダイレクト（通常は POST を使うため）
        response.sendRedirect(request.getContextPath() + "/application.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 文字エンコーディング（必要なら web.xml 等で統一設定）
        request.setCharacterEncoding("UTF-8");

        try {
            // ① リクエストパラメータ取得
            String applicationType = request.getParameter("applicationType"); // 申請種別
            String paymentMethod   = request.getParameter("paymentMethod");   // 精算方法
            String employeeId      = request.getParameter("employeeId");      // 社員ID
            String content         = request.getParameter("content");         // 申請内容
            String reason          = request.getParameter("reason");          // 申請理由
            String note            = request.getParameter("note");            // 備考
            String urgentFlagParam = request.getParameter("urgentFlag");      // 緊急フラグ (チェックボックス等)

            // ② Bean にデータ格納
            ApplicationBean bean = new ApplicationBean();
            bean.setType(applicationType);
            bean.setPaymentMethod(paymentMethod);
            bean.setEmployeeId(employeeId);
            bean.setContent(content);
            bean.setReason(reason);
            bean.setNote(note);

            // 緊急フラグの解釈（フォーム側で "on" / "true" / "1" のいずれかが送られる想定）
            boolean urgent = false;
            if (urgentFlagParam != null) {
                String u = urgentFlagParam.trim().toLowerCase();
                urgent = "on".equals(u) || "true".equals(u) || "1".equals(u);
            }
            bean.setUrgentFlag(urgent);

            // ③ 日時設定
            // TodaysDateTime.getNow() はプロジェクト実装に合わせて型 (String/Date/LocalDateTime) を合わせる
            bean.setCreateDate(TodaysDateTime.getNow());

            // ④ DB登録（DAO呼び出し）
            ApplicationDAO dao = new ApplicationDAO();
            dao.insert(bean); // 例外は上位で捕捉

            // ⑤ JSPへフォワード（完了画面）
            request.getRequestDispatcher("/app_submit.jsp").forward(request, response);

        } catch (Exception e) {
            // エラーハンドリング（ログ出力・エラーページ遷移など）
            log("ApplicationServlet error", e);
            request.setAttribute("errorMessage", "申請の登録中にエラーが発生しました。");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
}