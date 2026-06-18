package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bean.PositionBean;

public class PositionDAO extends DAO{
	public List<PositionBean> findAll(){
        String sql = "SELECT pos_id, pos_name FROM positions ORDER BY pos_id";
        List<PositionBean> list = new ArrayList<>();
        try (Connection con = dbConnect();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PositionBean p = new PositionBean();
                p.setPos_id(rs.getString("pos_id"));;
                p.setPos_name(rs.getString("pos_name"));
                list.add(p);
            }
        }catch(SQLException e) {
			System.out.println("SQLエラー");
			System.out.println(e.getMessage());
			return null;
		}
        return list;
    }
}