# sekawan-point

## How to run in local

### Prerequisite
1. Intellij Idea use 2023++

2. Java 17
You can use https://sdkman.io/ to easily switch between java version

### Running on IDE
1. Add this configuration

   1. VM option ( for debug ):
        ```
        -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=4011,suspend=n -Djava.locale.providers=COMPAT --add-opens=java.base/java.time=ALL-UNNAMED
        ```
   2. Main class :
        ```
        id.sekawan.point.MainKt
        ```
   3. Program arguments :
        ```
        run id.sekawan.point.MainVerticle -conf /Users/john.doe/Documents/sekawan/conf/config.json
        ```
    4. Name : 
       ```MainKt```
2. Run configuration 
   ```MainKt```

### Running on terminal
1. gradle build
    ```
    ./gradlew clean build
    ```
2. exec on terminal
   1. exec with exporter external ( jaeger / signoz )
      ```
      java -javaagent:otel/opentelemetry-javaagent.jar \
      -Dotel.service.name=sekawan-point-app \
      -Dotel.exporter.otlp.endpoint=http://192.168.100.100:4317 \
      -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=4012,suspend=n \
      -Dlogback.configurationFile=conf-local/mylog.xml \
      -jar build/libs/sekawan-point-1.0-SNAPSHOT-fat.jar conf-local/config.json
      ```
   2. exec with exporter logging
      ```
      java -javaagent:otel/opentelemetry-javaagent.jar \
      -Dotel.service.name=my-app \
      -Dotel.traces.exporter=logging \
      -Dotel.metrics.exporter=none \
      -Dotel.logs.exporter=none \   
      -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=4012,suspend=n \
      -Dlogback.configurationFile=conf-local/mylog.xml \
      -jar build/libs/sekawan-point-1.0-SNAPSHOT-fat.jar conf-local/config.json
      ```

