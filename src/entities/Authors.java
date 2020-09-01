package entities;

import utils.UtilsMethods;

public class Authors extends NoIdEntity{

	private static final long serialVersionUID = 1L;
	
	private int users_id;
	private double rating;
	private int operator_score;
	private int firm_id;
	private int votes;
	private int card_id;
	private boolean applied;
	
	public static final String Users_ID = "Users_ID";
	public static final String RATING = "RATING";
	public static final String OPERATOR_SCORE = "OPERATOR_SCORE";
	public static final String Firm_ID = "Firm_ID";
	public static final String VOTES = "VOTES";
	public static final String Card_ID = "Card_ID";
	public static final String APPLIED = "APPLIED";
	
	public Authors() {
		super();
		columnsName.add(Users_ID);
		columnsName.add(RATING);
		columnsName.add(OPERATOR_SCORE);
		columnsName.add(Firm_ID);
		columnsName.add(VOTES);
		columnsName.add(Card_ID);
		columnsName.add(APPLIED);
	}
	
	@Override
	public void setValueForColumnName(String columnName, Object value) {
		if (Users_ID.equals(columnName)) {
			setUsers_id(UtilsMethods.convertInt(value));
			return;
		}
		if (RATING.equals(columnName)) {
			setRating(UtilsMethods.convertDouble(value));
			return;
		}
		if (OPERATOR_SCORE.equals(columnName)) {
			setOperator_score(UtilsMethods.convertInt(value));
			return;
		}
		if (Firm_ID.equals(columnName)) {
			setFirm_id(UtilsMethods.convertInt(value));
			return;
		}
		if (VOTES.equals(columnName)) {
			setVotes(UtilsMethods.convertInt(value));
			return;
		}
		if (Card_ID.equals(columnName)) {
			setCard_id(UtilsMethods.convertInt(value));
			return;
		}
		if (APPLIED.equals(columnName)) {
			setApplied(UtilsMethods.convertTinyInt(value));
			return;
		}
		super.setValueForColumnName(columnName, value);
	}
	
	@Override
	public Object getValueForColumnName(String columnName) {
		if (Users_ID.equals(columnName)) {
			return getUsers_id();
		}
		if (RATING.equals(columnName)) {
			return getRating();
		}
		if (OPERATOR_SCORE.equals(columnName)) {
			return getOperator_score();
		}
		if (Firm_ID.equals(columnName)) {
			return getFirm_id();
		}
		if (VOTES.equals(columnName)) {
			return getVotes();
		}
		if (Card_ID.equals(columnName)) {
			return getCard_id();
		}
		if (APPLIED.equals(columnName)) {
			return isApplied();
		}
		return super.getValueForColumnName(columnName);
	}

	public int getUsers_id() {
		return users_id;
	}

	public void setUsers_id(int usersId) {
		this.users_id = usersId;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public int getOperator_score() {
		return operator_score;
	}
	
	public void setOperator_score(int operator_score) {
		this.operator_score = operator_score;
	}

	public int getFirm_id() {
		return firm_id;
	}
	
	public void setFirm_id(int firm_id) {
		this.firm_id = firm_id;
	}
	
	public int getVotes() {
		return votes;
	}
	
	public void setVotes(int votes) {
		this.votes = votes;
	}
	
	public int getCard_id() {
		return card_id;
	}
	
	public void setCard_id(int card_id) {
		this.card_id = card_id;
	}
	
	public void setApplied(boolean applied) {
		this.applied = applied;
	}
	
	public boolean isApplied() {
		return applied;
	}
}
