/*
      Copyright 2015 Paolo Rendano

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package it.paolorendano.clm.service.application.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.paolorendano.clm.Constants;
import it.paolorendano.clm.model.Group;
import it.paolorendano.clm.model.User;
import it.paolorendano.clm.service.application.api.UserManagementService;
import it.paolorendano.clm.service.repository.api.UserManagementDAO;

/**
 * The Class UserManagementServiceImpl.
 */
@Service
public class UserManagementServiceImpl implements UserManagementService {

	/** The Constant LOGGER. */
	protected static final Logger LOGGER = LoggerFactory.getLogger(UserManagementServiceImpl.class);
	
	/** The user management dao. */
	@Autowired UserManagementDAO userManagementDAO;

	/* (non-Javadoc)
	 * @see it.paolorendano.clm.service.application.api.UserManagementService#getUserGroups(java.lang.String)
	 */
	@Override
	public List<Group> getUserGroups(String userName) {
		if (userName==null || "".equals(userName.trim())) {
			throw new IllegalArgumentException("username is not valid");
		}
		if (userName.length()>Constants.USERNAME_MAX_LENGTH) {
			throw new IllegalArgumentException("username is too long");
		}
		return userManagementDAO.getUserGroups(userName);
	}

	/* (non-Javadoc)
	 * @see it.paolorendano.clm.service.application.api.UserManagementService#createUser(it.paolorendano.clm.model.User)
	 */
	@Override
	public void createUser(User user) {
		if (user == null)
			throw new IllegalArgumentException("user is null");
		if (user.getUserName()==null || "".equals(user.getUserName()) || user.getUserName().length()>Constants.USERNAME_MAX_LENGTH) {
			throw new IllegalArgumentException("userName is null, blank or wrong length");
		}
		if (user.getPassword()==null || "".equals(user.getPassword()) || user.getPassword().length()>Constants.PASSWORD_MAX_LENGTH) {
			throw new IllegalArgumentException("password is null, blank or wrong length");
		}
		if (user.getFirstName()==null || "".equals(user.getFirstName()) || user.getFirstName().length()>Constants.FIRSTNAME_MAX_LENGTH) {
			throw new IllegalArgumentException("firstName is null, blank or wrong length");
		}
		if (user.getLastName()==null || "".equals(user.getLastName()) || user.getLastName().length()>Constants.LASTNAME_MAX_LENGTH) {
			throw new IllegalArgumentException("lastName is null, blank or wrong length");
		}

		userManagementDAO.createUser(user);
	}

	/* (non-Javadoc)
	 * @see it.paolorendano.clm.service.application.api.UserManagementService#removeUser(java.lang.String)
	 */
	@Override
	public void removeUser(String userName) {
		if (userName==null || "".equals(userName) || userName.length()>Constants.USERNAME_MAX_LENGTH) {
			throw new IllegalArgumentException("userName is null, blank or wrong length");
		}
		//TODO: remove user and all related user groups
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see it.paolorendano.clm.service.application.api.UserManagementService#insertUserInGroup(java.lang.String, java.lang.String)
	 */
	@Override
	public void insertUserInGroup(String userName, String groupName) {
		if (userName==null || "".equals(userName) || userName.length()>Constants.USERNAME_MAX_LENGTH) {
			throw new IllegalArgumentException("userName is null, blank or wrong length");
		}
		if (groupName==null || "".equals(groupName) || groupName.length()>Constants.GROUPNAME_MAX_LENGTH) {
			throw new IllegalArgumentException("groupName is null, blank or wrong length");
		}

		userManagementDAO.insertUserInGroup(userName, groupName);
	}

	/* (non-Javadoc)
	 * @see it.paolorendano.clm.service.application.api.UserManagementService#removeUserFromGroup(java.lang.String, java.lang.String)
	 */
	@Override
	public void removeUserFromGroup(String userName, String groupName) {
		if (userName==null || "".equals(userName) || userName.length()>Constants.USERNAME_MAX_LENGTH) {
			throw new IllegalArgumentException("userName is null, blank or wrong length");
		}
		if (groupName==null || "".equals(groupName) || groupName.length()>Constants.GROUPNAME_MAX_LENGTH) {
			throw new IllegalArgumentException("groupName is null, blank or wrong length");
		}

		userManagementDAO.removeUserFromGroup(userName, groupName);
	}

	/* (non-Javadoc)
	 * @see it.paolorendano.clm.service.application.api.UserManagementService#changeUserPassword(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void changeUserPassword(String userName, String password,
			String newPassword) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see it.paolorendano.clm.service.application.api.UserManagementService#getUsers()
	 */
	@Override
	public List<User> getUsers() {
		throw new UnsupportedOperationException();
	}

}
