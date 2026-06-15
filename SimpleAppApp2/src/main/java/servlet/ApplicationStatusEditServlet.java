package servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import bean.ApplicationBean;
import bean.ApprovalBean;
import bean.EmployeeBean;
import dao.ApplicationDAO;
import dao.ApprovalDAO;

@WebServlet("/ApplicationStatusEdit")
public class ApplicationStatusEditServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // 1. ログインチェック
        HttpSession session = request.getSession();
        EmployeeBean employee = (EmployeeBean) session.getAttribute("loginEmployee");

        if (employee == null) {
            response.sendRedirect(request.getContextPath() + "/login_mock.jsp");
            return;
        }

        // 2. 権限チェック（経理部限定）
        String dptId = employee.getDpt_id();
        if (!"D200".equals(dptId)) {
            request.setAttribute("eMsg", "アクセス権限がありません。経理部専用の機能です。");
            request.getRequestDispatcher("/login_mock.jsp").forward(request, response);
            return;
        }

        // 3. パラメータの取得
        String apctId = request.getParameter("apct_id");

        if (apctId == null || apctId.trim().isEmpty()) {
            forwardToError(request, response, "不正なリクエストパラメータです。申請IDがありません。");
            return;
        }

        // 【修正点】JSPのコメント欄削除に伴い、変数を null で固定
        // これにより、履歴テーブル（approvals）の comment カラムには確実に NULL が挿入されます
        String commentValue = null;

        try {
            ApplicationDAO appDao = new ApplicationDAO();
            ApprovalDAO approvalDao = new ApprovalDAO();

            // 対象データの存在確認
            ApplicationBean application = appDao.findById(apctId);
            if (application == null) {
                forwardToError(request, response, "対象の申請データが見つかりません。");
                return;
            }

            // ステータスチェック（3:管理部承認 または 4:社長承認 のみ経理処理可能）
            int currentStatus = application.getStatus_id();
            if (currentStatus != 3 && currentStatus != 4) {
                forwardToError(request, response, "この申請は現在、経理処理を実行できるステータスではありません。");
                return;
            }

            // 次のステータスIDは「5（経理完了）」にサーバー側で決定
            int nextStatusId = 5;
            LocalDateTime now = LocalDateTime.now();

            // 4. 承認履歴ID (approval_id) の生成
            String timeStamp = new SimpleDateFormat("yyMMddHHmmssSSS").format(new Date());
            String approvalId = "APV" + timeStamp;

            // 5. ApprovalBean の作成と設定
            ApprovalBean approval = new ApprovalBean();
            approval.setApprovalId(approvalId);
            approval.setApctId(apctId);
            approval.setEmployeeId(employee.getEmp_id()); 
            approval.setStatusId(nextStatusId);
            approval.setComment(commentValue); // null を設定
            approval.setCreateDate(now);

            // 6. データベース更新処理
            int insertResult = approvalDao.insert(approval);

            if (insertResult > 0) {
                // applicationsテーブルのstatus_idを「5」に更新
                int updateResult = appDao.updateStatus(apctId, nextStatusId, now);
                if (updateResult == 0) {
                    forwardToError(request, response, "申請データの状態更新に失敗しました。");
                    return;
                }
            } else {
                forwardToError(request, response, "承認履歴データの登録に失敗しました。");
                return;
            }

            // 7. 更新後の最新データを再取得してリクエストスコープに格納
            ApplicationBean updatedApplication = appDao.findById(apctId);
            request.setAttribute("application", updatedApplication);
            request.setAttribute("showSuccessPopup", true);

            // app_status.jsp へフォワードして完了ポップアップを表示させる
            RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/jsp/app_status.jsp");
            rd.forward(request, response);

        } catch (Exception e) {
            log("ApplicationStatusEditServlet 処理エラー", e);
            forwardToError(request, response, "処理中にシステムエラーが発生しました。");
        }
    }

    private void forwardToError(HttpServletRequest request, HttpServletResponse response, String message) 
            throws ServletException, IOException {
        request.setAttribute("errorMessage", message);
        request.getRequestDispatcher("/ApplicationStatus").forward(request, response);
    }
}