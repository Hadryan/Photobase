package dao;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import entities.Authors;
import entities.Categories;
import entities.Firm;
import entities.Images;
import entities.Operators;
import entities.Users;
import utils.Config;
import utils.UtilsMethods;

public class DAOOperators extends DAOAbstractDatabase<Operators> implements IDAOOperator {

	public DAOOperators() {
		super(Operators.class);
	}

	public String login(HttpSession session, Operators operator) {
		if (session.getAttribute("operator") != null || session.getAttribute("admin") != null
				|| session.getAttribute("user") != null) {
			System.out.println("Already logged in.");
			return "-You are already logged in";
		}

		if (operator == null || operator.getUsername() == null || operator.getUsername().trim().equals("")
				|| operator.getPassword() == null || operator.getPassword().trim().equals("")) {
			System.out.println("Invalid input");
			return "-Invalid input";
		}

		Connection conn = createConnection();
		if (conn == null)
			return "-Connection error";

		try {
			PreparedStatement st = conn.prepareStatement(
					"SELECT * FROM OPERATORS WHERE USERNAME = ? AND PASSWORD = AES_ENCRYPT(?, 'secret')");
			st.setObject(1, operator.getUsername());
			st.setObject(2, operator.getPassword());
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				operator = readFromResultSet(rs);
				System.out.println(
						"Succesfully logged in as operator: " + operator.getUsername() + "(" + operator.getId() + ")");
				
				if (operator.getFirmId() != 0 && operator.isNeww()) {
					session.setAttribute("newOperator", true);
					session.setAttribute("operator", operator);
					st.close();
					rs.close();
					closeConnection(conn);
					System.out.println("newOperator");
					return "newFirmOperator";
				}
				
				if (operator.getFirmId() != 0) {
					session.setAttribute("operator", operator);
					st.close();
					rs.close();
					closeConnection(conn);
					System.out.println("FirmOperator");
					return "FirmOperator";
				}
				
				if (operator.isNeww()) {
					session.setAttribute("newOperator", true);
					System.out.println("You are required to change your password to fully activate your account.");
					session.setAttribute("operator", operator);
					st.close();
					rs.close();
					closeConnection(conn);
					System.out.println("newOperator");
					return "newOperator";
				}
				session.setAttribute("operator", operator);
				st.close();
				rs.close();
				closeConnection(conn);
				return "operator";
			}
			System.out.println("Invalid username or password, please try again.");
			st.close();
			rs.close();
			closeConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "-Invalid username or password";
	}

