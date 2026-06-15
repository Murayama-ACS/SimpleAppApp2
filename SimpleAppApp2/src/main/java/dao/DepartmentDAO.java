package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bean.DepartmentBean;

public class DepartmentDAO extends DAO{
	public List<DepartmentBean> findAll(){
        String sql = "SELECT dpt_id, dpt_name FROM departments ORDER BY dpt_name";
        List<DepartmentBean> list = new ArrayList<>();
        try (Connection con = dbConnect();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                DepartmentBean d = new DepartmentBean();
                d.setDpt_id(rs.getString("dpt_id"));;
                d.setDpt_name(rs.getString("dpt_name"));
                list.add(d);
            }
        }catch(SQLException e) {
			System.out.println("SQLエラー");
			System.out.println(e.getMessage());
			return null;
		}
        return list;
    }
}
