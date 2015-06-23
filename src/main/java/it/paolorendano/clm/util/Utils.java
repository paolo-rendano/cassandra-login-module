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

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class Utils.
 */
public class Utils {
	/** The Constant LOGGER. */
	protected static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);
	
	/**
	 * Hash password.
	 *
	 * @param password the password
	 * @return the string
	 */
	public static String hashPassword(String password) {
		MessageDigest mdigest;
		try {
			mdigest = MessageDigest.getInstance("SHA-256");
			mdigest.update(password.getBytes("UTF-8"));
			return Base64.encodeBase64String(mdigest.digest());
		} catch (NoSuchAlgorithmException e) {
			if (LOGGER.isErrorEnabled())
				LOGGER.error(e.getMessage(),e);
		} catch (UnsupportedEncodingException e) {
			if (LOGGER.isErrorEnabled())
				LOGGER.error(e.getMessage(),e);
		}
		return null;
	}
}
