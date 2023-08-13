FROM khipu/openjdk17-alpine
EXPOSE 5500
ADD target/Card2CardProject-0.0.1-SNAPSHOT.jar /opt/app/myapp.jar
ADD src/main/resources/static/file_db /opt/app/db
ENV CARDS_FILE_LOCATION=/opt/app/db/cards.json
ENV CONFIRMS_FILE_LOCATION=/opt/app/db/confirms.json
ENV TRANSFERS_FILE_LOCATION=/opt/app/db/transfers.json
ENTRYPOINT ["java", "-jar", "/opt/app/myapp.jar"]