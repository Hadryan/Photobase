package entities;

import java.sql.Timestamp;

import utils.UtilsMethods;

public class Creditcard extends BasicEntity{

	private static final long serialVersionUID = 1L;

	private String card_number;
	private int validation_code;
	private String expiration_date;
	private String owner_name;
	private int users_id;
	
	public static final String CARD_NUMBER = "CARD_NUMBER";
	public static final String VALIDATION_CODE = "VALIDATION_CODE";
	public static final String EXPIRATION_DATE = "EXPIRATION_DATE";
	public static final String OWNER_NAME = "OWNER_NAME";
	public static final String Users_ID = "Users_ID";
	
	public Creditcard() {
		columnsName.add(CARD_NUMBER);
		columnsName.add(VALIDATION_CODE);
		columnsName.add(EXPIRATION_DATE);
		columnsName.add(OWNER_NAME);
		columnsName.add(Users_ID);
	}
	
	@Override
	public void setValueForColumnName(String columnName, Object value) {
		if (CARD_NUMBER.equals(columnName)) {
			setCard_number(UtilsMethods.convertToString(value));
			return;
		}
		if (VALIDATION_CODE.equals(columnName)) {
			setValidation_code(UtilsMethods.convertInt(value));
			return;
		}
		if (EXPIRATION_DATE.equals(columnName)) {
			setExpiration_date(UtilsMethods.convertToString(value));
			return;
		}
		if (OWNER_NAME.equals(columnName)) {
			setOwner_name(UtilsMethods.convertToString(value));
			return;
		}
		if (Users_ID.equals(columnName)) {
			setUsers_id(UtilsMethods.convertInt(value));
			return;
		}
		super.setValueForColumnName(columnName, value);
	}
	
	@Override
	public Object getValueForColumnName(String columnName) {
		if (CARD_NUMBER.equals(columnName)) {
			return getCard_number();
		}
		if (VALIDATION_CODE.equals(columnName)) {
			return getValidation_code();
		}
		if (EXPIRATION_DATE.equals(columnName)) {
			return getExpiration_date();
		}
		if (OWNER_NAME.equals(columnName)) {
			return getOwner_name();
		}
		if (Users_ID.equals(columnName)) {
			return getUsers_id();
		}
		return super.getValueForColumnName(columnName);
	}
	
	public String getCard_number() {
		return card_number;
	}
	public void setCard_number(String cardNumber) {
		this.card_number = cardNumber;
	}
	public int getValidation_code() {
		return validation_code;
	}
	public void setValidation_code(int validationCode) {
		this.validation_code = validationCode;
	}
	public String getExpiration_date() {
		return expiration_date;
	}
	public void setExpiration_date(String expiration_date) {
		this.expiration_date = expiration_date;
	}
	public String getOwner_name() {
		return owner_name;
	}
	public void setOwner_name(String ownerName) {
		this.owner_name = ownerName;
	}
	public int getUsers_id() {
		return users_id;
	}
	public void setUsers_id(int usersId) {
		this.users_id = usersId;
	}
	
}
