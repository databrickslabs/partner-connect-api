FROM openjdk:11

# Install MAVEN
RUN apt update && apt install maven -y

# Install Node
RUN \
  curl -fsSL https://deb.nodesource.com/setup_18.x | bash - && \
  apt-get install -y nodejs

# Install api generator
RUN npm install -g @openapitools/openapi-generator-cli

# Compile
WORKDIR /partner-connect-api
COPY . /partner-connect-api
RUN mkdir /root/.m2
RUN ls -alt .
RUN --mount=type=cache,target=/root/.m2 mvn install -DskipTests dependency:copy-dependencies