# sekawan-point

## How to run in local

### Prerequisite
#### 1. Intellij Idea 2023
There is known issue when using Intellij Idea 2019. https://stackoverflow.com/questions/70499402/unable-to-find-method-org-gradle-api-artifacts-result-componentselectionreason

#### 2. Java 17
You can use https://sdkman.io/ to easily switch between java version

### Running
#### Add this configuration

VM option :
```
-Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=4011,suspend=n -Djava.locale.providers=COMPAT --add-opens=java.base/java.time=ALL-UNNAMED
```
Main class :
```
id.sekawan.point.MainKt
```
Program arguments :
```
run id.sekawan.point.MainVerticle -conf /Users/john.doe/Documents/sekawan/conf/config.json
```

## gradle build
```
./gradlew clean build
```

## run manual with exporter external ( jaeger / signoz )
```
java -javaagent:otel/opentelemetry-javaagent.jar \
-Dotel.service.name=sekawan-point-app \
-Dotel.exporter.otlp.endpoint=http://192.168.100.100:4317 \
-Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=4012,suspend=n \
-Dlogback.configurationFile=conf-local/mylog.xml \
-jar build/libs/sekawan-point-1.0-SNAPSHOT-fat.jar conf-local/config.json
```

## run manual with exporter logging
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
