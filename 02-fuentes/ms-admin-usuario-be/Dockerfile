FROM adoptopenjdk/openjdk11:alpine-jre

#ANTES DE COMPIAR DEBE SER EJECUTADO EN EL RUNNER.
ARG JAR_FILE=build/libs/*.jar
WORKDIR /opt/app

COPY ${JAR_FILE} app.jar
EXPOSE 9000
RUN apk add tzdata && cp /usr/share/zoneinfo/America/Santiago /etc/localtime \
                       && echo "America/Santiago" > /etc/timezone \
                       && apk del tzdata
ENTRYPOINT ["java","-jar","app.jar"]