	public boolean changePassword(HttpSession session, Operators operator1) {
		if (operator1 == null || operator1.getPassword() == null) {
			return false;
		}
		
		String newPassword = operator1.getPassword();
		
		if (session.getAttribute("operator") == null) {
			System.out.println("You are not logged in");
			return false;
		}
		if (session.getAttribute("newOperator") == null) {
			System.out.println("Account already fully activated.");
			return false;
		}

		if (newPassword == null || newPassword.trim().equals("")) {
			System.out.println("Invalid input");
			return false;
		}

		Operators operator = (Operators) session.getAttribute("operator");

		Connection conn = createConnection();
		if (conn == null)
			return false;

		try {
			PreparedStatement st = conn
					.prepareStatement("UPDATE USERS SET PASSWORD = AES_ENCRYPT(?, 'secret') WHERE USERNAME = ?");
			st.setObject(1, newPassword);
			st.setObject(2, operator.getUsername());

			int test = st.executeUpdate();
			if (test == 0) {
				System.out.println("Operator doesn't exist.");
				st.close();
				closeConnection(conn);
				return false;
			}
			
			st = conn
					.prepareStatement("UPDATE OPERATORS SET PASSWORD = AES_ENCRYPT(?, 'secret'), NEW = 0 WHERE ID = ?");
			st.setObject(1, newPassword);
			st.setObject(2, operator.getId());
			st.executeUpdate();

			st.close();
			closeConnection(conn);

			session.removeAttribute("newOperator");
			System.out.println("New password set. Account fully activated.");
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<Images> getPendingImages(HttpSession session, String init, String reverse) {
		if (session.getAttribute("operator") == null) {
			System.out.println("Not logged in as an operator.");
			return null;
		}
		if (session.getAttribute("newOperator") != null) {
			System.out.println(
					"Operator account not fully activated.\r\nPlease change your password in order to activate account.");
			return null;
		}

		List<Images> pendingImages = new ArrayList<>();

		Connection conn = createConnection();
		if (conn == null)
			return null;

		if (session.getAttribute("offsetPending") == null) {
			session.setAttribute("offsetPending", 0);
		}

		Integer offset = (int) session.getAttribute("offsetPending");
		
		if (init.equals("true"))
			session.setAttribute("offsetPending", 10);
		else if (!reverse.equals("true"))
			session.setAttribute("offsetPending", offset + 10);
		else
			session.setAttribute("offsetPending", offset - 10);
		
		String query = "SELECT * FROM IMAGES WHERE PENDING = 1 LIMIT 10 OFFSET ";
		
		if (init.equals("true"))
			query = query + "0";
		else if (reverse.equals("true"))
			query = query + (offset - 20);
		else
			query = query + offset;

		DAOImage imageService = new DAOImage();

		try {
			PreparedStatement st = conn
					.prepareStatement(query);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				pendingImages.add(imageService.readFromResultSet(rs));
			}
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		closeConnection(conn);
		return pendingImages;
	}

	public boolean approveImage(HttpSession session, Images image) {
		if (session.getAttribute("operator") == null) {
			System.out.println("Not logged in as an operator.");
			return false;
		}

		if (session.getAttribute("newOperator") != null) {
			System.out.println(
					"Operator account not fully activated.\r\nPlease change your password in order to activate account.");
			return false;
		}
		
		if (image == null || image.getId() <= 0) {
			return false;
		}

		DAOImage imageService = new DAOImage();
		image = imageService.getById(image.getId());

		if (image == null || !image.isPending()) {
			System.out.println(
					"Invalid input. The image you are trying to evaluate doesn't exist or is already approved");
			return false;
		}

		image.setPending(false);
		imageService.update(image, "");
		return true;
	}

	public List<Users> getApplicants(HttpSession session) {
		if (session.getAttribute("operator") == null) {
			System.out.println("Not logged in as an operator.");
			return null;
		}

		if (session.getAttribute("newOperator") != null) {
			System.out.println(
					"Operator account not fully activated.\r\nPlease change your password in order to activate account.");
			return null;
		}

		List<Users> applicants = new ArrayList<>();
		Connection conn = createConnection();
		if (conn == null)
			return null;

		DAOUser userService = new DAOUser();

		try {
			PreparedStatement st = conn
					.prepareStatement("SELECT * FROM USERS WHERE ROLE = ? AND BANNED = 0 AND DELETED = 0");
			st.setObject(1, "applied");
			ResultSet rs = st.executeQuery();
			while (rs.next())
				applicants.add(userService.readFromResultSet(rs));
			st.close();
			rs.close();
			closeConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return applicants;
	}

	public String getApplicantImage(HttpSession session, int user_id, int index) {
		if (session.getAttribute("operator") == null) {
			System.out.println("Not logged in as an operator.");
			return null;
		}

		if (session.getAttribute("newOperator") != null) {
			System.out.println(
					"Operator account not fully activated.\r\nPlease change your password in order to activate account.");
			return null;
		}
		
		if (user_id <= 0) {
			System.out.println("invalid input");
			return null;
		}
		
		DAOUser userService = new  DAOUser();
		Users user = userService.getById(user_id);
		
		if (user == null) {
			System.out.println("invalid input");
			return null;
		}
		
		String directoryName = Config.AUTHOR_REPOSITORY + user.getId() + ";" + user.getUsername();
		if (!Files.exists(Paths.get(directoryName))) {
			System.out.println("Invalid input");
			return null;
		}
		
		String image = UtilsMethods.encodeBase64(directoryName + "\\" + user.getId() + ";" + user.getUsername() + ";num" + index + ".png");
		
		return image;
	}

	public String rateApplicant(HttpSession session, int user_id, int grade) {
		if (session.getAttribute("operator") == null) {
			System.out.println("Not logged in as an operator.");
			return "-Not logged in as an operator";
		}

		if (session.getAttribute("newOperator") != null) {
			System.out.println(
					"Operator account not fully activated.\r\nPlease change your password in order to activate account.");
			return "-Account not fully activated";
		}

		if (user_id <= 0) {
			System.out.println("Invalid input");
			return "-Invalid input";
		}

		DAOUser userService = new DAOUser();
		Users user = userService.getById(user_id);

		if (user == null || !user.getRole().equals("applied")) {
			System.out.println("Invalid input");
		}

		if (user.isBanned() || user.isDeleted()) {
			System.out.println("The user you have selected is banned or has a deleted account");
			return "-The user you are trying to evaluate has a disabled account";
		}

		Connection conn = createConnection();
		if (conn == null)
			return "-Connection error";

		try {
			if (grade >= 4) {
			
			PreparedStatement st = conn.prepareStatement("UPDATE USERS SET ROLE = ? WHERE ROLE = ? AND ID = ?");
			st.setObject(1, "prodavac");
			st.setObject(2, "applied");
			st.setObject(3, user.getId());
			int test = st.executeUpdate();
			if (test == 0) {
				st.close();
				closeConnection(conn);
				System.out.println("Applicant has already been evaluated.");
				return "-Applicant has already been evaluated";
			}
			st = conn.prepareStatement("INSERT INTO AUTHORS (Users_ID, RATING, OPERATOR_SCORE, VOTES) VALUES (?, ?, ?, ?)");
			st.setObject(1, user.getId());
			st.setObject(2, 0);
			st.setObject(3, grade);
			st.setObject(4, 0);
			st.execute();
			return "Applicant evaluated";
			
			} else {
				PreparedStatement st = conn.prepareStatement("UPDATE USERS SET ROLE = 'kupac' WHERE ID = ? AND ROLE = 'applied'");
				st.setObject(1, user_id);
				int test = st.executeUpdate();
				if (test == 0) {
					st.close();
					closeConnection(conn);
					return "-Applicant has already been evaluated";
				}
				st.close();
				closeConnection(conn);
				return "Applicant evaluated";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "-Internal server error";
	}

	public String banUser(HttpSession session, Users user) {
		if (session.getAttribute("operator") == null) {
			System.out.println("Not logged in as an operator.");
			return "-Not logged in as an operator";
		}

		if (session.getAttribute("newOperator") != null) {
			System.out.println(
					"Operator account not fully activated.\r\nPlease change your password in order to activate account.");
			return "-Operator account not fully activated";
		}

		if (user == null) {
			System.out.println("Invalid input");
			return "-Invalid input";
		}

		DAOUser userService = new DAOUser();
		user = userService.getById(user.getId());

		if (user == null) {
			System.out.println("Invalid input");
			return "-Invalid input";
		}

		if (user.isBanned() || user.isDeleted()) {
			System.out.println("User is already banned or has a deleted account");
			return "-User is already banned";
		}

		user.setBanned(true);
		userService.update(user, "PASSWORD");
		return "User banned";
	}

	public int numberOfSoldImagesFromUser(HttpSession session, Users user) {
		if (session.getAttribute("operator") == null) {
			System.out.println("Not logged in as an operator.");
			return -1;
		}

		if (session.getAttribute("newOperator") != null) {
			System.out.println(
					"Operator account not fully activated.\r\nPlease change your password in order to activate account.");
			return -1;
		}

		if (user == null || user.getId() <= 0) {
			System.out.println("Invalid input");
			return -1;
		}

		DAOUser userService = new DAOUser();
		user = userService.getById(user.getId());

		if (user == null) {
			System.out.println("Invalid input");
			return -1;
		}
		
		if (!user.getRole().equals("prodavac")) {
			System.out.println("User not an author");
			return -1;
		}

		Connection conn = createConnection();
		if (conn == null)
			return -1;

		try {
			PreparedStatement st = conn
					.prepareStatement("SELECT SUM(images.SALES) FROM IMAGES WHERE Authors_Users_ID = ?");
			st.setObject(1, user.getId());
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				int test = UtilsMethods.convertInt(rs.getInt(1));
				rs.close();
				st.close();
				closeConnection(conn);
				return test;
			} else
				return -1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public int numberOfSoldImagesFromCategory(HttpSession session, Categories category) {
		if (session.getAttribute("operator") == null) {
			System.out.println("Not logged in as an operator.");
			return -1;
		}

		if (session.getAttribute("newOperator") != null) {
			System.out.println(
					"Operator account not fully activated.\r\nPlease change your password in order to activate account.");
			return -1;
		}

		if (category == null || category.getName().trim().equals("")) {
			System.out.println("invalid entry");
			return -1;
		}

		Connection conn = createConnection();
		if (conn == null)
			return -1;

		try {
			PreparedStatement st = conn.prepareStatement("SELECT sum(images.SALES) FROM IMAGES\r\n"
					+ "	INNER JOIN images_has_categories ON images_has_categories.Images_ID = images.id\r\n"
					+ "    INNER JOIN categories ON categories.ID = images_has_categories.Categories_ID\r\n"
					+ "    WHERE categories.NAME = ?");
			st.setObject(1, category.getName());
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				int test = UtilsMethods.convertInt(rs.getInt(1));
				rs.close();
				st.close();
				closeConnection(conn);
				return test;
			} else return -1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public List<String> mostFrequentlyBoughtImagesByUser(HttpSession session, Users user) {
		if (session.getAttribute("operator") == null) {
			System.out.println("Not logged in as an operator.");
			return null;
		}

		if (session.getAttribute("newOperator") != null) {
			System.out.println(
					"Operator account not fully activated.\r\nPlease change your password in order to activate account.");
			return null;
		}

		if (user == null || user.getId() <= 0) {
			System.out.println("Invalid input");
			return null;
		}
		
		DAOUser userService = new DAOUser();
		user = userService.getById(user.getId());
		
		if (user == null) {
			System.out.println("invalid input");
			return null;
		}

		Connection conn = createConnection();
		if (conn == null)
			return null;

		try {
			PreparedStatement st = conn.prepareStatement(
					"SELECT COUNT(image_resolutions.ID), Image_Resolutions_ID, image_resolutions.WIDTH, image_resolutions.HEIGHT FROM image_resolutions\r\n"
							+ "	INNER JOIN owned_images_owned_resolutions ON owned_images_owned_resolutions.Image_Resolutions_ID = image_resolutions.ID\r\n"
							+ "    WHERE owned_images_owned_resolutions.Owned_Images_Users_ID = ? GROUP BY (image_resolutions.ID) order by COUNT(image_resolutions.ID) desc");
			st.setObject(1, user.getId());

			ResultSet rs = st.executeQuery();
			List<String> list = new ArrayList<>();
			while (rs.next()) {
				list.add(UtilsMethods.convertInt(rs.getObject("WIDTH")) + "x" + UtilsMethods.convertInt(rs.getObject("HEIGHT")) + " (" + rs.getInt(1) + ")");
			}
			rs.close();
			st.close();
			closeConnection(conn);
			return list;
		} catch (SQLException e) {
			closeConnection(conn);
			e.printStackTrace();
		}
		return null;
	}

	public List<String> MostFrequentlyBoughtImagesInCategory(HttpSession session,
			Categories category) {
		if (session.getAttribute("operator") == null) {
			System.out.println("Not logged in as an operator.");
			return null;
		}

		if (session.getAttribute("newOperator") != null) {
			System.out.println(
					"Operator account not fully activated.\r\nPlease change your password in order to activate account.");
			return null;
		}

		if (category == null || category.getName().trim().equals("")) {
			System.out.println("invalid entry");
			return null;
		}

		Connection conn = createConnection();
		if (conn == null)
			return null;

		try {
			PreparedStatement st = conn.prepareStatement(
					"SELECT COUNT(image_resolutions.ID), Image_Resolutions_ID, image_resolutions.WIDTH, image_resolutions.HEIGHT FROM image_resolutions\r\n"
							+ "	INNER JOIN owned_images_owned_resolutions ON owned_images_owned_resolutions.Image_Resolutions_ID = image_resolutions.ID\r\n"
							+ "    INNER JOIN images_has_categories ON images_has_categories.Images_ID = owned_images_owned_resolutions.Owned_Images_Images_ID\r\n"
							+ "    INNER JOIN categories ON categories.ID = images_has_categories.Categories_ID\r\n"
							+ "    WHERE categories.NAME = ? GROUP BY (image_resolutions.ID) order by COUNT(image_resolutions.ID) desc");
			st.setObject(1, category.getName());
			ResultSet rs = st.executeQuery();
			List<String> list = new ArrayList<>();
			while (rs.next()) {
				list.add(UtilsMethods.convertInt(rs.getObject("WIDTH")) + "x" + UtilsMethods.convertInt(rs.getObject("HEIGHT")) + " (" + rs.getInt(1) + ")");
			}
			rs.close();
			st.close();
			closeConnection(conn);
			return list;
		} catch (SQLException e) {
			closeConnection(conn);
			e.printStackTrace();
		}
		return null;
	}

	public double averagePriceInCategory(HttpSession session, Categories category) {
		if (session.getAttribute("operator") == null) {
			System.out.println("Not logged in as an operator.");
			return -1;
		}

		if (session.getAttribute("newOperator") != null) {
			System.out.println(
					"Operator account not fully activated.\r\nPlease change your password in order to activate account.");
			return -1;
		}

		if (category == null || category.getName().trim().equals("")) {
			System.out.println("invalid entry");
			return -1;
		}

		Connection conn = createConnection();
		if (conn == null)
			return -1;

		try {
			PreparedStatement st = conn.prepareStatement("\r\n"
					+ "SELECT avg(PRICE) FROM images_has_image_resolutions\r\n"
					+ "	INNER JOIN images_has_categories ON images_has_categories.Images_ID = images_has_image_resolutions.Images_ID\r\n"
					+ "    INNER JOIN categories ON categories.ID = images_has_categories.Categories_ID\r\n"
					+ "    WHERE categories.NAME = ?");
			st.setObject(1, category.getName());
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				double test = UtilsMethods.convertDouble(rs.getDouble(1));
				rs.close();
				st.close();
				closeConnection(conn);
				return test;
			} else return -1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public double averagePriceByAuthor(HttpSession session, Users user) {
		if (session.getAttribute("operator") == null) {
			System.out.println("Not logged in as an operator.");
			return -1;
		}

		if (session.getAttribute("newOperator") != null) {
			System.out.println(
					"Operator account not fully activated.\r\nPlease change your password in order to activate account.");
			return -1;
		}

		if (user == null || user.getId() <= 0) {
			System.out.println("Invalid input");
			return -1;
		}
		
		DAOUser userService = new DAOUser();
		user = userService.getById(user.getId());
		
		if (user == null || !user.getRole().equals("prodavac")) {
			System.out.println("invalid input");
			return -1;
		}

		Connection conn = createConnection();
		if (conn == null)
			return -1;

		try {
			PreparedStatement st = conn
					.prepareStatement("\r\n" + "SELECT avg(PRICE) FROM images_has_image_resolutions\r\n"
							+ "	INNER JOIN images on images.ID = images_has_image_resolutions.Images_ID\r\n"
							+ "    where images.Authors_Users_ID = ?");
			st.setObject(1, user.getId());
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				double test = UtilsMethods.convertDouble(rs.getDouble(1));
				rs.close();
				st.close();
				closeConnection(conn);
				return test;
			} else return -1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public List<Firm> getAppliedFirms(HttpSession session){
		if (session.getAttribute("operator") == null) {
			System.out.println("Not logged in as an operator");
			return null;
		}
		
		if (session.getAttribute("newOperator") != null) {
			System.out.println(
					"Operator account not fully activated.\r\nPlease change your password in order to activate account.");
			return null;
		}
		
		Connection conn = createConnection();
		if (conn == null)
			return null;
		
		try {
			PreparedStatement st = conn.prepareStatement("SELECT * FROM FIRM WHERE STATUS = 1");
			ResultSet rs = st.executeQuery();
			List<Firm> list = new ArrayList<>();
			DAOFirm firmService = new DAOFirm();
			while (rs.next()) {
				list.add(firmService.readFromResultSet(rs));
			}
			rs.close();
			st.close();
			closeConnection(conn);
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		closeConnection(conn);
		return null;
	}
	
	public String approveFirm(HttpSession session, Firm firm) {
		if (session.getAttribute("operator") == null) {
			System.out.println("Not logged in as an operator");
			return "-Not logged in as an operator";
		}
		
		if (session.getAttribute("newOperator") != null) {
			System.out.println(
					"Operator account not fully activated.\r\nPlease change your password in order to activate account.");
			return "-Operator account not fully activated";
		}
		
		if (firm == null) {
			System.out.println("Invalid input");
			return "-Invalid input";
		}
		
		DAOFirm firmService = new DAOFirm();
		firm = firmService.getById(firm.getId());
		
		if (firm == null) {
			System.out.println("Invalid input");
			return "-Invalid input";
		}
		
		Connection conn = createConnection();
		if (conn == null)
			return "-Connection error";
		
		try {
	
			
			PreparedStatement st = conn.prepareStatement("UPDATE FIRM SET STATUS = 0 WHERE ID = ? AND STATUS = 1");
			st.setObject(1, firm.getId());
			int test = st.executeUpdate();
			if (test == 0) {
				System.out.println("Firm already approved");
				st.close();
				closeConnection(conn);
				return "-Firm already approved";
			}
			Operators operator = new Operators();
			operator.setUsername((firm.getName()+firm.getId()).toLowerCase().replace(" ", ""));
			String password = (Math.random()+"").substring(2) + System.currentTimeMillis();
			operator.setPassword(password);
			operator.setFirmId(firm.getId());
			operator.setNeww(true);
			DAOOperators operatorService = new DAOOperators();
			operatorService.addEncrypted(operator, "PASSWORD");
			
			st = conn.prepareStatement("INSERT INTO USERS (USERNAME, PASSWORD, EMAIL, STATE, ROLE) "
					+ "VALUES (?, AES_ENCRYPT(?, 'secret'), 'null', 'null1', 'operator')");
			st.setObject(1, operator.getUsername());
			st.setObject(2, operator.getPassword());
			st.executeUpdate();
			st.close();
			closeConnection(conn);
			
			UtilsMethods.sendMail(firm.getEmail(), "Photobase: Firm application - Approved", "Congratulations your firm application "
					+ "has been approved. \r\nYour first operator account info - username: "+operator.getUsername()+" password: "+password);
			return "Firm approved";
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "-An error occurred";
	}
	
	public String addFirmOperator(HttpSession session, Operators newOperator) {
		if (session.getAttribute("operator") == null) {
			System.out.println("Not logged in as an operator");
			return "-Not logged in as an operator";
		}
		
		if (session.getAttribute("newOperator") != null) {
			System.out.println(
					"Operator account not fully activated.\r\nPlease change your password in order to activate account.");
			return "-Operator account not fully activated";
		}
		
		if (newOperator.getUsername() == null || newOperator.getUsername().trim().equals("") ||
				newOperator.getPassword() == null || newOperator.getPassword().trim().equals("")) {
			System.out.println("Invalid entry");
			return "-Invalid entry";
		}
		
		Operators operator = (Operators)session.getAttribute("operator");
		int firmId = operator.getFirmId();
		
		DAOFirm firmService = new DAOFirm();
		Firm firm = firmService.getById(firmId);
		
		if (firm == null) {
			System.out.println("You do not have permission to perform the following task");
			return "-You do not have permission to perform the following task";
		}
		
		newOperator.setFirmId(operator.getFirmId());
		newOperator.setNeww(true);
		
		Connection conn = createConnection();
		if (conn == null)
			return "-Connection error";
		
		try {
			PreparedStatement st = conn.prepareStatement("INSERT INTO users (USERNAME, PASSWORD, EMAIL, STATE, ROLE)\r\n" + 
					"	SELECT * FROM (SELECT ?, aes_encrypt(?, 'secret'), 'null', 'null1', 'operator') AS tmp\r\n" + 
					"    WHERE NOT EXISTS (\r\n" + 
					"		SELECT USERNAME FROM USERS WHERE USERNAME = ?\r\n" + 
					"    ) LIMIT 1");
			st.setObject(1, newOperator.getUsername());
			st.setObject(2, newOperator.getPassword());
			st.setObject(3, newOperator.getUsername());

			int test1 = st.executeUpdate();
			if (test1 != 1) {
				System.out.println("That username is unavailable.");
				st.close();
				closeConnection(conn);
				return "-That username is unavailable";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		DAOOperators operatorService = new DAOOperators();
		operatorService.addEncrypted(newOperator, "PASSWORD");
		return "Firm operator added";
	}
	
	public List<Authors> getAppliedAuthors(HttpSession session){
		if (session.getAttribute("operator") == null) {
			System.out.println("Not logged in as an operator");
			return null;
		}
		
		if (session.getAttribute("newOperator") != null) {
			System.out.println(
					"Operator account not fully activated.\r\nPlease change your password in order to activate account.");
			return null;
		}
		
		Operators operator = (Operators)session.getAttribute("operator");
		int firmId = operator.getFirmId();
		
		DAOFirm firmService = new DAOFirm();
		Firm firm = firmService.getById(firmId);
		
		if (firm == null) {
			System.out.println("Permission denied.");
			return null;
		}
		
		Connection conn = createConnection();
		if (conn == null)
			return null;
		
		try {
			PreparedStatement st = conn.prepareStatement("SELECT * FROM AUTHORS WHERE APPLIED = 1 AND Firm_ID = ?");
			st.setObject(1, firmId);
			ResultSet rs = st.executeQuery();
			List<Authors> list = new ArrayList<>();
			DAO_NOID_Author authorService = new DAO_NOID_Author();
			while (rs.next()) {
				list.add(authorService.readFromResultSet(rs));
			}
			rs.close();
			st.close();
			closeConnection(conn);
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		closeConnection(conn);
		return null;
	}
	
	public String approveFirmAuthor(HttpSession session, Authors author) {
		if (session.getAttribute("operator") == null) {
			System.out.println("Not logged in as an operator");
			return "-Not logged in as an operator";
		}
		
		if (session.getAttribute("newOperator") != null) {
			System.out.println(
					"Operator account not fully activated.\r\nPlease change your password in order to activate account.");
			return "-Operator account not fully activated";
		}
		
		if (author == null) {
			System.out.println("Invalid entry");
			return "-Invalid entry";
		}
		
		Operators operator = (Operators)session.getAttribute("operator");
		int firmId = operator.getFirmId();
		
		DAOFirm firmService = new DAOFirm();
		Firm firm = firmService.getById(firmId);
		
		if (firm == null) {
			System.out.println("Permission denied.");
			return "-Permission denied";
		}
		
		DAO_NOID_Author authorService = new DAO_NOID_Author();
		author = authorService.getById(author.getUsers_id());
		
		DAOUser userService = new DAOUser();
		Users user = userService.getById(author.getUsers_id());
		
		if (author == null) {
			System.out.println("Invalid entry");
			return "-Invalid entry";
		}
		
		if (!author.isApplied() || author.getFirm_id() != firmId) {
			System.out.println("Invalid input");
			return "-Invalid input";
		}
		
		Connection conn = createConnection();
		if (conn == null)
			return "-Connection error";
		
		try {
			PreparedStatement st = conn.prepareStatement("UPDATE AUTHORS SET APPLIED = 0 WHERE Users_ID = ?");
			st.setObject(1, author.getUsers_id());
			int test = st.executeUpdate();
			if (test != 0) {
				System.out.println("Succesfully approved author.");
				st.close();
				closeConnection(conn);
				UtilsMethods.sendMail(user.getEmail(), "Photobase: Firm application - Accepted", "Congratulations, you are now a part of "+firm.getName());
				return "Author approved";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		closeConnection(conn);
		return "-An error has occurred";
	}
	
	public List<Users> getAllUsers(){
		Connection conn = createConnection();
		
		if (conn == null) {
			return null;
		}
		
		try {
			PreparedStatement st = conn.prepareStatement("SELECT * FROM USERS WHERE ROLE = 'kupac' OR ROLE = 'prodavac' OR ROLE = 'applied'");
			DAOUser userService = new DAOUser();
			List<Users> list = new ArrayList<>();
			
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				list.add(userService.readFromResultSet(rs));
			}
			return list;			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<Operators> getFirmOperators(HttpSession session){
		if (session.getAttribute("operator") == null) {
			System.out.println("Not logged in as an operator");
			return null;
		}
		
		Operators operator = (Operators)session.getAttribute("operator");
		int firmId = operator.getFirmId();
		
		Connection conn = createConnection();
		if (conn == null)
			return null;
		
		try {
			PreparedStatement st = conn.prepareStatement("SELECT * FROM OPERATORS WHERE Firm_ID = ?");
			st.setObject(1, firmId);
			
			ResultSet rs = st.executeQuery();
			List<Operators> list = new ArrayList<>();
			while (rs.next()) {
				list.add(readFromResultSet(rs));
			}
			st.close();
			closeConnection(conn);
			
			for (Operators o:list) {
				System.out.println(o.getUsername());
			}
			
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		closeConnection(conn);
		return null;
	}

}
