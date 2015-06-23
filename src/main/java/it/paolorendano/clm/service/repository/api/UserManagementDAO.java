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
package it.paolorendano.clm.service.repository.api;

import it.paolorendano.clm.model.User;

/**
 * The Interface UserManagementDAO.
 */
public interface UserManagementDAO {

	/**
	 * Load user by user name.
	 *
	 * @param userName the user name
	 * @return the user
	 */
	User loadUserByUserName(String userName);

	/**
	 * Creates the user.
	 *
	 * @param user the user
	 */
	void createUser(User user);
	
	/**
	 * Change password.
	 *
	 * @param userName the user name
	 * @param password the password
	 * @param newPassword the new password
	 */
	void changePassword(String userName, String password, String newPassword);

}
