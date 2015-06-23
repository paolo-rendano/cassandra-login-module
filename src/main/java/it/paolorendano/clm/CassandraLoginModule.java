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
package it.paolorendano.clm;

import it.paolorendano.clm.service.application.api.AuthenticationService;

import java.io.IOException;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.apache.activemq.jaas.GroupPrincipal;
import org.apache.activemq.jaas.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * The Class CassandraLoginModule.
 */
public class CassandraLoginModule implements LoginModule {

	/** The Constant LOGGER. */
	private final static Logger LOGGER = LoggerFactory.getLogger(CassandraLoginModule.class);

		/** The subject. */
		private Subject subject;
		
		/** The callback handler. */
		private CallbackHandler callbackHandler;
		
		/** The shared state. */
		private Map<String, ?> sharedState;
		
		/** The options. */
		private Map<String, ?> options;

		/** The login succeeded. */
		private Boolean loginSucceeded;
		
		/** The user. */
		private String user;
		
		/* (non-Javadoc)
		 * @see javax.security.auth.spi.LoginModule#initialize(javax.security.auth.Subject, javax.security.auth.callback.CallbackHandler, java.util.Map, java.util.Map)
		 */
		@Override
		public void initialize(Subject subject, CallbackHandler callbackHandler,
				Map<String, ?> sharedState, Map<String, ?> options) {

			this.subject = subject;
			this.callbackHandler = callbackHandler;
			this.sharedState = sharedState;
			this.options = options;

			this.loginSucceeded = false;
			this.user = null;
		}

		/* (non-Javadoc)
		 * @see javax.security.auth.spi.LoginModule#login()
		 */
		@Override
		public boolean login() throws LoginException {

			if (callbackHandler == null) {
				if (LOGGER.isErrorEnabled())
					LOGGER.error("No callback available");
				throw new LoginException("No callback available");
			}

			ApplicationContext ctx = SpringContextHolder.getApplicationContext();
			if (ctx!=null) {
				AuthenticationService authenticationService = ctx.getBean(AuthenticationService.class);
				
				if (authenticationService==null) {
					if (LOGGER.isErrorEnabled())
						LOGGER.error("No auth service available");
					throw new LoginException("No auth service available");
				}

				Callback[] callbacks = new Callback[2];
				callbacks[0] = new NameCallback("user name: ");
				callbacks[1] = new PasswordCallback("password: ", false);
				try {
					callbackHandler.handle(callbacks);
					String userName = ((NameCallback)callbacks[0]).getName();
					char[] tmpPassword = ((PasswordCallback)callbacks[1]).getPassword();
					String password = new String(tmpPassword);
					Boolean result = authenticationService.authenticate(userName, password);
					if (result) {
						this.user = userName;
						this.loginSucceeded = true;
					} else {
						if (LOGGER.isInfoEnabled())
							LOGGER.info("[" + user + "] access denied");
					}
				} catch (IOException | UnsupportedCallbackException e) {
					if (LOGGER.isErrorEnabled())
						LOGGER.error(e.getMessage(),e);
				}
			} else {
				if (LOGGER.isErrorEnabled())
					LOGGER.error("No spring context available");
				throw new LoginException("No spring context available");
			}
			return this.loginSucceeded;
		}

		/* (non-Javadoc)
		 * @see javax.security.auth.spi.LoginModule#commit()
		 */
		@Override
		public boolean commit() throws LoginException {
			boolean result = loginSucceeded;
			if (result) {
				subject.getPrincipals().add(new UserPrincipal(user));
				subject.getPrincipals().add(new GroupPrincipal("USR_" + user));
				if (LOGGER.isInfoEnabled())
					LOGGER.info("[" + user + "] has logged in successfully");
			}
			clear();
			return result;
		}

		/* (non-Javadoc)
		 * @see javax.security.auth.spi.LoginModule#abort()
		 */
		@Override
		public boolean abort() throws LoginException {
			clear();
			return true;
		}

		/* (non-Javadoc)
		 * @see javax.security.auth.spi.LoginModule#logout()
		 */
		@Override
		public boolean logout() throws LoginException {
			clear();
			subject.getPrincipals().clear();
			return true;
		}

		/**
		 * Clear.
		 */
		private void clear() {
			this.loginSucceeded = false;
			this.user = null;
		}
		
}
