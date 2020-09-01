package services;

import java.util.List;

import javax.servlet.http.HttpSession;

import dao.IDAOOperator;
import entities.Authors;
import entities.Categories;
import entities.Firm;
import entities.Images;
import entities.Operators;
import entities.Users;

public class ServiceOperators extends ServiceAbstract<Operators, IDAOOperator> implements IServiceOperator{

	public ServiceOperators(IDAOOperator dao) {
		super(dao);
	}

	@Override
	public String login(HttpSession session, Operators operator) {
		return this.dao.login(session, operator);
	}

	@Override
	public boolean changePassword(HttpSession session, Operators operator) {
		return this.dao.changePassword(session, operator);
	}

	@Override
	public List<Images> getPendingImages(HttpSession session, String init, String reverse) {
		return this.dao.getPendingImages(session, init, reverse);
	}

	@Override
	public boolean approveImage(HttpSession session, Images image) {
		return this.dao.approveImage(session, image);
	}

	@Override
	public List<Users> getApplicants(HttpSession session) {
		return this.dao.getApplicants(session);
	}

	@Override
	public String rateApplicant(HttpSession session, int user_id, int grade) {
		return this.dao.rateApplicant(session, user_id, grade);
	}

	@Override
	public String banUser(HttpSession session, Users user) {
		return this.dao.banUser(session, user);
	}

	@Override
	public int numberOfSoldImagesFromUser(HttpSession session, Users user) {
		return this.dao.numberOfSoldImagesFromUser(session, user);
	}

	@Override
	public int numberOfSoldImagesFromCategory(HttpSession session, Categories category) {
		return this.dao.numberOfSoldImagesFromCategory(session, category);
	}

	@Override
	public List<String> mostFrequentlyBoughtImagesByUser(HttpSession session, Users user) {
		return this.dao.mostFrequentlyBoughtImagesByUser(session, user);
	}

	@Override
	public List<String> MostFrequentlyBoughtImagesInCategory(HttpSession session,
			Categories category) {
		return this.dao.MostFrequentlyBoughtImagesInCategory(session, category);
	}

	@Override
	public double averagePriceInCategory(HttpSession session, Categories category) {
		return this.dao.averagePriceInCategory(session, category);
	}

	@Override
	public double averagePriceByAuthor(HttpSession session, Users user) {
		return this.dao.averagePriceByAuthor(session, user);
	}

	@Override
	public List<Firm> getAppliedFirms(HttpSession session) {
		return this.dao.getAppliedFirms(session);
	}

	@Override
	public String approveFirm(HttpSession session, Firm firm) {
		return this.dao.approveFirm(session, firm);
	}

	@Override
	public String addFirmOperator(HttpSession session, Operators newOperator) {
		return this.dao.addFirmOperator(session, newOperator);
	}

	@Override
	public List<Authors> getAppliedAuthors(HttpSession session) {
		return this.dao.getAppliedAuthors(session);
	}

	@Override
	public String approveFirmAuthor(HttpSession session, Authors author) {
		return this.dao.approveFirmAuthor(session, author);
	}

	@Override
	public List<Users> getAllUsers() {
		return this.dao.getAllUsers();
	}

	@Override
	public String getApplicantImage(HttpSession session, int user_id, int index) {
		return this.dao.getApplicantImage(session, user_id, index);
	}

	@Override
	public List<Operators> getFirmOperators(HttpSession session) {
		return this.dao.getFirmOperators(session);
	}

}
