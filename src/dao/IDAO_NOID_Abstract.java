package dao;

import java.util.List;

import entities.NoIdEntity;

public interface IDAO_NOID_Abstract <T extends NoIdEntity>{
	
	boolean add(T object);
	int addReturnId(T object);
	boolean removeById(int id);
	boolean update(T object, String excludeColumn);
	List<T> getAll();
	T getById(int id);

}
