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

## run manual
```
java -javaagent:otel/opentelemetry-javaagent.jar \
-Dotel.service.name=id.sekawan.point \
-Dotel.exporter.otlp.endpoint={isi_url_endpoint_collector_misal_jaeger_atau_signoz} \
-Dotel.traces.exporter=jaeger
-Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=4012,suspend=n \
-Dlogback.configurationFile=conf-local/mylog.xml \
-jar build/libs/sekawan-point-1.0-SNAPSHOT-fat.jar conf-local/config.json
```