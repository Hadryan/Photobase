package services;

import java.util.List;

import dao.IDAOFirm;
import entities.Firm;

public class ServiceFirm extends ServiceAbstract<Firm, IDAOFirm> implements IServiceFirm{

	public ServiceFirm(IDAOFirm dao) {
		super(dao);
	}

	@Override
	public boolean register(Firm firm) {
		return this.dao.register(firm);
	}

	@Override
	public List<Firm> getApprovedFirms() {
		return this.dao.getApprovedFirms();
	}

}
