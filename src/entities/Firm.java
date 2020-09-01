package entities;

import utils.UtilsMethods;

public class Firm extends BasicEntity{

	private static final long serialVersionUID = 1L;

	private String name;
	private String location;
	private int pib;
	private double provision;
	private boolean status;
	private String email;
	private int partnerCategoriesId;
	
	public static final String NAME = "NAME";
	public static final String LOCATION = "LOCATION";
	public static final String PIB = "PIB";
	public static final String PROVISION = "PROVISION";
	public static final String STATUS = "STATUS";
	public static final String EMAIL = "EMAIL";
	public static final String PARTNER_CATEGORIES_ID = "PARTNER_CATEGORIES_ID";
	
	public Firm() {
		super();
		this.columnsName.add(NAME);
		this.columnsName.add(LOCATION);
		this.columnsName.add(PIB);
		this.columnsName.add(PROVISION);
		this.columnsName.add(STATUS);
		this.columnsName.add(EMAIL);
		this.columnsName.add(PARTNER_CATEGORIES_ID);
	}
	
	@Override
	public void setValueForColumnName(String columnName, Object value) {
		if (NAME.equals(columnName)) {
			setName(UtilsMethods.convertToString(value));
			return;
		}
		if (LOCATION.equals(columnName)) {
			setLocation(UtilsMethods.convertToString(value));
			return;
		}
		if (PIB.equals(columnName)) {
			setPib(UtilsMethods.convertInt(value));
			return;
		}
		if (PROVISION.equals(columnName)) {
			setProvision(UtilsMethods.convertDouble(value));
			return;
		}
		if (STATUS.equals(columnName)) {
			setStatus(UtilsMethods.convertTinyInt(value));
			return;
		}
		if (EMAIL.equals(columnName)) {
			setEmail(UtilsMethods.convertToString(value));
			return;
		}
		if (PARTNER_CATEGORIES_ID.equals(columnName)) {
			setPartnerCategoriesId(UtilsMethods.convertInt(value));
			return;
		}
		super.setValueForColumnName(columnName, value);
	}
	
	@Override
	public Object getValueForColumnName(String columnName) {
		if (NAME.equals(columnName)) {
			return getName();
		}
		if (LOCATION.equals(columnName)) {
			return getLocation();
		}
		if (PIB.equals(columnName)) {
			return getPib();
		}
		if (PROVISION.equals(columnName)) {
			return getProvision();
		}
		if (STATUS.equals(columnName)) {
			return isStatus();
		}
		if (EMAIL.equals(columnName)) {
			return getEmail();
		}
		if (PARTNER_CATEGORIES_ID.equals(columnName)) {
			return getPartnerCategoriesId();
		}
		return super.getValueForColumnName(columnName);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public int getPib() {
		return pib;
	}
	public void setPib(int pib) {
		this.pib = pib;
	}
	public double getProvision() {
		return provision;
	}
	public void setProvision(double provision) {
		this.provision = provision;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getPartnerCategoriesId() {
		return partnerCategoriesId;
	}
	public void setPartnerCategoriesId(int partnerCategoriesId) {
		this.partnerCategoriesId = partnerCategoriesId;
	}
	
}
