package services;

import java.util.List;

import javax.servlet.http.HttpSession;

import entities.Admins;
import entities.Operators;

public interface IServiceAdmin extends IServiceAbstract<Admins>{

	public String login(Admins admin, HttpSession session);
	public String addOperator(Operators operator, HttpSession session);
	public boolean removeOperator(int operator_id, HttpSession session);
	public List<Operators> getOperators(HttpSession session);
	public String addCategory(String categoryName, HttpSession session);
	
}
