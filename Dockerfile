FROM bellsoft/liberica-openjdk-alpine
EXPOSE 8080
ADD build/libs/jclo_transfer-0.0.1-SNAPSHOT.jar transafer.jar
ENTRYPOINT ["java","-jar","/transafer.jar"]