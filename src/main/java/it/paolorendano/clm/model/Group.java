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
package it.paolorendano.clm.model;

/**
 * The Class User.
 */
public class Group {
    
    /** The user name. */
    private String userName;
	
	/** The group name. */
	private String groupName;
    
    /**
     * Instantiates a new group.
     */
    protected Group() {}

    /**
     * Instantiates a new group.
     *
     * @param userName the user name
     * @param groupName the group name
     */
    public Group(String userName, String groupName) {
    	this.userName = userName;
    	this.groupName = groupName;
    }

	/**
	 * Gets the user name.
	 *
	 * @return the user name
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Sets the user name.
	 *
	 * @param userName the new user name
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Gets the group name.
	 *
	 * @return the group name
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * Sets the group name.
	 *
	 * @param groupName the new group name
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

}
