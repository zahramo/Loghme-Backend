# # FROM maven:3.5-jdk-8 as BUILD
# COPY src /usr/loghme/src
# COPY pom.xml /usr/loghme
# RUN mvn -f /usr/loghme/pom.xml clean package

# FROM tomcat:9.0.20-jre11
# COPY --from=BUILD /usr/loghme/target/Loghme-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/
# CMD ["catalina.sh", "run"]

# FROM openjdk:8-jdk-alpine 

# COPY target/Loghme-0.0.1-SNAPSHOT.jar app.jar 

# ENTRYPOINT ["java", "-jar", "/app.jar"] 

FROM  maven:3.5-jdk-8 AS MAVEN_BUILD
 
# copy the pom and src code to the container
COPY src /usr/loghme/src
COPY pom.xml /usr/loghme
 
# package our application code
RUN mvn -e -X -f /usr/loghme/pom.xml clean package
 
# the second stage of our build will use open jdk 8 on alpine 3.9
FROM openjdk:8-jdk-alpine 
 
# copy only the artifacts we need from the first stage and discard the rest
COPY --from=MAVEN_BUILD /usr/loghme/target/Loghme-0.0.1-SNAPSHOT.jar app.jar
 
# set the startup command to execute the jar
CMD ["java", "-jar", "app.jar"]