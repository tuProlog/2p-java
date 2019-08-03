FROM gciatto/my-images:jdk12-alpine-gradle551
COPY . /root/2p
WORKDIR /root/2p
RUN ./gradlew clean assemble
CMD ./gradlew repl -q