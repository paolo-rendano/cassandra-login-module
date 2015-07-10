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

import it.paolorendano.clm.exception.ConfigurationException;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.SSLOptions;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SocketOptions;
import com.datastax.driver.core.exceptions.NoHostAvailableException;
import com.datastax.driver.core.policies.ExponentialReconnectionPolicy;

/**
 * The Class AbstractCassandraDAO.
 */
public abstract class AbstractCassandraDAO {

	/** The Constant LOGGER. */
	private final static Logger LOGGER = LoggerFactory.getLogger(AbstractCassandraDAO.class);

	/** The session. */
	protected Session session;

	/** The cluster. */
	protected Cluster cluster;

	/** The contact point. */
	@Value("${cassandra.contactPoints}") private String contactPointList;

	/** The contact points. */
	private List<String> contactPoints;

	/** The keyspace. */
	@Value("${cassandra.keyspace}") private String keyspace;

	/** The use ssl. */
	@Value("${cassandra.useSSL}") private Boolean useSSL;

	/** The truststore path. */
	@Value("${cassandra.truststorePath}") private String truststorePath;

	/** The truststore password. */
	@Value("${cassandra.truststorePassword}") private String truststorePassword;

	/** The keystore path. */
	@Value("${cassandra.keystorePath}") private String keystorePath;

	/** The keystore password. */
	@Value("${cassandra.keystorePassword}") private String keystorePassword;

	/** The authentication. */
	@Value("${cassandra.authentication}") private Boolean authentication;

	/** The username. */
	@Value("${cassandra.username}") private String username;

	/** The password. */
	@Value("${cassandra.password}") private String password;

	/** The connection timeout. */
	@Value("${cassandra.connectionTimeout}") private Integer connectionTimeout;
	
	/** The reconnection base delay. */
	@Value("${cassandra.reconnectionBaseDelay}") private Integer reconnectionBaseDelay;
	
	/** The reconnection max delay. */
	@Value("${cassandra.reconnectionMaxDelay}") private Integer reconnectionMaxDelay;

	/** The bootstrap reconnection delay. */
	@Value("${cassandra.bootstrapReconnectionDelay}") private Integer bootstrapReconnectionDelay;

	@Value("${cassandra.bootstrapReconnectionRetries}") private Integer bootstrapReconnectionRetries;
			
	/**
	 * Inits the.
	 */
	@PostConstruct
	public void init() {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Init with Cassandra ContactPoints:");
		}
		contactPoints = Arrays.asList(contactPointList.trim().split(","));

