package dao;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;

import com.google.common.io.Files;

import entities.Authors;
import entities.Categories;
import entities.Image_Resolutions;
import entities.Images;
import entities.Images_has_Categories;
import entities.Images_has_Image_Resolutions;
import entities.Users;
import services.ServiceUser;
import utils.Config;
import utils.MultipartFile;
import utils.UtilsMethods;

public class DAOImage extends DAOAbstractDatabase<Images> implements IDAOImage {

	public DAOImage() {
		super(Images.class);
	}

	@Override
	public String uploadImage(MultipartFile file, HttpSession session) {
		if (session.getAttribute("user") == null) {
			System.out.println("Not logged in.");
			return "-Not logged in";
		}
		
		Users user = (Users)session.getAttribute("user");
		int userId = user.getId();
		
		ServiceUser userService = new ServiceUser(new DAOUser());
		user = userService.getById(userId);
		
		// provera
		if (user == null || user.getRole() == null || !user.getRole().equals("prodavac") || user.isBanned() || user.isDeleted()) {
			System.out.println("You do not have permission to upload images.");
			return "-You do not have permission to upload images";
		}

		DAO_NOID_Author authorService = new DAO_NOID_Author();
		Authors author = authorService.getById(user.getId());
		if (author.getCard_id() <= 0) {
			System.out.println("You must add a creditcard before you put images for sale.");
			return "You must add a creditcard before you put images for sale";
		}
		
		if (!dailyLimitCheck(user.getId())) {
			System.out.println("Daily limit of 3 images reached.");
			return "-Daily limit of 3 image uploads reached";
		}

		if (!weeklyLimitCheck(user.getId())) {
			System.out.println("Weekly limit of 8 images reached.");
			return "-Weekly limit of 8 image uploads reached";
		}
		
		
		if (file.getImage() == null || file.getImage().getName() == null || file.getImage().getName().trim().equals("")) {
			System.out.println("Invalid input.");
			return "-Invalid input";
		}

		try {
			// set information
			// AUTOR
			file.getImage().setAuthorsUsersId(userId);
			file.getImage().setPending(true);
			file.getImage().setUploadDate(new Timestamp(System.currentTimeMillis()));

			// dodavanje slike u bazu
			int imageId = super.addReturnId(file.getImage());

			// cuvanje slike lokalno
			String fileName = imageId + ";" + file.getImage().getName() + ".png";
			synchronized (this) {
				Files.write(file.getData(), new File(Config.IMAGE_REPOSITORY + fileName));

				createThumbnailAndPreview(Config.IMAGE_REPOSITORY + fileName);
			}
			// provera i pravljenje resized kopija
			if (!checkAndResize(fileName, file.getResolutions().getItems())) {

				synchronized (this) {
					File file1 = new File(Config.IMAGE_REPOSITORY + fileName);
					file1.delete();
					File file2 = new File(Config.IMAGE_REPOSITORY + fileName.substring(0, fileName.length() - 4) + ";thumbnail.png");
					file2.delete();
				}

				super.removeById(imageId);

				return "You cannot select a higher resolution than the original image";
			}

			// dodavanje rezolucija slike
			DAO_NOID_Images_has_Image_Resolutions resolutionService = new DAO_NOID_Images_has_Image_Resolutions();
			
			for (Images_has_Image_Resolutions res : file.getResolutions().getItems()) {
				res.setImages_id(imageId);
				resolutionService.add(res);
			}

			// dodavanje kategorija slike
			DAO_NOID_Images_has_Categories categoriesService = new DAO_NOID_Images_has_Categories();

			for (Categories cat : file.getCategories().getItems()) {
				Images_has_Categories record = new Images_has_Categories();
				record.setImagesId(imageId);
				record.setCategoriesId(cat.getId());
				categoriesService.add(record);
			}

			return "Succesfully uploaded image";
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "-Internal server error";
	}

	private boolean dailyLimitCheck(int user_id) {
		Connection conn = createConnection();
		if (conn == null)
			return false;

		try {
			PreparedStatement st = conn.prepareStatement(
					"SELECT * FROM IMAGES WHERE (UPLOAD_DATE > DATE_SUB(now(), INTERVAL 1 DAY)) AND Authors_Users_ID = ?");
			st.setObject(1, user_id);
			ResultSet rs = st.executeQuery();
			int count = 0;
			while (rs.next()) {
				count++;
			}
			if (count < 3)
				return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean weeklyLimitCheck(int user_id) {
		Connection conn = createConnection();
		if (conn == null)
			return false;

		try {
			PreparedStatement st = conn.prepareStatement(
					"SELECT * FROM IMAGES WHERE (UPLOAD_DATE > DATE_SUB(now(), INTERVAL 7 DAY)) AND Authors_Users_ID = ?");
			st.setObject(1, user_id);
			ResultSet rs = st.executeQuery();
			int count = 0;
			while (rs.next()) {
				count++;
			}
			if (count < 8)
				return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	private synchronized boolean checkAndResize(String fileName, ArrayList<Images_has_Image_Resolutions> res)
			throws IOException {

		DAOImage_Resolutions resolutionService = new DAOImage_Resolutions();

		ArrayList<Image_Resolutions> resolutions = new ArrayList<>();
		for (Images_has_Image_Resolutions rez : res) {
			resolutions.add(resolutionService.getById(rez.getImage_resolutions_id()));
		}

		File input = new File(Config.IMAGE_REPOSITORY + fileName);
		BufferedImage original = ImageIO.read(input);

		int originalWidth = original.getWidth();
		int originalHeight = original.getHeight();

		for (Image_Resolutions rez : resolutions) {

			for (Images_has_Image_Resolutions test : res) {

				if (rez.getId() == test.getImage_resolutions_id()) {

					if (rez.getWidth() > originalWidth || rez.getHeight() > originalHeight) {
						System.out.println("Selected resolution is larger than the original photo.");
						return false;
					}

					if (test.getPrice() < rez.getMin_price() || test.getPrice() > rez.getMax_price()) {
						System.out.println(
								"The price you have entered is not in the price boundaries for the selected resolution.");
						return false;
					}

				}

			}

		}

		// test passed, now we can resize and save photos

		for (Image_Resolutions rez : resolutions) {

			// originalWidth:originalHeight = rez.getWidth() : x
			// x = (originalHeight * rez.getWidth())/ originalWidth
			int calculatedHeight = (originalHeight * rez.getWidth()) / originalWidth;

			System.out.println("Resized photo: " + rez.getWidth() + "x" + calculatedHeight);

			String imageName = fileName.substring(0, fileName.length() - 4) + ";" + rez.getId() + ".png";

			BufferedImage resized = resize(original, rez.getWidth(), calculatedHeight);
			File output = new File(Config.IMAGE_REPOSITORY + imageName);
			ImageIO.write(resized, "png", output);

		}

		return true;
	}

	private static BufferedImage resize(BufferedImage img, int width, int height) {
		Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = resized.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();
		return resized;
	}

	public synchronized void createThumbnailAndPreview(String path) {
		try {
			File input = new File(path);
			BufferedImage original = ImageIO.read(input);

			int thumbnailWidth = (original.getWidth() * Config.THUMBNAIL_HEIGHT) / original.getHeight();
			String fileName = path.substring(0, path.length() - 4) + ";thumbnail.png";

			BufferedImage resized = resize(original, thumbnailWidth, Config.THUMBNAIL_HEIGHT);
			File output = new File(fileName);
			ImageIO.write(resized, "png", output);

			int previewWidth = (original.getWidth() * Config.PREVIEW_HEIGHT) / original.getHeight();
			fileName = path.substring(0, path.length() - 4) + ";preview.png";

			resized = resize(original, previewWidth, Config.PREVIEW_HEIGHT);
			
			createPreviewWithWatermark(resized, fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void createPreviewWithWatermark(BufferedImage image, String destination) {
		try {
	        BufferedImage overlay = ImageIO.read(new File("D:\\TestImageRepository\\watermark.png"));

	        // create the new image, canvas size is the max. of both image siz
	        
	        int overlayWidth = overlay.getWidth();
	        int overlayHeight = overlay.getHeight();
	        
	        if (image.getWidth() > overlay.getWidth()) {
	        	overlayWidth = image.getWidth();
	        }
	        
	        if (image.getHeight() > overlay.getHeight()) {
	        	overlayHeight = image.getHeight();
	        }
	        
	        if (overlayWidth != overlay.getWidth() || overlayHeight != overlay.getHeight())
	        	overlay = resize(overlay, image.getWidth(), image.getHeight());
	        
	        BufferedImage combined = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

	        // paint both images, preserving the alpha channels
	        Graphics g = combined.getGraphics();
	        g.drawImage(image, 0, 0, null);
	        g.drawImage(overlay, 10, 0, null);

	        ImageIO.write(combined, "PNG", new File(destination));
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	public String getImageThumbnail(Images image) {
		if (image == null || image.getId() <= 0 || image.getName() == null || image.getName().trim().equals("")) {
			return "Invalid input.";
		}
		
		String fileName = image.getId() + ";" + image.getName() + ";thumbnail.png";
		return UtilsMethods.encodeBase64(Config.IMAGE_REPOSITORY + fileName);
	}
	
	public String getImagePreview(Images image) {
		if (image == null || image.getId() <= 0 || image.getName() == null || image.getName().trim().equals("")) {
			return "Invalid input.";
		}
		
		String fileName = image.getId() + ";" + image.getName() + ";preview.png";
		return UtilsMethods.encodeBase64(Config.IMAGE_REPOSITORY + fileName);
	}

	@Override
	public List<Images> searchByKeyword(HttpSession session, String entry, String init, String reverse) {
		if (entry == null || entry.trim().equals(""))
			return null;

		if (session.getAttribute("keywordOffset") == null) {
			session.setAttribute("keywordOffset", 0);
		}

		Integer offset = (Integer) session.getAttribute("keywordOffset");
		if (init.equals("true"))
			session.setAttribute("keywordOffset", 10);
		else if (!reverse.equals("true"))
			session.setAttribute("keywordOffset", offset + 10);
		else
			session.setAttribute("keywordOffset", offset - 10);

		String tags[] = entry.trim().replace(" ", "").split(",");

		String query = "SELECT * FROM IMAGES WHERE PENDING = 0 AND (";
		String questionMarks = "";

		for (String tag : tags) {
			questionMarks = questionMarks == "" ? " TAGS LIKE ?" : questionMarks + " OR TAGS LIKE ?";
		}

		query += questionMarks + ") LIMIT 10 OFFSET ";
		
		if (init.equals("true"))
			query = query + "0";
		else if (reverse.equals("true"))
			query = query + (offset - 20);
		else
			query = query + offset;

		Connection conn = createConnection();
		if (conn == null)
			return null;

		try {
			PreparedStatement st = conn.prepareStatement(query);
			int paramIndex = 1;

			for (String tag : tags) {
				st.setObject(paramIndex++, "%" + tag + "%");
			}

			ResultSet rs = st.executeQuery();
			List<Images> list = new ArrayList<>();
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

	public List<Images> searchBy(HttpSession session, String criteria, String text, String init, String reverse) {
		if (!criteria.equals("Image") && !criteria.equals("Author") && !criteria.equals("Category")
				&& !criteria.equals("Keyword"))
			return null;

		if (session.getAttribute("lastSearch") == null) {
			session.setAttribute("lastSearch", criteria);
			session.setAttribute("offset", 0);
		}

		Integer offset = (Integer) session.getAttribute("offset");
		String lastSearch = (String) session.getAttribute("lastSearch");

		if (!lastSearch.equals(criteria)) {
			offset = 0;
		}

		if (init.equals("true"))
			session.setAttribute("offset", 10);
		else if (!reverse.equals("true"))
			session.setAttribute("offset", offset + 10);
		else
			session.setAttribute("offset", offset - 10);
		session.setAttribute("lastSearch", criteria);

		String searchQuery = "";

		switch (criteria) {
		case "Image":
			searchQuery = "SELECT * FROM IMAGES WHERE PENDING = 0 AND NAME LIKE ? LIMIT 10 OFFSET ";
			break;
		case "Author": {
			searchQuery = "SELECT images.ID, images.NAME, images.TAGS, images.UPLOAD_DATE, images.RATING, images.LOCATION, images.SALES, images.DESCRIPTION, images.Authors_Users_ID, images.PENDING, images.VOTES FROM photobase.images \r\n"
					+ "	INNER JOIN photobase.authors ON photobase.authors.Users_ID = photobase.images.Authors_Users_ID\r\n"
					+ "    INNER JOIN photobase.users ON photobase.users.ID = photobase.authors.Users_ID\r\n"
					+ "    WHERE users.USERNAME LIKE ? AND images.PENDING = 0 LIMIT 10 OFFSET ";
			break;
		}
		case "Category": {
			searchQuery = "SELECT images.ID, images.NAME, images.TAGS, images.UPLOAD_DATE, images.RATING, images.LOCATION, images.SALES, images.DESCRIPTION, images.Authors_Users_ID, images.PENDING, images.VOTES FROM photobase.images\r\n"
					+ "	INNER JOIN photobase.images_has_categories ON photobase.images.ID = photobase.images_has_categories.Images_ID\r\n"
					+ "    INNER JOIN photobase.categories ON photobase.categories.ID = photobase.images_has_categories.Categories_ID\r\n"
					+ "    WHERE photobase.categories.NAME = ? AND images.PENDING = 0 LIMIT 10 OFFSET ";
			break;
		}
		}

		if (init.equals("true"))
			searchQuery = searchQuery + "0";
		else if (reverse.equals("true"))
			searchQuery = searchQuery + (offset - 20);
		else
			searchQuery = searchQuery + offset;

		Connection conn = createConnection();
		if (conn == null)
			return null;

		try {
			PreparedStatement st = conn.prepareStatement(searchQuery);
			if (criteria.equals("Image") || criteria.equals("Author"))
				st.setObject(1, text + "%");
			else
				st.setObject(1, text);
			ResultSet rs = st.executeQuery();
			List<Images> list = new ArrayList<>();
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

	public List<Images> sortBy(HttpSession session, String criteria, int order, String init, String reverse) {
		if (!criteria.equals("Date") && !criteria.equals("Price") && !criteria.equals("Sales")
				&& !criteria.equals("Name") && !criteria.equals("Rating"))
			return null;

		if (session.getAttribute("lastSort") == null) {
			session.setAttribute("lastSort", criteria);
			session.setAttribute("offsetSort", 0);
			session.setAttribute("order", order);
		}

		Integer offset = (Integer) session.getAttribute("offsetSort");
		String lastSort = (String) session.getAttribute("lastSort");
		Integer lastOrder = (Integer) session.getAttribute("order");

		if (!criteria.equals(lastSort) || lastOrder != order) {
			offset = 0;
		}
		
		if (init.equals("true"))
			session.setAttribute("offsetSort", 10);
		else if (!reverse.equals("true"))
			session.setAttribute("offsetSort", offset + 10);
		else
			session.setAttribute("offsetSort", offset - 10);
		session.setAttribute("lastSort", criteria);
		session.setAttribute("order", order);

		String searchQuery = "";

		switch (criteria) {
		case "Date":
			searchQuery = "SELECT * FROM IMAGES WHERE PENDING = 0 ORDER BY UPLOAD_DATE LIMIT 10 OFFSET ";
			break;
		case "Sales": {
			if (order != 0)
				searchQuery = "SELECT * FROM IMAGES WHERE PENDING = 0 ORDER BY SALES LIMIT 10 OFFSET ";
			else
				searchQuery = "SELECT * FROM IMAGES WHERE PENDING = 0 ORDER BY SALES DESC LIMIT 10 OFFSET ";
			break;
		}
		case "Price": {
			if (order != 0)
				searchQuery = "SELECT images.ID, images.NAME, images.TAGS, images.UPLOAD_DATE, images.RATING, images.LOCATION, images.SALES, images.DESCRIPTION, images.Authors_Users_ID, images.PENDING, images.VOTES,\r\n"
						+ " MIN(images_has_image_resolutions.PRICE) FROM images \r\n"
						+ "	join images_has_image_resolutions\r\n"
						+ "    on images.ID = images_has_image_resolutions.Images_ID\r\n"
						+ " WHERE images.PENDING = 0\r\n" + "    group by images.id \r\n"
						+ " ORDER BY PRICE LIMIT 10 OFFSET ";
			else
				searchQuery = "SELECT images.ID, images.NAME, images.TAGS, images.UPLOAD_DATE, images.RATING, images.LOCATION, images.SALES, images.DESCRIPTION, images.Authors_Users_ID, images.PENDING, images.VOTES,\r\n"
						+ " MIN(images_has_image_resolutions.PRICE) FROM images \r\n"
						+ "	join images_has_image_resolutions\r\n"
						+ "    on images.ID = images_has_image_resolutions.Images_ID\r\n"
						+ " WHERE images.PENDING = 0\r\n" + "    group by images.id \r\n"
						+ " ORDER BY PRICE DESC LIMIT 10 OFFSET ";
			break;
		}
		case "Name": {
			if (order != 0)
				searchQuery = "SELECT * FROM IMAGES WHERE images.PENDING = 0 ORDER BY images.NAME LIMIT 10 OFFSET ";
			else
				searchQuery = "SELECT * FROM IMAGES WHERE images.PENDING = 0 ORDER BY images.NAME DESC LIMIT 10 OFFSET ";
			break;
		}
		case "Rating": {
			if (order != 0)
				searchQuery = "SELECT * FROM IMAGES WHERE images.PENDING = 0 ORDER BY images.RATING LIMIT 10 OFFSET ";
			else
				searchQuery = "SELECT * FROM IMAGES WHERE images.PENDING = 0 ORDER BY images.RATING DESC LIMIT 10 OFFSET ";
			break;
		}
		}
		
		if (init.equals("true"))
			searchQuery = searchQuery + "0";
		else if (reverse.equals("true"))
			searchQuery = searchQuery + (offset - 20);
		else
			searchQuery = searchQuery + offset;

		Connection conn = createConnection();
		if (conn == null)
			return null;

		try {
			PreparedStatement st = conn.prepareStatement(searchQuery);
			ResultSet rs = st.executeQuery();
			List<Images> list = new ArrayList<>();
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
	
	public List<Categories> getCategories(){
		Connection conn = createConnection();
		if (conn == null)
			return null;
		
		try {
			PreparedStatement st = conn.prepareStatement("SELECT * FROM CATEGORIES");
			ResultSet rs = st.executeQuery();
			List<Categories> list = new ArrayList<>();
			while (rs.next()) {
				Categories category = new Categories();
				category.setId(UtilsMethods.convertInt(rs.getObject("ID")));
				category.setName(UtilsMethods.convertToString(rs.getObject("NAME")));
				list.add(category);
			}
			rs.close();
			st.close();
			closeConnection(conn);
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<Image_Resolutions> getResolutions(){
		DAOImage_Resolutions resolutionService = new DAOImage_Resolutions();
		return resolutionService.getAll();
	}
	
	public List<Categories> getCategoriesOfImage(int id){
		if (id <= 0) {
			return null;
		}
		
		Connection conn = createConnection();
		if (conn == null)
			return null;
		
		try {
			PreparedStatement st = conn.prepareStatement("SELECT CATEGORIES.ID, CATEGORIES.NAME FROM CATEGORIES\r\n" + 
					"	INNER JOIN images_has_categories ON images_has_categories.Categories_ID = CATEGORIES.ID\r\n" + 
					"    WHERE Images_ID = ?");
			st.setObject(1, id);
			ResultSet rs = st.executeQuery();
			List<Categories> list = new ArrayList<>();
			while (rs.next()) {
				Categories category = new Categories();
				category.setId(UtilsMethods.convertInt(rs.getObject("ID")));
				category.setName(UtilsMethods.convertToString(rs.getObject("NAME")));
				list.add(category);
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
	
	public List<Images_has_Image_Resolutions> getAvailableResolutions(int id){
		if (id <= 0) {
			return null;
		}
		
		Connection conn = createConnection();
		if (conn == null)
			return null;
		
		try {
			PreparedStatement st = conn.prepareStatement("SELECT Images_ID, Image_Resolutions_ID, PRICE FROM images_has_image_resolutions\r\n" + 
					"	WHERE Images_ID = ?");
			st.setObject(1, id);
			ResultSet rs = st.executeQuery();
			List<Images_has_Image_Resolutions> list = new ArrayList<>();
			while (rs.next()) {
				Images_has_Image_Resolutions res = new Images_has_Image_Resolutions();
				res.setImages_id(UtilsMethods.convertInt(rs.getObject("Images_ID")));
				res.setImage_resolutions_id(UtilsMethods.convertInt(rs.getObject("Image_Resolutions_ID")));
				res.setPrice(UtilsMethods.convertDouble(rs.getObject("PRICE")));
				list.add(res);
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
	
	public List<Image_Resolutions> getResolutionsOfImage(int id){
		if (id <= 0) {
			return null;
		}
		
		Connection conn = createConnection();
		if (conn == null)
			return null;
		
		try {
			PreparedStatement st = conn.prepareStatement("SELECT ID, WIDTH, HEIGHT, MAX_PRICE, MIN_PRICE FROM image_resolutions\r\n" + 
					"	INNER JOIN images_has_image_resolutions ON images_has_image_resolutions.Image_Resolutions_ID = image_resolutions.ID\r\n" + 
					"    WHERE images_has_image_resolutions.Images_ID = ?");
			st.setObject(1, id);
			ResultSet rs = st.executeQuery();
			List<Image_Resolutions> list = new ArrayList<>();
			DAOImage_Resolutions resService = new DAOImage_Resolutions();
			while (rs.next()) {
				list.add(resService.readFromResultSet(rs));
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
