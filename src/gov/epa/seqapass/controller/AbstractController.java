package gov.epa.seqapass.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.epa.seqapass.model.User;
import gov.epa.seqapass.util.UserSession;

public class AbstractController implements Serializable {

	private static final long serialVersionUID = -4862069600034765532L;

	protected transient Logger logger = LoggerFactory.getLogger(getClass());

	@ManagedProperty(value = "#{userSession}")
	protected UserSession userSession;

	/**
	 * Adds a global error message to the response.
	 * 
	 * @param summary
	 *            The message summary.
	 */
	protected void addErrorMessage(String summary) {
		FacesMessage msg = new FacesMessage(summary);
		msg.setSeverity(FacesMessage.SEVERITY_ERROR);
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	/**
	 * Convenience method to return the logged in user.
	 * 
	 * @return A {@link User} object.
	 */
	protected User getLoggedInUser() {
		User user = null;
		if (userSession != null) {
			user = userSession.getUser();
		}
		return user;
	}

	/**
	 * Handle deserialization from passivated session and restore transient
	 * fields.
	 * 
	 * @param ois
	 *            The ObjectInputStream object.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		ois.defaultReadObject();
		logger = LoggerFactory.getLogger(getClass());
	}

	public void setUserSession(UserSession userSession) {
		this.userSession = userSession;
	}

}
