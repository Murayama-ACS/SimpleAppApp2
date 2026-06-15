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

    // =========================================================================
    // 【POSTリクエスト処理ブロック】詳細画面からの完了指示に伴うDB更新制御
    // =========================================================================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // 【1. ログイン検証】
        HttpSession session = request.getSession();
        EmployeeBean employee = (EmployeeBean) session.getAttribute("loginEmployee");

        if (employee == null) {
            response.sendRedirect(request.getContextPath() + "/login_mock.jsp");
            return;
        }

        // 【2. 経理部権限検証】
        String dptId = employee.getDpt_id();
        if (!"D200".equals(dptId)) {
            request.setAttribute("eMsg", "アクセス権限がありません。経理部専用の機能です。");
            request.getRequestDispatcher("/login_mock.jsp").forward(request, response);
            return;
        }

        // 【3. リクエストパラメータ取得・検証】
        String apctId = request.getParameter("apct_id");

        if (apctId == null || apctId.trim().isEmpty()) {
            forwardToError(request, response, "不正なリクエストパラメータです。申請IDがありません。");
            return;
        }

        // 履歴テーブル(approvals)のcommentカラム挿入用。null固定によりDBにはNULLが保持されます
        String commentValue = null;

        // 【4. 申請状況のチェックおよび更新データの作成】
        try {
            ApplicationDAO appDao = new ApplicationDAO();
            ApprovalDAO approvalDao = new ApprovalDAO();

            // 対象申請データの存在確認
            ApplicationBean application = appDao.findById(apctId);
            if (application == null) {
                forwardToError(request, response, "対象の申請データが見つかりません。");
                return;
            }

            // 現在の状態検証（3:管理部承認 または 4:社長承認 のみ経理処理可能）
            int currentStatus = application.getStatus_id();
            if (currentStatus != 3 && currentStatus != 4) {
                forwardToError(request, response, "この申請は現在、経理処理を実行できるステータスではありません。");
                return;
            }

            // 更新用ステータス（5:経理完了）と現在日時の確定
            int nextStatusId = 5;
            LocalDateTime now = LocalDateTime.now();

            // 承認履歴ID(approval_id)のユニークキー生成
            String timeStamp = new SimpleDateFormat("yyMMddHHmmssSSS").format(new Date());
            String approvalId = "APV" + timeStamp;

            // 登録用ApprovalBeanの構築
            ApprovalBean approval = new ApprovalBean();
            approval.setApprovalId(approvalId);
            approval.setApctId(apctId);
            approval.setEmployeeId(employee.getEmp_id()); 
            approval.setStatusId(nextStatusId);
            approval.setComment(commentValue);
            approval.setCreateDate(now);

            // 【5. データベーストランザクション処理（履歴挿入および状態更新）】
            int insertResult = approvalDao.insert(approval);

            if (insertResult > 0) {
                // applicationsテーブルの対象レコードをステータス5へ上書き更新
                int updateResult = appDao.updateStatus(apctId, nextStatusId, now);
                if (updateResult == 0) {
                    forwardToError(request, response, "申請データの状態更新に失敗しました。");
                    return;
                }
            } else {
                forwardToError(request, response, "承認履歴データの登録に失敗しました。");
                return;
            }

            // 【6. 更新結果の再取得および完了画面への遷移】
            // 画面に最新状態を反映するため再度データを読み直してリクエストへ格納
            ApplicationBean updatedApplication = appDao.findById(apctId);
            request.setAttribute("application", updatedApplication);
            request.setAttribute("showSuccessPopup", true);

            // 完了通知用ポップアップを実行させるため、再度詳細画面(app_status.jsp)へフォワード
            RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/jsp/app_status.jsp");
            rd.forward(request, response);

        } catch (Exception e) {
            // 例外発生時のログ出力およびエラー復旧処理
            log("ApplicationStatusEditServlet 処理エラー", e);
            forwardToError(request, response, "処理中にシステムエラーが発生しました。");
        }
    }

    // =========================================================================
    // 【共通エラー処理ブロック】不具合発生時の安全な画面引き戻し制御
    // =========================================================================
    private void forwardToError(HttpServletRequest request, HttpServletResponse response, String message) 
            throws ServletException, IOException {
        request.setAttribute("errorMessage", message);
        request.getRequestDispatcher("/ApplicationStatus").forward(request, response);
    }
}