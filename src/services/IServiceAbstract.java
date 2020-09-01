package services;

import java.util.List;

import entities.BasicEntity;

public interface IServiceAbstract <T extends BasicEntity>{

	boolean add(T object);
	boolean addEncrypted(T object, String encryptedColumnName);
	boolean removeById(int id);
	boolean update(T object, String excludeColumn);
	List<T> getAll();
	T getById(int id);
	
}
