FROM scsb-base as builder
WORKDIR application
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} scsb-etl.jar
RUN java -Djarmode=layertools -jar scsb-etl.jar extract

FROM scsb-base

WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/scsb-etl.jar/ ./
ENTRYPOINT java -jar -Denvironment=$ENV scsb-etl.jar && bash
