package dao;

import entities.Creditcard;

public class DAOCreditcard extends DAOAbstractDatabase<Creditcard> implements IDAOAbstract<Creditcard>{

	public DAOCreditcard() {
		super(Creditcard.class);
	}

}
