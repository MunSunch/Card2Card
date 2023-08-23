FROM khipu/openjdk17-alpine
EXPOSE 5500
ADD target/Card2CardProject-0.0.1-SNAPSHOT.jar /opt/app/myapp.jar
ENTRYPOINT ["java", "-jar", "/opt/app/myapp.jar"]