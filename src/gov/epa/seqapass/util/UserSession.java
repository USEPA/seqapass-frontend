package gov.epa.seqapass.util;

//import java.io.Serializable;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import gov.epa.seqapass.model.ActiveUsers;
import gov.epa.seqapass.model.User;

@Named("userSession")
@SessionScoped
public class UserSession {

	@Inject
	private ActiveUsers activeUsers;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8195784747974150341L;

	private User user;

	/**
	 * Returns the current {@link User}.
	 * 
	 * @return A User object.
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Sets the {@link User}.
	 * 
	 * @param user
	 *            The User object.
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * CDI calls this method before the bean is destroyed. Since this class is
	 * session-scoped, it will get called if the user session expires, allowing
	 * us to remove the user from the active user list.
	 */
	public void release() {
		activeUsers.remove(user);
	}

}