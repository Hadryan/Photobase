package services;

import java.util.List;

import javax.servlet.http.HttpSession;

import dao.IDAOAdmin;
import entities.Admins;
import entities.Operators;

public class ServiceAdmin extends ServiceAbstract<Admins, IDAOAdmin> implements IServiceAdmin{

	public ServiceAdmin(IDAOAdmin dao) {
		super(dao);
	}

	@Override
	public String login(Admins admin, HttpSession session) {
		return this.dao.login(admin, session);
	}

	@Override
	public String addOperator(Operators operator, HttpSession session) {
		return this.dao.addOperator(operator, session);
	}

	@Override
	public List<Operators> getOperators(HttpSession session) {
		return this.dao.getOperators(session);
	}

	@Override
	public String addCategory(String categoryName, HttpSession session) {
		return this.dao.addCategory(categoryName, session);
	}

	@Override
	public boolean removeOperator(int operator_id, HttpSession session) {
		return this.dao.removeOperator(operator_id, session);
	}

}
