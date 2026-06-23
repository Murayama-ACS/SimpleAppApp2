package Test;

import java.time.LocalDateTime;
import java.util.List;

import bean.ApplicationBean;
import bean.ApprovalBean;
import bean.NotificationBean;
import dao.ApplicationDAO;
import dao.ApprovalDAO;

public class ApprovalDAOTest {
    public static void main(String[] args) {
        ApprovalDAO approvalDao = new ApprovalDAO();
        ApplicationDAO appDao = new ApplicationDAO();

        // テスト用ID（必要に応じて環境に合わせて変更してください）
        String applicantApctId = "TEST_APCT_FUNC";      // application の ID（存在しなければ作成）
        String applicantEmpId = "A1";   // applications.emp_id（申請者）
        String approverEmpId  = "A1230";    // approvals.emp_id（承認者）
        String approvalId1    = "TEST_APPROV_001";
        String approvalId2    = "TEST_APPROV_002";

        System.out.println("=== ApprovalDAO 関数テスト開始 ===");

        // 事前チェック：対象の application が無ければ ApplicationDAO.insert で作る
        try {
            ApplicationBean app = appDao.findById(applicantApctId);
            if (app == null) {
                System.out.println("テスト用 application が存在しないため作成します: " + applicantApctId);
                ApplicationBean newApp = new ApplicationBean();
                newApp.setApctId(applicantApctId);
                newApp.setEmployeeId(applicantEmpId);
                newApp.setContent("テスト用コンテンツ");
                newApp.setType("TestType");
                newApp.setPaymentMethod("振込");
                newApp.setAmount(1000);
                newApp.setReason("テスト準備");
                newApp.setNote("備考");
                newApp.setUrgent("false");
                newApp.setStatus_id(1);
                newApp.setCreateDate(LocalDateTime.now());
                newApp.setUpdateDate(LocalDateTime.now());
                newApp.setDeleted(false);
                int r = appDao.insert(newApp);
                System.out.println("ApplicationDAO.insert returned: " + r + " (0/1)");
            } else {
                System.out.println("テスト対象 application が既に存在: " + applicantApctId);
            }
        } catch (Exception e) {
            System.err.println("事前準備（application チェック/作成）で例外: " + e.getMessage());
            e.printStackTrace();
        }

        // 1) insert with createDate specified
        try {
            System.out.println("\n--- Test1: insert (createDate 指定) ---");
            ApprovalBean ab1 = new ApprovalBean();
            ab1.setApprovalId(approvalId1);
            ab1.setApctId(applicantApctId);
            ab1.setEmployeeId(approverEmpId);
            ab1.setStatusId(2);
            ab1.setComment("test1 comment");
            ab1.setCreateDate(LocalDateTime.of(2023, 1, 2, 12, 34, 56));

            int inserted1 = approvalDao.insert(ab1);
            System.out.println("approvalDao.insert returned: " + inserted1 + " (期待:1)");

            // 検証：selectNotificationsByApplicant で見つける（NotificationBean の timeStr や time を確認）
            List<NotificationBean> notifs = approvalDao.selectNotificationsByApplicant(applicantEmpId);
            boolean found = false;
            if (notifs != null) {
                for (NotificationBean n : notifs) {
                    if (approvalId1.equals(n.getApprovalId())) {
                        System.out.println("見つかった通知: approvalId=" + n.getApprovalId() + ", timeStr=" + n.getTimeStr());
                        found = true;
                        break;
                    }
                }
            }
            if (!found) System.out.println("Test1: 警告: insert 直後に通知一覧に見つかりませんでした（環境により反映順が異なるか既存データが影響しています）");
        } catch (Exception e) {
            System.err.println("Test1 で例外: " + e.getMessage());
            e.printStackTrace();
        }

        // 2) insert with createDate == null -> current timestamp should be stored
        try {
            System.out.println("\n--- Test2: insert (createDate null -> now が保存される) ---");
            ApprovalBean ab2 = new ApprovalBean();
            ab2.setApprovalId(approvalId2);
            ab2.setApctId(applicantApctId);
            ab2.setEmployeeId(approverEmpId);
            ab2.setStatusId(3);
            ab2.setComment("test2 comment");
            ab2.setCreateDate(null);

            int inserted2 = approvalDao.insert(ab2);
            System.out.println("approvalDao.insert returned: " + inserted2 + " (期待:1)");

            // 検証：通知一覧で見つけて timeStr が --- でないことを確認
            List<NotificationBean> notifs2 = approvalDao.selectNotificationsByApplicant(applicantEmpId);
            boolean found2 = false;
            if (notifs2 != null) {
                for (NotificationBean n : notifs2) {
                    if (approvalId2.equals(n.getApprovalId())) {
                        System.out.println("見つかった通知: approvalId=" + n.getApprovalId() + ", timeStr=" + n.getTimeStr());
                        found2 = true;
                        break;
                    }
                }
            }
            if (!found2) System.out.println("Test2: 警告: 通知一覧に見つかりませんでした（反映遅延の可能性あり）");
        } catch (Exception e) {
            System.err.println("Test2 で例外: " + e.getMessage());
            e.printStackTrace();
        }

        // 3) insert causing SQLException (duplicate approval_id) -> should return 0
        try {
            System.out.println("\n--- Test3: insert duplicate approval_id (SQLException を期待) ---");
            ApprovalBean dup = new ApprovalBean();
            dup.setApprovalId(approvalId1); // 既に使った approvalId を再利用（重複 PK を想定）
            dup.setApctId(applicantApctId);
            dup.setEmployeeId(approverEmpId);
            dup.setStatusId(2);
            dup.setComment("duplicate test");
            dup.setCreateDate(LocalDateTime.now());

            int rdup = approvalDao.insert(dup);
            System.out.println("approvalDao.insert (duplicate) returned: " + rdup + " (期待:0 if SQLException handled)");
        } catch (Exception e) {
            System.err.println("Test3 で例外: " + e.getMessage());
            e.printStackTrace();
        }

        // 4) selectByApctId existing
        try {
            System.out.println("\n--- Test4: selectByApctId (existing) ---");
            ApprovalBean sel = approvalDao.selectByApctId(applicantApctId);
            if (sel != null) {
                System.out.println("selectByApctId returned approvalId=" + sel.getApprovalId() + ", apctId=" + sel.getApctId() + ", status=" + sel.getStatusId());
            } else {
                System.out.println("selectByApctId returned null (期待: ApprovalBean または null)");
            }
        } catch (Exception e) {
            System.err.println("Test4 で例外: " + e.getMessage());
            e.printStackTrace();
        }

        // 5) selectByApctId non-existing
        try {
            System.out.println("\n--- Test5: selectByApctId (non-existing) ---");
            ApprovalBean none = approvalDao.selectByApctId("NON_EXISTING_APCT_XXXXX");
            System.out.println("selectByApctId(non-existing) returned: " + none + " (期待: null)");
        } catch (Exception e) {
            System.err.println("Test5 で例外: " + e.getMessage());
            e.printStackTrace();
        }

        // 6) selectNotificationsByApplicant: ensure multiple notifications exist (we inserted a few above)
        try {
            System.out.println("\n--- Test6: selectNotificationsByApplicant (複数取得) ---");
            List<NotificationBean> listNot = approvalDao.selectNotificationsByApplicant(applicantEmpId);
            System.out.println("通知件数(最大10件) = " + (listNot != null ? listNot.size() : 0));
            if (listNot != null) {
                for (NotificationBean n : listNot) {
                    System.out.println(" approvalId=" + n.getApprovalId() + ", apctId=" + n.getApctId()
                            + ", statusName=" + n.getStatusName() + ", approver=" + n.getApproverName()
                            + ", timeStr=" + n.getTimeStr());
                }
            }
        } catch (Exception e) {
            System.err.println("Test6 で例外: " + e.getMessage());
            e.printStackTrace();
        }

        // 7) selectNotificationsByApplicant null handling (content/status/approver null)
        try {
            System.out.println("\n--- Test7: selectNotificationsByApplicant (null ハンドリング) ---");
            String tempApctNullContent = "TEMP_APCT_NULL_";
            // Application を関数経由で作成して content を null にする（ApplicationDAO.insert を利用）
            ApplicationBean appNull = new ApplicationBean();
            appNull.setApctId(tempApctNullContent);
            appNull.setEmployeeId(applicantEmpId);
            appNull.setContent(null); // content を null にする
            appNull.setType("T");
            appNull.setPaymentMethod("M");
            appNull.setAmount(1);
            appNull.setReason(null);
            appNull.setNote(null);
            appNull.setUrgent("false");
            appNull.setStatus_id(1);
            appNull.setCreateDate(LocalDateTime.now());
            appNull.setUpdateDate(LocalDateTime.now());
            appNull.setDeleted(false);
            int ai = appDao.insert(appNull);
            System.out.println("ApplicationDAO.insert (null content) returned: " + ai);

            // Approval を作成：employeeId を null にして approver 情報を null にする
            String approvalNulls = "TEST_APV_NULLS";
            ApprovalBean abNull = new ApprovalBean();
            abNull.setApprovalId(approvalNulls);
            abNull.setApctId(tempApctNullContent);
            abNull.setEmployeeId(null); // approver NULL
            abNull.setStatusId(999);    // 存在しない status_id（status_name は NULL になる想定）
            abNull.setComment(null);
            abNull.setCreateDate(LocalDateTime.now());
            int rnull = approvalDao.insert(abNull);
            System.out.println("approvalDao.insert (null-fields) returned: " + rnull);

            // 取得して該当レコードを確認
            List<NotificationBean> notifNullList = approvalDao.selectNotificationsByApplicant(applicantEmpId);
            boolean foundNull = false;
            if (notifNullList != null) {
                for (NotificationBean n : notifNullList) {
                    if (approvalNulls.equals(n.getApprovalId())) {
                        foundNull = true;
                        System.out.println("Found special notification: content=" + n.getContent()
                                + ", statusName=" + n.getStatusName()
                                + ", approverName=" + n.getApproverName()
                                + ", timeStr=" + n.getTimeStr());
                        // デフォルト文字列が適用されているかを人間が目視確認
                    }
                }
            }
            if (!foundNull) System.out.println("Test7: 警告: 特殊通知が見つかりませんでした");
        } catch (Exception e) {
            System.err.println("Test7 で例外: " + e.getMessage());
            e.printStackTrace();
        }

        // 8) selectNotificationsByApplicant: time NULL 検証（注：既存関数のみでは time=null 行を生成できないためスキップ）
        System.out.println("\n--- Test8: selectNotificationsByApplicant (time NULL 検証はスキップ) ---");
        System.out.println("注: ApprovalDAO.insert は time を自動セットするため、既存関数のみでは time=NULL のレコードを作成できません。");
        System.out.println("    time=NULL を検証するには直接 SQL で time=NULL の行を作成するか、DAO に time を指定して NULL を許す別メソッドを追加してください。");

        // 9) updateAllNotificationsAsRead: mark unread -> expect updated count
        try {
            System.out.println("\n--- Test9: updateAllNotificationsAsRead (未読がある場合) ---");
            // 未読の承認を1件追加（is_read デフォルト 0 と想定）
            String unreadApprovalId = "TEST_UNREAD_001";
            ApprovalBean unread = new ApprovalBean();
            unread.setApprovalId(unreadApprovalId);
            unread.setApctId(applicantApctId);
            unread.setEmployeeId(approverEmpId);
            unread.setStatusId(2);
            unread.setComment("unread test");
            unread.setCreateDate(LocalDateTime.now());
            int rUn = approvalDao.insert(unread);
            System.out.println("insert unread approval returned: " + rUn);

            int updatedCount = approvalDao.updateAllNotificationsAsRead(applicantEmpId);
            System.out.println("updateAllNotificationsAsRead returned: " + updatedCount + " (期待: >=1)");

            // 確認: selectNotificationsByApplicant で is_read が true（NotificationBean#setRead により反映）か確認
            List<NotificationBean> allNotifs = approvalDao.selectNotificationsByApplicant(applicantEmpId);
            int unreadRemain = 0;
            if (allNotifs != null) {
                for (NotificationBean n : allNotifs) {
                    // NotificationBean に isRead() もしくは getRead() がある前提。ここでは isRead() を想定。
                    try {
                        boolean isRead = n.isRead(); // メソッド名が異なる場合は読み替えてください
                        if (!isRead) unreadRemain++;
                    } catch (NoSuchMethodError ex) {
                        // Bean の API が異なる場合はログを出すだけ
                        System.out.println("NotificationBean に isRead() が見つかりません。手動確認してください。");
                        break;
                    }
                }
            }
            System.out.println("remaining unread after update: " + unreadRemain);
        } catch (Exception e) {
            System.err.println("Test9 で例外: " + e.getMessage());
            e.printStackTrace();
        }

        // 10) updateAllNotificationsAsRead when none -> expect 0
        try {
            System.out.println("\n--- Test10: updateAllNotificationsAsRead (未読なし) ---");
            int r10 = approvalDao.updateAllNotificationsAsRead(applicantEmpId);
            System.out.println("updateAllNotificationsAsRead returned: " + r10 + " (期待: 0)");
        } catch (Exception e) {
            System.err.println("Test10 で例外: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n=== ApprovalDAO 関数テスト終了 ===");
    }
}