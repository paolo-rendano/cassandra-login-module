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
package it.paolorendano.clm.service.application.api;

import it.paolorendano.clm.model.Group;
import it.paolorendano.clm.model.User;

import java.util.List;

/**
 * The Interface UserManagementService.
 */
public interface UserManagementService {
	
	/**
	 * Creates the user.
	 *
	 * @param user the user
	 */
	void createUser(User user);
	
	/**
	 * Change user password.
	 *
	 * @param userName the user name
	 * @param password the password
	 * @param newPassword the new password
	 */
	void changeUserPassword(String userName, String password,
			String newPassword);

	/**
	 * Removes the user.
	 *
	 * @param userName the user name
	 */
	void removeUser(String userName);

	/**
	 * Gets the users.
	 *
	 * @return the users
	 */
	List<User> getUsers();
	
	/**
	 * Gets the user groups.
	 *
	 * @param userName the user name
	 * @return the user groups
	 */
	List<Group> getUserGroups(String userName);

	/**
	 * Insert user in group.
	 *
	 * @param userName the user name
	 * @param groupName the group name
	 */
	void insertUserInGroup(String userName, String groupName);

	/**
	 * Removes the user from group.
	 *
	 * @param userName the user name
	 * @param groupName the group name
	 */
	void removeUserFromGroup(String userName, String groupName);

}
