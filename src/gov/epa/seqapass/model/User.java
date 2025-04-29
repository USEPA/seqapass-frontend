package gov.epa.seqapass.model;

public class User extends AbstractEntity {
	
	private static final long serialVersionUID = 8009394076089033693L;
	
	private String email;
	private int userid;
	private String firstName;
	private String lastName;
	private String isAdmin;
	private String isItasser;
	private String displayName;
	private String uid;
	private String isEnabled;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getIsAdmin() {
		return isAdmin;
	}
	public void setIsAdmin(String isAdmin) {
		this.isAdmin = isAdmin;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getIsEnabled() {
		return isEnabled;
	}
	public void setIsEnabled(String isEnabled) {
		this.isEnabled = isEnabled;
	}
	public String getIsItasser() {
		return isItasser;
	}
	public void setIsItasser(String isItasser) {
		this.isItasser = isItasser;
	}
	



}
