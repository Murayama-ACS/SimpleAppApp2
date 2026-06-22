package Test;

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
        
        System.out.println(dao.findById("AP260618153758194"));
	}
}
