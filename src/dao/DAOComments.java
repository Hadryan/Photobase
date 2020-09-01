package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import entities.Comments;

public class DAOComments extends DAOAbstractDatabase<Comments> implements IDAOAbstract<Comments>{

	public DAOComments() {
		super(Comments.class);
	}
	
	public List<Comments> getAllFromAuthor(int author_id){
		if (author_id <= 0) return null;
		
		Connection conn = createConnection();
		if (conn == null)
			return null;
		
		try {
			PreparedStatement st = conn.prepareStatement("SELECT * FROM COMMENTS WHERE Authors_Users_ID = ? ORDER BY DATE DESC");
			st.setObject(1, author_id);
			ResultSet rs = st.executeQuery();
			List<Comments> list = new ArrayList<>();
			while (rs.next()) {
				list.add(readFromResultSet(rs));
			}
			st.close();
			rs.close();
			closeConnection(conn);
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
