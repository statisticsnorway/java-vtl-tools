FROM openjdk:8-jre-alpine

ADD target/java-vtl-tools-0.1.8.jar java-vtl-tools.jar
RUN sh -c 'touch /java-vtl-tools.jar'
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx300m", "-Djava.security.egd=file:/dev/./urandom","-jar","/java-vtl-tools.jar"]
