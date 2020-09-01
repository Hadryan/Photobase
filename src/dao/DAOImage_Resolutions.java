package dao;

import entities.Image_Resolutions;

public class DAOImage_Resolutions extends DAOAbstractDatabase<Image_Resolutions> implements IDAOImage_Resolutions{

	public DAOImage_Resolutions() {
		super(Image_Resolutions.class);
	}

}
