# cassandra-login-module
A JAAS login module to implement authentication with cassandra database


Getting started:

1 - download sources
2 - go in project home and type: mvn clean package
3 - get the jar and put into your 


Install in ActiveMQ:
A - put jar in lib
B - add configuration taken from login.config to conf/login.config 
C - put and properly configure conf/cassandra.properties

cassandra.properties layout:
cassandra.contactPoint=ec2-x-y-z-w.eu-central-1.compute.amazonaws.com
cassandra.keyspace=keyspace-name
cassandra.useSSL=false
cassandra.authentication=false
cassandra.username=
cassandra.password=
