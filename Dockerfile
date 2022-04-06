FROM maven:3.8.5-openjdk-11 as builder
COPY . /src/cloudbeaver
WORKDIR /src/cloudbeaver/deploy
RUN curl -sL https://deb.nodesource.com/setup_16.x | bash - && \
    curl https://dl.yarnpkg.com/debian/pubkey.gpg | apt-key add - && \
    echo "deb https://dl.yarnpkg.com/debian/ stable main" | tee /etc/apt/sources.list.d/yarn.list && \
    apt-get update && \
    apt-get install -y nodejs yarn && \
    yarn add lerna
RUN ./build.sh

FROM eclipse-temurin:11.0.14.1_1-jre-focal
#adoptopenjdk/openjdk11:jdk-11.0.12_7-ubuntu-slim

COPY --from=builder /src/deploy/cloudbeaver /opt/cloudbeaver

EXPOSE 8978

WORKDIR /opt/cloudbeaver/
ENTRYPOINT ["./run-server.sh"]
