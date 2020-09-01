package services;

import java.util.List;

import javax.servlet.http.HttpSession;

import dao.IDAOUser;
import entities.Authors;
import entities.Creditcard;
import entities.Firm;
import entities.Images;
import entities.Images_has_Image_Resolutions;
import entities.Users;
import utils.MultipartApplicationFile;
import utils.ShoppingCart;

public class ServiceUser extends ServiceAbstract<Users, IDAOUser> implements IServiceUser{

	public ServiceUser(IDAOUser dao) {
		super(dao);
	}

	@Override
	public boolean register(Users user) {
		return this.dao.register(user);
	}
	
	@Override
	public boolean confirmRegistration(String token) {
		return this.dao.confirmRegistration(token);
	}

	@Override
	public String login(Users user, HttpSession session) {
		return this.dao.login(user, session);
	}

	@Override
	public boolean addToShoppingCart(HttpSession session, Images_has_Image_Resolutions image) {
		return this.dao.addToShoppingCart(session, image);
	}

	@Override
	public ShoppingCart getShoppingCart(HttpSession session) {
		return this.dao.getShoppingCart(session);
	}

	@Override
	public boolean removeFromShoppingCart(HttpSession session, Images_has_Image_Resolutions image) {
		return this.dao.removeFromShoppingCart(session, image);
	}

	@Override
	public String addCreditcard(HttpSession session, Creditcard card) {
		return this.dao.addCreditcard(session, card);
	}

	@Override
	public List<Creditcard> getCreditcards(HttpSession session) {
		return this.dao.getCreditcards(session);
	}

	@Override
	public String buy(HttpSession session, Creditcard card) {
		return this.dao.buy(session, card);
	}

	@Override
	public String rateImage(HttpSession session, int image_id, int value) {
		return this.dao.rateImage(session, image_id, value);
	}

	@Override
	public String rateAuthor(HttpSession session, int author_id, int value) {
		return this.dao.rateAuthor(session, author_id, value);
	}

	@Override
	public String comment(HttpSession session, int author_id, String text) {
		return this.dao.comment(session, author_id, text);
	}

	@Override
	public String applyForAuthor(HttpSession session, MultipartApplicationFile file) {
		return this.dao.applyForAuthor(session, file);
	}

	@Override
	public boolean setAuthorCreditcard(HttpSession session, int card_id) {
		return this.dao.setAuthorCreditcard(session, card_id);
	}

	@Override
	public boolean forgotPassword(HttpSession session, String username) {
		return this.dao.forgotPassword(session, username);
	}

	@Override
	public boolean resetPassword(HttpSession session, String token, String newPassword) {
		return this.dao.resetPassword(session, token, newPassword);
	}

	@Override
	public String changePassword(HttpSession session, String currentPassword, String newPassword) {
		return this.dao.changePassword(session, currentPassword, newPassword);
	}

	@Override
	public boolean deleteAccount(HttpSession session) {
		return this.dao.deleteAccount(session);
	}

	@Override
	public boolean deleteImage(HttpSession session, Images image) {
		return this.dao.deleteImage(session, image);
	}

	@Override
	public List<Firm> getFirms(HttpSession session) {
		return this.dao.getFirms(session);
	}

	@Override
	public String applyToFirm(HttpSession session, Firm firm) {
		return this.dao.applyToFirm(session, firm);
	}

	@Override
	public String leaveFirm(HttpSession session) {
		return this.dao.leaveFirm(session);
	}

	@Override
	public Authors getAuthorById(int id) {
		return this.dao.getAuthorById(id);
	}

	@Override
	public int getFirmId(HttpSession session) {
		return this.dao.getFirmId(session);
	}

	@Override
	public boolean checkIfAppliedForFirm(HttpSession session) {
		return this.dao.checkIfAppliedForFirm(session);
	}

	@Override
	public List<Images> getImagesFromUser(HttpSession session) {
		return this.dao.getImagesFromUser(session);
	}

	@Override
	public List<Images> getOwnedImages(HttpSession session) {
		return this.dao.getOwnedImages(session);
	}

	@Override
	public boolean send(HttpSession session, Images image) {
		return this.dao.send(session, image);
	}


}
