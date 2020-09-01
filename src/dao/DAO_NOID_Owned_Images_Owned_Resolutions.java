package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import entities.Owned_Images_Owned_Resolutions;

public class DAO_NOID_Owned_Images_Owned_Resolutions extends DAO_NOID_AbstractDatabase<Owned_Images_Owned_Resolutions> implements IDAO_NOID_Abstract<Owned_Images_Owned_Resolutions>{

	public DAO_NOID_Owned_Images_Owned_Resolutions() {
		super(Owned_Images_Owned_Resolutions.class);
	}
	
	public boolean checkIfOwned(int users_id, int images_id, int resolutions_id) {
		Connection conn = createConnection();
		if (conn == null)
			return false;
		
		try {
			PreparedStatement st = conn.prepareStatement("SELECT * FROM Owned_Images_Owned_Resolutions WHERE Owned_Images_Users_ID = ? AND Owned_Images_Images_ID = ?"
					+ " AND Image_Resolutions_ID = ?");
			st.setObject(1, users_id);
			st.setObject(2, images_id);
			st.setObject(3, resolutions_id);
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				rs.close();
				st.close();
				closeConnection(conn);
				return true;
			}
			rs.close();
			st.close();
			closeConnection(conn);
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

}
