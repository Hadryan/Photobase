package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import entities.Authors;

public class DAO_NOID_Author extends DAO_NOID_AbstractDatabase<Authors> implements IDAO_NOID_Abstract<Authors> {

	public DAO_NOID_Author() {
		super(Authors.class);
	}

	@Override
	public Authors getById(int id) {
		if (id < 0)
			return null;

		Connection conn = createConnection();
		if (conn == null)
			return null;

		try {

			Authors objectFromId = Authors.class.newInstance();
			PreparedStatement ps = conn.prepareStatement(
					String.format("SELECT * FROM %s WHERE %s = ?", Authors.class.getSimpleName(), "Users_ID"));
			ps.setObject(1, id);

			ResultSet rs = ps.executeQuery();
			Authors object = null;
			if (rs.next()) {
				object = readFromResultSet(rs);
			}
			closeStat(ps);
			closeResultSet(rs);
			return object;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
