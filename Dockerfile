FROM sbtscala/scala-sbt:graalvm-community-22.0.1_1.10.7_3.6.2

WORKDIR /app
COPY . .
COPY build.sbt .
EXPOSE 8080
CMD sbt test