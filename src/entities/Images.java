package entities;

import java.sql.Timestamp;
import java.util.Date;

import utils.UtilsMethods;

public class Images extends BasicEntity{

	private static final long serialVersionUID = 1L;

	private String name;
	private String tags;
	private Timestamp uploadDate;
	private double rating;
	private String location;
	private int sales;
	private String description;
	private boolean pending;
	private int authorsUsersId;
	private int votes;
	
	public static final String NAME = "NAME";
	public static final String TAGS = "TAGS";
	public static final String UPLOAD_DATE = "UPLOAD_DATE";
	public static final String RATING = "RATING";
	public static final String LOCATION = "LOCATION";
	public static final String SALES = "SALES";
	public static final String DESCRIPTION = "DESCRIPTION";
	public static final String Authors_Users_ID = "Authors_Users_ID";
	public static final String PENDING = "PENDING";
	public static final String VOTES = "VOTES";
	
	public Images() {
		columnsName.add(NAME);
		columnsName.add(TAGS);
		columnsName.add(UPLOAD_DATE);
		columnsName.add(RATING);
		columnsName.add(LOCATION);
		columnsName.add(SALES);
		columnsName.add(DESCRIPTION);
		columnsName.add(Authors_Users_ID);
		columnsName.add(PENDING);
		columnsName.add(VOTES);
	}
	
	@Override
	public void setValueForColumnName(String columnName, Object value) {
		if (NAME.equals(columnName)) {
			setName(UtilsMethods.convertToString(value));
			return;
		}
		if (TAGS.equals(columnName)) {
			setTags(UtilsMethods.convertToString(value));
			return;
		}
		if (UPLOAD_DATE.equals(columnName)) {
			setUploadDate((Timestamp)value);
			return;
		}
		if (RATING.equals(columnName)) {
			setRating(UtilsMethods.convertDouble(value));
			return;
		}
		if (LOCATION.equals(columnName)) {
			setLocation(UtilsMethods.convertToString(value));
			return;
		}
		if (SALES.equals(columnName)) {
			setSales(UtilsMethods.convertInt(value));
			return;
		}
		if (DESCRIPTION.equals(columnName)) {
			setDescription(UtilsMethods.convertToString(value));
			return;
		}
		if (Authors_Users_ID.equals(columnName)) {
			setAuthorsUsersId(UtilsMethods.convertInt(value));
			return;
		}
		if (PENDING.equals(columnName)) {
			setPending(UtilsMethods.convertTinyInt(value));
			return;
		}
		if (VOTES.equals(columnName)) {
			setVotes(UtilsMethods.convertInt(value));
			return;
		}
		super.setValueForColumnName(columnName, value);
	}
	
	@Override
	public Object getValueForColumnName(String columnName) {
		if (NAME.equals(columnName)) {
			return getName();
		}
		if (TAGS.equals(columnName)) {
			return getTags();
		}
		if (UPLOAD_DATE.equals(columnName)) {
			return getUploadDate();
		}
		if (RATING.equals(columnName)) {
			return getRating();
		}
		if (LOCATION.equals(columnName)) {
			return getLocation();
		}
		if (SALES.equals(columnName)) {
			return getSales();
		}
		if (DESCRIPTION.equals(columnName)) {
			return getDescription();
		}
		if (Authors_Users_ID.equals(columnName)) {
			return getAuthorsUsersId();
		}
		if (PENDING.equals(columnName)) {
			return isPending();
		}
		if (VOTES.equals(columnName)) {
			return getVotes();
		}
		return super.getValueForColumnName(columnName);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public Timestamp getUploadDate() {
		return uploadDate;
	}
	public void setUploadDate(Timestamp uploadDate) {
		this.uploadDate = uploadDate;
	}
	public double getRating() {
		return rating;
	}
	public void setRating(double rating) {
		this.rating = rating;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public int getSales() {
		return sales;
	}
	public void setSales(int sales) {
		this.sales = sales;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isPending() {
		return pending;
	}
	public void setPending(boolean pending) {
		this.pending = pending;
	}
	public int getAuthorsUsersId() {
		return authorsUsersId;
	}
	public void setAuthorsUsersId(int authorsUsersId) {
		this.authorsUsersId = authorsUsersId;
	}
	public int getVotes() {
		return votes;
	}
	public void setVotes(int votes) {
		this.votes = votes;
	}

}
