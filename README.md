# cassandra-login-module
A JAAS login module to implement authentication with Apache Cassandra

*Perfectly fit with Apache Active*

#Features

1. Username and password authentication based on an external cassandra datasource
2. Password is stored obscured with one-way hash SHA-256 function
3. External configuration point
4. (TO-DO) Optional authentication with cassandra database
5. (TO-DO) Optional SSL communication between driver and database

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

*   insert example user (dduck/password)

```
	INSERT INTO users (uname, fname, lname, pwd) VALUES ( 'dduck', 'Donald', 'Duck', 'XohImNooBHFR0OVvjcYpJ3NgPQ1qq73WKhHvch0VQtg=');
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

cassandra.properties layout:

```
	cassandra.contactPoints=ec2-x-y-z-w.eu-central-1.compute.amazonaws.com
	cassandra.keyspace=keyspace-name
	cassandra.useSSL=false
	cassandra.authentication=false
	cassandra.username=
	cassandra.password=
```

- cassandra.contactPoints: a comma separated list of contact points
- cassandra.keyspace: the name of the keyspace to connect to
- cassandra.useSSL: for future use
- cassandra.authentication: for future use
- cassandra.username: for future use
- cassandra.password: for future use

