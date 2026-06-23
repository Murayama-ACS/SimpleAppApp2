package Test;

import java.time.LocalDateTime;
import java.util.List;

import bean.ApplicationBean;
import bean.EmployeeBean;
import dao.ApplicationDAO;

public class ApplicationDAOTest {

	public static void main(String[] args) {
//		ApplicationBean bean = new ApplicationBean();
//		bean.setApctId("A001");
//        bean.setEmployeeId("A1");
//        bean.setContent("コンテンツ");
//        bean.setType("交通費");
//        bean.setPaymentMethod("振込");
//        bean.setAmount(1000);
//        bean.setReason("出張");
//        bean.setNote("備考");
//        bean.setUrgent("false");
//        bean.setStatus_id(1);
//        bean.setCreateDate(LocalDateTime.now());
//        bean.setUpdateDate(LocalDateTime.now());
//        bean.setDeleted(false);
//        
        ApplicationDAO dao = new ApplicationDAO();
//        //int r = dao.insert(bean);
//        System.out.println(r);
//        ApplicationBean upbean = new ApplicationBean();
//        upbean.setApctId("A001");
//        upbean.setEmployeeId("A1");
//        upbean.setContent("コンテンツ変更できてる？");
//        upbean.setType("交通費変更化");
//        upbean.setPaymentMethod("振込");
//        upbean.setAmount(1575);
//        upbean.setReason("出張帰りの温泉代");
//        upbean.setNote("備考なし");
//        upbean.setUrgent("緊急");
//        upbean.setStatus_id(1);
//        upbean.setCreateDate(LocalDateTime.now());
//        upbean.setUpdateDate(LocalDateTime.now());
//        upbean.setDeleted(false);
//        
//        int upr = dao.update(upbean);
//        System.out.println(upr);
        
//        int upsr = dao.updateStatus("A001", 3, LocalDateTime.now());
//        System.out.println(upsr);
//        int delr = dao.logicalDelete("A001", "A1");
//        System.out.println(delr);
        
        //System.out.println(dao.findById("AP260618153758194"));
        
//        EmployeeBean empBean = new EmployeeBean("A20180926", "鈴木 健", "user117@example.com", "D700", "E03");
//        List<ApplicationBean> list = dao.getPendingApplications(empBean);
//        System.out.println("list.size()=" + list.size());
//        for(ApplicationBean appBean : list) {
//        	System.out.println(appBean.getApctId());
//        	System.out.println(appBean.getEmployeeId());
//        	System.out.println(appBean.getContent());
//        	System.out.println(appBean.getType());
//        	System.out.println(appBean.getPaymentMethod());
//        	System.out.println(appBean.getAmount());
//        	System.out.println(appBean.getReason());
//        	System.out.println(appBean.getUrgent());
//        	System.out.println(appBean.getStatus_id());
//        	System.out.println(appBean.getCreateDate());
//        	System.out.println(appBean.getUpdateDate());
//        	System.out.println(appBean.isDeleted());
//        }
    
        // --- 前準備の項目 ---
        String testEmpId = "A1";       // 必要に応じて既存の emp_id に置き換えてください
        String testApctId = "TEST_APCT_001";   // insert 用にユニークな apct_id を指定
        String operatorId = "A1";      // logicalDelete の操作者 emp_id（実DBに存在することを推奨）

        System.out.println("--- 前提チェック ---");
        try {
            EmployeeBean chk = dao.selectEmployee(testEmpId);
            if (chk == null) {
                System.out.println("注意: テストに使う社員 " + testEmpId + " が存在しません。insert 等は失敗する可能性があります。");
            } else {
                System.out.println("社員存在確認OK: " + chk.getEmp_id() + " / " + chk.getEmp_name());
            }
        } catch (Exception e) {
            System.out.println("selectEmployee 実行で例外: " + e.getMessage());
        }

        // 1) insert
        try {
            System.out.println("\n=== Test1: insert ===");
            ApplicationBean bean = new ApplicationBean();
            bean.setApctId(testApctId);
            bean.setEmployeeId(testEmpId); // 存在する emp_id を指定してください
            bean.setContent("テスト申請 content");
            bean.setType("交通費");
            bean.setPaymentMethod("振込");
            bean.setAmount(1200);
            bean.setReason("テスト出張");
            bean.setNote("特記事項");
            bean.setUrgent("false");
            bean.setStatus_id(1);
            bean.setCreateDate(LocalDateTime.now());
            bean.setUpdateDate(LocalDateTime.now());
            bean.setDeleted(false);

            int r = dao.insert(bean);
            System.out.println("insert の戻り値: " + r + " (期待:1)");
        } catch (Exception e) {
            System.out.println("insert 実行で例外: " + e.getMessage());
        }

        // 2) update
        try {
            System.out.println("\n=== Test2: update ===");
            ApplicationBean updateBean = new ApplicationBean();
            updateBean.setApctId(testApctId); // 既に存在する apctId を指定
            updateBean.setType("精算");
            updateBean.setPaymentMethod("現金");
            updateBean.setAmount(1500);
            updateBean.setContent("更新後コンテンツ");
            updateBean.setReason("更新理由");
            updateBean.setNote("更新備考");
            updateBean.setUrgent("true");

            int r2 = dao.update(updateBean);
            System.out.println("update の戻り値: " + r2 + " (期待:1)");
        } catch (Exception e) {
            System.out.println("update 実行で例外: " + e.getMessage());
        }

        // 3) updateStatus
        try {
            System.out.println("\n=== Test3: updateStatus ===");
            LocalDateTime now = LocalDateTime.now();
            int r3 = dao.updateStatus(testApctId, 4, now); // 例: 4 に更新
            System.out.println("updateStatus の戻り値: " + r3 + " (期待:1)");
        } catch (Exception e) {
            System.out.println("updateStatus 実行で例外: " + e.getMessage());
        }

        // 4) logicalDelete 正常系
        try {
            System.out.println("\n=== Test4: logicalDelete ===");
            int r4 = dao.logicalDelete(testApctId, operatorId);
            System.out.println("logicalDelete の戻り値: " + r4 + " (期待:1 => 履歴追加 & status=7,is_deleted=1 の更新)");
        } catch (Exception e) {
            System.out.println("logicalDelete 実行で例外: " + e.getMessage());
        }

        // 5) logicalDelete 異常系（SQLException を途中で発生させる検証） — 自動実行不可
        System.out.println("\n=== Test5: logicalDelete 異常系（スキップ） ===");
        System.out.println("注意: 途中で SQLException を発生させるにはテスト専用の方法（モックかスキーマ破壊）が必要です。");
        System.out.println("ここでは安全のため実行しません。必要なら手順を提示します。");

        // 6) findById
        try {
            System.out.println("\n=== Test6: findById ===");
            ApplicationBean found = dao.findById(testApctId);
            if (found == null) {
                System.out.println("findById: レコードが見つかりませんでした (apctId=" + testApctId + ")");
            } else {
                System.out.println("findById: apctId=" + found.getApctId());
                System.out.println(" statusName=" + found.getStatusName());
                System.out.println(" employeeName=" + found.getEmployeeName());
                System.out.println(" departmentName=" + found.getDepartmentName());
            }
        } catch (Exception e) {
            System.out.println("findById 実行で例外: " + e.getMessage());
        }

        // 7) getPendingApplications for E03 (部長)
        try {
            System.out.println("\n=== Test7: getPendingApplications (E03) ===");
            EmployeeBean boss = new EmployeeBean("BOSS_ID", "部長 太郎", "boss@example.com", "D700", "E03"); // 実DBで適切な emp_id/dpt/pos を使ってください
            List<ApplicationBean> list = dao.getPendingApplications(boss);
            System.out.println("取得件数: " + (list != null ? list.size() : 0));
            if (list != null) {
                for (ApplicationBean a : list) {
                    System.out.println(" apctId=" + a.getApctId() + " emp=" + a.getEmployeeId() + " amount=" + a.getAmount() + " status=" + a.getStatus_id());
                }
            }
        } catch (Exception e) {
            System.out.println("getPendingApplications 実行で例外: " + e.getMessage());
        }

        // 8) searchApplications: scope=subordinate + 検索条件 + ソート + ページング
        try {
            System.out.println("\n=== Test8: searchApplications (scope=subordinate) ===");
            EmployeeBean leader = new EmployeeBean("LEAD01", "Leader", "lead@example.com", "D701", "E02"); // 実DBの実行者情報を指定
            ApplicationDAO.PageResult<ApplicationBean> pr = dao.searchApplications(
                    leader,
                    "subordinate",    // scope
                    null,             // statusFilter
                    null,             // qStatus
                    null,             // qName
                    null,             // qDepartment
                    null,             // qType
                    null,             // qAmountMin
                    null,             // qAmountMax
                    "amount",         // sortKey
                    "ASC",            // sortDir
                    10,               // limit
                    0                 // offset
            );
            System.out.println("検索結果 件数(page) = " + pr.getItems().size() + " hasNext=" + pr.hasNext());
            for (ApplicationBean a : pr.getItems()) {
                System.out.println(" apctId=" + a.getApctId() + " emp=" + a.getEmployeeId() + " amount=" + a.getAmount());
            }
        } catch (Exception e) {
            System.out.println("searchApplications 実行で例外: " + e.getMessage());
        }

        // 9) getAccountingApplications
        try {
            System.out.println("\n=== Test9: getAccountingApplications ===");
            List<ApplicationBean> accList = dao.getAccountingApplications();
            System.out.println("取得件数: " + (accList != null ? accList.size() : 0));
            if (accList != null) {
                for (ApplicationBean a : accList) {
                    System.out.println(" apctId=" + a.getApctId() + " status=" + a.getStatus_id());
                }
            }
        } catch (Exception e) {
            System.out.println("getAccountingApplications 実行で例外: " + e.getMessage());
        }

        System.out.println("\n=== テスト終了 ===");

	}
}
