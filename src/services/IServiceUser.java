package services;

import java.util.List;

import javax.servlet.http.HttpSession;

import entities.Authors;
import entities.Creditcard;
import entities.Firm;
import entities.Images;
import entities.Images_has_Image_Resolutions;
import entities.Users;
import utils.MultipartApplicationFile;
import utils.ShoppingCart;

public interface IServiceUser extends IServiceAbstract<Users>{

	public boolean register(Users user);
	public boolean confirmRegistration(String token);
	public String login(Users user, HttpSession session);
	public boolean addToShoppingCart(HttpSession session, Images_has_Image_Resolutions image);
	public boolean removeFromShoppingCart(HttpSession session, Images_has_Image_Resolutions image);
	public ShoppingCart getShoppingCart(HttpSession session);
	public String addCreditcard(HttpSession session, Creditcard card);
	public List<Creditcard> getCreditcards(HttpSession session);
	public String buy(HttpSession session, Creditcard card);
	public String rateImage(HttpSession session, int image_id, int value);
	public String rateAuthor(HttpSession session, int author_id, int value);
	public String comment(HttpSession session, int author_id, String text);
	public String applyForAuthor(HttpSession session, MultipartApplicationFile file);
	public boolean setAuthorCreditcard(HttpSession session, int card_id);
	public boolean forgotPassword(HttpSession session, String username);
	public boolean resetPassword(HttpSession session, String token, String newPassword);
	public String changePassword(HttpSession session,String currentPassword, String newPassword);
	public boolean deleteAccount(HttpSession session);
	public boolean deleteImage(HttpSession session, Images image);
	public List<Firm> getFirms(HttpSession session);
	public String applyToFirm(HttpSession session, Firm firm);
	public String leaveFirm(HttpSession session);
	public Authors getAuthorById(int id);
	public int getFirmId(HttpSession session);
	public boolean checkIfAppliedForFirm(HttpSession session);
	public List<Images> getImagesFromUser(HttpSession session);
	public List<Images> getOwnedImages(HttpSession session);
	public boolean send(HttpSession session, Images image);
	
}
