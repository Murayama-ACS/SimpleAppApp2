package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import bean.ApplicationBean;
import bean.EmployeeBean;

public class ApplicationDAO extends DAO {
    
    /**
     * 申請データをデータベースに登録する（網羅版Beanに対応）
     */
    public int insert(ApplicationBean bean) {
        Connection con = dbConnect();
        int result = 0;
        
        // テーブル定義の全13カラムに適合するINSERT文（status -> status_id）
        String sql = "INSERT INTO applications (apct_id, emp_id, content, type, method, amount, reason, remark, urgent, status_id, create_date, update_date, is_deleted) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                
        try {
            if(con != null) {
                PreparedStatement st = con.prepareStatement(sql);
                
                // すべての値をBeanから取得してプレースホルダーに設定
                st.setString(1, bean.getApctId());
                st.setString(2, bean.getEmployeeId());
                st.setString(3, bean.getContent());
                st.setString(4, bean.getType());
                st.setString(5, bean.getPaymentMethod());
                st.setInt(6, bean.getAmount()); 
                st.setString(7, bean.getReason());
                st.setString(8, bean.getNote());
                st.setString(9, bean.getUrgent());
                st.setInt(10, bean.getStatus_id()); // int型に変更
                
                // LocalDateTime から Timestamp への変換処理
                if (bean.getCreateDate() != null) {
                    st.setTimestamp(11, Timestamp.valueOf(bean.getCreateDate()));
                } else {
                    st.setTimestamp(11, new Timestamp(System.currentTimeMillis()));
                }
                
                if (bean.getUpdateDate() != null) {
                    st.setTimestamp(12, Timestamp.valueOf(bean.getUpdateDate()));
                } else {
                    st.setTimestamp(12, new Timestamp(System.currentTimeMillis()));
                }
                
                // 削除フラグの設定
                st.setInt(13, bean.isDeleted() ? 1 : 0);

                result = st.executeUpdate();
            }
        } catch(SQLException e) {
            System.out.println("insertエラー");
            System.out.println(e.getMessage());
        } finally {
            dbClose(con);
        }
        
        return result;
    }

    /**
     * 社員IDを基に、指定されたEmployeeBeanを取得する
     */
    public EmployeeBean selectEmployee(String empId) {
        Connection con = dbConnect();
        PreparedStatement st = null;
        ResultSet rs = null;
        EmployeeBean employee = null;

        String sql = "SELECT emp_id, emp_name, email, dpt_id, pos_id, is_deleted FROM employees WHERE emp_id = ? AND is_deleted = 0";

        try {
            if (con != null) {
                st = con.prepareStatement(sql);
                st.setString(1, empId);
                rs = st.executeQuery();

                if (rs.next()) {
                    employee = new EmployeeBean();
                    employee.setEmp_id(rs.getString("emp_id"));
                    employee.setEmp_name(rs.getString("emp_name"));
                    employee.setEmail(rs.getString("email"));
                    employee.setDpt_id(rs.getString("dpt_id"));
                    employee.setPos_id(rs.getString("pos_id"));
                    employee.setIs_deleted(rs.getBoolean("is_deleted"));
                }
            }
        } catch (SQLException e) {
            System.out.println("selectEmployeeエラー");
            System.out.println(e.getMessage());
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
            if (st != null) try { st.close(); } catch (SQLException e) {}
            dbClose(con);
        }
        return employee;
    }

    /**
     * 部署IDを基に、部署名を取得する
     */
    public String selectDepartmentName(String dptId) {
        Connection con = dbConnect();
        PreparedStatement st = null;
        ResultSet rs = null;
        String dptName = "未所属";

        String sql = "SELECT dpt_name FROM departments WHERE dpt_id = ?";

        try {
            if (con != null) {
                st = con.prepareStatement(sql);
                st.setString(1, dptId);
                rs = st.executeQuery();

                if (rs.next()) {
                    dptName = rs.getString("dpt_name");
                }
            }
        } catch (SQLException e) {
            System.out.println("selectDepartmentNameエラー");
            System.out.println(e.getMessage());
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
            if (st != null) try { st.close(); } catch (SQLException e) {}
            dbClose(con);
        }
        return dptName;
    }

    /**
     * 指定されたステータスID（status_id）に応じた申請一覧を取得する
     * サーブレット側から渡される引数がString型（"1"など）であることを考慮し、内部でint型に変換して検索します
     */
    public List<ApplicationBean> getPendingApplications(String pendingStatus) {
        Connection con = dbConnect();
        PreparedStatement st = null;
        ResultSet rs = null;
        List<ApplicationBean> list = new ArrayList<>();

        String sql = "SELECT apct_id, emp_id, content, type, method, amount, reason, remark, urgent, status_id, create_date, update_date, is_deleted "
                + "FROM applications "
                + "WHERE status_id = ? AND is_deleted = 0 "
                + "ORDER BY create_date DESC";

        try {
            if (con != null) {
                st = con.prepareStatement(sql);
                // Stringで受け取ったパラメータをintに変換してプレースホルダーに設定
                st.setInt(1, Integer.parseInt(pendingStatus.trim()));
                rs = st.executeQuery();

                while (rs.next()) {
                    ApplicationBean b = mapRowToBean(rs);
                    list.add(b);
                }
            }
        } catch (Exception e) {
            System.out.println("getPendingApplicationsエラー");
            System.out.println(e.getMessage());
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
            if (st != null) try { st.close(); } catch (SQLException e) {}
            dbClose(con);
        }
        return list;
    }

    /**
     * IDで詳細取得
     */
    public ApplicationBean findById(String id) {
        Connection con = dbConnect();
        PreparedStatement st = null;
        ResultSet rs = null;
        ApplicationBean bean = null;

        String sql = "SELECT apct_id, emp_id, content, type, method, amount, reason, remark, urgent, status_id, create_date, update_date, is_deleted "
                + "FROM applications "
                + "WHERE apct_id = ? AND is_deleted = 0";

        try {
            if (con != null) {
                st = con.prepareStatement(sql);
                st.setString(1, id);
                rs = st.executeQuery();

                if (rs.next()) {
                    bean = mapRowToBean(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("findByIdエラー");
            System.out.println(e.getMessage());
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
            if (st != null) try { st.close(); } catch (SQLException e) {}
            dbClose(con);
        }
        return bean;
    }

    /**
     * ResultSet -> Bean マッピング（内部補助関数）
     */
    private ApplicationBean mapRowToBean(ResultSet rs) throws SQLException {
        ApplicationBean b = new ApplicationBean();
        
        b.setApctId(rs.getString("apct_id"));
        b.setEmployeeId(rs.getString("emp_id"));
        b.setContent(rs.getString("content"));
        b.setType(rs.getString("type"));
        b.setPaymentMethod(rs.getString("method"));
        b.setAmount(rs.getInt("amount"));
        b.setReason(rs.getString("reason"));
        b.setNote(rs.getString("remark"));
        b.setUrgent(rs.getString("urgent")); 
        b.setStatus_id(rs.getInt("status_id")); // int型としてマッピング
        b.setDeleted(rs.getInt("is_deleted") == 1); 

        Timestamp createTs = rs.getTimestamp("create_date");
        if (createTs != null) {
            b.setCreateDate(createTs.toLocalDateTime());
        }
        
        Timestamp updateTs = rs.getTimestamp("update_date");
        if (updateTs != null) {
            b.setUpdateDate(updateTs.toLocalDateTime());
        }
        
        return b;
    }
}