# Dockerfile for BE01 ISO Simulator - pulls latest master code from git

FROM java:8-jdk-alpine

RUN mkdir -p /opt/EPFSimulator/src/main/resources

#COPY EPFserver.service /usr/lib/systemd/system/
COPY EPFTestServer7.jar /opt/EPFSimulator/
COPY ./src/main/resources/* /opt/EPFSimulator/src/main/resources/

WORKDIR /opt/EPFSimulator
EXPOSE 3301

#ENTRYPOINT ["/usr/bin/java", "-jar", "EPFTestServer7.jar"]
CMD ["/usr/bin/java","-Xms128m","-Xmx256m","-jar","EPFTestServer7.jar"]
