# cassandra-login-module
A JAAS login module to implement authentication with Apache Cassandra

*Perfectly fits with Apache ActiveMQ*

#Features

1. [X] Username and password authentication based on an external cassandra datasource
2. [X] Password is stored obscured with one-way hash SHA-256 function
3. [X] External configuration point
4. [X] Optional authentication with cassandra database
5. [X] Optional SSL communication between driver and database
6. [X] User groups association on datasource
7. [ ] API to manage users and groups (in progress...)
8. [X] Customized connection retry at startup
 
#Getting started

##Compile Jar
Prerequisites: JDK 1.7 and Apache Maven 3.x properly installed

1. download sources from git and explode them
2. go in project home and type: mvn clean package

##Cassandra keyspace initialization
Prerequisites: Apache Cassandra 2.1.x properly installed. Use cqlsh to input following commands.

*   create keyspace (according to your architectural decisions)

```
	CREATE KEYSPACE keyspace-name WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };
```

*   create users table 

```
	USE keyspace-name;
```
	
```
	CREATE TABLE users (
	   uname varchar,
	   fname varchar,
	   lname varchar,
	   pwd varchar
	PRIMARY KEY (uname));
```

*   create groups table

```
	CREATE TABLE groups (
		uname varchar,
		gname varchar,
	PRIMARY KEY(uname, gname));
```


*   insert example user (user: dduck password: password)

```
	INSERT INTO users (uname, fname, lname, pwd) VALUES ( 'dduck', 'Donald', 'Duck', 'XohImNooBHFR0OVvjcYpJ3NgPQ1qq73WKhHvch0VQtg=');
```

*   insert example group (user: dduck group: ducks)

```
	INSERT INTO groups (uname, gname) VALUES ( 'dduck', 'ducks');
```


###Note  
You can easily hash password with java function:

```
	String password = "password";
	MessageDigest mdigest = MessageDigest.getInstance("SHA-256");
	mdigest.update(password.getBytes("UTF-8"));
	String hashed = Base64.encodeBase64String(mdigest.digest());
```

##Example 1 - Install in ActiveMQ

*   put artifact jar (cassandra-login-module-x.y.z.jar) into $ACTIVEMQ_HOME/lib
*   put external dependencies into $ACTIVEMQ_HOME/lib:
  - cassandra-driver-core-2.1.x-shaded.jar
  - guava-14.0.1.jar
  - metrics-core-3.0.2.jar
  - hawtbuf-1.11.jar
  - slf4j-api-1.7.10.jar


*   add following block into $ACTIVEMQ_HOME/conf/login.config

```
	activemq-cassandra {
    		it.paolorendano.cml.CassandraLoginModule required;
	};
```

*   merge following block within $ACTIVEMQ_HOME/conf/activemq.xml

```
	<bean class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
		<property name="locations">
			<list>
				...
				<value>file:${activemq.conf}/cassandra.properties</value>
			</list>
		</property>
	</bean>
	
	<import resource="classpath*:/cassandra-jaas-config.xml" />
	
	<broker>
		<plugins>
			<jaasAuthenticationPlugin configuration="activemq-cassandra" />
		</plugins>
	</broker>
	
```

*   create and properly configure $ACTIVEMQ_HOME/conf/cassandra.properties

cassandra.properties layout example:

```
	cassandra.contactPoints=ec2-x-y-z-w.eu-central-1.compute.amazonaws.com, ec2-x-y-z-w.eu-central-2.compute.amazonaws.com
	cassandra.keyspace=keyspace-name
	cassandra.authentication=true
	cassandra.username=cassandra
	cassandra.password=cassandra
	cassandra.useSSL=true
	cassandra.truststorePath=${activemq.conf}/.truststore
	cassandra.truststorePassword=<truststore password here>
	cassandra.keystorePath=${activemq.conf}/.keystore
	cassandra.keystorePassword=<keystore password here>
	cassandra.connectionTimeout=60000
	cassandra.reconnectionBaseDelay=3000
	cassandra.reconnectionMaxDelay=60000
	cassandra.bootstrapReconnectionDelay=10000
	cassandra.bootstrapReconnectionRetries=6
	
```

- cassandra.contactPoints: a comma separated list of contact points
- cassandra.keyspace: the name of the keyspace to connect to
- cassandra.authentication: true if cassandra requires username/password authentication
- cassandra.username: username to authentication against cassandra
- cassandra.password: password to authentication against cassandra
- cassandra.useSSL: true if cassandra requires client-to-node ssl encryption
- cassandra.truststorePath: path to .truststore file
- cassandra.truststorePassword: the password of truststore
- cassandra.keystorePath: path to .keystore file
- cassandra.keystorePassword: the password of keystore
- cassandra.connectionTimeout: the timeout during connection to nodes
- cassandra.reconnectionBaseDelay: (Exponential Backoff) reconnection delay after the connected node goes down
- cassandra.reconnectionMaxDelay: (Exponential Backoff) reconnection delay cap after the connected node goes down
- cassandra.bootstrapReconnectionDelay: (Periodic retry) reconnection delay on jaas module startup. (-1 fails immediately if cannot find nodes)
- cassandra.bootstrapReconnectionRetries: number of reconnection to retry before throwing exception

##Note - Enabling SSL client-to-node
Please refer to official documentation at http://docs.datastax.com/en/cassandra/2.0/cassandra/security/secureSSLCertificates_t.html
