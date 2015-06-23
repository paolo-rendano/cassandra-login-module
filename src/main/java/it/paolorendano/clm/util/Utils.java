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
package it.paolorendano.clm.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.util.Base64Utils;

/**
 * The Class Utils.
 */
public class Utils {
	
	/**
	 * Hash password.
	 *
	 * @param password the password
	 * @return the string
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	public static String hashPassword(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest mdigest = MessageDigest.getInstance("SHA-256");
		mdigest.update(password.getBytes("UTF-8"));
		
		return Base64Utils.encodeToString(mdigest.digest());
	}
	
	public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		System.out.println(Utils.hashPassword("password"));
	}


}
