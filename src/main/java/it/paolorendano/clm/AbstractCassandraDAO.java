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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

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
    @Value("${cassandra.contactPoint}") private String contactPoint;
    
    /** The keyspace. */
    @Value("${cassandra.keyspace}") private String keyspace;
    
    /** The use ssl. */
    @Value("${cassandra.useSSL}") private Boolean useSSL;
    
    /** The authentication. */
    @Value("${cassandra.authentication}") private Boolean authentication;
    
    /** The username. */
    @Value("${cassandra.username}") private String username;
    
    /** The password. */
    @Value("${cassandra.password}") private String password;
    
	/**
	 * Inits the.
	 */
	@PostConstruct
	public void init() {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Init with Cassandra ContactPoint:" + contactPoint);
		}
		this.cluster = Cluster.builder()
				.addContactPoint(contactPoint)
                .build();
		this.session = cluster.connect(keyspace);
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
}