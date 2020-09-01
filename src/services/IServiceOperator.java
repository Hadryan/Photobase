package services;

import java.util.List;

import javax.servlet.http.HttpSession;

import entities.Authors;
import entities.Categories;
import entities.Firm;
import entities.Images;
import entities.Operators;
import entities.Users;

public interface IServiceOperator extends IServiceAbstract<Operators>{

	public String login(HttpSession session, Operators operator);
	public boolean changePassword(HttpSession session, Operators operator);
	public List<Images> getPendingImages(HttpSession session, String init, String reverse);
	public boolean approveImage(HttpSession session, Images image);
	public List<Users> getApplicants(HttpSession session);
	public String rateApplicant(HttpSession session, int user_id, int grade);
	public String banUser(HttpSession session, Users user);
	public int numberOfSoldImagesFromUser(HttpSession session, Users user);
	public int numberOfSoldImagesFromCategory(HttpSession session, Categories category);
	public List<String> mostFrequentlyBoughtImagesByUser(HttpSession session, Users user);
	public List<String> MostFrequentlyBoughtImagesInCategory(HttpSession session, Categories category);
	public double averagePriceInCategory(HttpSession session, Categories category);
	public double averagePriceByAuthor(HttpSession session, Users user);
	public List<Firm> getAppliedFirms(HttpSession session);
	public String approveFirm(HttpSession session, Firm firm);
	public String addFirmOperator(HttpSession session, Operators newOperator);
	public List<Authors> getAppliedAuthors(HttpSession session);
	public String approveFirmAuthor(HttpSession session, Authors author);
	public String getApplicantImage(HttpSession session, int user_id, int index);
	public List<Users> getAllUsers();
	public List<Operators> getFirmOperators(HttpSession session);
	
}
