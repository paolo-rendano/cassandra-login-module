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
package it.paolorendano.clm.exception;

/**
 * The Class ConfigurationException.
 */
public class ConfigurationException extends RuntimeException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3659876225059030375L;

	/**
	 * Instantiates a new configuration exception.
	 */
	public ConfigurationException() {
	}

	/**
	 * Instantiates a new configuration exception.
	 *
	 * @param message the message
	 */
	public ConfigurationException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new configuration exception.
	 *
	 * @param cause the cause
	 */
	public ConfigurationException(Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates a new configuration exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public ConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new configuration exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 * @param enableSuppression the enable suppression
	 * @param writableStackTrace the writable stack trace
	 */
	public ConfigurationException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
