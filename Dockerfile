FROM openjdk:jdk-slim as builder

RUN mkdir -p /build/
WORKDIR /build/

COPY . /build/

# Installs maven
RUN apt-get update &&  \
    apt-get install -y maven git

# Roulette
RUN mvn package && \
    cd target/ && \
    mkdir -p bundle && \
    cp ../api_sample.json bundle/ && \
    cp ../db_sample.json bundle/ && \
    cp $(ls | grep dependencies.jar) bundle/ && \
    cd bundle && \
    mv *.jar roulette.jar


FROM openjdk:8-jre-alpine

LABEL maintainer="poloniodavide@gmail.com"
LABEL license="GPLv3+"
LABEL description="Roulette Docker image"

# Available environment variables:
# -ROULETTE_PORT: custom port in which harbor will run (the default is 80)
# -ROULETTE_API_CONFIG: path to your API configuration json
# -ROULETTE_DATABASE_IP: database IP address
# -ROULETTE_DATABASE_PORT: database port
# -ROULETTE_DATABASE_NAME: main Roulette database name
# -ROULETTE_DATABASE_USERNAME: username which Roulette will use to log in the db
# -ROULETTE_DATABASE_PASSWORD: password which Roulette will use to log in the db
# -ROULETTE_DATABASE_JSON_CONFIG: convenience file to log database settings at once
ENV ROULETTE_API_CONFIG=api_sample.json

RUN mkdir -p /config/
VOLUME /config/
WORKDIR /srv/

COPY --from=builder /build/target/bundle/roulette.jar /srv/
COPY --from=builder /build/target/bundle/api_sample.json /srv/
COPY --from=builder /build/target/bundle/db_sample.json /srv/

ENTRYPOINT ["java", "-jar", "roulette.jar"]

