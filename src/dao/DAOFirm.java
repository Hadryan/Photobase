package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import entities.Firm;

public class DAOFirm extends DAOAbstractDatabase<Firm> implements IDAOFirm{

	public DAOFirm() {
		super(Firm.class);
	}
	
	public boolean register(Firm firm) {
		if (firm == null || firm.getName() == null || firm.getName().trim().equals("") ||
				firm.getLocation() == null || firm.getLocation().trim().equals("") ||
				firm.getPib() <= 0 || firm.getEmail() == null || firm.getEmail().trim().equals("")) {
			System.out.println("Name, location, email and PIB are required for firm registration.");
			return false;
		}
		
		Connection conn = createConnection();
		if (conn == null)
			return false;
		
		try {
			PreparedStatement st = conn.prepareStatement("SELECT * FROM FIRM WHERE NAME = ?");
			st.setObject(1, firm.getName());
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				System.out.println("Name already taken.");
				rs.close();
				st.close();
				closeConnection(conn);
				return false;
			}
			rs.close();
			st.close();
			closeConnection(conn);
		} catch (SQLException e) {
			return false;
		}
		firm.setStatus(true);
		firm.setPartnerCategoriesId(1);
		add(firm);
		return true;
	}
	
	public List<Firm> getApprovedFirms(){
		Connection conn = createConnection();
		
		try {
			PreparedStatement st = conn.prepareStatement("SELECT * FROM FIRM WHERE STATUS = 0");
			ResultSet rs = st.executeQuery();
			List<Firm> list = new ArrayList<>();
			while (rs.next()) {
				list.add(readFromResultSet(rs));
			}
			st.close();
			closeConnection(conn);
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		closeConnection(conn);
		return null;
	}

}
