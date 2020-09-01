package dao;

import java.util.List;

import entities.Firm;

public interface IDAOFirm extends IDAOAbstract<Firm>{
	
	public boolean register(Firm firm);
	public List<Firm> getApprovedFirms();

}
