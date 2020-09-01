package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpSession;

import entities.Admins;
import entities.Operators;

public class DAOAdmins extends DAOAbstractDatabase<Admins> implements IDAOAdmin{

	public DAOAdmins() {
		super(Admins.class);
	}
	
	public String login(Admins admin, HttpSession session) {
		if (session.getAttribute("admin") != null || session.getAttribute("user") != null || session.getAttribute("operator") != null) {
			System.out.println("already logged in");
			return "-You are already logged in";
		}
		
		if (admin.getUsername() == null || admin.getUsername().trim().equals("") || admin.getPassword() == null || admin.getPassword().trim().equals("")) {
			System.out.println("Invalid username or password");
			return "-Invalid username or password";
		}
		
		Connection conn = createConnection();
		if (conn == null)
			return "-Connection error";
		
		try {
			PreparedStatement st = conn.prepareStatement("SELECT * FROM ADMINS WHERE USERNAME = ? AND PASSWORD = AES_ENCRYPT(?, 'secret')");
			st.setObject(1, admin.getUsername());
			st.setObject(2, admin.getPassword());
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				admin = readFromResultSet(rs);
				session.setAttribute("admin", admin);
				System.out.println("Succesfully logged in as " + admin.getUsername() + "(" + admin.getId() + ")");
				st.close();
				rs.close();
				closeConnection(conn);
				return "admin";
			} else {
				System.out.println("Invalid username or password, please try again.");
			}
			st.close();
			rs.close();
			closeConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return "-Invalid username or password";
	}
	
	public String addOperator(Operators operator, HttpSession session) {
		if (session.getAttribute("admin") == null) {
			System.out.println("You must first be logged in as an admin.");
			return "-Not logged in as an admin";
		}
		
		if (operator == null || operator.getUsername() == null || operator.getUsername().trim().equals("")
				|| operator.getPassword() == null || operator.getPassword().trim().equals(""))
			return "-Invalid input";
		
		Connection conn = createConnection();
		if (conn == null)
			return "-Server connection error";
		
		try {
			PreparedStatement st = conn.prepareStatement("INSERT INTO users (USERNAME, PASSWORD, EMAIL, STATE, ROLE)\r\n" + 
					"	SELECT * FROM (SELECT ?, aes_encrypt(?, 'secret'), 'null', 'null1', 'operator') AS tmp\r\n" + 
					"    WHERE NOT EXISTS (\r\n" + 
					"		SELECT USERNAME FROM USERS WHERE USERNAME = ?\r\n" + 
					"    ) LIMIT 1");
			st.setObject(1, operator.getUsername());
			st.setObject(2, operator.getPassword());
			st.setObject(3, operator.getUsername());
			int test1 = st.executeUpdate();
			if (test1 != 1) {
				System.out.println("That username is unavailable.");
				st.close();
				closeConnection(conn);
				return "-That username is unavailable";
			}
			
			st = conn.prepareStatement("INSERT INTO OPERATORS (USERNAME, PASSWORD, NEW)\r\n" + 
					"	SELECT * FROM (SELECT ?, aes_encrypt(?, 'secret'), 1) AS tmp\r\n" + 
					"    WHERE NOT EXISTS (\r\n" + 
					"		SELECT USERNAME FROM OPERATORS WHERE USERNAME = ?\r\n" + 
					"    ) LIMIT 1");
			st.setObject(1, operator.getUsername());
			st.setObject(2, operator.getPassword());
			st.setObject(3, operator.getUsername());
			int test = st.executeUpdate();
			if (test == 1) {
				System.out.println("Succesfully registered operator.");
				st.close();
				closeConnection(conn);
				return "Succesfully registered operator";
			} else {
				System.out.println("That username is unavailable.");
				st.close();
				closeConnection(conn);
				return "-That username is unavailable";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "-Internal server error";
	}
	
	public boolean removeOperator(int operator_id, HttpSession session) {
		if (session.getAttribute("admin") == null) {
			System.out.println("You must first be logged in as an admin");
			return false;
		}
		
		if (operator_id <= 0) {
			System.out.println("invalid input");
			return false;
		}
		
		DAOOperators operatorService = new DAOOperators();
		Operators operator = operatorService.getById(operator_id);
		if (operator == null) {
			System.out.println("Operator doesn't exist");
			return false;
		}
		
		Connection conn = createConnection();
		
		try {
			PreparedStatement st = conn.prepareStatement("DELETE FROM USERS WHERE USERNAME = ?");
			st.setObject(1, operator.getUsername());
			int t = st.executeUpdate();
			if (t == 0) {
				st.close();
				closeConnection(conn);
				return false;
			}
			st.close();
			closeConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		
		operatorService.removeById(operator_id);
		return true;
	}
	
	public List<Operators> getOperators(HttpSession session) {
		if (session.getAttribute("admin") == null) {
			System.out.println("You must first be logged in as an admin.");
			return null;
		}
		
		DAOOperators operatorService = new DAOOperators();
		return operatorService.getAll();
	}
	
	public String addCategory(String categoryName, HttpSession session) {
		if (session.getAttribute("admin") == null) {
			System.out.println("You must first be logged in as an admin");
			return "-Not logged in as an admin";
		}
		
		if (categoryName == null || categoryName.trim().equals("")) {
			System.out.println("Invalid input");
			return "-Invalid input";
		}
		
		Connection conn = createConnection();
		if (conn == null)
			return "-Server connection error";
		
		try {
			PreparedStatement st = conn.prepareStatement("INSERT INTO CATEGORIES (NAME) \r\n" + 
					"					SELECT * FROM (SELECT ?) AS tmp \r\n" + 
					"					WHERE NOT EXISTS (\r\n" + 
					"					SELECT NAME FROM CATEGORIES WHERE NAME = ? \r\n" + 
					"					) LIMIT 1");
			st.setObject(1, categoryName);
			st.setObject(2, categoryName);
			int test = st.executeUpdate();
			if (test == 1) {
				System.out.println("Succesfully added category");
				st.close();
				closeConnection(conn);
				return "Successfully added category";
			}
			System.out.println("Category already exists");
			st.close();
			closeConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "-Category already exists";
	}

}
