FROM dockette/jdk8:latest
VOLUME /tmp
WORKDIR /tmp

RUN mkdir ~/.ssh
RUN touch ~/.ssh/known_hosts

COPY target/auto-renew-blog-cert.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
