package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import entities.Owned_Images;

public class DAO_NOID_Owned_Images extends DAO_NOID_AbstractDatabase<Owned_Images> implements IDAO_NOID_Abstract<Owned_Images>{

	public DAO_NOID_Owned_Images() {
		super(Owned_Images.class);
	}
	
	public boolean checkIfOwned(int users_id, int images_id) {
		Connection conn = createConnection();
		if (conn == null)
			return false;
		
		try {
			PreparedStatement st = conn.prepareStatement("SELECT * FROM Owned_Images WHERE Users_ID = ? AND Images_ID = ?");
			st.setObject(1, users_id);
			st.setObject(2, images_id);
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
	
	public boolean checkIfVoted(int users_id, int images_id) {
		Connection conn = createConnection();
		if (conn == null)
			return false;
		
		try {
			PreparedStatement st = conn.prepareStatement("SELECT VOTED FROM Owned_Images WHERE Users_ID = ? AND Images_ID = ?");
			st.setObject(1, users_id);
			st.setObject(2, images_id);
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				int test = (int)rs.getInt(1);
				rs.close();
				st.close();
				closeConnection(conn);
				if (test == 0) 
					return false;
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
	
	public boolean setVoted(int users_id, int images_id) {
		Connection conn = createConnection();
		if (conn == null)
			return false;
		
		try {
			PreparedStatement st = conn.prepareStatement("UPDATE Owned_Images SET VOTED = ? WHERE Users_ID = ? AND Images_ID = ?");
			st.setObject(1, 1);
			st.setObject(2, users_id);
			st.setObject(3, images_id);
			st.executeUpdate();
			st.close();
			closeConnection(conn);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean checkIfOwnedFromAuthor(int user_id, int author_id) {
		Connection conn = createConnection();
		if (conn == null)
			return false;
		
		try {
			PreparedStatement st = conn.prepareStatement("SELECT owned_images.Users_ID, owned_images.Images_ID FROM Owned_Images\r\n" + 
					"	INNER JOIN Images ON owned_images.Images_ID = images.id\r\n" + 
					"    WHERE owned_images.Users_ID = ? AND images.Authors_Users_ID = ?");
			st.setObject(1, user_id);
			st.setObject(2, author_id);
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
