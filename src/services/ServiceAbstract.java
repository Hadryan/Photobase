package services;

import java.util.List;

import dao.IDAOAbstract;
import entities.BasicEntity;

public class ServiceAbstract <T extends BasicEntity, DAO extends IDAOAbstract<T>> implements IServiceAbstract<T>{

	protected DAO dao;
	
	public ServiceAbstract(DAO dao) {
		this.dao = dao;
	}
	
	@Override
	public boolean add(T object) {
		return this.dao.add(object);
	}

	@Override
	public boolean removeById(int id) {
		return this.dao.removeById(id);
	}

	@Override
	public boolean update(T object, String excludeColumn) {
		return this.dao.update(object, excludeColumn);
	}

	@Override
	public List<T> getAll() {
		return this.dao.getAll();
	}

	@Override
	public T getById(int id) {
		return this.dao.getById(id);
	}

	@Override
	public boolean addEncrypted(T object, String encryptedColumnName) {
		return this.dao.addEncrypted(object, encryptedColumnName);
	}

}
