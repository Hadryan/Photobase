package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class UtilsMethods {

	private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static int convertInt(Object o) {
		int id = 0;
		try {
			id = Integer.parseInt(o == null ? "0" : o.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return id;
	}

	public static double convertDouble(Object o) {
		double id = 0;
		try {
			id = Double.parseDouble(o == null ? "0" : o.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return id;
	}

	public static String convertToString(Object o) {
		return o == null ? "" : o.toString();
	}

	public static boolean convertTinyInt(Object o) {
		int id = 0;
		try {
			id = Integer.parseInt(o == null ? "0" : o.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return id == 0 ? false : true;
	}

	public static Date convertToDate(Object o) {
		Date date = null;
		try {
			date = df.parse((String) o);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}

	public static String[] convertToStringArray(Object o) {
		if (o != null) {
			String array[] = null;
			try {
				array = ((String) o).split(",");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return array;
		} else {
			return null;
		}
	}
	
	public static void sendMail(String email, String subject, String text) {
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

			System.out.println("Mail succesfully sent.");
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static synchronized String encodeBase64(String imagePath) {
		String base64Image = "";
		File file = new File(imagePath);
		try (FileInputStream imageInFile = new FileInputStream(file)) {
			// Reading a Image file from file system
			byte imageData[] = new byte[(int) file.length()];
			imageInFile.read(imageData);
			base64Image = Base64.getEncoder().encodeToString(imageData);
		} catch (FileNotFoundException e) {
			System.out.println("Image not found" + e);
		} catch (IOException ioe) {
			System.out.println("Exception while reading the Image " + ioe);
		}
		return base64Image;
	}
	
	public static synchronized void decodeBase64(String base64Image, String pathFile) {
		try (FileOutputStream imageOutFile = new FileOutputStream(pathFile)) {
			// Converting a Base64 String into Image byte array
			byte[] imageByteArray = Base64.getDecoder().decode(base64Image);
			imageOutFile.write(imageByteArray);
		} catch (FileNotFoundException e) {
			System.out.println("Image not found" + e);
		} catch (IOException ioe) {
			System.out.println("Exception while reading the Image " + ioe);
		}
	}

}
