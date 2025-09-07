FROM eclipse-temurin:17-jdk-alpine as builder

RUN apk add --no-cache maven

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="Miguel Arcos"
LABEL version="1.0"
LABEL description="Distributed Inventory Management Service"

EXPOSE 8080

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

RUN mkdir -p /data && chmod 777 /data

ENV SPRING_PROFILES_ACTIVE=docker
ENV SERVER_PORT=8080
ENV SPRING_R2DBC_URL=r2dbc:h2:mem:///inventorydb
ENV SPRING_R2DBC_USERNAME=sa
ENV SPRING_R2DBC_PASSWORD=
ENV JWT_SECRET=mySecretKeyForJWTGenerationWithAtLeast256Bits
ENV JWT_EXPIRATION=3600000

RUN mkdir -p /data

ENTRYPOINT ["java", "-jar", "/app/app.jar"]