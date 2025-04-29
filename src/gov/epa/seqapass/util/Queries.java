package gov.epa.seqapass.util;

import gov.epa.seqapass.model.User;

public interface Queries {


	/**
	 * Finds a {@link User} by partial name.
	 */
	public static final String USER_FIND_BY_PARTIAL_NAME = "User.findByPartialName";

	/**
	 * Finds a {@link User} by username and password.
	 */
	public static final String USER_FIND_BY_USERNAME_PASSWORD = "User.findByUsernamePassword";

}
