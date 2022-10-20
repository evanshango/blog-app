FROM openjdk:11-ea-11-jdk-slim as build
#FROM openjdk:8-jdk-alpine as build
WORKDIR /app

COPY mvnw /app
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN sed -i 's/\r$//' mvnw
RUN chmod +x mvnw && ./mvnw install -DskipTests
RUN ./mvnw install -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM openjdk:11-jre-slim as production
VOLUME /tmp
ARG DEPENDENCY=/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENV JAVA_OPTS=''
ENTRYPOINT ["java","-cp","app:app/lib/*","com.codewithevans.blog.BlogApplication"]