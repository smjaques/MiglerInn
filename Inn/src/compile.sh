#!/bin/bash

#export CLASSPATH=$CLASSPATH:mysql-connector-java-8.0.16.jar:.
export HP_JDBC_URL=jdbc:mysql://db.labthreesixfive.com/rcohngru?autoReconnect=true\&useSSL=false
export HP_JDBC_USER=rcohngru
export HP_JDBC_PW=CSC365-F2019_010750905
javac ui/InnReservations.java
