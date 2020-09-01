package dao;

import java.util.List;

import entities.BasicEntity;

public interface IDAOAbstract <T extends BasicEntity>{
	
	boolean add(T object);
	boolean addEncrypted(T object, String encryptedColumnName);
	int addEncryptedReturnId(T object, String encryptedColumnName);
	int addReturnId(T object);
	boolean removeById(int id);
	boolean update(T object, String excludeColumn);
	List<T> getAll();
	T getById(int id);

}
