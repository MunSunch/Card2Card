FROM khipu/openjdk17-alpine
EXPOSE 5500
ADD target/Card2CardProject-0.0.1-SNAPSHOT.jar /opt/app/myapp.jar
ADD target/classes/static/file_db /opt/app/db
ADD transfer-logs.log /opt/app/log/transfers.log
ENTRYPOINT ["java", "-jar", "/opt/app/myapp.jar"]