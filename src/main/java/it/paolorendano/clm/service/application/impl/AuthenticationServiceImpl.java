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

import it.paolorendano.clm.Constants;
import it.paolorendano.clm.model.User;
import it.paolorendano.clm.service.application.api.AuthenticationService;
import it.paolorendano.clm.service.repository.api.UserManagementDAO;
import it.paolorendano.clm.util.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The Class AuthenticationServiceImpl.
 */
@Service
public class AuthenticationServiceImpl implements AuthenticationService{
	
	/** The Constant LOGGER. */
	protected static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
	
	/** The user management dao. */
	@Autowired UserManagementDAO userManagementDAO;
	
	/* (non-Javadoc)
	 * @see it.paolorendano.clm.service.application.api.AuthenticationService#authenticate(java.lang.String, java.lang.String)
	 */
	@Override
	public Boolean authenticate(String userName, String password) {
		if (userName==null || "".equals(userName.trim())) {
			throw new IllegalArgumentException("username is not valid");
		}
		if (userName.length()>Constants.USERNAME_MAX_LENGTH) {
			throw new IllegalArgumentException("username is too long");
		}
		if (password==null || "".equals(password)) {
			throw new IllegalArgumentException("username is not valid");
		}
		if (password.length()>Constants.PASSWORD_MAX_LENGTH) {
			throw new IllegalArgumentException("password is too long");
		}
		User user = userManagementDAO.loadUserByUserName(userName);
		if (user==null) {
			return false;
		}
		String hashed = Utils.hashPassword(password);
		if (hashed==null) {
			throw new IllegalStateException("hash password cannot be calculated");
		}
			
		if (user.getPassword()!=null && hashed.equals(user.getPassword())) {
			return true;
		}
			
		return false;
	}
}
