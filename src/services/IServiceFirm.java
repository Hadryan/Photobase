package services;

import java.util.List;

import entities.Firm;

public interface IServiceFirm extends IServiceAbstract<Firm>{

	public boolean register(Firm firm);
	public List<Firm> getApprovedFirms();
	
}
