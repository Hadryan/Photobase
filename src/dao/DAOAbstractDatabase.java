package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import entities.BasicEntity;

public class DAOAbstractDatabase<T extends BasicEntity> implements IDAOAbstract<T>{
	
	private Class<T> clazz;
	
	private String username = "root";
	private String password = "****";

	public DAOAbstractDatabase(Class<T> clazz) {
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
			if (object.primaryKeyColumnName().equals(columnName))
				continue;
			columnsName = columnsName == "" ? columnName : String.format("%s, %s", columnsName, columnName);
			questionMarks = questionMarks == "" ? "?" : String.format("%s, ?", questionMarks);
		}
		
		String strQuery = String.format("INSERT INTO %s (%s) VALUES (%s)", this.clazz.getSimpleName(), columnsName, questionMarks);
		
		try {
			 PreparedStatement st = conn.prepareStatement(strQuery);
			 int parameterIndex  = 1;
			 for (String columnName:object.columnsName()) {
				 if (object.primaryKeyColumnName().equals(columnName))
					 continue;
				 st.setObject(parameterIndex++, object.getValueForColumnName(columnName));
			 }
			 return st.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean addEncrypted(T object, String encryptedColumnName) {
		if (object == null)
			return false;
		Connection conn = createConnection();
		if (conn == null)
			return false;
		
		String columnsName = "";
		String questionMarks = "";
		for (String columnName:object.columnsName()) {
			if (object.primaryKeyColumnName().equals(columnName))
				continue;
			columnsName = columnsName == "" ? columnName : String.format("%s, %s", columnsName, columnName);
			if (columnName.equals(encryptedColumnName)) {
				questionMarks = questionMarks == "" ? "AES_ENCRYPT(?, 'secret')" : String.format("%s, AES_ENCRYPT(?, 'secret')", questionMarks);
			} else 
			questionMarks = questionMarks == "" ? "?" : String.format("%s, ?", questionMarks);
		}
		
		String strQuery = String.format("INSERT INTO %s (%s) VALUES (%s)", this.clazz.getSimpleName(), columnsName, questionMarks);
		try {
			 PreparedStatement st = conn.prepareStatement(strQuery);
			 int parameterIndex  = 1;
			 for (String columnName:object.columnsName()) {
				 if (object.primaryKeyColumnName().equals(columnName))
					 continue;
				 st.setObject(parameterIndex++, object.getValueForColumnName(columnName));
			 }
			 int test = st.executeUpdate();
			 if (test != 0) return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public int addEncryptedReturnId(T object, String encryptedColumnName) {
		System.out.println("doradi");
		return -1;
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
			if (object.primaryKeyColumnName().equals(columnName))
				continue;
			columnsName = columnsName == "" ? columnName : String.format("%s, %s", columnsName, columnName);
			questionMarks = questionMarks == "" ? "?" : String.format("%s, ?", questionMarks);
		}
		
		String strQuery = String.format("INSERT INTO %s (%s) VALUES (%s)", this.clazz.getSimpleName(), columnsName, questionMarks);
		
		try {
			 PreparedStatement st = conn.prepareStatement(strQuery, Statement.RETURN_GENERATED_KEYS);
			 int parameterIndex  = 1;
			 for (String columnName:object.columnsName()) {
				 if (object.primaryKeyColumnName().equals(columnName))
					 continue;
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
		if (id < 1) return false;
		
		Connection conn = createConnection();
		if (conn == null)
			return false;
		
		String query = String.format("DELETE FROM %s WHERE ID = ?", this.clazz.getSimpleName());
		
		try {
			PreparedStatement st = conn.prepareStatement(query);
			st.setObject(1, id);
			
			return st.execute();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean update(T object, String excludeColumn) {
		if (object == null)
			return false;
		
		Connection conn = createConnection();
		if (conn == null)
			return false;
		
		String set = "";
		for (String columnName:object.columnsName()) {
			if (object.primaryKeyColumnName().equals(columnName) || excludeColumn.equals(columnName))
				continue;
			set = set == "" ? columnName + " = ?" : String.format("%s, %s", set, columnName + " = ?");
		}
		
		String strQuery = String.format("UPDATE %s SET %s WHERE %s = %s", this.clazz.getSimpleName(), set, object.primaryKeyColumnName(),
				object.getValueForColumnName(object.primaryKeyColumnName()));
		
		try {
			PreparedStatement st = conn.prepareStatement(strQuery);
			int parameterIndex = 1;
			for (String columnName:object.columnsName()) {
				if (object.primaryKeyColumnName().equals(columnName) || excludeColumn.equals(columnName))
					continue;
				st.setObject(parameterIndex++, object.getValueForColumnName(columnName));
			}
			return st.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public List<T> getAll() {
		Connection conn = createConnection();
		if (conn == null)
			return null;
		try {
			PreparedStatement st = conn.prepareStatement(String.format("select * from %s", this.clazz.getSimpleName()));
			ResultSet rs = st.executeQuery();
			List<T> list = new ArrayList<T>();
			while (rs.next()) {
				list.add(readFromResultSet(rs));
			}
			closeStat(st);
			closeResultSet(rs);
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection(conn);
		}
		return null;
	}

	@Override
	public T getById(int id) {
		if (id < 0) return null;
		
		Connection conn = createConnection();
		if (conn == null) return null;
		
		try {
			
			T objectFromId = this.clazz.newInstance();
			PreparedStatement ps = conn.prepareStatement(String.format("SELECT * FROM %s WHERE %s = ?", this.clazz.getSimpleName(), objectFromId.primaryKeyColumnName()));
			ps.setObject(1, id);
			
			ResultSet rs = ps.executeQuery();
			T object = null;
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
