package gov.epa.seqapass.service;

import java.util.List;

import gov.epa.seqapass.model.User;

public interface UserService {

	/**
	 * Finds a {@link User} with the given username and password. Used for
	 * authentication.
	 * 
	 * @param username
	 *            The username.
	 * @param password
	 *            The password.
	 * @return A User object if found.
	 */
	User findUser(String username, String password);

	/**
	 * Finds a {@link User} by ID.
	 * 
	 * @param id
	 *            The user's ID.
	 * @return A User object.
	 */
	User findUserById(Integer id);

	/**
	 * Finds a {@link User} by username.
	 * 
	 * @param username
	 *            The username.
	 * @return A User object.
	 */
	User findUserByUsername(String username);

	/**
	 * Finds a List of all {@link User} objects.
	 * 
	 * @return A List of User objects.
	 */
	List<User> findUsers();

	/**
	 * Finds a List of {@link User} objects that have a first name or last name
	 * that partially matches the suggestion string.
	 * 
	 * @param suggest
	 *            The partial string.
	 * @return A List of User objects.
	 */
	List<User> findUsersByName(String suggest);

	/**
	 * Saves a {@link User} object.
	 * 
	 * @param user
	 *            The user to save.
	 * @return The saved object.
	 */
	User saveUser(User user);

}
