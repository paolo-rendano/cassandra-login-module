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
import it.paolorendano.clm.model.Group;
import it.paolorendano.clm.model.User;
import it.paolorendano.clm.service.repository.api.UserManagementDAO;
import it.paolorendano.clm.util.Utils;

import java.util.ArrayList;
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
		String hashedPassword = Utils.hashPassword(user.getPassword());
		if (hashedPassword==null) {
			throw new IllegalStateException("hash password cannot be calculated");
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
			user = convertUser(rows.get(0));
		return user;
	}
	
	/**
	 * Convert the User.
	 *
	 * @param row the row
	 * @return the user
	 */
	private User convertUser(Row row) {
		User u = new User(row.getString("uname"),row.getString("fname"),row.getString("lname"), row.getString("pwd"));
		return u;
	}

	/**
	 * Convert group.
	 *
	 * @param row the row
	 * @return the group
	 */
	private Group convertGroup(Row row) {
		Group g = new Group(row.getString("uname"),row.getString("gname"));
		return g;
	}

	/* (non-Javadoc)
	 * @see it.paolorendano.clm.service.repository.api.UserManagementDAO#getUserGroups(java.lang.String)
	 */
	@Override
	public List<Group> getUserGroups(String userName) {
		List<Group> groups = new ArrayList<Group>();
		
		String query = "SELECT * from groups where uname = ?";
		ResultSet result = session.execute(query, userName);
		List<Row> rows = result.all();
		for (Row row:rows) {
			Group g = convertGroup(row);
			groups.add(g);
		}
		return groups;
	}

	/* (non-Javadoc)
	 * @see it.paolorendano.clm.service.repository.api.UserManagementDAO#insertUserInGroup(java.lang.String, java.lang.String)
	 */
	@Override
	public void insertUserInGroup(String userName, String groupName) {
		session.execute(
				"INSERT INTO groups (uname, fname) values (?, ?)", 
				userName, 
				groupName);
	}
	
	/* (non-Javadoc)
	 * @see it.paolorendano.clm.service.repository.api.UserManagementDAO#removeUserFromGroup(java.lang.String, java.lang.String)
	 */
	@Override
	public void removeUserFromGroup(String userName, String groupName) {
		session.execute(
				"DELETE FROM groups WHERE uname=? and gname=?", 
				userName, 
				groupName);
	}

}
