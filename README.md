# cassandra-login-module
A JAAS login module to implement authentication with Apache Cassandra

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

*   create table 

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

*   insert example user (dynablaster/password)

```
	INSERT INTO users VALUES('dynablaster76', 'Paolo', 'Rendano', 'XohImNooBHFR0OVvjcYpJ3NgPQ1qq73WKhHvch0VQtg=');
```

###Note  
You can easily hash password with java function:

```
	String password = "password";
	MessageDigest mdigest = MessageDigest.getInstance("SHA-256");
	mdigest.update(password.getBytes("UTF-8"));
	String hashed = Base64Utils.encodeToString(mdigest.digest());
```

##Example 1 - Install in ActiveMQ

*   put artifact jar (cassandra-login-module.jar) into $ACTIVEMQ_HOME/lib
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
```

*   create and properly configure $ACTIVEMQ_HOME/conf/cassandra.properties

cassandra.properties layout:

```
	cassandra.contactPoint=ec2-x-y-z-w.eu-central-1.compute.amazonaws.com
	cassandra.keyspace=keyspace-name
	cassandra.useSSL=false
	cassandra.authentication=false
	cassandra.username=
	cassandra.password=
```