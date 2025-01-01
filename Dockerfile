FROM apluslms/grade-scala:scala3-2.13-4.2

WORKDIR /app
COPY . .
COPY build.sbt .
EXPOSE 8080
CMD sbt test