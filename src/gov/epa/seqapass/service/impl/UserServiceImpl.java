package gov.epa.seqapass.service.impl;

import java.util.List;

import gov.epa.seqapass.model.User;
import gov.epa.seqapass.service.UserService;
//import gov.epa.seqapass.util.Queries;

public class UserServiceImpl extends AbstractService implements UserService {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User findUser(String username, String password) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User findUserById(Integer id) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User findUserByUsername(String username) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<User> findUsers() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<User> findUsersByName(String suggest) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User saveUser(User user) {
		return null;
	}
}