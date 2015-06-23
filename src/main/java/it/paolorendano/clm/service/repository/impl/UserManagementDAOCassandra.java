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
package it.paolorendano.clm.service.repository.impl;

import it.paolorendano.clm.AbstractCassandraDAO;
import it.paolorendano.clm.Constants;
import it.paolorendano.clm.model.User;
import it.paolorendano.clm.service.repository.api.UserManagementDAO;
import it.paolorendano.clm.util.Utils;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;

/**
 * The Class UserManagementDAOCassandra.
 */
@Service
public class UserManagementDAOCassandra extends AbstractCassandraDAO implements UserManagementDAO {
	
	/** The Constant LOGGER. */
	protected static final Logger LOGGER = LoggerFactory.getLogger(UserManagementDAOCassandra.class);
	
	/* (non-Javadoc)
	 * @see it.paolorendano.clm.service.repository.api.UserManagementDAO#createUser(it.paolorendano.clm.model.User)
	 */
	@Override
	public void createUser(User user) {
		if (user == null)
			return;
		if (user.getUserName()==null || "".equals(user.getUserName()) || user.getUserName().length()>Constants.USERNAME_MAX_LENGTH) {
			return;
		}
		if (user.getPassword()==null || "".equals(user.getPassword()) || user.getPassword().length()>Constants.PASSWORD_MAX_LENGTH) {
			return;
		}
		if (user.getFirstName()==null || "".equals(user.getFirstName()) || user.getFirstName().length()>Constants.FIRSTNAME_MAX_LENGTH) {
			return;
		}
		if (user.getLastName()==null || "".equals(user.getLastName()) || user.getLastName().length()>Constants.LASTNAME_MAX_LENGTH) {
			return;
		}

		String hashedPassword = Utils.hashPassword(user.getPassword());
		if (hashedPassword==null) {
			return;
		}
			
		session.execute(
				"INSERT INTO users (uname, fname, lname, pwd) values ( ?, ?, ?, ? )", 
				user.getUserName(), 
				user.getFirstName(), 
				user.getLastName(), 
				hashedPassword);
	}

	/* (non-Javadoc)
	 * @see it.paolorendano.clm.service.repository.api.UserManagementDAO#changePassword(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void changePassword(String userName, String password,
			String newPassword) {

	}

	/* (non-Javadoc)
	 * @see it.paolorendano.clm.service.repository.api.UserManagementDAO#loadUserByUserName(java.lang.String)
	 */
	@Override
	public User loadUserByUserName(String userName) {
		User user = null;

		String query = "SELECT * FROM users WHERE uname = ?";

		ResultSet result = session.execute(query, userName);
		List<Row> rows = result.all();
		if (rows.size()>0)
			user = convert(rows.get(0));
		return user;
	}
	
	/**
	 * Convert.
	 *
	 * @param row the row
	 * @return the user
	 */
	private User convert(Row row) {
		User u = new User(row.getString("uname"),row.getString("fname"),row.getString("lname"), row.getString("pwd"));
		return u;
	}
}
