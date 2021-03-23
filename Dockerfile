FROM bellsoft/liberica-openjdk-alpine
EXPOSE 8080
ADD build/libs/transfer_test-0.0.1-SNAPSHOT.jar transaction.jar
ENTRYPOINT ["java","-jar","/transaction.jar"]