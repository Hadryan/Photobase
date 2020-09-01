package dao;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpSession;

import com.google.common.io.Files;

import entities.Authors;
import entities.Comments;
import entities.Creditcard;
import entities.Firm;
import entities.Images;
import entities.Images_has_Image_Resolutions;
import entities.Owned_Images;
import entities.Owned_Images_Owned_Resolutions;
import entities.Users;
import utils.Config;
import utils.MultipartApplicationFile;
import utils.ShoppingCart;
import utils.UtilsMethods;

public class DAOUser extends DAOAbstractDatabase<Users> implements IDAOUser {

	public DAOUser() {
		super(Users.class);
	}

	@Override
	public boolean register(Users user) {
		String username = user.getUsername();
		String password = user.getPassword();
		String email = user.getEmail();
		String state = user.getState();

		user.setRole("kupac");

		if (username == null || password == null || email == null || state == null) {
			return false;
		}

		if (username.contains(";") || password.contains(";")) {
			System.out.println("not allowed to use the symbol ; in username or password.");
			return false;
		}

		Connection conn = createConnection();
		if (conn == null)
			return false;

		try {
			PreparedStatement ps = conn
					.prepareStatement(String.format("SELECT * FROM Users WHERE USERNAME='%s'", username));
			ResultSet rs = ps.executeQuery();

			if (!rs.next()) {

				generateRegistrationToken(user);

				boolean test = addEncrypted(user, "PASSWORD");
				if (test) {
					sendConfirmationMail(user);
					return true;
				}
			}
			System.out.println("Username already taken.");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	private void generateRegistrationToken(Users user) {
		String token = Math.random() + "";
		token = token.substring(2);
		token = token + System.currentTimeMillis() + ";" + user.getUsername();
		user.setToken(token);
	}

	private void sendConfirmationMail(Users user) {
		final String username = "photobase.gt@gmail.com";
		final String password = "****";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("photobase.gt@gmail.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
			message.setSubject("Photobase: Account activation");
			message.setText("To activate your Photobase account please click on the link below: ,"
					+ "\n\nhttp://localhost:8080/Photoshop/rest/user/registerConfirmation?token=" + user.getToken());

			Transport.send(message);

			System.out.println("Registration mail sent");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean confirmRegistration(String token) {
		String partitionedToken[] = token.split(";");
		String username = partitionedToken[partitionedToken.length - 1];

		Connection conn = createConnection();
		if (conn == null)
			return false;

		try {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM Users WHERE USERNAME = '" + username + "'");
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				Users user = readFromResultSet(rs);
				if (user.getToken().equals(token)) {
					user.setToken(null);
					super.update(user, "PASSWORD");

					System.out.println("Activated");
					return true;
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public String login(Users user, HttpSession session) {
		if (session.getAttribute("user") != null || session.getAttribute("admin") != null
				|| session.getAttribute("operator") != null) {
			System.out.println("You are already logged in.");
			return "-Already logged in";
		}
		String username = user.getUsername();
		String password = user.getPassword();

		System.out.println(username + " " + password);

		if (username == null || password == null || username.trim().equals("") || password.trim().equals("")) {
			System.out.println("Invalid input");
			return "-Invalid input";
		}

		Connection conn = createConnection();
		if (conn == null)
			return "-Connection error";

		try {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM Users WHERE USERNAME=? AND PASSWORD=AES_ENCRYPT(?, 'secret')");
			ps.setObject(1, username);
			ps.setObject(2, password);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				Users databaseUser = readFromResultSet(rs);

				if (databaseUser.isBanned()) {
					System.out.println("Your account is banned or deleted.");
					return "-Your account is banned";
				}
				if (databaseUser.isDeleted()) {
					System.out.println("-Wrong username or password");
					return "-Wrong username or password";
				}
				
				if (databaseUser.getRole().equals("operator") || databaseUser.getRole().equals("admin")) {
					return "-"+databaseUser.getRole();
				}
				if (databaseUser.getToken() == null || databaseUser.getToken().trim().equals("")) {
					System.out.println("Succesful login userID = " + databaseUser.getId());
					session.setAttribute("user", databaseUser);
					return databaseUser.getRole();
				} else {
					System.out.println("Account activation required. Please visit your mail.");
					return "-Account activation required. Please visit your mail.";
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "-Wrong username or password";
	}

	public boolean forgotPassword(HttpSession session, String username) {
		if (session.getAttribute("user") != null || session.getAttribute("admin") != null
				|| session.getAttribute("operator") != null) {
			System.out.println("You are already logged in.");
			return false;
		}

		if (username == null || username.trim().equals("")) {
			System.out.println("Invalid input");
			return false;
		}

		Connection conn = createConnection();
		if (conn == null)
			return false;

		try {
			PreparedStatement st = conn.prepareStatement("SELECT * FROM USERS WHERE USERNAME = ?");
			st.setObject(1, username);
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				Users user = readFromResultSet(rs);
				if (user == null) {
					System.out.println("Invalid user.");
					return false;
				}
				
				if (user.isBanned() || user.isDeleted()) {
					sendMailToUser(user.getEmail(), "Photobase: Password reset", "We're sorry to inform you but it seems your account is banned or disabled.");
					return true;
				}

				generateRegistrationToken(user);
				super.update(user, "PASSWORD");

				sendMailToUser(user.getEmail(), "Photobase: Password reset",
						"Please click on the following link in order to set a new password:\r\n"
								+ "http://localhost:8080/Photoshop/#/reset_password/" + user.getToken());
				return true;
			} else {
				System.out.println("invalid username");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean resetPassword(HttpSession session, String token, String newPassword) {
		if (session.getAttribute("user") != null || session.getAttribute("admin") != null
				|| session.getAttribute("operator") != null) {
			System.out.println("You are already logged in.");
			return false;
		}

		if (token == null || token.trim().equals("")) {
			System.out.println("Invalid token");
			return false;
		}

		if (newPassword == null || newPassword.trim().equals("") || newPassword.contains(";")) {
			System.out.println("Invalid password entry");
			return false;
		}

		String username = token.split(";")[1];

		Connection conn = createConnection();
		if (conn == null)
			return false;

		try {
			PreparedStatement st = conn.prepareStatement("SELECT * FROM USERS WHERE USERNAME = ?");
			st.setObject(1, username);
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				Users user = readFromResultSet(rs);
				if (user == null) {
					System.out.println("Invalid token");
					return false;
				}
				if (!user.getToken().equals(token)) {
					System.out.println("Invalid token");
					return false;
				}
				st = conn.prepareStatement(
						"UPDATE USERS SET PASSWORD = AES_ENCRYPT(?, 'secret'), TOKEN = NULL WHERE ID = ?");
				st.setObject(1, newPassword);
				st.setObject(2, user.getId());
				st.executeUpdate();
				System.out.println("Password has been succcesfully reset.");
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public String changePassword(HttpSession session,String currentPassword, String newPassword) {
		if (session.getAttribute("user") == null) {
			System.out.println("You must log in.");
			return "-Not logged in";
		}
		
		Users user = (Users)session.getAttribute("user");
		if (user.isBanned() || user.isDeleted()) {
			System.out.println("Your account is banned or deleted.");
			return "-Account banned or deleted";
		}
		
		if (newPassword == null || newPassword.trim().equals("") || newPassword.contains(";")) {
			System.out.println("Invalid password entry");
			return "-Invalid entry";
		}
		
		if (currentPassword == null || currentPassword.trim().equals("")) {
			System.out.println("Invalid password entry");
			return "-Invalid entry";
		}
		
		Connection conn = createConnection();
		if (conn == null)
			return "-Connection error";
		
		try {
			PreparedStatement st = conn.prepareStatement("SELECT * FROM USERS WHERE ID = ? AND PASSWORD = AES_ENCRYPT(?, 'secret')");
			st.setObject(1, user.getId());
			st.setObject(2, currentPassword);
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				st = conn.prepareStatement("UPDATE USERS SET PASSWORD = AES_ENCRYPT(?, 'secret') WHERE ID = ?");
				st.setObject(1, newPassword);
				st.setObject(2, user.getId());
				st.executeUpdate();
				return "Successfully changed password";
			} else {
				System.out.println("Wrong current password.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "-Wrong current password";
	}
	
	public boolean deleteAccount(HttpSession session) {
		if (session.getAttribute("user") == null) {
			System.out.println("You are not logged in.");
			return false;
		}
		
		Users user = (Users)session.getAttribute("user");
		user = getById(user.getId());
		
		if (user.isBanned()) {
			System.out.println("You are already banned.");
			return false;
		}
		
		user.setDeleted(true);
		update(user, "PASSWORD");
		return true;
	}

	@Override
	public boolean addToShoppingCart(HttpSession session, Images_has_Image_Resolutions image) {
		if (image == null)
			return false;

		DAOImage imageService = new DAOImage();
		Images im = imageService.getById(image.getImages_id());

		if (im.isPending()) {
			System.out.println("Invalid image");
			return false;
		}

		ShoppingCart cart;
		if (session.getAttribute("shoppingCart") == null) {
			cart = new ShoppingCart();
			session.setAttribute("shoppingCart", cart);
		}
		cart = (ShoppingCart) session.getAttribute("shoppingCart");
		cart.add(image);
		return true;
	}

	@Override
	public boolean removeFromShoppingCart(HttpSession session, Images_has_Image_Resolutions image) {
		if (image == null)
			return false;
		ShoppingCart cart;
		if (session.getAttribute("shoppingCart") == null)
			return true;
		cart = (ShoppingCart) session.getAttribute("shoppingCart");
		try {
		cart.remove(image);
		return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public ShoppingCart getShoppingCart(HttpSession session) {
		if (session.getAttribute("shoppingCart") == null)
			return null;
		ShoppingCart cart = (ShoppingCart) session.getAttribute("shoppingCart");
		return cart;
	}

	@Override
	public String addCreditcard(HttpSession session, Creditcard card) {
		if (card == null || card.getCard_number() == null || card.getExpiration_date() == null
				|| card.getOwner_name() == null)
			return "-Invalid input";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = df.parse(card.getExpiration_date());
			if (date.before(new Date())) {
				System.out.println("Invalid date.");
				return "-Invalid date";
			}
		} catch (ParseException e) {
			System.out.println("Invalid date format. Required format is yyyy-MM-dd");
			return "-Invalid date format. Reuired format is YYYY-MM-DD";
		}

		if (session.getAttribute("user") == null) {
			System.out.println("must login before adding card");
			return "-You're not logged in";
		}

		Users user = (Users) session.getAttribute("user");
		user = getById(user.getId());

		if (user.isBanned() || user.isDeleted()) {
			System.out.println("Your account is banned or deleted.");
			return "-Invalid account";
		}

		card.setUsers_id(user.getId());

		DAOCreditcard cardService = new DAOCreditcard();
		cardService.addEncrypted(card, "CARD_NUMBER");
		return "-Succesfully added credit card";
	}

	@Override
	public List<Creditcard> getCreditcards(HttpSession session) {
		if (session.getAttribute("user") == null) {
			System.out.println("Not logged in!");
			return null;
		}
		Users user = (Users) session.getAttribute("user");
		user = getById(user.getId());

		if (user.isBanned() || user.isDeleted()) {
			System.out.println("Your account is banned or deleted");
			return null;
		}

		DAOCreditcard cardService = new DAOCreditcard();

		Connection conn = createConnection();
		if (conn == null)
			return null;

		try {
			PreparedStatement ps = conn.prepareStatement("SELECT ID, CAST(aes_decrypt(CARD_NUMBER, 'secret') AS CHAR), EXPIRATION_DATE FROM creditcard WHERE Users_ID = ?");
			ps.setObject(1, user.getId());

			ResultSet rs = ps.executeQuery();
			List<Creditcard> list = new ArrayList<>();
			while (rs.next()) {
				Creditcard cd = new Creditcard();
				String num1 = UtilsMethods.convertToString(rs.getObject(2));
				String num = num1.substring(0, 4) + "-XXXX-XXXX-XXXX";
				cd.setId(UtilsMethods.convertInt(rs.getObject(1)));
				cd.setCard_number(num);
				cd.setExpiration_date(UtilsMethods.convertToString(rs.getObject(3)));
				list.add(cd);
			}
			closeStat(ps);
			closeResultSet(rs);

			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String buy(HttpSession session, Creditcard card) {
		if (card == null)
			return "-Invalid  input";
		if (session.getAttribute("user") == null) {
			System.out.println("You need to be logged in in order to buy.");
			return "-You need to be logged in first";
		}
		if (session.getAttribute("shoppingCart") == null) {
			System.out.println("ShoppingCart is empty.");
			return "-Shopping cart is empty";
		}
		Users user = (Users) session.getAttribute("user");
		user = getById(user.getId());

		if (user.isBanned() || user.isDeleted()) {
			System.out.println("Your account is banned or deleted");
			return "-Invalid account";
		}

		ShoppingCart cart = (ShoppingCart) session.getAttribute("shoppingCart");

		if (cart.getItems().size() == 0) {
			System.out.println("Cart is empty.");
			return "-Shopping cart is empty";
		}

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

		if (card.getId() != 0) {
			DAOCreditcard cardService = new DAOCreditcard();
			card = cardService.getById(card.getId());
			if (card.getUsers_id() != user.getId()) {
				System.out.println("invalid card");
				return "-Invalid card";
			}
			Date date = null;
			try {
				date = df.parse(card.getExpiration_date());
				if (date.before(new Date())) {
					System.out.println("Card expired!");
					return "-Card expired";
				}
				System.out.println("Card succesfully validated");
			} catch (ParseException e) {
				System.out.println("Invalid date");
				return "-Card expired";
			}
		} else {
			if (card.getCard_number() == null || card.getValidation_code() <= 0 || card.getValidation_code() >= 1000
					|| card.getExpiration_date() == null || card.getOwner_name() == null)
				return "-Invalid input";

			try {
				Date date = df.parse(card.getExpiration_date());
				if (date.before(new Date())) {
					System.out.println("Card expired");
					return "-Card expired";
				}
			} catch (ParseException e) {
				System.out.println("Invalid date.");
				return "-InvalidDate";
			}
			System.out.println("Card succesfully validated");
		}

		Connection conn = createConnection();
		if (conn == null)
			return "-Connection error";

		DAO_NOID_Owned_Images ownedImagesService = new DAO_NOID_Owned_Images();
		DAO_NOID_Owned_Images_Owned_Resolutions ownedResolutionsService = new DAO_NOID_Owned_Images_Owned_Resolutions();
		DAOImage imageService = new DAOImage();

		ArrayList<String> files = new ArrayList<>();

		try {

			for (Images_has_Image_Resolutions image : cart.getItems()) {

				boolean flag = true;
				if (!ownedImagesService.checkIfOwned(user.getId(), image.getImages_id())) {
					Owned_Images owned = new Owned_Images();
					owned.setImages_id(image.getImages_id());
					owned.setUsers_id(user.getId());
					ownedImagesService.add(owned);
					flag = false;
				}

				if (flag) {
					if (ownedResolutionsService.checkIfOwned(user.getId(), image.getImages_id(),
							image.getImage_resolutions_id())) {
						System.out.println("You already own this picture.");
						return "-You already own one or more of these photos";
					}
				}

				Owned_Images_Owned_Resolutions ownedRes = new Owned_Images_Owned_Resolutions();
				ownedRes.setImage_resolutions_id(image.getImage_resolutions_id());
				ownedRes.setOwned_images_users_id(user.getId());
				ownedRes.setOwned_images_images_id(image.getImages_id());
				ownedRes.setTransaction_date(new Timestamp(System.currentTimeMillis()));
				ownedResolutionsService.add(ownedRes);

				while (!updateSales(image, conn)) {
				}

				Images im = imageService.getById(image.getImages_id());
				files.add(Config.IMAGE_REPOSITORY + im.getId() + ";" + im.getName() + ";"
						+ image.getImage_resolutions_id() + ".png");
			}

		} catch (Exception e) {
			System.out.println("SQL Exception");
			return "-Internal server error";
		}

		sendImages(files, user);

		notifyAuthors(cart.getItems());

		cart.getItems().clear();
		cart.setPrice(0);

		return "Success, a mail containing your order has been sent. Thank you for using photobase.";
	}

	private void sendImages(ArrayList<String> files, Users user) {
		final String username = "photobase.gt@gmail.com";
		final String password = "****";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("photobase.gt@gmail.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
			message.setSubject("Photobase: Image delivery - Thank you for using Photobase");
			message.setText("Your images have arrived!\nThank you for using Photobase.");

			Multipart multipart = new MimeMultipart();

			int index = 1;
			for (String file : files) {
				MimeBodyPart messageBodyPart = new MimeBodyPart();
				String fileName = "image" + index + ".png";
				index++;
				DataSource source = new FileDataSource(file);
				messageBodyPart.setDataHandler(new DataHandler(source));
				messageBodyPart.setFileName(fileName);
				multipart.addBodyPart(messageBodyPart);
			}
			message.setContent(multipart);
			Transport.send(message);

			System.out.println("Images sent.");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	private void notifyAuthors(ArrayList<Images_has_Image_Resolutions> items) {
		HashMap<Integer, String> authorIds = new HashMap<>();
		HashMap<Integer, String> authorMessages = new HashMap<>();
		DAOUser userService = new DAOUser();
		DAOImage imageService = new DAOImage();
		for (Images_has_Image_Resolutions img : items) {
			Images image = imageService.getById(img.getImages_id());
			Users user = userService.getById(image.getAuthorsUsersId());
			authorIds.put(user.getId(), user.getEmail());
			if (authorMessages.get(user.getId()) == null) {
				authorMessages.put(user.getId(), "Your following images have been bought: " + image.getName());
			} else {
				authorMessages.put(user.getId(), authorMessages.get(user.getId()) + ", " + image.getName());
			}
		}

		for (Integer id : authorIds.keySet()) {
			sendMailToUser(authorIds.get(id), "Photobase: Images sold - Congratulations", authorMessages.get(id));
		}
	}

	public void sendMailToUser(String email, String subject, String text) {
		final String username = "photobase.gt@gmail.com";
		final String password = "****";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("photobase.gt@gmail.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
			message.setSubject(subject);
			message.setText(text);

			Transport.send(message);

			System.out.println("Registration mail sent");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean updateSales(Images_has_Image_Resolutions image, Connection conn) {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT SALES FROM IMAGES WHERE ID = ?");
			ps.setObject(1, image.getImages_id());
			ResultSet rs = ps.executeQuery();
			int oldSales = 0;
			while (rs.next()) {
				oldSales = rs.getInt(1);
			}
			ps.close();
			rs.close();

			PreparedStatement ps1 = conn.prepareStatement("UPDATE IMAGES SET SALES = ? WHERE ID = ? AND SALES = ?");
			ps1.setObject(1, oldSales + 1);
			ps1.setObject(2, image.getImages_id());
			ps1.setObject(3, oldSales);
			if (ps1.executeUpdate() != 1) {
				ps1.close();
				return false;
			}
			ps1.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public String rateImage(HttpSession session, int image_id, int value) {
		if (session.getAttribute("user") == null) {
			System.out.println("You must be logged in before you can rate an image.");
			return "-You must be logged in before you can rate an image";
		}
		if (image_id <= 0 || value < 1 || value > 5) {
			return "-Invalid input";
		}

		Users user = (Users) session.getAttribute("user");
		user = getById(user.getId());

		if (user.isBanned() || user.isDeleted()) {
			System.out.println("Your account is banned or deleted");
			return "-You cannot rate an image using a banned or deleted account";
		}

		DAOImage imageService = new DAOImage();
		Images image = imageService.getById(image_id);

		if (image == null)
			return "-Invalid input";

		DAO_NOID_Owned_Images ownedService = new DAO_NOID_Owned_Images();
		if (!ownedService.checkIfOwned(user.getId(), image_id)) {
			System.out.println("You need to buy an image before you can rate it.");
			return "-You must own the image before you can rate it";
		}

		if (ownedService.checkIfVoted(user.getId(), image_id)) {
			System.out.println("You have already rated this image.");
			return "-You have already rated this image";
		}

		Connection conn = createConnection();

		while (!vote(conn, image_id, value, "image")) {
		}

		closeConnection(conn);

		ownedService.setVoted(user.getId(), image_id);
		return "Vote submited";
	}

	private boolean vote(Connection conn, int id, int value, String what) {
		try {
			String query = "";
			if (what.equals("image"))
				query = "SELECT VOTES, RATING FROM IMAGES WHERE ID = ?";
			else
				query = "SELECT VOTES, RATING FROM AUTHORS WHERE Users_ID = ?";
			PreparedStatement st = conn.prepareStatement(query);
			st.setObject(1, id);
			ResultSet rs = st.executeQuery();

			double oldRating = 0;
			int oldVotes = 0;

			if (rs.next()) {
				oldRating = UtilsMethods.convertDouble(rs.getObject("RATING"));
				oldVotes = UtilsMethods.convertInt(rs.getObject("VOTES"));
			}

			st.close();
			rs.close();
			
			double newRating = (oldRating * oldVotes + value) / (double) (oldVotes + 1);

			if (what.equals("image"))
				query = "UPDATE IMAGES SET RATING = ?, VOTES = ? WHERE ID = ? AND VOTES = ? AND RATING = ?";
			else
				query = "UPDATE AUTHORS SET RATING = ?, VOTES = ? WHERE Users_ID = ? AND VOTES = ? AND RATING = ?";

			PreparedStatement st1 = conn.prepareStatement(query);
			st1.setObject(1, newRating);
			st1.setObject(2, oldVotes + 1);
			st1.setObject(3, id);
			st1.setObject(4, oldVotes);
			st1.setObject(5, oldRating);
			
			int test = st1.executeUpdate();
			if (test != 1 && test != 2) {
				st1.close();
				return false;
			}
			st1.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public String rateAuthor(HttpSession session, int author_id, int value) {
		if (session.getAttribute("user") == null) {
			System.out.println("You must be logged in before you can rate an author.");
			return "-You must be logged in before you can rate an author";
		}
		if (author_id <= 0 || value < 1 || value > 5)
			return "-Invalid input";

		Users user = (Users) session.getAttribute("user");
		user = getById(user.getId());

		if (user.isBanned() || user.isDeleted()) {
			System.out.println("Your account is banned or deleted");
			return "-You cannot rate an author using a banned or deleted account";
		}

		DAO_NOID_Author authorService = new DAO_NOID_Author();
		Authors author = authorService.getById(author_id);
		if (author == null) {
			System.out.println("invalid author");
			return "-invalid input";
		}

		DAO_NOID_Owned_Images ownedService = new DAO_NOID_Owned_Images();
		if (!ownedService.checkIfOwnedFromAuthor(user.getId(), author_id)) {
			System.out.println("You need to own an image of an author before you can rate him.");
			return "-You need to own at least one image from an author before you can rate him";
		}

		DAO_NOID_User_Voted_Author votedService = new DAO_NOID_User_Voted_Author();
		if (votedService.checkIfVoted(user.getId(), author_id)) {
			System.out.println("You have already rated this author.");
			return "-You have already rated this author";
		}

		Connection conn = createConnection();
		if (conn == null)
			return "-Server connection error";

		while (!vote(conn, author_id, value, "author")) {
		}
		
		closeConnection(conn);
		votedService.setVoted(user.getId(), author_id);
		return "Vote submited";
	}

	@Override
	public String comment(HttpSession session, int author_id, String text) {
		if (session.getAttribute("user") == null) {
			System.out.println("You must be logged in before you can comment");
			return "-You must be logged in before you can comment";
		}
		if (author_id <= 0 || text == null || text.trim().equals("")) {
			System.out.println("invalid input");
			return "-Invalid input";
		}

		Users user = (Users) session.getAttribute("user");
		user = getById(user.getId());

		if (user.isBanned() || user.isDeleted()) {
			System.out.println("Your account is banned or deleted");
			return "-You cannot leave a comment using a banned or deleted account";
		}

		DAO_NOID_Author authorService = new DAO_NOID_Author();
		Authors author = authorService.getById(author_id);
		if (author == null) {
			System.out.println("invalid author");
			return "-Invalid input";
		}

		DAO_NOID_Owned_Images ownedService = new DAO_NOID_Owned_Images();
		if (!ownedService.checkIfOwnedFromAuthor(user.getId(), author_id)) {
			System.out.println("You need to own an image of an author before you can comment him.");
			return "-You need to own at least one image from an author before you can comment.";
		}

		Connection conn = createConnection();
		if (conn == null)
			return "-Server connection error";

		DAOComments commentService = new DAOComments();
		Comments comment = new Comments();
		comment.setUsers_id(user.getId());
		comment.setAuthors_users_id(author_id);
		comment.setText(text);
		comment.setDate(new Timestamp(System.currentTimeMillis()));
		commentService.add(comment);
		return "Comment submited";
	}

	public String applyForAuthor(HttpSession session, MultipartApplicationFile file) {
		if (session.getAttribute("user") == null) {
			System.out.println("Not logged in!");
			return "-You must be logged in";
		}

		if (file.getData().size() != 10) {
			System.out.println("10 images required");
			return "-10 images are required";
		}

		Users user = (Users) session.getAttribute("user");
		user = getById(user.getId());

		if (!user.getRole().equals("kupac")) {
			System.out.println("You cannot apply to be an author.");
			return "-You cannot apply to be an author";
		}

		if (user.isBanned() || user.isDeleted()) {
			System.out.println("Disabled account.");
			return "-Invalid account";
		}

		user.setRole("applied");
		update(user, "PASSWORD");
		try {
			for (int i = 0; i < 10; i++) {
				byte[] data = file.getData().get(i);
				String fileName = user.getId() + ";" + user.getUsername() + ";num" + i + ".png";
				String directoryName = user.getId() + ";" + user.getUsername() + "//";
				new File(Config.AUTHOR_REPOSITORY + directoryName).mkdirs();
				Files.write(data, new File(Config.AUTHOR_REPOSITORY + directoryName + fileName));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Application succesfully submitted.";
	}

	public boolean setAuthorCreditcard(HttpSession session, int card_id) {
		if (card_id <= 0) {
			System.out.println("invalid card");
			return false;
		}

		if (session.getAttribute("user") == null) {
			System.out.println("not logged in");
			return false;
		}

		Users user = (Users) session.getAttribute("user");
		user = getById(user.getId());

		DAO_NOID_Author authorService = new DAO_NOID_Author();
		Authors author = authorService.getById(user.getId());
		if (author == null || !user.getRole().equals("prodavac")) {
			System.out.println("not an author");
			return false;
		}

		DAOCreditcard cardService = new DAOCreditcard();
		Creditcard card = cardService.getById(card_id);

		if (card.getUsers_id() != user.getId()) {
			System.out.println("invalid card");
			return false;
		}

		Connection conn = createConnection();
		if (conn == null)
			return false;

		author.setCard_id(card_id);
		PreparedStatement st;
		try {
			st = conn.prepareStatement("UPDATE AUTHORS SET Card_ID = ? WHERE Users_ID = ?");
			st.setObject(1, card_id);
			st.setObject(2, author.getUsers_id());
			st.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean deleteImage(HttpSession session, Images image) {
		if (session.getAttribute("user") == null) {
			System.out.println("Not logged in");
			return false;
		}
		
		Users user = (Users)session.getAttribute("user");
		if (!user.getRole().equals("prodavac")) {
			System.out.println("You do not have permission to delete images.");
			return false;
		}
		
		if (user.isBanned() || user.isDeleted()) {
			System.out.println("Invalid accound.");
			return false;
		}
		
		if (image == null || image.getId() <= 0) {
			System.out.println("Invalid input");
			return false;
		}
		
		DAOImage imageService = new DAOImage();
		image = imageService.getById(image.getId());
		
		if (image.getAuthorsUsersId() != user.getId()) {
			System.out.println("You do not have permission to delete that image.");
			return false;
		}
		
		Connection conn = createConnection();
		if (conn == null)
			return false;
		
		try {
			PreparedStatement st = conn.prepareStatement("DELETE FROM images_has_categories WHERE Images_ID = ?");
			st.setObject(1, image.getId());
			st.execute();
			
			st = conn.prepareStatement("DELETE FROM images_has_image_resolutions WHERE Images_ID = ?");
			st.setObject(1, image.getId());
			st.execute();
			
			st = conn.prepareStatement("DELETE FROM owned_images_owned_resolutions WHERE Owned_Images_Images_ID = ?");
			st.setObject(1,  image.getId());
			st.execute();
			
			st = conn.prepareStatement("DELETE FROM owned_images WHERE images_id = ?");
			st.setObject(1,  image.getId());
			st.execute();
			
			st = conn.prepareStatement("DELETE FROM IMAGES WHERE ID = ?");
			st.setObject(1, image.getId());
			st.execute();
			
			st.close();
			closeConnection(conn);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		closeConnection(conn);
		return false;
	}
	
	public List<Firm> getFirms(HttpSession session){
		if (session.getAttribute("user") == null) {
			System.out.println("Not logged in.");
			return null;
		}
		
		Users user = (Users)session.getAttribute("user");
		user = getById(user.getId());
		
		if (user.isBanned() || user.isDeleted()) {
			System.out.println("Disable account");
			return null;
		}
		
		if (!user.getRole().equals("prodavac")) {
			System.out.println("Permission denied.");
			return null;
		}
		
		Connection conn = createConnection();
		if (conn == null)
			return null;
		
		try {
			PreparedStatement st = conn.prepareStatement("SELECT * FROM FIRM WHERE STATUS = 0");
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
	
	public String applyToFirm(HttpSession session, Firm firm) {
		if (session.getAttribute("user") == null) {
			System.out.println("Not logged in.");
			return "-Not logged in";
		}
		
		Users user = (Users)session.getAttribute("user");
		if (!user.getRole().equals("prodavac")) {
			System.out.println("Permission denied.");
			return "-Permission denied";
		}
		
		user = getById(user.getId());
		if (user.isBanned() || user.isDeleted()) {
			System.out.println("Your accound is banned or deleted.");
			return "-Your account is banned or deleted";
		}
		
		DAO_NOID_Author authorService = new DAO_NOID_Author();
		Authors author = authorService.getById(user.getId());
		
		if (author.isApplied()) {
			System.out.println("You have already applied for a firm.");
			return "-You have already applied for a firm";
		}
		
		if (author.getFirm_id() > 0) {
			System.out.println("You are already part of a firm.");
			return "-You are already part of a firm";
		}
		
		if (firm == null || firm.getId() <= 0) {
			System.out.println("Invalid input");
			return "-Invalid input";
		}
		
		Connection conn = createConnection();
		if (conn == null)
			return "-Connection error";
		
		try {
			PreparedStatement st = conn.prepareStatement("SELECT * FROM IMAGES WHERE Authors_Users_ID = ?");
			st.setObject(1, user.getId());
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				System.out.println("You must delete all images before applying to a firm.");
				rs.close();
				st.close();
				closeConnection(conn);
				return "-You must delete all images before applying to a firm";
			}
			rs.close();
			
			st = conn.prepareStatement("UPDATE AUTHORS SET APPLIED = 1, Firm_ID = ? WHERE Users_ID = ?");
			st.setObject(1, firm.getId());
			st.setObject(2, user.getId());
			int test = st.executeUpdate();
			if (test != 0) {
				System.out.println("Succesfully applied.");
				st.close();
				closeConnection(conn);
				return "Application submited";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		closeConnection(conn);
		return "-An error has occurred";
	}
	
	public String leaveFirm(HttpSession session) {
		if (session.getAttribute("user") == null) {
			System.out.println("Not logged in");
			return "-Not logged in";
		}
		
		Users user = (Users)session.getAttribute("user");
		if (!user.getRole().equals("prodavac")) {
			System.out.println("Permission denied.");
			return "-Permission denied";
		}
		
		user = getById(user.getId());
		if (user.isBanned() || user.isDeleted()) {
			System.out.println("Your accound is banned or deleted.");
			return "-Your account is banned or deleted";
		}
		
		DAO_NOID_Author authorService = new DAO_NOID_Author();
		Authors author = authorService.getById(user.getId());
		
		DAOFirm firmService = new DAOFirm();
		Firm firm = firmService.getById(author.getFirm_id());
		
		if (firm == null) {
			System.out.println("You already aren't a part of a firm.");
			return "-You aren't a part of any firm";
		}
		
		Connection conn = createConnection();
		if (conn == null)
			return "-Connection error";
		
		try {
			PreparedStatement st = conn.prepareStatement("UPDATE AUTHORS SET Firm_ID = NULL WHERE Users_ID = ?");
			st.setObject(1, user.getId());
			int test = st.executeUpdate();
			if (test != 0) {
				System.out.println("Succesfully left the firm. Attempting to delete all images.");
				List<Images> images = new ArrayList<>();
				DAOImage imageService = new DAOImage();
				st = conn.prepareStatement("SELECT * FROM IMAGES WHERE Authors_Users_ID = ?");
				st.setObject(1, user.getId());
				ResultSet rs = st.executeQuery();
				while (rs.next()) {
					images.add(imageService.readFromResultSet(rs));
				}
				rs.close();
				
				for (Images image: images) {
					st = conn.prepareStatement("DELETE FROM images_has_categories WHERE Images_ID = ?");
					st.setObject(1, image.getId());
					st.execute();
					
					st = conn.prepareStatement("DELETE FROM images_has_image_resolutions WHERE Images_ID = ?");
					st.setObject(1, image.getId());
					st.execute();
					
					st = conn.prepareStatement("DELETE FROM owned_images_owned_resolutions WHERE Owned_Images_Images_ID = ?");
					st.setObject(1,  image.getId());
					st.execute();
					
					st = conn.prepareStatement("DELETE FROM owned_images WHERE images_id = ?");
					st.setObject(1,  image.getId());
					st.execute();
					
					st = conn.prepareStatement("DELETE FROM IMAGES WHERE ID = ?");
					st.setObject(1, image.getId());
					st.execute();
				}
				System.out.println("All images associated with your account have been deleted. You may now apply to another firm.");
				st.close();
				closeConnection(conn);
				return "All images associated with your account have been deleted. You may now apply to another firm";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		closeConnection(conn);
		return "-An error has occurred";
	}
	
	public Authors getAuthorById(int id) {
		DAO_NOID_Author authorService = new DAO_NOID_Author();
		return authorService.getById(id);
	}
	
	public int getFirmId(HttpSession session) {
		if (session.getAttribute("user") == null) {
			return -1;
		}
		
		Users user = (Users)session.getAttribute("user");
		if (!user.getRole().equals("prodavac"))
			return -1;
		
		DAO_NOID_Author authorService = new DAO_NOID_Author();
		Authors author = authorService.getById(user.getId());
		return author.getFirm_id();
	}
	
	public boolean checkIfAppliedForFirm(HttpSession session) {
		if (session.getAttribute("user") == null) {
			return false;
		}
		
		Users user = (Users)session.getAttribute("user");
		if (!user.getRole().equals("prodavac"))
			return false;
		
		DAO_NOID_Author authorService = new DAO_NOID_Author();
		Authors author = authorService.getById(user.getId());
		return author.isApplied();
	}
	
	public List<Images> getImagesFromUser(HttpSession session){
		if (session.getAttribute("user") == null) {
			System.out.println("Not logged in");
			return null;
		}
		
		Users user = (Users)session.getAttribute("user");
		if (!user.getRole().equals("prodavac")) {
			System.out.println("Permission denied.");
			return null;
		}
		
		user = getById(user.getId());
		
		Connection conn = createConnection();
		if (conn == null)
			return null;
		
		try {
			PreparedStatement st = conn.prepareStatement("SELECT * FROM IMAGES WHERE authors_users_id = ?");
			st.setObject(1, user.getId());
			ResultSet rs = st.executeQuery();
			
			List<Images> list = new ArrayList<>();
			DAOImage imageService = new DAOImage();
			while (rs.next()) {
				list.add(imageService.readFromResultSet(rs));
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
	
	public List<Images> getOwnedImages(HttpSession session){
		if (session.getAttribute("user") == null) {
			System.out.println("Not logged in");
			return null;
		}
		
		Users user = (Users)session.getAttribute("user");
		if (!user.getRole().equals("prodavac")) {
			System.out.println("Permission denied.");
			return null;
		}
		
		user = getById(user.getId());
		
		Connection conn = createConnection();
		if (conn == null)
			return null;
		
		try {
			PreparedStatement st = conn.prepareStatement("SELECT ID, NAME, TAGS, UPLOAD_DATE, RATING, LOCATION, SALES, DESCRIPTION, Authors_Users_id, PENDING, VOTES from images\r\n" + 
					"	INNER JOIN owned_images ON owned_images.Images_ID = images.ID\r\n" + 
					"    WHERE Users_ID = ?");
			st.setObject(1, user.getId());
			ResultSet rs = st.executeQuery();
			
			
			List<Images> list = new ArrayList<>();
			DAOImage imageService = new DAOImage();
			while (rs.next()) {
				list.add(imageService.readFromResultSet(rs));
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
	
	public boolean send(HttpSession session, Images image) {
		if (session.getAttribute("user") == null) {
			System.out.println("Not logged in");
			return false;
		}
		
		Users user = (Users)session.getAttribute("user");
		if (!user.getRole().equals("prodavac")) {
			System.out.println("Permission denied.");
			return false;
		}
		
		user = getById(user.getId());
		
		if (image == null || image.getId() <= 0) {
			return false;
		}
		
		DAOImage imageService = new DAOImage();
		image = imageService.getById(image.getId());
		
		if (image == null)
			return false;
		
		Connection conn = createConnection();
		if (conn == null)
			return false;
		
		try {
			PreparedStatement st = conn.prepareStatement("SELECT * FROM owned_images_owned_resolutions WHERE owned_images_users_id = ? and owned_images_images_id = ?");
			st.setObject(1, user.getId());
			st.setObject(2, image.getId());
			ResultSet rs = st.executeQuery();
			ArrayList<String> files = new ArrayList<>();
			while (rs.next()) {
				files.add(Config.IMAGE_REPOSITORY + image.getId() + ";" + image.getName() + ";"
						+ UtilsMethods.convertInt(rs.getObject("Image_Resolutions_ID")) + ".png");
			}
			
			rs.close();
			st.close();
			closeConnection(conn);
			sendImages(files, user);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}

}
