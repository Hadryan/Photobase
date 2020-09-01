package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import entities.NoIdEntity;

public class DAO_NOID_AbstractDatabase<T extends NoIdEntity> implements IDAO_NOID_Abstract<T> {

	private Class<T> clazz;

	private String username = "root";
	private String password = "****";

	public DAO_NOID_AbstractDatabase(Class<T> clazz) {
		this.clazz = clazz;
	}

	@Override
	public boolean add(T object) {
		if (object == null)
			return false;
		Connection conn = createConnection();
		if (conn == null)
			return false;
		
		String columnsName = "";
		String questionMarks = "";
		for (String columnName:object.columnsName()) {
			columnsName = columnsName == "" ? columnName : String.format("%s, %s", columnsName, columnName);
			questionMarks = questionMarks == "" ? "?" : String.format("%s, ?", questionMarks);
		}
		
		String strQuery = String.format("INSERT INTO %s (%s) VALUES (%s)", this.clazz.getSimpleName(), columnsName, questionMarks);
		
		try {
			 PreparedStatement st = conn.prepareStatement(strQuery);
			 int parameterIndex  = 1;
			 for (String columnName:object.columnsName()) {
				 st.setObject(parameterIndex++, object.getValueForColumnName(columnName));
			 }
			 return st.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public int addReturnId(T object) {
		int id = -1;
		if (object == null)
			return id;
		Connection conn = createConnection();
		if (conn == null)
			return id;
		
		String columnsName = "";
		String questionMarks = "";
		for (String columnName:object.columnsName()) {
			columnsName = columnsName == "" ? columnName : String.format("%s, %s", columnsName, columnName);
			questionMarks = questionMarks == "" ? "?" : String.format("%s, ?", questionMarks);
		}
		
		String strQuery = String.format("INSERT INTO %s (%s) VALUES (%s)", this.clazz.getSimpleName(), columnsName, questionMarks);
		
		try {
			 PreparedStatement st = conn.prepareStatement(strQuery, Statement.RETURN_GENERATED_KEYS);
			 int parameterIndex  = 1;
			 for (String columnName:object.columnsName()) {
				 st.setObject(parameterIndex++, object.getValueForColumnName(columnName));
			 }
			st.executeUpdate();
			ResultSet rs = st.getGeneratedKeys();
			if (rs.next()) {
				id = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return id;
	}

	@Override
	public boolean removeById(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(T object, String excludeColumn) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<T> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T getById(int id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected T readFromResultSet(ResultSet rs) {
		if (rs == null)
			return null;
		
		try {
			T object = this.clazz.newInstance();
			for (String columnName:object.columnsName()) {
				object.setValueForColumnName(columnName, rs.getObject(columnName));
			}
			return object;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected Connection createConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			return DriverManager.getConnection("jdbc:mysql://localhost/photobase", username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected void closeConnection(Connection conn) {
		if (conn == null)
			return;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	protected void closeResultSet(ResultSet rs) {
		if (rs == null) {
			return;
		}
		try {
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected void closeStat(PreparedStatement stat) {
		if (stat == null)
			return;
		try {
			stat.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