		if (contactPoints!=null) {
			Cluster.Builder builder = Cluster.builder();

			for (String contactPoint:contactPoints) {
				String contactPointTrimmed = contactPoint.trim();
				if (!"".equals(contactPointTrimmed)) {
					if (LOGGER.isInfoEnabled()) {
						LOGGER.info(" -> [" + contactPointTrimmed + "]");
					}
					builder.addContactPoint(contactPointTrimmed);
				}
			}
			// at least one contact point is required
			if (builder.getContactPoints().size()==0) {
				throw new ConfigurationException("No valid contact point found in cassandra.properties. Please check configuration.");
			}

			if (authentication) {
				// username and password is required
				if (username!=null && !"".equals(username)
						&& password!=null && !"".equals(password)) {
					builder.withCredentials(username, password);
				} else {
					throw new ConfigurationException("While authentication flag is set, no valid authentication username and password has been provided in cassandra.properties. Please check configuration.");
				}
			}
			if (useSSL) {
				try {
					if (truststorePath==null || "".equals(truststorePath.trim()))
						throw new ConfigurationException("No valid truststore path found in cassandra.properties. Please check configuration.");
					if (keystorePath==null || "".equals(keystorePath.trim()))
						throw new ConfigurationException("No valid keystore path found in cassandra.properties. Please check configuration.");

					/* taken from http://www.datastax.com/dev/blog/accessing-secure-dse-clusters-with-cql-native-protocol */
					SSLContext context = getSSLContext(
							truststorePath, 
							truststorePassword, 
							keystorePath, 
							keystorePassword);

					String[] cipherSuites = { 
							"TLS_RSA_WITH_AES_128_CBC_SHA", 
							"TLS_RSA_WITH_AES_256_CBC_SHA" 
					};
					builder.withSSL(new SSLOptions(context, cipherSuites));

				} catch (UnrecoverableKeyException | KeyManagementException
						| NoSuchAlgorithmException | KeyStoreException
						| CertificateException | IOException e) {
					throw new ConfigurationException("Error while setting ssl context using data specified in cassandra.properties. Please check configuration.", e);
				}

			}
			
			if (connectionTimeout==null) 
				throw new ConfigurationException("No valid Connection Timeout has been provided in cassandra.properties. Please check configuration.");

			if (reconnectionBaseDelay==null) 
				throw new ConfigurationException("No valid Reconnection Base Delay has been provided in cassandra.properties. Please check configuration.");

			if (reconnectionMaxDelay==null) 
				throw new ConfigurationException("No valid Reconnection Max Delay has been provided in cassandra.properties. Please check configuration.");

			if (bootstrapReconnectionDelay==null) 
				throw new ConfigurationException("No valid Bootstrap Reconnection Delay has been provided in cassandra.properties. Please check configuration.");

			if (bootstrapReconnectionRetries==null) 
				throw new ConfigurationException("No valid Bootstrap Reconnection Retries has been provided in cassandra.properties. Please check configuration.");
			
			
			SocketOptions options = new SocketOptions();
			options.setConnectTimeoutMillis(connectionTimeout);
			builder.withSocketOptions(options);
			
			builder.withReconnectionPolicy(new ExponentialReconnectionPolicy(reconnectionBaseDelay, reconnectionMaxDelay));
			
			Integer currentRetry = 0;
			while (this.session == null) {
				try {
					currentRetry++;
					this.cluster = builder.build();
					this.session = cluster.connect(keyspace);
				} catch (NoHostAvailableException e) {
					try {
						 if (bootstrapReconnectionDelay==-1)
							throw e;
						if (currentRetry >= bootstrapReconnectionRetries) {
							if (LOGGER.isInfoEnabled()) {
								LOGGER.info("Attempt #" + currentRetry + " (Final) - No cassandra hosts can be contacted.");
							}
							throw e;
						}
						else {
							if (LOGGER.isInfoEnabled()) {
								LOGGER.info("Attempt #" + currentRetry + " - No cassandra hosts can be contacted. Retrying in " + (bootstrapReconnectionDelay/1000) + " seconds...");
							}
						}
						
						Thread.sleep(bootstrapReconnectionDelay);
					} catch (InterruptedException e1) {
						Thread.currentThread().interrupt();
					}
				}
			}
		}
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Cassandra connection successfully initialized");
		}
	}

	/**
	 * Close.
	 */
	@PreDestroy
	public void close() {
		if (this.session!=null)
			this.session.close();
		if (this.cluster!=null)
			this.cluster.close();
	}

	/**
	 * Gets the SSL context.
	 *
	 * @param truststorePath the truststore path
	 * @param truststorePassword the truststore password
	 * @param keystorePath the keystore path
	 * @param keystorePassword the keystore password
	 * @return the SSL context
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 * @throws KeyStoreException the key store exception
	 * @throws CertificateException the certificate exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws UnrecoverableKeyException the unrecoverable key exception
	 * @throws KeyManagementException the key management exception
	 */
	private static SSLContext getSSLContext(String truststorePath, 
			String truststorePassword,
			String keystorePath, 
			String keystorePassword) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException, UnrecoverableKeyException, KeyManagementException
	{
		/* taken from http://www.datastax.com/dev/blog/accessing-secure-dse-clusters-with-cql-native-protocol */

		FileInputStream tsf = new FileInputStream(truststorePath);
		FileInputStream ksf = new FileInputStream(keystorePath);
		SSLContext ctx = SSLContext.getInstance("SSL");

		KeyStore ts = KeyStore.getInstance("JKS");
		ts.load(tsf, truststorePassword.toCharArray());
		TrustManagerFactory tmf = 
				TrustManagerFactory.getInstance(
						TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(ts);

		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(ksf, keystorePassword.toCharArray());
		KeyManagerFactory kmf = 
				KeyManagerFactory.getInstance(
						KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(ks, keystorePassword.toCharArray());

		ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
		return ctx;
	}
}
