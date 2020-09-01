package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import entities.User_Voted_Author;

public class DAO_NOID_User_Voted_Author extends DAO_NOID_AbstractDatabase<User_Voted_Author> implements IDAO_NOID_Abstract<User_Voted_Author>{

	public DAO_NOID_User_Voted_Author() {
		super(User_Voted_Author.class);
	}
	
	public boolean checkIfVoted(int user_id, int author_id) {
		Connection conn = createConnection();
		if (conn == null)
			return false;
		try {
			PreparedStatement st = conn.prepareStatement("SELECT * FROM USER_VOTED_AUTHOR WHERE Users_ID = ? AND Authors_Users_ID = ?");
			st.setObject(1, user_id);
			st.setObject(2, author_id);
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				st.close();
				rs.close();
				closeConnection(conn);
				return true;
			}
			st.close();
			rs.close();
			closeConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean setVoted(int user_id, int author_id) {
		Connection conn = createConnection();
		if (conn == null)
			return false;
		try {
			PreparedStatement st = conn.prepareStatement("INSERT INTO User_Voted_Author (users_ID, authors_Users_ID, VOTED) VALUES (?, ?, ?)");
			st.setObject(1, user_id);
			st.setObject(2, author_id);
			st.setObject(3, 1);
			st.execute();
			st.close();
			closeConnection(conn);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

}
