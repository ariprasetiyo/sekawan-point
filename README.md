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
### Stress test with ab ( apache branch )
1. ab script  ab -n 1000 -c 250 http://localhost:8080/test/vertx/virtualThread/eventBus
   ```
   chrisferdian@Chriss-MacBook-Air ~ %  ab -n 1000 -c 250 http://localhost:8080/test/vertx/virtualThread/eventBus
   This is ApacheBench, Version 2.3 <$Revision: 1879490 $>
   Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
   Licensed to The Apache Software Foundation, http://www.apache.org/
   
   Benchmarking localhost (be patient)
   Completed 100 requests
   Completed 200 requests
   Completed 300 requests
   Completed 400 requests
   Completed 500 requests
   Completed 600 requests
   Completed 700 requests
   Completed 800 requests
   Completed 900 requests
   Completed 1000 requests
   Finished 1000 requests
   
   
   Server Software:        
   Server Hostname:        localhost
   Server Port:            8080
   
   Document Path:          /test/vertx/virtualThread/eventBus
   Document Length:        8 bytes
   
   Concurrency Level:      250
   Time taken for tests:   123.864 seconds
   Complete requests:      1000
   Failed requests:        76
   (Connect: 0, Receive: 0, Length: 76, Exceptions: 0)
   Total transferred:      257796 bytes
   HTML transferred:       7392 bytes
   Requests per second:    8.07 [#/sec] (mean)
   Time per request:       30965.974 [ms] (mean)
   Time per request:       123.864 [ms] (mean, across all concurrent requests)
   Transfer rate:          2.03 [Kbytes/sec] received
   
   Connection Times (ms)
   min  mean[+/-sd] median   max
   Connect:        0    2   2.5      0       9
   Processing:   889 29242 10770.7  30305   57568
   Waiting:        0 26817 13138.6  30243   57568
   Total:        889 29244 10768.8  30305   57568
   
   Percentage of the requests served within a certain time (ms)
   50%  30305
   66%  31296
   75%  33729
   80%  36667
   90%  44428
   95%  45047
   98%  45110
   99%  56568
   100%  57568 (longest request)
      ```
2. - ab -n 1000 -c 250 http://localhost:8080/test/vertx/virtualThread/executorService 
   ```
   ab -n 10 -c 10 http://localhost:8080/test/vertx/virtualThread/executorService
   
   chrisferdian@Chriss-MacBook-Air ~ % ab -n 1000 -c 250 http://localhost:8080/test/vertx/virtualThread/executorService
   This is ApacheBench, Version 2.3 <$Revision: 1879490 $>
   Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
   Licensed to The Apache Software Foundation, http://www.apache.org/
   
   Benchmarking localhost (be patient)
   Completed 100 requests
   Completed 200 requests
   Completed 300 requests
   Completed 400 requests
   Completed 500 requests
   Completed 600 requests
   Completed 700 requests
   Completed 800 requests
   Completed 900 requests
   Completed 1000 requests
   Finished 1000 requests
   
   
   Server Software:        
   Server Hostname:        localhost
   Server Port:            8080
   
   Document Path:          /test/vertx/virtualThread/executorService
   Document Length:        72 bytes
   
   Concurrency Level:      250
   Time taken for tests:   121.855 seconds
   Complete requests:      1000
   Failed requests:        24
   (Connect: 0, Receive: 0, Length: 24, Exceptions: 0)
   Total transferred:      335744 bytes
   HTML transferred:       70272 bytes
   Requests per second:    8.21 [#/sec] (mean)
   Time per request:       30463.841 [ms] (mean)
   Time per request:       121.855 [ms] (mean, across all concurrent requests)
   Transfer rate:          2.69 [Kbytes/sec] received
   
   Connection Times (ms)
   min  mean[+/-sd] median   max
   Connect:        0    1   2.3      0       9
   Processing:   757 29127 9740.9  28053   63716
   Waiting:        0 28000 10121.7  27814   61483
   Total:        757 29128 9739.6  28053   63721
   
   Percentage of the requests served within a certain time (ms)
   50%  28053
   66%  31339
   75%  33032
   80%  35880
   90%  42680
   95%  45892
   98%  46055
   99%  49686
   100%  63721 (longest request)

   ```
3.  ab -n 1000 -c 250 http://localhost:8080/test/vertx/virtualThread/executeBlocking 
   ```
   chrisferdian@Chriss-MacBook-Air ~ % ab -n 1000 -c 250 http://localhost:8080/test/vertx/virtualThread/executeBlocking 
   This is ApacheBench, Version 2.3 <$Revision: 1879490 $>
   Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
   Licensed to The Apache Software Foundation, http://www.apache.org/
   
   Benchmarking localhost (be patient)
   Completed 100 requests
   Completed 200 requests
   Completed 300 requests
   Completed 400 requests
   Completed 500 requests
   Completed 600 requests
   Completed 700 requests
   Completed 800 requests
   Completed 900 requests
   Completed 1000 requests
   Finished 1000 requests
   
   
   Server Software:        
   Server Hostname:        localhost
   Server Port:            8080
   
   Document Path:          /test/vertx/virtualThread/executeBlocking
   Document Length:        72 bytes
   
   Concurrency Level:      250
   Time taken for tests:   122.014 seconds
   Complete requests:      1000
   Failed requests:        59
      (Connect: 0, Receive: 0, Length: 59, Exceptions: 0)
   Total transferred:      323704 bytes
   HTML transferred:       67752 bytes
   Requests per second:    8.20 [#/sec] (mean)
   Time per request:       30503.548 [ms] (mean)
   Time per request:       122.014 [ms] (mean, across all concurrent requests)
   Transfer rate:          2.59 [Kbytes/sec] received
   
   Connection Times (ms)
                 min  mean[+/-sd] median   max
   Connect:        0    2   2.8      0      10
   Processing:   717 28323 12042.5  29081   58440
   Waiting:        0 26229 13506.2  26640   58440
   Total:        717 28325 12040.9  29082   58440
   
   Percentage of the requests served within a certain time (ms)
     50%  29082
     66%  33646
     75%  34333
     80%  39268
     90%  44697
     95%  46798
     98%  50480
     99%  52628
    100%  58440 (longest request)
   ```
4. ab -n 1000 -c 250 http://localhost:8080/test/vertx/rxJava3/organic
   ```
   ab -n 10 -c 10 http://localhost:8080/test/vertx/rxJava3/organic
   chrisferdian@Chriss-MacBook-Air ~ % ab -n 1000 -c 250 http://localhost:8080/test/vertx/rxJava3/organic
   This is ApacheBench, Version 2.3 <$Revision: 1879490 $>
   Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
   Licensed to The Apache Software Foundation, http://www.apache.org/
   
   Benchmarking localhost (be patient)
   Completed 100 requests
   Completed 200 requests
   Completed 300 requests
   Completed 400 requests
   Completed 500 requests
   Completed 600 requests
   Completed 700 requests
   Completed 800 requests
   Completed 900 requests
   Completed 1000 requests
   Finished 1000 requests
   
   
   Server Software:        
   Server Hostname:        localhost
   Server Port:            8080
   
   Document Path:          /test/vertx/rxJava3/organic
   Document Length:        7 bytes
   
   Concurrency Level:      250
   Time taken for tests:   120.761 seconds
   Complete requests:      1000
   Failed requests:        21
   (Connect: 0, Receive: 0, Length: 21, Exceptions: 0)
   Total transferred:      272162 bytes
   HTML transferred:       6853 bytes
   Requests per second:    8.28 [#/sec] (mean)
   Time per request:       30190.164 [ms] (mean)
   Time per request:       120.761 [ms] (mean, across all concurrent requests)
   Transfer rate:          2.20 [Kbytes/sec] received
   
   Connection Times (ms)
   min  mean[+/-sd] median   max
   Connect:        0    1   2.6      0      10
   Processing:   733 28516 5067.4  30583   34175
   Waiting:        0 27805 6463.2  30529   34175
   Total:        733 28518 5067.7  30588   34175
   
   Percentage of the requests served within a certain time (ms)
   50%  30588
   66%  31040
   75%  31446
   80%  31607
   90%  33012
   95%  33341
   98%  33693
   99%  33930
   100%  34175 (longest request)
   ```
5. thread logs
   ```
   OpenJDK 64-Bit Server VM warning: Sharing is only supported for boot loader classes because bootstrap classpath has been appended
   [otel.javaagent 2025-12-26 14:48:37:540 +0700] [main] INFO io.opentelemetry.javaagent.tooling.VersionLogger - opentelemetry-javaagent - version: 2.22.0
   Listening for transport dt_socket at address: 4012
   2025-12-26 14:48:43.713 DEBUG [main] io.netty.util.internal.logging.InternalLoggerFactory - traceId= spanId= - Using SLF4J as the default logging framework
   2025-12-26 14:48:43.720 DEBUG [main] io.netty.util.internal.PlatformDependent0 - traceId= spanId= - Java version: 21
   2025-12-26 14:48:43.720 DEBUG [main] io.netty.util.internal.PlatformDependent0 - traceId= spanId= - -Dio.netty.noUnsafe: false
   2025-12-26 14:48:43.720 DEBUG [main] io.netty.util.internal.PlatformDependent0 - traceId= spanId= - sun.misc.Unsafe.theUnsafe: available
   2025-12-26 14:48:43.721 DEBUG [main] io.netty.util.internal.PlatformDependent0 - traceId= spanId= - sun.misc.Unsafe base methods: all available
   2025-12-26 14:48:43.721 DEBUG [main] io.netty.util.internal.PlatformDependent0 - traceId= spanId= - java.nio.Buffer.address: available
   2025-12-26 14:48:43.721 DEBUG [main] io.netty.util.internal.PlatformDependent0 - traceId= spanId= - direct buffer constructor: unavailable: Reflective setAccessible(true) disabled
   2025-12-26 14:48:43.721 DEBUG [main] io.netty.util.internal.PlatformDependent0 - traceId= spanId= - java.nio.Bits.unaligned: available, true
   2025-12-26 14:48:43.722 DEBUG [main] io.netty.util.internal.PlatformDependent0 - traceId= spanId= - jdk.internal.misc.Unsafe.allocateUninitializedArray(int): unavailable: symbolic reference class is not accessible: class jdk.internal.misc.Unsafe, from class io.netty.util.internal.PlatformDependent0 (unnamed module @7a362b6b)
   2025-12-26 14:48:43.722 DEBUG [main] io.netty.util.internal.PlatformDependent0 - traceId= spanId= - java.nio.DirectByteBuffer.<init>(long, {int,long}): unavailable
   2025-12-26 14:48:43.722 DEBUG [main] io.netty.util.internal.PlatformDependent - traceId= spanId= - sun.misc.Unsafe: available
   2025-12-26 14:48:43.722 DEBUG [main] io.netty.util.internal.PlatformDependent - traceId= spanId= - -Dio.netty.tmpdir: /var/folders/46/j6yp9fd10t7gjhpqhzvyh96r0000gn/T (java.io.tmpdir)
   2025-12-26 14:48:43.722 DEBUG [main] io.netty.util.internal.PlatformDependent - traceId= spanId= - -Dio.netty.bitMode: 64 (sun.arch.data.model)
   2025-12-26 14:48:43.722 DEBUG [main] io.netty.util.internal.PlatformDependent - traceId= spanId= - Platform: MacOS
   2025-12-26 14:48:43.722 DEBUG [main] io.netty.util.internal.PlatformDependent - traceId= spanId= - -Dio.netty.maxDirectMemory: -1 bytes
   2025-12-26 14:48:43.723 DEBUG [main] io.netty.util.internal.CleanerJava9 - traceId= spanId= - java.nio.ByteBuffer.cleaner(): available
   2025-12-26 14:48:43.723 DEBUG [main] io.netty.util.internal.PlatformDependent - traceId= spanId= - -Dio.netty.noPreferDirect: false
   2025-12-26 14:48:43.725 DEBUG [main] io.netty.util.internal.PlatformDependent - traceId= spanId= - -Dio.netty.jfr.enabled: true
   2025-12-26 14:48:43.731 DEBUG [main] io.vertx.core.internal.logging.LoggerFactory - traceId= spanId= - Using io.vertx.core.logging.SLF4JLogDelegateFactory
   2025-12-26 14:48:43.753 DEBUG [main] io.netty.util.ResourceLeakDetector - traceId= spanId= - -Dio.netty.leakDetection.level: simple
   2025-12-26 14:48:43.753 DEBUG [main] io.netty.util.ResourceLeakDetector - traceId= spanId= - -Dio.netty.leakDetection.targetRecords: 4
   2025-12-26 14:48:43.829 DEBUG [main] io.netty.channel.MultithreadEventLoopGroup - traceId= spanId= - -Dio.netty.eventLoopThreads: 16
   2025-12-26 14:48:43.831 DEBUG [main] io.netty.channel.nio.NioIoHandler - traceId= spanId= - -Dio.netty.noKeySetOptimization: false
   2025-12-26 14:48:43.831 DEBUG [main] io.netty.channel.nio.NioIoHandler - traceId= spanId= - -Dio.netty.selectorAutoRebuildThreshold: 512
   2025-12-26 14:48:43.874 DEBUG [main] io.netty.util.concurrent.GlobalEventExecutor - traceId= spanId= - -Dio.netty.globalEventExecutor.quietPeriodSeconds: 1
   2025-12-26 14:48:43.887 DEBUG [main] io.netty.util.internal.InternalThreadLocalMap - traceId= spanId= - -Dio.netty.threadLocalMap.stringBuilder.initialSize: 1024
   2025-12-26 14:48:43.887 DEBUG [main] io.netty.util.internal.InternalThreadLocalMap - traceId= spanId= - -Dio.netty.threadLocalMap.stringBuilder.maxSize: 4096
   2025-12-26 14:48:43.915 DEBUG [main] io.netty.util.internal.PlatformDependent - traceId= spanId= - org.jctools-core.MpscChunkedArrayQueue: available
   2025-12-26 14:48:43.927 DEBUG [main] io.netty.resolver.dns.DefaultDnsServerAddressStreamProvider - traceId= spanId= - Default DNS servers: [/55.55.55.1:53] (sun.net.dns.ResolverConfiguration)
   2025-12-26 14:48:43.943 DEBUG [main] io.netty.util.NetUtil - traceId= spanId= - -Djava.net.preferIPv4Stack: false
   2025-12-26 14:48:43.943 DEBUG [main] io.netty.util.NetUtil - traceId= spanId= - -Djava.net.preferIPv6Addresses: false
   2025-12-26 14:48:43.949 DEBUG [main] io.netty.util.NetUtilInitializations - traceId= spanId= - Loopback interface: lo0 (lo0, 0:0:0:0:0:0:0:1%lo0)
   2025-12-26 14:48:43.949 DEBUG [main] io.netty.util.NetUtil - traceId= spanId= - Failed to get SOMAXCONN from sysctl and file /proc/sys/net/core/somaxconn. Default: 128
   2025-12-26 14:48:43.949 DEBUG [main] io.netty.resolver.dns.DnsNameResolver - traceId= spanId= - Default ResolvedAddressTypes: IPV4_ONLY
   2025-12-26 14:48:43.949 DEBUG [main] io.netty.resolver.dns.DnsNameResolver - traceId= spanId= - Localhost address: localhost/127.0.0.1
   2025-12-26 14:48:43.949 DEBUG [main] io.netty.resolver.dns.DnsNameResolver - traceId= spanId= - Windows hostname: null
   2025-12-26 14:48:43.950 DEBUG [main] io.netty.resolver.dns.DnsNameResolver - traceId= spanId= - Default search domains: []
   2025-12-26 14:48:43.950 DEBUG [main] io.netty.resolver.dns.DnsNameResolver - traceId= spanId= - Default UnixResolverOptions{ndots=1, timeout=5, attempts=16}
   2025-12-26 14:48:43.956 DEBUG [main] io.netty.resolver.DefaultHostsFileEntriesResolver - traceId= spanId= - -Dio.netty.hostsFileRefreshInterval: 0
   2025-12-26 14:48:43.957 WARN  [main] io.netty.resolver.dns.DnsServerAddressStreamProviders - traceId= spanId= - Can not find io.netty.resolver.dns.macos.MacOSDnsServerAddressStreamProvider in the classpath, fallback to system defaults. This may result in incorrect DNS resolutions on MacOS. Check whether you have a dependency on 'io.netty:netty-resolver-dns-native-macos'
   2025-12-26 14:48:44.014 DEBUG [vert.x-eventloop-thread-0] com.zaxxer.hikari.HikariConfig - traceId= spanId= - HikariPool-1 - configuration:
   2025-12-26 14:48:44.015 DEBUG [vert.x-eventloop-thread-0] com.zaxxer.hikari.HikariConfig - traceId= spanId= - allowPoolSuspension................................false
   2025-12-26 14:48:44.015 DEBUG [vert.x-eventloop-thread-0] com.zaxxer.hikari.HikariConfig - traceId= spanId= - autoCommit................................true
   2025-12-26 14:48:44.015 DEBUG [vert.x-eventloop-thread-0] com.zaxxer.hikari.HikariConfig - traceId= spanId= - catalog................................none
   2025-12-26 14:48:44.015 DEBUG [vert.x-eventloop-thread-0] com.zaxxer.hikari.HikariConfig - traceId= spanId= - connectionInitSql................................none
   2025-12-26 14:48:44.015 DEBUG [vert.x-eventloop-thread-0] com.zaxxer.hikari.HikariConfig - traceId= spanId= - connectionTestQuery................................none
   2025-12-26 14:48:44.016 DEBUG [vert.x-eventloop-thread-0] com.zaxxer.hikari.HikariConfig - traceId= spanId= - connectionTimeout................................20000
   2025-12-26 14:48:44.016 DEBUG [vert.x-eventloop-thread-0] com.zaxxer.hikari.HikariConfig - traceId= spanId= - dataSource................................none
   2025-12-26 14:48:44.016 DEBUG [vert.x-eventloop-thread-0] com.zaxxer.hikari.HikariConfig - traceId= spanId= - dataSourceClassName................................none
   2025-12-26 14:48:44.016 DEBUG [vert.x-eventloop-thread-0] com.zaxxer.hikari.HikariConfig - traceId= spanId= - dataSourceJNDI................................none
   2025-12-26 14:48:44.016 DEBUG [vert.x-eventloop-thread-0] com.zaxxer.hikari.HikariConfig - traceId= spanId= - dataSourceProperties................................{password=<masked>}
   2025-12-26 14:48:44.016 DEBUG [vert.x-eventloop-thread-0] com.zaxxer.hikari.HikariConfig - traceId= spanId= - driverClassName................................none
   2025-12-26 14:48:44.016 DEBUG [vert.x-eventloop-thread-0] com.zaxxer.hikari.HikariConfig - traceId= spanId= - exceptionOverrideClassName................................none
   2025-12-26 14:48:44.016 DEBUG [vert.x-eventloop-thread-0] com.zaxxer.hikari.HikariConfig - traceId= spanId= - healthCheckProperties................................{}
   2025-12-26 14:48:44.016 DEBUG [vert.x-eventloop-thread-0] com.zaxxer.hikari.HikariConfig - traceId= spanId= - healthCheckRegistry................................none
   2025-12-26 14:48:44.016 DEBUG [vert.x-eventloop-thread-0] com.zaxxer.hikari.HikariConfig - traceId= spanId= - idleTimeout................................600000
   2025-12-26 14:48:44.016 DEBUG [vert.x-eventloop-thread-0] com.zaxxer.hikari.HikariConfig - traceId= spanId= - initializationFailTimeout................................1
   2025-12-26 14:48:44.017 DEBUG [vert.x-eventloop-thread-0] com.zaxxer.hikari.HikariConfig - traceId= spanId= - isolateInternalQueries................................false
   2025-12-26 14:48:44.017 DEBUG [vert.x-eventloop-thread-0] com.zaxxer.hikari.HikariConfig - traceId= spanId= - jdbcUrl................................jdbc:postgresql://localhost:5432/sekawan_db
   2025-12-26 14:48:44.017 DEBUG [vert.x-eventloop-thread-0] com.zaxxer.hikari.HikariConfig - traceId= spanId= - keepaliveTime................................0
   2025-12-26 14:48:44.017 DEBUG [vert.x-eventloop-thread-0] com.zaxxer.hikari.HikariConfig - traceId= spanId= - leakDetectionThreshold................................3000
   2025-12-26 14:48:44.017 DEBUG [vert.x-eventloop-thread-0] com.zaxxer.hikari.HikariConfig - traceId= spanId= - maxLifetime................................1800000
   2025-12-26 14:48:44.017 DEBUG [vert.x-eventloop-thread-0] com.zaxxer.hikari.HikariConfig - traceId= spanId= - maximumPoolSize................................20
   2025-12-26 14:48:44.017 DEBUG [vert.x-eventloop-thread-0] com.zaxxer.hikari.HikariConfig - traceId= spanId= - metricRegistry................................none
   2025-12-26 14:48:44.017 DEBUG [vert.x-eventloop-thread-0] com.zaxxer.hikari.HikariConfig - traceId= spanId= - metricsTrackerFactory................................none
   2025-12-26 14:48:44.017 DEBUG [vert.x-eventloop-thread-0] com.zaxxer.hikari.HikariConfig - traceId= spanId= - minimumIdle................................5
   2025-12-26 14:48:44.017 DEBUG [vert.x-eventloop-thread-0] com.zaxxer.hikari.HikariConfig - traceId= spanId= - password................................<masked>
   2025-12-26 14:48:44.017 DEBUG [vert.x-eventloop-thread-0] com.zaxxer.hikari.HikariConfig - traceId= spanId= - poolName................................"HikariPool-1"
   2025-12-26 14:48:44.017 DEBUG [vert.x-eventloop-thread-0] com.zaxxer.hikari.HikariConfig - traceId= spanId= - readOnly................................false
   2025-12-26 14:48:44.017 DEBUG [vert.x-eventloop-thread-0] com.zaxxer.hikari.HikariConfig - traceId= spanId= - registerMbeans................................false
   2025-12-26 14:48:44.017 DEBUG [vert.x-eventloop-thread-0] com.zaxxer.hikari.HikariConfig - traceId= spanId= - scheduledExecutor................................none
   2025-12-26 14:48:44.017 DEBUG [vert.x-eventloop-thread-0] com.zaxxer.hikari.HikariConfig - traceId= spanId= - schema................................none
   2025-12-26 14:48:44.018 DEBUG [vert.x-eventloop-thread-0] com.zaxxer.hikari.HikariConfig - traceId= spanId= - threadFactory................................internal
   2025-12-26 14:48:44.018 DEBUG [vert.x-eventloop-thread-0] com.zaxxer.hikari.HikariConfig - traceId= spanId= - transactionIsolation................................default
   2025-12-26 14:48:44.018 DEBUG [vert.x-eventloop-thread-0] com.zaxxer.hikari.HikariConfig - traceId= spanId= - username................................"sekawan"
   2025-12-26 14:48:44.018 DEBUG [vert.x-eventloop-thread-0] com.zaxxer.hikari.HikariConfig - traceId= spanId= - validationTimeout................................5000
   2025-12-26 14:48:44.020 INFO  [vert.x-eventloop-thread-0] com.zaxxer.hikari.HikariDataSource - traceId= spanId= - HikariPool-1 - Starting...
   2025-12-26 14:48:44.033 DEBUG [vert.x-eventloop-thread-0] com.zaxxer.hikari.util.DriverDataSource - traceId= spanId= - Loaded driver with class name org.postgresql.Driver for jdbcUrl=jdbc:postgresql://localhost:5432/sekawan_db
   [otel.javaagent 2025-12-26 14:48:44:157 +0700] [vert.x-eventloop-thread-0] INFO io.opentelemetry.exporter.logging.LoggingSpanExporter - 'sekawan_db' : ac20f0f43b1c748e8b680b17eb58d3b9 ea2cb79c8fd710dc CLIENT [tracer: io.opentelemetry.jdbc:2.22.0-alpha] AttributesMap{data={server.port=5432, db.name=sekawan_db, thread.id=37, thread.name=vert.x-eventloop-thread-0, db.user=sekawan, server.address=localhost, db.connection_string=postgresql://localhost:5432, db.system=postgresql, db.statement=}, capacity=128, totalAddedValues=9}
   [otel.javaagent 2025-12-26 14:48:44:162 +0700] [vert.x-eventloop-thread-0] INFO io.opentelemetry.exporter.logging.LoggingSpanExporter - 'sekawan_db' : 35efeca7f551c47d9b30e2510c17bf3e b50e586636603b05 CLIENT [tracer: io.opentelemetry.jdbc:2.22.0-alpha] AttributesMap{data={server.port=5432, db.name=sekawan_db, thread.id=37, thread.name=vert.x-eventloop-thread-0, db.user=sekawan, server.address=localhost, db.connection_string=postgresql://localhost:5432, db.system=postgresql, db.statement=SHOW TRANSACTION ISOLATION LEVEL}, capacity=128, totalAddedValues=9}
   2025-12-26 14:48:44.169 DEBUG [vert.x-eventloop-thread-0] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@31eca811
   2025-12-26 14:48:44.173 INFO  [vert.x-eventloop-thread-0] com.zaxxer.hikari.HikariDataSource - traceId= spanId= - HikariPool-1 - Start completed.
   2025-12-26 14:48:44.187 WARN  [vert.x-eventloop-thread-0] io.vertx.core.impl.VertxImpl - traceId= spanId= - You're already on a Vert.x context, are you sure you want to create a new Vertx instance?
   2025-12-26 14:48:44.278 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - Before cleanup stats (total=1, active=0, idle=1, waiting=0)
   2025-12-26 14:48:44.279 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - After cleanup  stats (total=1, active=0, idle=1, waiting=0)
   2025-12-26 14:48:44.285 DEBUG [HikariPool-1 connection adder] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@7f640621
   2025-12-26 14:48:44.290 DEBUG [HikariPool-1 connection adder] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@71a3f7bd
   2025-12-26 14:48:44.295 DEBUG [HikariPool-1 connection adder] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@759c6d51
   2025-12-26 14:48:44.301 DEBUG [HikariPool-1 connection adder] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@309efd86
   2025-12-26 14:48:44.301 DEBUG [HikariPool-1 connection adder] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - After adding stats (total=5, active=0, idle=5, waiting=0)
   2025-12-26 14:48:44.336 DEBUG [vert.x-eventloop-thread-0] io.netty.buffer.AdaptiveByteBufAllocator - traceId= spanId= - -Dio.netty.allocator.useCachedMagazinesForNonEventLoopThreads: false
   2025-12-26 14:48:44.343 DEBUG [vert.x-eventloop-thread-0] io.netty.util.Recycler - traceId= spanId= - -Dio.netty.recycler.maxCapacityPerThread: 4096
   2025-12-26 14:48:44.343 DEBUG [vert.x-eventloop-thread-0] io.netty.util.Recycler - traceId= spanId= - -Dio.netty.recycler.ratio: 8
   2025-12-26 14:48:44.343 DEBUG [vert.x-eventloop-thread-0] io.netty.util.Recycler - traceId= spanId= - -Dio.netty.recycler.chunkSize: 32
   2025-12-26 14:48:44.343 DEBUG [vert.x-eventloop-thread-0] io.netty.util.Recycler - traceId= spanId= - -Dio.netty.recycler.blocking: false
   2025-12-26 14:48:44.343 DEBUG [vert.x-eventloop-thread-0] io.netty.util.Recycler - traceId= spanId= - -Dio.netty.recycler.batchFastThreadLocalOnly: true
   2025-12-26 14:48:44.347 DEBUG [vert.x-eventloop-thread-0] io.netty.buffer.ByteBufUtil - traceId= spanId= - -Dio.netty.allocator.type: adaptive
   2025-12-26 14:48:44.347 DEBUG [vert.x-eventloop-thread-0] io.netty.buffer.ByteBufUtil - traceId= spanId= - -Dio.netty.threadLocalDirectBufferSize: 0
   2025-12-26 14:48:44.347 DEBUG [vert.x-eventloop-thread-0] io.netty.buffer.ByteBufUtil - traceId= spanId= - -Dio.netty.maxThreadLocalCharBufferSize: 16384
   2025-12-26 14:48:44.349 DEBUG [vert.x-eventloop-thread-0] io.netty.buffer.AbstractByteBuf - traceId= spanId= - -Dio.netty.buffer.checkAccessible: true
   2025-12-26 14:48:44.349 DEBUG [vert.x-eventloop-thread-0] io.netty.buffer.AbstractByteBuf - traceId= spanId= - -Dio.netty.buffer.checkBounds: true
   2025-12-26 14:48:44.350 DEBUG [vert.x-eventloop-thread-0] io.netty.util.ResourceLeakDetectorFactory - traceId= spanId= - Loaded default ResourceLeakDetector: io.netty.util.ResourceLeakDetector@75abc3ac
   2025-12-26 14:48:44.431 DEBUG [vert.x-eventloop-thread-0] io.netty.channel.DefaultChannelId - traceId= spanId= - -Dio.netty.processId: 17377 (auto-detected)
   2025-12-26 14:48:44.433 DEBUG [vert.x-eventloop-thread-0] io.netty.channel.DefaultChannelId - traceId= spanId= - -Dio.netty.machineId: 50:ed:3c:ff:fe:3e:7e:07 (auto-detected)
   2025-12-26 14:48:44.463 DEBUG [vert.x-eventloop-thread-0] io.netty.bootstrap.ChannelInitializerExtensions - traceId= spanId= - -Dio.netty.bootstrap.extensions: null
   2025-12-26 14:48:44.502 INFO  [vert.x-eventloop-thread-0] mylog.id.sekawan.point.MainVerticle - traceId= spanId= - 🚀 HTTP server running on port 8080
   2025-12-26 14:49:00.954 DEBUG [vert.x-eventloop-thread-0] io.netty.handler.codec.compression.ZlibCodecFactory - traceId=67cee83b2b47f58186bf344cd837a1de spanId=d58739106552e838 - -Dio.netty.noJdkZlibDecoder: false
   2025-12-26 14:49:00.954 DEBUG [vert.x-eventloop-thread-0] io.netty.handler.codec.compression.ZlibCodecFactory - traceId=67cee83b2b47f58186bf344cd837a1de spanId=d58739106552e838 - -Dio.netty.noJdkZlibEncoder: false
   2025-12-26 14:49:00.954 DEBUG [vert.x-eventloop-thread-0] io.netty.handler.codec.compression.ZlibCodecFactory - traceId=67cee83b2b47f58186bf344cd837a1de spanId=d58739106552e838 - JZlib not in the classpath; the only window bits supported value will be 15

   
   ab -n 10 -c 5 http://localhost:8080/test/vertx/rxJava3/organic
   2025-12-26 14:49:00.985 INFO  [vert.x-eventloop-thread-0] mylog.id.sekawan.point.MainVerticle - traceId=67cee83b2b47f58186bf344cd837a1de spanId=d58739106552e838 - method=GET | url=http://localhost:8080/test/vertx/rxJava3/organic | body=null
   2025-12-26 14:49:00.985 INFO  [vert.x-eventloop-thread-0] mylog.VertxRxJava3Testing - traceId=67cee83b2b47f58186bf344cd837a1de spanId=d58739106552e838 - VT THREAD1: Thread[#37,vert.x-eventloop-thread-0,5,main]
   2025-12-26 14:49:01.697 INFO  [RxCachedThreadScheduler-2] mylog.VertxRxJava3Testing - traceId=67cee83b2b47f58186bf344cd837a1de spanId=d58739106552e838 - VT THREAD2: Thread[#47,RxCachedThreadScheduler-2,5,main]
   2025-12-26 14:49:01.699 INFO  [vert.x-eventloop-thread-1] mylog.DefaultSubscriber - traceId=67cee83b2b47f58186bf344cd837a1de spanId=d58739106552e838 - VT THREAD3: Thread[#48,vert.x-eventloop-thread-1,5,main]
   2025-12-26 14:49:01.699 INFO  [vert.x-eventloop-thread-1] mylog.DefaultSubscriber - traceId=67cee83b2b47f58186bf344cd837a1de spanId=d58739106552e838 - response: success
   2025-12-26 14:49:01.712 INFO  [vert.x-eventloop-thread-1] mylog.DefaultSubscriber - traceId=67cee83b2b47f58186bf344cd837a1de spanId=d58739106552e838 - LATENCY endpoint VertxRxJava3Testing : 725 milliseconds
   [otel.javaagent 2025-12-26 14:49:01:723 +0700] [vert.x-eventloop-thread-0] INFO io.opentelemetry.exporter.logging.LoggingSpanExporter - 'GET /test/vertx/rxJava3/organic' : 67cee83b2b47f58186bf344cd837a1de d58739106552e838 SERVER [tracer: io.opentelemetry.netty-4.1:2.22.0-alpha] AttributesMap{data={network.peer.port=63977, url.scheme=http, thread.name=vert.x-eventloop-thread-0, user_agent.original=ApacheBench/2.3, network.protocol.version=1, server.port=8080, http.response.status_code=200, thread.id=37, url.path=/test/vertx/rxJava3/organic, server.address=localhost, client.address=0:0:0:0:0:0:0:1, network.peer.address=0:0:0:0:0:0:0:1, http.route=/test/vertx/rxJava3/organic, http.request.method=GET}, capacity=128, totalAddedValues=14}
   2025-12-26 14:49:01.732 INFO  [vert.x-eventloop-thread-0] mylog.id.sekawan.point.MainVerticle - traceId=aac5639c21203b22d514a88163423ead spanId=a024b82c87c35018 - method=GET | url=http://localhost:8080/test/vertx/rxJava3/organic | body=null
   2025-12-26 14:49:01.733 INFO  [vert.x-eventloop-thread-0] mylog.VertxRxJava3Testing - traceId=aac5639c21203b22d514a88163423ead spanId=a024b82c87c35018 - VT THREAD1: Thread[#37,vert.x-eventloop-thread-0,5,main]
   2025-12-26 14:49:01.733 INFO  [vert.x-eventloop-thread-0] mylog.id.sekawan.point.MainVerticle - traceId=9e7a7547bc8f72a8b7975feff9a1fc74 spanId=7c258f7be83fb3fc - method=GET | url=http://localhost:8080/test/vertx/rxJava3/organic | body=null
   2025-12-26 14:49:01.734 INFO  [vert.x-eventloop-thread-0] mylog.VertxRxJava3Testing - traceId=9e7a7547bc8f72a8b7975feff9a1fc74 spanId=7c258f7be83fb3fc - VT THREAD1: Thread[#37,vert.x-eventloop-thread-0,5,main]
   2025-12-26 14:49:01.735 INFO  [vert.x-eventloop-thread-0] mylog.id.sekawan.point.MainVerticle - traceId=ef41e7194095e2f4cfa5e595b20fa483 spanId=690d9ff05781fe67 - method=GET | url=http://localhost:8080/test/vertx/rxJava3/organic | body=null
   2025-12-26 14:49:01.735 INFO  [vert.x-eventloop-thread-0] mylog.VertxRxJava3Testing - traceId=ef41e7194095e2f4cfa5e595b20fa483 spanId=690d9ff05781fe67 - VT THREAD1: Thread[#37,vert.x-eventloop-thread-0,5,main]
   2025-12-26 14:49:01.735 INFO  [vert.x-eventloop-thread-0] mylog.id.sekawan.point.MainVerticle - traceId=be135320911b424a1ddc8da8765832d3 spanId=3246afc5228faea7 - method=GET | url=http://localhost:8080/test/vertx/rxJava3/organic | body=null
   2025-12-26 14:49:01.735 INFO  [vert.x-eventloop-thread-0] mylog.VertxRxJava3Testing - traceId=be135320911b424a1ddc8da8765832d3 spanId=3246afc5228faea7 - VT THREAD1: Thread[#37,vert.x-eventloop-thread-0,5,main]
   2025-12-26 14:49:01.736 INFO  [vert.x-eventloop-thread-0] mylog.id.sekawan.point.MainVerticle - traceId=289fe58c166a7a344015ce20cec19b1e spanId=71038cccc4c5e7b6 - method=GET | url=http://localhost:8080/test/vertx/rxJava3/organic | body=null
   2025-12-26 14:49:01.736 INFO  [vert.x-eventloop-thread-0] mylog.VertxRxJava3Testing - traceId=289fe58c166a7a344015ce20cec19b1e spanId=71038cccc4c5e7b6 - VT THREAD1: Thread[#37,vert.x-eventloop-thread-0,5,main]
   2025-12-26 14:49:02.517 INFO  [RxCachedThreadScheduler-4] mylog.VertxRxJava3Testing - traceId=ef41e7194095e2f4cfa5e595b20fa483 spanId=690d9ff05781fe67 - VT THREAD2: Thread[#50,RxCachedThreadScheduler-4,5,main]
   2025-12-26 14:49:02.518 INFO  [vert.x-eventloop-thread-2] mylog.DefaultSubscriber - traceId=ef41e7194095e2f4cfa5e595b20fa483 spanId=690d9ff05781fe67 - VT THREAD3: Thread[#53,vert.x-eventloop-thread-2,5,main]
   2025-12-26 14:49:02.518 INFO  [vert.x-eventloop-thread-2] mylog.DefaultSubscriber - traceId=ef41e7194095e2f4cfa5e595b20fa483 spanId=690d9ff05781fe67 - response: success
   2025-12-26 14:49:02.519 INFO  [RxCachedThreadScheduler-5] mylog.VertxRxJava3Testing - traceId=be135320911b424a1ddc8da8765832d3 spanId=3246afc5228faea7 - VT THREAD2: Thread[#51,RxCachedThreadScheduler-5,5,main]
   2025-12-26 14:49:02.519 INFO  [vert.x-eventloop-thread-3] mylog.DefaultSubscriber - traceId=be135320911b424a1ddc8da8765832d3 spanId=3246afc5228faea7 - VT THREAD3: Thread[#54,vert.x-eventloop-thread-3,5,main]
   2025-12-26 14:49:02.519 INFO  [vert.x-eventloop-thread-2] mylog.DefaultSubscriber - traceId=ef41e7194095e2f4cfa5e595b20fa483 spanId=690d9ff05781fe67 - LATENCY endpoint VertxRxJava3Testing : 784 milliseconds
   2025-12-26 14:49:02.519 INFO  [vert.x-eventloop-thread-3] mylog.DefaultSubscriber - traceId=be135320911b424a1ddc8da8765832d3 spanId=3246afc5228faea7 - response: success
   2025-12-26 14:49:02.520 INFO  [vert.x-eventloop-thread-3] mylog.DefaultSubscriber - traceId=be135320911b424a1ddc8da8765832d3 spanId=3246afc5228faea7 - LATENCY endpoint VertxRxJava3Testing : 785 milliseconds
   [otel.javaagent 2025-12-26 14:49:02:520 +0700] [vert.x-eventloop-thread-0] INFO io.opentelemetry.exporter.logging.LoggingSpanExporter - 'GET /test/vertx/rxJava3/organic' : ef41e7194095e2f4cfa5e595b20fa483 690d9ff05781fe67 SERVER [tracer: io.opentelemetry.netty-4.1:2.22.0-alpha] AttributesMap{data={network.peer.port=63980, url.scheme=http, thread.name=vert.x-eventloop-thread-0, user_agent.original=ApacheBench/2.3, network.protocol.version=1, server.port=8080, http.response.status_code=200, thread.id=37, url.path=/test/vertx/rxJava3/organic, server.address=localhost, client.address=0:0:0:0:0:0:0:1, network.peer.address=0:0:0:0:0:0:0:1, http.route=/test/vertx/rxJava3/organic, http.request.method=GET}, capacity=128, totalAddedValues=14}
   2025-12-26 14:49:02.521 INFO  [RxCachedThreadScheduler-3] mylog.VertxRxJava3Testing - traceId=9e7a7547bc8f72a8b7975feff9a1fc74 spanId=7c258f7be83fb3fc - VT THREAD2: Thread[#49,RxCachedThreadScheduler-3,5,main]
   2025-12-26 14:49:02.523 INFO  [vert.x-eventloop-thread-4] mylog.DefaultSubscriber - traceId=9e7a7547bc8f72a8b7975feff9a1fc74 spanId=7c258f7be83fb3fc - VT THREAD3: Thread[#55,vert.x-eventloop-thread-4,5,main]
   2025-12-26 14:49:02.523 INFO  [vert.x-eventloop-thread-4] mylog.DefaultSubscriber - traceId=9e7a7547bc8f72a8b7975feff9a1fc74 spanId=7c258f7be83fb3fc - response: success
   [otel.javaagent 2025-12-26 14:49:02:523 +0700] [vert.x-eventloop-thread-0] INFO io.opentelemetry.exporter.logging.LoggingSpanExporter - 'GET /test/vertx/rxJava3/organic' : be135320911b424a1ddc8da8765832d3 3246afc5228faea7 SERVER [tracer: io.opentelemetry.netty-4.1:2.22.0-alpha] AttributesMap{data={network.peer.port=63981, url.scheme=http, thread.name=vert.x-eventloop-thread-0, user_agent.original=ApacheBench/2.3, network.protocol.version=1, server.port=8080, http.response.status_code=200, thread.id=37, url.path=/test/vertx/rxJava3/organic, server.address=localhost, client.address=0:0:0:0:0:0:0:1, network.peer.address=0:0:0:0:0:0:0:1, http.route=/test/vertx/rxJava3/organic, http.request.method=GET}, capacity=128, totalAddedValues=14}
   2025-12-26 14:49:02.524 INFO  [vert.x-eventloop-thread-4] mylog.DefaultSubscriber - traceId=9e7a7547bc8f72a8b7975feff9a1fc74 spanId=7c258f7be83fb3fc - LATENCY endpoint VertxRxJava3Testing : 791 milliseconds
   [otel.javaagent 2025-12-26 14:49:02:525 +0700] [vert.x-eventloop-thread-0] INFO io.opentelemetry.exporter.logging.LoggingSpanExporter - 'GET /test/vertx/rxJava3/organic' : 9e7a7547bc8f72a8b7975feff9a1fc74 7c258f7be83fb3fc SERVER [tracer: io.opentelemetry.netty-4.1:2.22.0-alpha] AttributesMap{data={network.peer.port=63979, url.scheme=http, thread.name=vert.x-eventloop-thread-0, user_agent.original=ApacheBench/2.3, network.protocol.version=1, server.port=8080, http.response.status_code=200, thread.id=37, url.path=/test/vertx/rxJava3/organic, server.address=localhost, client.address=0:0:0:0:0:0:0:1, network.peer.address=0:0:0:0:0:0:0:1, http.route=/test/vertx/rxJava3/organic, http.request.method=GET}, capacity=128, totalAddedValues=14}
   2025-12-26 14:49:02.527 INFO  [vert.x-eventloop-thread-0] mylog.id.sekawan.point.MainVerticle - traceId=1835520fa66a9de46deaab6f47de2a39 spanId=2ace400f8251453c - method=GET | url=http://localhost:8080/test/vertx/rxJava3/organic | body=null
   2025-12-26 14:49:02.527 INFO  [vert.x-eventloop-thread-0] mylog.VertxRxJava3Testing - traceId=1835520fa66a9de46deaab6f47de2a39 spanId=2ace400f8251453c - VT THREAD1: Thread[#37,vert.x-eventloop-thread-0,5,main]
   2025-12-26 14:49:02.528 INFO  [vert.x-eventloop-thread-0] mylog.id.sekawan.point.MainVerticle - traceId=c1a0ca959c87a19bb5e5914b85e24214 spanId=de2cae06442342d0 - method=GET | url=http://localhost:8080/test/vertx/rxJava3/organic | body=null
   2025-12-26 14:49:02.528 INFO  [vert.x-eventloop-thread-0] mylog.VertxRxJava3Testing - traceId=c1a0ca959c87a19bb5e5914b85e24214 spanId=de2cae06442342d0 - VT THREAD1: Thread[#37,vert.x-eventloop-thread-0,5,main]
   2025-12-26 14:49:02.529 INFO  [vert.x-eventloop-thread-0] mylog.id.sekawan.point.MainVerticle - traceId=fa0feb36bd11a9aab6250db9f5c5c784 spanId=8e48f552d7ac86ff - method=GET | url=http://localhost:8080/test/vertx/rxJava3/organic | body=null
   2025-12-26 14:49:02.529 INFO  [vert.x-eventloop-thread-0] mylog.VertxRxJava3Testing - traceId=fa0feb36bd11a9aab6250db9f5c5c784 spanId=8e48f552d7ac86ff - VT THREAD1: Thread[#37,vert.x-eventloop-thread-0,5,main]
   2025-12-26 14:49:02.532 INFO  [RxCachedThreadScheduler-6] mylog.VertxRxJava3Testing - traceId=289fe58c166a7a344015ce20cec19b1e spanId=71038cccc4c5e7b6 - VT THREAD2: Thread[#52,RxCachedThreadScheduler-6,5,main]
   2025-12-26 14:49:02.533 INFO  [vert.x-eventloop-thread-5] mylog.DefaultSubscriber - traceId=289fe58c166a7a344015ce20cec19b1e spanId=71038cccc4c5e7b6 - VT THREAD3: Thread[#56,vert.x-eventloop-thread-5,5,main]
   2025-12-26 14:49:02.533 INFO  [vert.x-eventloop-thread-5] mylog.DefaultSubscriber - traceId=289fe58c166a7a344015ce20cec19b1e spanId=71038cccc4c5e7b6 - response: success
   2025-12-26 14:49:02.534 INFO  [vert.x-eventloop-thread-5] mylog.DefaultSubscriber - traceId=289fe58c166a7a344015ce20cec19b1e spanId=71038cccc4c5e7b6 - LATENCY endpoint VertxRxJava3Testing : 798 milliseconds
   [otel.javaagent 2025-12-26 14:49:02:535 +0700] [vert.x-eventloop-thread-0] INFO io.opentelemetry.exporter.logging.LoggingSpanExporter - 'GET /test/vertx/rxJava3/organic' : 289fe58c166a7a344015ce20cec19b1e 71038cccc4c5e7b6 SERVER [tracer: io.opentelemetry.netty-4.1:2.22.0-alpha] AttributesMap{data={network.peer.port=63982, url.scheme=http, thread.name=vert.x-eventloop-thread-0, user_agent.original=ApacheBench/2.3, network.protocol.version=1, server.port=8080, http.response.status_code=200, thread.id=37, url.path=/test/vertx/rxJava3/organic, server.address=localhost, client.address=0:0:0:0:0:0:0:1, network.peer.address=0:0:0:0:0:0:0:1, http.route=/test/vertx/rxJava3/organic, http.request.method=GET}, capacity=128, totalAddedValues=14}
   2025-12-26 14:49:02.536 INFO  [RxCachedThreadScheduler-2] mylog.VertxRxJava3Testing - traceId=aac5639c21203b22d514a88163423ead spanId=a024b82c87c35018 - VT THREAD2: Thread[#47,RxCachedThreadScheduler-2,5,main]
   2025-12-26 14:49:02.536 INFO  [vert.x-eventloop-thread-1] mylog.DefaultSubscriber - traceId=aac5639c21203b22d514a88163423ead spanId=a024b82c87c35018 - VT THREAD3: Thread[#48,vert.x-eventloop-thread-1,5,main]
   2025-12-26 14:49:02.537 INFO  [vert.x-eventloop-thread-1] mylog.DefaultSubscriber - traceId=aac5639c21203b22d514a88163423ead spanId=a024b82c87c35018 - response: success
   2025-12-26 14:49:02.538 INFO  [vert.x-eventloop-thread-1] mylog.DefaultSubscriber - traceId=aac5639c21203b22d514a88163423ead spanId=a024b82c87c35018 - LATENCY endpoint VertxRxJava3Testing : 805 milliseconds
   2025-12-26 14:49:02.538 INFO  [vert.x-eventloop-thread-0] mylog.id.sekawan.point.MainVerticle - traceId=7f07ce4868ed7f9458110447aa3fbe6e spanId=b4130dfb73e4f6b8 - method=GET | url=http://localhost:8080/test/vertx/rxJava3/organic | body=null
   2025-12-26 14:49:02.538 INFO  [vert.x-eventloop-thread-0] mylog.VertxRxJava3Testing - traceId=7f07ce4868ed7f9458110447aa3fbe6e spanId=b4130dfb73e4f6b8 - VT THREAD1: Thread[#37,vert.x-eventloop-thread-0,5,main]
   [otel.javaagent 2025-12-26 14:49:02:539 +0700] [vert.x-eventloop-thread-0] INFO io.opentelemetry.exporter.logging.LoggingSpanExporter - 'GET /test/vertx/rxJava3/organic' : aac5639c21203b22d514a88163423ead a024b82c87c35018 SERVER [tracer: io.opentelemetry.netty-4.1:2.22.0-alpha] AttributesMap{data={network.peer.port=63978, url.scheme=http, thread.name=vert.x-eventloop-thread-0, user_agent.original=ApacheBench/2.3, network.protocol.version=1, server.port=8080, http.response.status_code=200, thread.id=37, url.path=/test/vertx/rxJava3/organic, server.address=localhost, client.address=0:0:0:0:0:0:0:1, network.peer.address=0:0:0:0:0:0:0:1, http.route=/test/vertx/rxJava3/organic, http.request.method=GET}, capacity=128, totalAddedValues=14}
   2025-12-26 14:49:03.255 INFO  [RxCachedThreadScheduler-4] mylog.VertxRxJava3Testing - traceId=1835520fa66a9de46deaab6f47de2a39 spanId=2ace400f8251453c - VT THREAD2: Thread[#50,RxCachedThreadScheduler-4,5,main]
   2025-12-26 14:49:03.255 INFO  [vert.x-eventloop-thread-2] mylog.DefaultSubscriber - traceId=1835520fa66a9de46deaab6f47de2a39 spanId=2ace400f8251453c - VT THREAD3: Thread[#53,vert.x-eventloop-thread-2,5,main]
   2025-12-26 14:49:03.255 INFO  [vert.x-eventloop-thread-2] mylog.DefaultSubscriber - traceId=1835520fa66a9de46deaab6f47de2a39 spanId=2ace400f8251453c - response: success
   2025-12-26 14:49:03.256 INFO  [vert.x-eventloop-thread-2] mylog.DefaultSubscriber - traceId=1835520fa66a9de46deaab6f47de2a39 spanId=2ace400f8251453c - LATENCY endpoint VertxRxJava3Testing : 729 milliseconds
   [otel.javaagent 2025-12-26 14:49:03:257 +0700] [vert.x-eventloop-thread-0] INFO io.opentelemetry.exporter.logging.LoggingSpanExporter - 'GET /test/vertx/rxJava3/organic' : 1835520fa66a9de46deaab6f47de2a39 2ace400f8251453c SERVER [tracer: io.opentelemetry.netty-4.1:2.22.0-alpha] AttributesMap{data={network.peer.port=63983, url.scheme=http, thread.name=vert.x-eventloop-thread-0, user_agent.original=ApacheBench/2.3, network.protocol.version=1, server.port=8080, http.response.status_code=200, thread.id=37, url.path=/test/vertx/rxJava3/organic, server.address=localhost, client.address=0:0:0:0:0:0:0:1, network.peer.address=0:0:0:0:0:0:0:1, http.route=/test/vertx/rxJava3/organic, http.request.method=GET}, capacity=128, totalAddedValues=14}
   2025-12-26 14:49:03.258 INFO  [RxCachedThreadScheduler-3] mylog.VertxRxJava3Testing - traceId=c1a0ca959c87a19bb5e5914b85e24214 spanId=de2cae06442342d0 - VT THREAD2: Thread[#49,RxCachedThreadScheduler-3,5,main]
   2025-12-26 14:49:03.259 INFO  [vert.x-eventloop-thread-4] mylog.DefaultSubscriber - traceId=c1a0ca959c87a19bb5e5914b85e24214 spanId=de2cae06442342d0 - VT THREAD3: Thread[#55,vert.x-eventloop-thread-4,5,main]
   2025-12-26 14:49:03.259 INFO  [vert.x-eventloop-thread-4] mylog.DefaultSubscriber - traceId=c1a0ca959c87a19bb5e5914b85e24214 spanId=de2cae06442342d0 - response: success
   2025-12-26 14:49:03.259 INFO  [vert.x-eventloop-thread-4] mylog.DefaultSubscriber - traceId=c1a0ca959c87a19bb5e5914b85e24214 spanId=de2cae06442342d0 - LATENCY endpoint VertxRxJava3Testing : 731 milliseconds
   2025-12-26 14:49:03.259 INFO  [RxCachedThreadScheduler-6] mylog.VertxRxJava3Testing - traceId=7f07ce4868ed7f9458110447aa3fbe6e spanId=b4130dfb73e4f6b8 - VT THREAD2: Thread[#52,RxCachedThreadScheduler-6,5,main]
   [otel.javaagent 2025-12-26 14:49:03:259 +0700] [vert.x-eventloop-thread-0] INFO io.opentelemetry.exporter.logging.LoggingSpanExporter - 'GET /test/vertx/rxJava3/organic' : c1a0ca959c87a19bb5e5914b85e24214 de2cae06442342d0 SERVER [tracer: io.opentelemetry.netty-4.1:2.22.0-alpha] AttributesMap{data={network.peer.port=63984, url.scheme=http, thread.name=vert.x-eventloop-thread-0, user_agent.original=ApacheBench/2.3, network.protocol.version=1, server.port=8080, http.response.status_code=200, thread.id=37, url.path=/test/vertx/rxJava3/organic, server.address=localhost, client.address=0:0:0:0:0:0:0:1, network.peer.address=0:0:0:0:0:0:0:1, http.route=/test/vertx/rxJava3/organic, http.request.method=GET}, capacity=128, totalAddedValues=14}
   2025-12-26 14:49:03.259 INFO  [vert.x-eventloop-thread-5] mylog.DefaultSubscriber - traceId=7f07ce4868ed7f9458110447aa3fbe6e spanId=b4130dfb73e4f6b8 - VT THREAD3: Thread[#56,vert.x-eventloop-thread-5,5,main]
   2025-12-26 14:49:03.259 INFO  [vert.x-eventloop-thread-5] mylog.DefaultSubscriber - traceId=7f07ce4868ed7f9458110447aa3fbe6e spanId=b4130dfb73e4f6b8 - response: success
   2025-12-26 14:49:03.260 INFO  [vert.x-eventloop-thread-5] mylog.DefaultSubscriber - traceId=7f07ce4868ed7f9458110447aa3fbe6e spanId=b4130dfb73e4f6b8 - LATENCY endpoint VertxRxJava3Testing : 722 milliseconds
   [otel.javaagent 2025-12-26 14:49:03:260 +0700] [vert.x-eventloop-thread-0] INFO io.opentelemetry.exporter.logging.LoggingSpanExporter - 'GET /test/vertx/rxJava3/organic' : 7f07ce4868ed7f9458110447aa3fbe6e b4130dfb73e4f6b8 SERVER [tracer: io.opentelemetry.netty-4.1:2.22.0-alpha] AttributesMap{data={network.peer.port=63986, url.scheme=http, thread.name=vert.x-eventloop-thread-0, user_agent.original=ApacheBench/2.3, network.protocol.version=1, server.port=8080, http.response.status_code=200, thread.id=37, url.path=/test/vertx/rxJava3/organic, server.address=localhost, client.address=0:0:0:0:0:0:0:1, network.peer.address=0:0:0:0:0:0:0:1, http.route=/test/vertx/rxJava3/organic, http.request.method=GET}, capacity=128, totalAddedValues=14}
   2025-12-26 14:49:03.262 INFO  [RxCachedThreadScheduler-5] mylog.VertxRxJava3Testing - traceId=fa0feb36bd11a9aab6250db9f5c5c784 spanId=8e48f552d7ac86ff - VT THREAD2: Thread[#51,RxCachedThreadScheduler-5,5,main]
   2025-12-26 14:49:03.262 INFO  [vert.x-eventloop-thread-3] mylog.DefaultSubscriber - traceId=fa0feb36bd11a9aab6250db9f5c5c784 spanId=8e48f552d7ac86ff - VT THREAD3: Thread[#54,vert.x-eventloop-thread-3,5,main]
   2025-12-26 14:49:03.262 INFO  [vert.x-eventloop-thread-3] mylog.DefaultSubscriber - traceId=fa0feb36bd11a9aab6250db9f5c5c784 spanId=8e48f552d7ac86ff - response: success
   2025-12-26 14:49:03.263 INFO  [vert.x-eventloop-thread-3] mylog.DefaultSubscriber - traceId=fa0feb36bd11a9aab6250db9f5c5c784 spanId=8e48f552d7ac86ff - LATENCY endpoint VertxRxJava3Testing : 734 milliseconds
   [otel.javaagent 2025-12-26 14:49:03:263 +0700] [vert.x-eventloop-thread-0] INFO io.opentelemetry.exporter.logging.LoggingSpanExporter - 'GET /test/vertx/rxJava3/organic' : fa0feb36bd11a9aab6250db9f5c5c784 8e48f552d7ac86ff SERVER [tracer: io.opentelemetry.netty-4.1:2.22.0-alpha] AttributesMap{data={network.peer.port=63985, url.scheme=http, thread.name=vert.x-eventloop-thread-0, user_agent.original=ApacheBench/2.3, network.protocol.version=1, server.port=8080, http.response.status_code=200, thread.id=37, url.path=/test/vertx/rxJava3/organic, server.address=localhost, client.address=0:0:0:0:0:0:0:1, network.peer.address=0:0:0:0:0:0:0:1, http.route=/test/vertx/rxJava3/organic, http.request.method=GET}, capacity=128, totalAddedValues=14}
   2025-12-26 14:49:14.286 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - Before cleanup stats (total=5, active=0, idle=5, waiting=0)
   2025-12-26 14:49:14.289 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - After cleanup  stats (total=5, active=0, idle=5, waiting=0)
   2025-12-26 14:49:14.290 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - Fill pool skipped, pool is at sufficient level.
   2025-12-26 14:49:44.298 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - Before cleanup stats (total=5, active=0, idle=5, waiting=0)
   2025-12-26 14:49:44.300 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - After cleanup  stats (total=5, active=0, idle=5, waiting=0)
   2025-12-26 14:49:44.300 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - Fill pool skipped, pool is at sufficient level.
   2025-12-26 14:50:14.306 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - Before cleanup stats (total=5, active=0, idle=5, waiting=0)
   2025-12-26 14:50:14.310 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - After cleanup  stats (total=5, active=0, idle=5, waiting=0)
   2025-12-26 14:50:14.310 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - Fill pool skipped, pool is at sufficient level.
   2025-12-26 14:50:44.312 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - Before cleanup stats (total=5, active=0, idle=5, waiting=0)
   2025-12-26 14:50:44.317 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - After cleanup  stats (total=5, active=0, idle=5, waiting=0)
   2025-12-26 14:50:44.317 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - Fill pool skipped, pool is at sufficient level.
   
   
   
   ab -n 10 -c 5 http://localhost:8080/test/vertx/virtualThread/executorService 
   2025-12-26 14:51:11.744 INFO  [vert.x-eventloop-thread-0] mylog.id.sekawan.point.MainVerticle - traceId=da2df798313265624b8108f766cb5d10 spanId=c0d92603ad532678 - method=GET | url=http://localhost:8080/test/vertx/virtualThread/executorService | body=null
   2025-12-26 14:51:11.747 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecutorService - traceId=da2df798313265624b8108f766cb5d10 spanId=c0d92603ad532678 - VT THREAD1: Thread[#37,vert.x-eventloop-thread-0,5,main]
   2025-12-26 14:51:11.769 INFO  [vert.x-worker-thread-0] mylog.VirtualThreadExecutorService - traceId=da2df798313265624b8108f766cb5d10 spanId=c0d92603ad532678 - VT THREAD2: Thread[#57,vert.x-worker-thread-0,5,main]
   2025-12-26 14:51:12.578 INFO  [virtual-58] mylog.VirtualThreadExecutorService - traceId=da2df798313265624b8108f766cb5d10 spanId=c0d92603ad532678 - VT THREAD3: VirtualThread[#58]/runnable@ForkJoinPool-1-worker-1
   2025-12-26 14:51:12.579 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecutorService - traceId=da2df798313265624b8108f766cb5d10 spanId=c0d92603ad532678 - VT THREAD4: Thread[#37,vert.x-eventloop-thread-0,5,main]
   [otel.javaagent 2025-12-26 14:51:12:580 +0700] [vert.x-eventloop-thread-0] INFO io.opentelemetry.exporter.logging.LoggingSpanExporter - 'GET /test/vertx/virtualThread/executorService' : da2df798313265624b8108f766cb5d10 c0d92603ad532678 SERVER [tracer: io.opentelemetry.netty-4.1:2.22.0-alpha] AttributesMap{data={network.peer.port=63997, url.scheme=http, thread.name=vert.x-eventloop-thread-0, user_agent.original=ApacheBench/2.3, network.protocol.version=1, server.port=8080, http.response.status_code=200, thread.id=37, url.path=/test/vertx/virtualThread/executorService, server.address=localhost, client.address=0:0:0:0:0:0:0:1, network.peer.address=0:0:0:0:0:0:0:1, http.route=/test/vertx/virtualThread/executorService, http.request.method=GET}, capacity=128, totalAddedValues=14}
   2025-12-26 14:51:12.582 INFO  [vert.x-eventloop-thread-0] mylog.id.sekawan.point.MainVerticle - traceId=76513b6d24dab9331dcceefae5e3cffc spanId=7c0172b4b96da0e9 - method=GET | url=http://localhost:8080/test/vertx/virtualThread/executorService | body=null
   2025-12-26 14:51:12.582 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecutorService - traceId=76513b6d24dab9331dcceefae5e3cffc spanId=7c0172b4b96da0e9 - VT THREAD1: Thread[#37,vert.x-eventloop-thread-0,5,main]
   2025-12-26 14:51:12.582 INFO  [vert.x-worker-thread-1] mylog.VirtualThreadExecutorService - traceId=76513b6d24dab9331dcceefae5e3cffc spanId=7c0172b4b96da0e9 - VT THREAD2: Thread[#61,vert.x-worker-thread-1,5,main]
   2025-12-26 14:51:12.583 INFO  [vert.x-eventloop-thread-0] mylog.id.sekawan.point.MainVerticle - traceId=f105df3dc370787494619af5b8fd61a2 spanId=3b284d13ef35c357 - method=GET | url=http://localhost:8080/test/vertx/virtualThread/executorService | body=null
   2025-12-26 14:51:12.583 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecutorService - traceId=f105df3dc370787494619af5b8fd61a2 spanId=3b284d13ef35c357 - VT THREAD1: Thread[#37,vert.x-eventloop-thread-0,5,main]
   2025-12-26 14:51:12.583 INFO  [vert.x-worker-thread-2] mylog.VirtualThreadExecutorService - traceId=f105df3dc370787494619af5b8fd61a2 spanId=3b284d13ef35c357 - VT THREAD2: Thread[#63,vert.x-worker-thread-2,5,main]
   2025-12-26 14:51:12.583 INFO  [vert.x-eventloop-thread-0] mylog.id.sekawan.point.MainVerticle - traceId=e49f707951331651ffb1a5f3e481cb3f spanId=d68294392015c5f3 - method=GET | url=http://localhost:8080/test/vertx/virtualThread/executorService | body=null
   2025-12-26 14:51:12.583 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecutorService - traceId=e49f707951331651ffb1a5f3e481cb3f spanId=d68294392015c5f3 - VT THREAD1: Thread[#37,vert.x-eventloop-thread-0,5,main]
   2025-12-26 14:51:12.583 INFO  [vert.x-worker-thread-3] mylog.VirtualThreadExecutorService - traceId=e49f707951331651ffb1a5f3e481cb3f spanId=d68294392015c5f3 - VT THREAD2: Thread[#65,vert.x-worker-thread-3,5,main]
   2025-12-26 14:51:12.584 INFO  [vert.x-eventloop-thread-0] mylog.id.sekawan.point.MainVerticle - traceId=806f0c0a9147c6dd12229d1cf7917f6a spanId=5b747bdd83066994 - method=GET | url=http://localhost:8080/test/vertx/virtualThread/executorService | body=null
   2025-12-26 14:51:12.584 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecutorService - traceId=806f0c0a9147c6dd12229d1cf7917f6a spanId=5b747bdd83066994 - VT THREAD1: Thread[#37,vert.x-eventloop-thread-0,5,main]
   2025-12-26 14:51:12.584 INFO  [vert.x-worker-thread-4] mylog.VirtualThreadExecutorService - traceId=806f0c0a9147c6dd12229d1cf7917f6a spanId=5b747bdd83066994 - VT THREAD2: Thread[#68,vert.x-worker-thread-4,5,main]
   2025-12-26 14:51:12.585 INFO  [vert.x-eventloop-thread-0] mylog.id.sekawan.point.MainVerticle - traceId=3e805907af5782a68480c930104dc245 spanId=37433cf2d547bef3 - method=GET | url=http://localhost:8080/test/vertx/virtualThread/executorService | body=null
   2025-12-26 14:51:12.585 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecutorService - traceId=3e805907af5782a68480c930104dc245 spanId=37433cf2d547bef3 - VT THREAD1: Thread[#37,vert.x-eventloop-thread-0,5,main]
   2025-12-26 14:51:12.586 INFO  [vert.x-worker-thread-5] mylog.VirtualThreadExecutorService - traceId=3e805907af5782a68480c930104dc245 spanId=37433cf2d547bef3 - VT THREAD2: Thread[#71,vert.x-worker-thread-5,5,main]
   2025-12-26 14:51:13.375 INFO  [virtual-64] mylog.VirtualThreadExecutorService - traceId=f105df3dc370787494619af5b8fd61a2 spanId=3b284d13ef35c357 - VT THREAD3: VirtualThread[#64]/runnable@ForkJoinPool-1-worker-2
   2025-12-26 14:51:13.376 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecutorService - traceId=f105df3dc370787494619af5b8fd61a2 spanId=3b284d13ef35c357 - VT THREAD4: Thread[#37,vert.x-eventloop-thread-0,5,main]
   [otel.javaagent 2025-12-26 14:51:13:377 +0700] [vert.x-eventloop-thread-0] INFO io.opentelemetry.exporter.logging.LoggingSpanExporter - 'GET /test/vertx/virtualThread/executorService' : f105df3dc370787494619af5b8fd61a2 3b284d13ef35c357 SERVER [tracer: io.opentelemetry.netty-4.1:2.22.0-alpha] AttributesMap{data={network.peer.port=63999, url.scheme=http, thread.name=vert.x-eventloop-thread-0, user_agent.original=ApacheBench/2.3, network.protocol.version=1, server.port=8080, http.response.status_code=200, thread.id=37, url.path=/test/vertx/virtualThread/executorService, server.address=localhost, client.address=0:0:0:0:0:0:0:1, network.peer.address=0:0:0:0:0:0:0:1, http.route=/test/vertx/virtualThread/executorService, http.request.method=GET}, capacity=128, totalAddedValues=14}
   2025-12-26 14:51:13.379 INFO  [vert.x-eventloop-thread-0] mylog.id.sekawan.point.MainVerticle - traceId=158113f8c65c59572158369fca0a4ab8 spanId=4ca9d37052333c45 - method=GET | url=http://localhost:8080/test/vertx/virtualThread/executorService | body=null
   2025-12-26 14:51:13.379 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecutorService - traceId=158113f8c65c59572158369fca0a4ab8 spanId=4ca9d37052333c45 - VT THREAD1: Thread[#37,vert.x-eventloop-thread-0,5,main]
   2025-12-26 14:51:13.379 INFO  [vert.x-worker-thread-6] mylog.VirtualThreadExecutorService - traceId=158113f8c65c59572158369fca0a4ab8 spanId=4ca9d37052333c45 - VT THREAD2: Thread[#75,vert.x-worker-thread-6,5,main]
   2025-12-26 14:51:13.381 INFO  [virtual-72] mylog.VirtualThreadExecutorService - traceId=3e805907af5782a68480c930104dc245 spanId=37433cf2d547bef3 - VT THREAD3: VirtualThread[#72]/runnable@ForkJoinPool-1-worker-5
   2025-12-26 14:51:13.381 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecutorService - traceId=3e805907af5782a68480c930104dc245 spanId=37433cf2d547bef3 - VT THREAD4: Thread[#37,vert.x-eventloop-thread-0,5,main]
   [otel.javaagent 2025-12-26 14:51:13:382 +0700] [vert.x-eventloop-thread-0] INFO io.opentelemetry.exporter.logging.LoggingSpanExporter - 'GET /test/vertx/virtualThread/executorService' : 3e805907af5782a68480c930104dc245 37433cf2d547bef3 SERVER [tracer: io.opentelemetry.netty-4.1:2.22.0-alpha] AttributesMap{data={network.peer.port=64002, url.scheme=http, thread.name=vert.x-eventloop-thread-0, user_agent.original=ApacheBench/2.3, network.protocol.version=1, server.port=8080, http.response.status_code=200, thread.id=37, url.path=/test/vertx/virtualThread/executorService, server.address=localhost, client.address=0:0:0:0:0:0:0:1, network.peer.address=0:0:0:0:0:0:0:1, http.route=/test/vertx/virtualThread/executorService, http.request.method=GET}, capacity=128, totalAddedValues=14}
   2025-12-26 14:51:13.384 INFO  [vert.x-eventloop-thread-0] mylog.id.sekawan.point.MainVerticle - traceId=1e8f7177116996ba765e9d2adb03569d spanId=dd847ba43a683a7a - method=GET | url=http://localhost:8080/test/vertx/virtualThread/executorService | body=null
   2025-12-26 14:51:13.385 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecutorService - traceId=1e8f7177116996ba765e9d2adb03569d spanId=dd847ba43a683a7a - VT THREAD1: Thread[#37,vert.x-eventloop-thread-0,5,main]
   2025-12-26 14:51:13.385 INFO  [vert.x-worker-thread-7] mylog.VirtualThreadExecutorService - traceId=1e8f7177116996ba765e9d2adb03569d spanId=dd847ba43a683a7a - VT THREAD2: Thread[#77,vert.x-worker-thread-7,5,main]
   2025-12-26 14:51:13.389 INFO  [virtual-62] mylog.VirtualThreadExecutorService - traceId=76513b6d24dab9331dcceefae5e3cffc spanId=7c0172b4b96da0e9 - VT THREAD3: VirtualThread[#62]/runnable@ForkJoinPool-1-worker-1
   2025-12-26 14:51:13.389 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecutorService - traceId=76513b6d24dab9331dcceefae5e3cffc spanId=7c0172b4b96da0e9 - VT THREAD4: Thread[#37,vert.x-eventloop-thread-0,5,main]
   [otel.javaagent 2025-12-26 14:51:13:390 +0700] [vert.x-eventloop-thread-0] INFO io.opentelemetry.exporter.logging.LoggingSpanExporter - 'GET /test/vertx/virtualThread/executorService' : 76513b6d24dab9331dcceefae5e3cffc 7c0172b4b96da0e9 SERVER [tracer: io.opentelemetry.netty-4.1:2.22.0-alpha] AttributesMap{data={network.peer.port=63998, url.scheme=http, thread.name=vert.x-eventloop-thread-0, user_agent.original=ApacheBench/2.3, network.protocol.version=1, server.port=8080, http.response.status_code=200, thread.id=37, url.path=/test/vertx/virtualThread/executorService, server.address=localhost, client.address=0:0:0:0:0:0:0:1, network.peer.address=0:0:0:0:0:0:0:1, http.route=/test/vertx/virtualThread/executorService, http.request.method=GET}, capacity=128, totalAddedValues=14}
   2025-12-26 14:51:13.392 INFO  [vert.x-eventloop-thread-0] mylog.id.sekawan.point.MainVerticle - traceId=2754a5543ca5ad96556ba7a354850af5 spanId=9256b79e924cbd7f - method=GET | url=http://localhost:8080/test/vertx/virtualThread/executorService | body=null
   2025-12-26 14:51:13.393 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecutorService - traceId=2754a5543ca5ad96556ba7a354850af5 spanId=9256b79e924cbd7f - VT THREAD1: Thread[#37,vert.x-eventloop-thread-0,5,main]
   2025-12-26 14:51:13.395 INFO  [vert.x-worker-thread-8] mylog.VirtualThreadExecutorService - traceId=2754a5543ca5ad96556ba7a354850af5 spanId=9256b79e924cbd7f - VT THREAD2: Thread[#79,vert.x-worker-thread-8,5,main]
   2025-12-26 14:51:13.397 INFO  [virtual-66] mylog.VirtualThreadExecutorService - traceId=e49f707951331651ffb1a5f3e481cb3f spanId=d68294392015c5f3 - VT THREAD3: VirtualThread[#66]/runnable@ForkJoinPool-1-worker-3
   2025-12-26 14:51:13.397 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecutorService - traceId=e49f707951331651ffb1a5f3e481cb3f spanId=d68294392015c5f3 - VT THREAD4: Thread[#37,vert.x-eventloop-thread-0,5,main]
   [otel.javaagent 2025-12-26 14:51:13:399 +0700] [vert.x-eventloop-thread-0] INFO io.opentelemetry.exporter.logging.LoggingSpanExporter - 'GET /test/vertx/virtualThread/executorService' : e49f707951331651ffb1a5f3e481cb3f d68294392015c5f3 SERVER [tracer: io.opentelemetry.netty-4.1:2.22.0-alpha] AttributesMap{data={network.peer.port=64000, url.scheme=http, thread.name=vert.x-eventloop-thread-0, user_agent.original=ApacheBench/2.3, network.protocol.version=1, server.port=8080, http.response.status_code=200, thread.id=37, url.path=/test/vertx/virtualThread/executorService, server.address=localhost, client.address=0:0:0:0:0:0:0:1, network.peer.address=0:0:0:0:0:0:0:1, http.route=/test/vertx/virtualThread/executorService, http.request.method=GET}, capacity=128, totalAddedValues=14}
   2025-12-26 14:51:13.401 INFO  [vert.x-eventloop-thread-0] mylog.id.sekawan.point.MainVerticle - traceId=11bc13eeeccfdba846c7ef5cc59cb735 spanId=32ccda64e731e1aa - method=GET | url=http://localhost:8080/test/vertx/virtualThread/executorService | body=null
   2025-12-26 14:51:13.401 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecutorService - traceId=11bc13eeeccfdba846c7ef5cc59cb735 spanId=32ccda64e731e1aa - VT THREAD1: Thread[#37,vert.x-eventloop-thread-0,5,main]
   2025-12-26 14:51:13.402 INFO  [vert.x-worker-thread-9] mylog.VirtualThreadExecutorService - traceId=11bc13eeeccfdba846c7ef5cc59cb735 spanId=32ccda64e731e1aa - VT THREAD2: Thread[#81,vert.x-worker-thread-9,5,main]
   2025-12-26 14:51:13.411 INFO  [virtual-69] mylog.VirtualThreadExecutorService - traceId=806f0c0a9147c6dd12229d1cf7917f6a spanId=5b747bdd83066994 - VT THREAD3: VirtualThread[#69]/runnable@ForkJoinPool-1-worker-4
   2025-12-26 14:51:13.411 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecutorService - traceId=806f0c0a9147c6dd12229d1cf7917f6a spanId=5b747bdd83066994 - VT THREAD4: Thread[#37,vert.x-eventloop-thread-0,5,main]
   [otel.javaagent 2025-12-26 14:51:13:413 +0700] [vert.x-eventloop-thread-0] INFO io.opentelemetry.exporter.logging.LoggingSpanExporter - 'GET /test/vertx/virtualThread/executorService' : 806f0c0a9147c6dd12229d1cf7917f6a 5b747bdd83066994 SERVER [tracer: io.opentelemetry.netty-4.1:2.22.0-alpha] AttributesMap{data={network.peer.port=64001, url.scheme=http, thread.name=vert.x-eventloop-thread-0, user_agent.original=ApacheBench/2.3, network.protocol.version=1, server.port=8080, http.response.status_code=200, thread.id=37, url.path=/test/vertx/virtualThread/executorService, server.address=localhost, client.address=0:0:0:0:0:0:0:1, network.peer.address=0:0:0:0:0:0:0:1, http.route=/test/vertx/virtualThread/executorService, http.request.method=GET}, capacity=128, totalAddedValues=14}
   2025-12-26 14:51:14.126 INFO  [virtual-76] mylog.VirtualThreadExecutorService - traceId=158113f8c65c59572158369fca0a4ab8 spanId=4ca9d37052333c45 - VT THREAD3: VirtualThread[#76]/runnable@ForkJoinPool-1-worker-6
   2025-12-26 14:51:14.128 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecutorService - traceId=158113f8c65c59572158369fca0a4ab8 spanId=4ca9d37052333c45 - VT THREAD4: Thread[#37,vert.x-eventloop-thread-0,5,main]
   [otel.javaagent 2025-12-26 14:51:14:135 +0700] [vert.x-eventloop-thread-0] INFO io.opentelemetry.exporter.logging.LoggingSpanExporter - 'GET /test/vertx/virtualThread/executorService' : 158113f8c65c59572158369fca0a4ab8 4ca9d37052333c45 SERVER [tracer: io.opentelemetry.netty-4.1:2.22.0-alpha] AttributesMap{data={network.peer.port=64003, url.scheme=http, thread.name=vert.x-eventloop-thread-0, user_agent.original=ApacheBench/2.3, network.protocol.version=1, server.port=8080, http.response.status_code=200, thread.id=37, url.path=/test/vertx/virtualThread/executorService, server.address=localhost, client.address=0:0:0:0:0:0:0:1, network.peer.address=0:0:0:0:0:0:0:1, http.route=/test/vertx/virtualThread/executorService, http.request.method=GET}, capacity=128, totalAddedValues=14}
   2025-12-26 14:51:14.141 INFO  [virtual-78] mylog.VirtualThreadExecutorService - traceId=1e8f7177116996ba765e9d2adb03569d spanId=dd847ba43a683a7a - VT THREAD3: VirtualThread[#78]/runnable@ForkJoinPool-1-worker-2
   2025-12-26 14:51:14.141 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecutorService - traceId=1e8f7177116996ba765e9d2adb03569d spanId=dd847ba43a683a7a - VT THREAD4: Thread[#37,vert.x-eventloop-thread-0,5,main]
   [otel.javaagent 2025-12-26 14:51:14:142 +0700] [vert.x-eventloop-thread-0] INFO io.opentelemetry.exporter.logging.LoggingSpanExporter - 'GET /test/vertx/virtualThread/executorService' : 1e8f7177116996ba765e9d2adb03569d dd847ba43a683a7a SERVER [tracer: io.opentelemetry.netty-4.1:2.22.0-alpha] AttributesMap{data={network.peer.port=64004, url.scheme=http, thread.name=vert.x-eventloop-thread-0, user_agent.original=ApacheBench/2.3, network.protocol.version=1, server.port=8080, http.response.status_code=200, thread.id=37, url.path=/test/vertx/virtualThread/executorService, server.address=localhost, client.address=0:0:0:0:0:0:0:1, network.peer.address=0:0:0:0:0:0:0:1, http.route=/test/vertx/virtualThread/executorService, http.request.method=GET}, capacity=128, totalAddedValues=14}
   2025-12-26 14:51:14.159 INFO  [virtual-82] mylog.VirtualThreadExecutorService - traceId=11bc13eeeccfdba846c7ef5cc59cb735 spanId=32ccda64e731e1aa - VT THREAD3: VirtualThread[#82]/runnable@ForkJoinPool-1-worker-3
   2025-12-26 14:51:14.159 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecutorService - traceId=11bc13eeeccfdba846c7ef5cc59cb735 spanId=32ccda64e731e1aa - VT THREAD4: Thread[#37,vert.x-eventloop-thread-0,5,main]
   [otel.javaagent 2025-12-26 14:51:14:160 +0700] [vert.x-eventloop-thread-0] INFO io.opentelemetry.exporter.logging.LoggingSpanExporter - 'GET /test/vertx/virtualThread/executorService' : 11bc13eeeccfdba846c7ef5cc59cb735 32ccda64e731e1aa SERVER [tracer: io.opentelemetry.netty-4.1:2.22.0-alpha] AttributesMap{data={network.peer.port=64006, url.scheme=http, thread.name=vert.x-eventloop-thread-0, user_agent.original=ApacheBench/2.3, network.protocol.version=1, server.port=8080, http.response.status_code=200, thread.id=37, url.path=/test/vertx/virtualThread/executorService, server.address=localhost, client.address=0:0:0:0:0:0:0:1, network.peer.address=0:0:0:0:0:0:0:1, http.route=/test/vertx/virtualThread/executorService, http.request.method=GET}, capacity=128, totalAddedValues=14}
   2025-12-26 14:51:14.164 INFO  [virtual-80] mylog.VirtualThreadExecutorService - traceId=2754a5543ca5ad96556ba7a354850af5 spanId=9256b79e924cbd7f - VT THREAD3: VirtualThread[#80]/runnable@ForkJoinPool-1-worker-5
   2025-12-26 14:51:14.164 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecutorService - traceId=2754a5543ca5ad96556ba7a354850af5 spanId=9256b79e924cbd7f - VT THREAD4: Thread[#37,vert.x-eventloop-thread-0,5,main]
   [otel.javaagent 2025-12-26 14:51:14:165 +0700] [vert.x-eventloop-thread-0] INFO io.opentelemetry.exporter.logging.LoggingSpanExporter - 'GET /test/vertx/virtualThread/executorService' : 2754a5543ca5ad96556ba7a354850af5 9256b79e924cbd7f SERVER [tracer: io.opentelemetry.netty-4.1:2.22.0-alpha] AttributesMap{data={network.peer.port=64005, url.scheme=http, thread.name=vert.x-eventloop-thread-0, user_agent.original=ApacheBench/2.3, network.protocol.version=1, server.port=8080, http.response.status_code=200, thread.id=37, url.path=/test/vertx/virtualThread/executorService, server.address=localhost, client.address=0:0:0:0:0:0:0:1, network.peer.address=0:0:0:0:0:0:0:1, http.route=/test/vertx/virtualThread/executorService, http.request.method=GET}, capacity=128, totalAddedValues=14}
   2025-12-26 14:51:14.324 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - Before cleanup stats (total=5, active=0, idle=5, waiting=0)
   2025-12-26 14:51:14.324 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - After cleanup  stats (total=5, active=0, idle=5, waiting=0)
   2025-12-26 14:51:14.324 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - Fill pool skipped, pool is at sufficient level.
   2025-12-26 14:51:44.328 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - Before cleanup stats (total=5, active=0, idle=5, waiting=0)
   2025-12-26 14:51:44.330 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - After cleanup  stats (total=5, active=0, idle=5, waiting=0)
   2025-12-26 14:51:44.330 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - Fill pool skipped, pool is at sufficient level.
   2025-12-26 14:52:14.338 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - Before cleanup stats (total=5, active=0, idle=5, waiting=0)
   2025-12-26 14:52:14.342 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - After cleanup  stats (total=5, active=0, idle=5, waiting=0)
   2025-12-26 14:52:14.343 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - Fill pool skipped, pool is at sufficient level.
   2025-12-26 14:52:44.346 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - Before cleanup stats (total=5, active=0, idle=5, waiting=0)
   2025-12-26 14:52:44.347 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - After cleanup  stats (total=5, active=0, idle=5, waiting=0)
   2025-12-26 14:52:44.347 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - Fill pool skipped, pool is at sufficient level.
   2025-12-26 14:53:14.352 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - Before cleanup stats (total=5, active=0, idle=5, waiting=0)
   2025-12-26 14:53:14.354 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - After cleanup  stats (total=5, active=0, idle=5, waiting=0)
   2025-12-26 14:53:14.354 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - Fill pool skipped, pool is at sufficient level.
   2025-12-26 14:53:44.360 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - Before cleanup stats (total=5, active=0, idle=5, waiting=0)
   2025-12-26 14:53:44.361 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - After cleanup  stats (total=5, active=0, idle=5, waiting=0)
   2025-12-26 14:53:44.361 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - Fill pool skipped, pool is at sufficient level.
   2025-12-26 14:54:14.367 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - Before cleanup stats (total=5, active=0, idle=5, waiting=0)
   2025-12-26 14:54:14.371 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - After cleanup  stats (total=5, active=0, idle=5, waiting=0)
   2025-12-26 14:54:14.371 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - Fill pool skipped, pool is at sufficient level.
   2025-12-26 14:54:44.373 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - Before cleanup stats (total=5, active=0, idle=5, waiting=0)
   2025-12-26 14:54:44.375 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - After cleanup  stats (total=5, active=0, idle=5, waiting=0)
   2025-12-26 14:54:44.375 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - Fill pool skipped, pool is at sufficient level.
   
   
   
   ab -n 10 -c 5 http://localhost:8080/test/vertx/virtualThread/executeBlocking  
   2025-12-26 14:55:13.497 INFO  [vert.x-eventloop-thread-0] mylog.id.sekawan.point.MainVerticle - traceId=68cf77286371622fbec4f0625d3abdb2 spanId=ef0453969db40650 - method=GET | url=http://localhost:8080/test/vertx/virtualThread/executeBlocking | body=null
   2025-12-26 14:55:13.500 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecuteBlocking - traceId=68cf77286371622fbec4f0625d3abdb2 spanId=ef0453969db40650 - VT THREAD1: Thread[#37,vert.x-eventloop-thread-0,5,main]
   2025-12-26 14:55:13.514 INFO  [my-worker-pool-0] mylog.VirtualThreadExecuteBlocking - traceId=68cf77286371622fbec4f0625d3abdb2 spanId=ef0453969db40650 - VT THREAD2: Thread[#84,my-worker-pool-0,5,main]
   2025-12-26 14:55:14.220 INFO  [virtual-85] mylog.VirtualThreadExecuteBlocking - traceId=68cf77286371622fbec4f0625d3abdb2 spanId=ef0453969db40650 - VT THREAD3: VirtualThread[#85]/runnable@ForkJoinPool-1-worker-7
   2025-12-26 14:55:14.221 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecuteBlocking - traceId=68cf77286371622fbec4f0625d3abdb2 spanId=ef0453969db40650 - VT THREAD4: Thread[#37,vert.x-eventloop-thread-0,5,main]
   [otel.javaagent 2025-12-26 14:55:14:222 +0700] [vert.x-eventloop-thread-0] INFO io.opentelemetry.exporter.logging.LoggingSpanExporter - 'GET /test/vertx/virtualThread/executeBlocking' : 68cf77286371622fbec4f0625d3abdb2 ef0453969db40650 SERVER [tracer: io.opentelemetry.netty-4.1:2.22.0-alpha] AttributesMap{data={network.peer.port=64021, url.scheme=http, thread.name=vert.x-eventloop-thread-0, user_agent.original=ApacheBench/2.3, network.protocol.version=1, server.port=8080, http.response.status_code=200, thread.id=37, url.path=/test/vertx/virtualThread/executeBlocking, server.address=localhost, client.address=0:0:0:0:0:0:0:1, network.peer.address=0:0:0:0:0:0:0:1, http.route=/test/vertx/virtualThread/executeBlocking, http.request.method=GET}, capacity=128, totalAddedValues=14}
   2025-12-26 14:55:14.223 INFO  [vert.x-eventloop-thread-0] mylog.id.sekawan.point.MainVerticle - traceId=c3627020a978463187b802bb6a5e0ccc spanId=1776cda6aa7be962 - method=GET | url=http://localhost:8080/test/vertx/virtualThread/executeBlocking | body=null
   2025-12-26 14:55:14.224 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecuteBlocking - traceId=c3627020a978463187b802bb6a5e0ccc spanId=1776cda6aa7be962 - VT THREAD1: Thread[#37,vert.x-eventloop-thread-0,5,main]
   2025-12-26 14:55:14.224 INFO  [my-worker-pool-1] mylog.VirtualThreadExecuteBlocking - traceId=c3627020a978463187b802bb6a5e0ccc spanId=1776cda6aa7be962 - VT THREAD2: Thread[#88,my-worker-pool-1,5,main]
   2025-12-26 14:55:14.224 INFO  [vert.x-eventloop-thread-0] mylog.id.sekawan.point.MainVerticle - traceId=d13d1d0ba7cd0de403bf3fad05e2227e spanId=86190acfee18a035 - method=GET | url=http://localhost:8080/test/vertx/virtualThread/executeBlocking | body=null
   2025-12-26 14:55:14.224 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecuteBlocking - traceId=d13d1d0ba7cd0de403bf3fad05e2227e spanId=86190acfee18a035 - VT THREAD1: Thread[#37,vert.x-eventloop-thread-0,5,main]
   2025-12-26 14:55:14.225 INFO  [my-worker-pool-2] mylog.VirtualThreadExecuteBlocking - traceId=d13d1d0ba7cd0de403bf3fad05e2227e spanId=86190acfee18a035 - VT THREAD2: Thread[#90,my-worker-pool-2,5,main]
   2025-12-26 14:55:14.225 INFO  [vert.x-eventloop-thread-0] mylog.id.sekawan.point.MainVerticle - traceId=b57a97eb10bf67b17b6ae3771484230a spanId=3c984559128d81a3 - method=GET | url=http://localhost:8080/test/vertx/virtualThread/executeBlocking | body=null
   2025-12-26 14:55:14.225 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecuteBlocking - traceId=b57a97eb10bf67b17b6ae3771484230a spanId=3c984559128d81a3 - VT THREAD1: Thread[#37,vert.x-eventloop-thread-0,5,main]
   2025-12-26 14:55:14.226 INFO  [my-worker-pool-3] mylog.VirtualThreadExecuteBlocking - traceId=b57a97eb10bf67b17b6ae3771484230a spanId=3c984559128d81a3 - VT THREAD2: Thread[#92,my-worker-pool-3,5,main]
   2025-12-26 14:55:14.226 INFO  [vert.x-eventloop-thread-0] mylog.id.sekawan.point.MainVerticle - traceId=53219a126d4540238129d21446c0b007 spanId=c87ec6c4e028560e - method=GET | url=http://localhost:8080/test/vertx/virtualThread/executeBlocking | body=null
   2025-12-26 14:55:14.226 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecuteBlocking - traceId=53219a126d4540238129d21446c0b007 spanId=c87ec6c4e028560e - VT THREAD1: Thread[#37,vert.x-eventloop-thread-0,5,main]
   2025-12-26 14:55:14.227 INFO  [my-worker-pool-4] mylog.VirtualThreadExecuteBlocking - traceId=53219a126d4540238129d21446c0b007 spanId=c87ec6c4e028560e - VT THREAD2: Thread[#95,my-worker-pool-4,5,main]
   2025-12-26 14:55:14.227 INFO  [vert.x-eventloop-thread-0] mylog.id.sekawan.point.MainVerticle - traceId=0d649d214ef3200cb4de7c30b1d4125f spanId=9025554fa2e3557e - method=GET | url=http://localhost:8080/test/vertx/virtualThread/executeBlocking | body=null
   2025-12-26 14:55:14.228 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecuteBlocking - traceId=0d649d214ef3200cb4de7c30b1d4125f spanId=9025554fa2e3557e - VT THREAD1: Thread[#37,vert.x-eventloop-thread-0,5,main]
   2025-12-26 14:55:14.228 INFO  [my-worker-pool-5] mylog.VirtualThreadExecuteBlocking - traceId=0d649d214ef3200cb4de7c30b1d4125f spanId=9025554fa2e3557e - VT THREAD2: Thread[#98,my-worker-pool-5,5,main]
   2025-12-26 14:55:14.381 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - Before cleanup stats (total=5, active=0, idle=5, waiting=0)
   2025-12-26 14:55:14.381 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - After cleanup  stats (total=5, active=0, idle=5, waiting=0)
   2025-12-26 14:55:14.382 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - Fill pool skipped, pool is at sufficient level.
   2025-12-26 14:55:15.045 INFO  [virtual-89] mylog.VirtualThreadExecuteBlocking - traceId=c3627020a978463187b802bb6a5e0ccc spanId=1776cda6aa7be962 - VT THREAD3: VirtualThread[#89]/runnable@ForkJoinPool-1-worker-7
   2025-12-26 14:55:15.046 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecuteBlocking - traceId=c3627020a978463187b802bb6a5e0ccc spanId=1776cda6aa7be962 - VT THREAD4: Thread[#37,vert.x-eventloop-thread-0,5,main]
   [otel.javaagent 2025-12-26 14:55:15:048 +0700] [vert.x-eventloop-thread-0] INFO io.opentelemetry.exporter.logging.LoggingSpanExporter - 'GET /test/vertx/virtualThread/executeBlocking' : c3627020a978463187b802bb6a5e0ccc 1776cda6aa7be962 SERVER [tracer: io.opentelemetry.netty-4.1:2.22.0-alpha] AttributesMap{data={network.peer.port=64022, url.scheme=http, thread.name=vert.x-eventloop-thread-0, user_agent.original=ApacheBench/2.3, network.protocol.version=1, server.port=8080, http.response.status_code=200, thread.id=37, url.path=/test/vertx/virtualThread/executeBlocking, server.address=localhost, client.address=0:0:0:0:0:0:0:1, network.peer.address=0:0:0:0:0:0:0:1, http.route=/test/vertx/virtualThread/executeBlocking, http.request.method=GET}, capacity=128, totalAddedValues=14}
   2025-12-26 14:55:15.051 INFO  [vert.x-eventloop-thread-0] mylog.id.sekawan.point.MainVerticle - traceId=b5a17dd70d0406db2468e02cd7d82fd7 spanId=d5960c8c6b273078 - method=GET | url=http://localhost:8080/test/vertx/virtualThread/executeBlocking | body=null
   2025-12-26 14:55:15.051 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecuteBlocking - traceId=b5a17dd70d0406db2468e02cd7d82fd7 spanId=d5960c8c6b273078 - VT THREAD1: Thread[#37,vert.x-eventloop-thread-0,5,main]
   2025-12-26 14:55:15.051 INFO  [my-worker-pool-6] mylog.VirtualThreadExecuteBlocking - traceId=b5a17dd70d0406db2468e02cd7d82fd7 spanId=d5960c8c6b273078 - VT THREAD2: Thread[#102,my-worker-pool-6,5,main]
   2025-12-26 14:55:15.054 INFO  [virtual-96] mylog.VirtualThreadExecuteBlocking - traceId=53219a126d4540238129d21446c0b007 spanId=c87ec6c4e028560e - VT THREAD3: VirtualThread[#96]/runnable@ForkJoinPool-1-worker-10
   2025-12-26 14:55:15.054 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecuteBlocking - traceId=53219a126d4540238129d21446c0b007 spanId=c87ec6c4e028560e - VT THREAD4: Thread[#37,vert.x-eventloop-thread-0,5,main]
   [otel.javaagent 2025-12-26 14:55:15:056 +0700] [vert.x-eventloop-thread-0] INFO io.opentelemetry.exporter.logging.LoggingSpanExporter - 'GET /test/vertx/virtualThread/executeBlocking' : 53219a126d4540238129d21446c0b007 c87ec6c4e028560e SERVER [tracer: io.opentelemetry.netty-4.1:2.22.0-alpha] AttributesMap{data={network.peer.port=64025, url.scheme=http, thread.name=vert.x-eventloop-thread-0, user_agent.original=ApacheBench/2.3, network.protocol.version=1, server.port=8080, http.response.status_code=200, thread.id=37, url.path=/test/vertx/virtualThread/executeBlocking, server.address=localhost, client.address=0:0:0:0:0:0:0:1, network.peer.address=0:0:0:0:0:0:0:1, http.route=/test/vertx/virtualThread/executeBlocking, http.request.method=GET}, capacity=128, totalAddedValues=14}
   2025-12-26 14:55:15.057 INFO  [vert.x-eventloop-thread-0] mylog.id.sekawan.point.MainVerticle - traceId=441ec234ed15278c543e638704422fc2 spanId=8b54a513d9dc6a7c - method=GET | url=http://localhost:8080/test/vertx/virtualThread/executeBlocking | body=null
   2025-12-26 14:55:15.058 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecuteBlocking - traceId=441ec234ed15278c543e638704422fc2 spanId=8b54a513d9dc6a7c - VT THREAD1: Thread[#37,vert.x-eventloop-thread-0,5,main]
   2025-12-26 14:55:15.058 INFO  [my-worker-pool-7] mylog.VirtualThreadExecuteBlocking - traceId=441ec234ed15278c543e638704422fc2 spanId=8b54a513d9dc6a7c - VT THREAD2: Thread[#104,my-worker-pool-7,5,main]
   2025-12-26 14:55:15.061 INFO  [virtual-91] mylog.VirtualThreadExecuteBlocking - traceId=d13d1d0ba7cd0de403bf3fad05e2227e spanId=86190acfee18a035 - VT THREAD3: VirtualThread[#91]/runnable@ForkJoinPool-1-worker-8
   2025-12-26 14:55:15.061 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecuteBlocking - traceId=d13d1d0ba7cd0de403bf3fad05e2227e spanId=86190acfee18a035 - VT THREAD4: Thread[#37,vert.x-eventloop-thread-0,5,main]
   [otel.javaagent 2025-12-26 14:55:15:061 +0700] [vert.x-eventloop-thread-0] INFO io.opentelemetry.exporter.logging.LoggingSpanExporter - 'GET /test/vertx/virtualThread/executeBlocking' : d13d1d0ba7cd0de403bf3fad05e2227e 86190acfee18a035 SERVER [tracer: io.opentelemetry.netty-4.1:2.22.0-alpha] AttributesMap{data={network.peer.port=64023, url.scheme=http, thread.name=vert.x-eventloop-thread-0, user_agent.original=ApacheBench/2.3, network.protocol.version=1, server.port=8080, http.response.status_code=200, thread.id=37, url.path=/test/vertx/virtualThread/executeBlocking, server.address=localhost, client.address=0:0:0:0:0:0:0:1, network.peer.address=0:0:0:0:0:0:0:1, http.route=/test/vertx/virtualThread/executeBlocking, http.request.method=GET}, capacity=128, totalAddedValues=14}
   2025-12-26 14:55:15.066 INFO  [vert.x-eventloop-thread-0] mylog.id.sekawan.point.MainVerticle - traceId=e1bf9a41c7c77c73a151347e0ed5be22 spanId=b67c1e4affef483a - method=GET | url=http://localhost:8080/test/vertx/virtualThread/executeBlocking | body=null
   2025-12-26 14:55:15.066 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecuteBlocking - traceId=e1bf9a41c7c77c73a151347e0ed5be22 spanId=b67c1e4affef483a - VT THREAD1: Thread[#37,vert.x-eventloop-thread-0,5,main]
   2025-12-26 14:55:15.066 INFO  [my-worker-pool-8] mylog.VirtualThreadExecuteBlocking - traceId=e1bf9a41c7c77c73a151347e0ed5be22 spanId=b67c1e4affef483a - VT THREAD2: Thread[#106,my-worker-pool-8,5,main]
   2025-12-26 14:55:15.067 INFO  [virtual-93] mylog.VirtualThreadExecuteBlocking - traceId=b57a97eb10bf67b17b6ae3771484230a spanId=3c984559128d81a3 - VT THREAD3: VirtualThread[#93]/runnable@ForkJoinPool-1-worker-9
   2025-12-26 14:55:15.067 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecuteBlocking - traceId=b57a97eb10bf67b17b6ae3771484230a spanId=3c984559128d81a3 - VT THREAD4: Thread[#37,vert.x-eventloop-thread-0,5,main]
   [otel.javaagent 2025-12-26 14:55:15:068 +0700] [vert.x-eventloop-thread-0] INFO io.opentelemetry.exporter.logging.LoggingSpanExporter - 'GET /test/vertx/virtualThread/executeBlocking' : b57a97eb10bf67b17b6ae3771484230a 3c984559128d81a3 SERVER [tracer: io.opentelemetry.netty-4.1:2.22.0-alpha] AttributesMap{data={network.peer.port=64024, url.scheme=http, thread.name=vert.x-eventloop-thread-0, user_agent.original=ApacheBench/2.3, network.protocol.version=1, server.port=8080, http.response.status_code=200, thread.id=37, url.path=/test/vertx/virtualThread/executeBlocking, server.address=localhost, client.address=0:0:0:0:0:0:0:1, network.peer.address=0:0:0:0:0:0:0:1, http.route=/test/vertx/virtualThread/executeBlocking, http.request.method=GET}, capacity=128, totalAddedValues=14}
   2025-12-26 14:55:15.070 INFO  [vert.x-eventloop-thread-0] mylog.id.sekawan.point.MainVerticle - traceId=3451d6125cc6af1d1713918d7435931a spanId=6d9403d8cdb75db7 - method=GET | url=http://localhost:8080/test/vertx/virtualThread/executeBlocking | body=null
   2025-12-26 14:55:15.070 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecuteBlocking - traceId=3451d6125cc6af1d1713918d7435931a spanId=6d9403d8cdb75db7 - VT THREAD1: Thread[#37,vert.x-eventloop-thread-0,5,main]
   2025-12-26 14:55:15.070 INFO  [my-worker-pool-9] mylog.VirtualThreadExecuteBlocking - traceId=3451d6125cc6af1d1713918d7435931a spanId=6d9403d8cdb75db7 - VT THREAD2: Thread[#108,my-worker-pool-9,5,main]
   2025-12-26 14:55:15.074 INFO  [virtual-99] mylog.VirtualThreadExecuteBlocking - traceId=0d649d214ef3200cb4de7c30b1d4125f spanId=9025554fa2e3557e - VT THREAD3: VirtualThread[#99]/runnable@ForkJoinPool-1-worker-11
   2025-12-26 14:55:15.075 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecuteBlocking - traceId=0d649d214ef3200cb4de7c30b1d4125f spanId=9025554fa2e3557e - VT THREAD4: Thread[#37,vert.x-eventloop-thread-0,5,main]
   [otel.javaagent 2025-12-26 14:55:15:077 +0700] [vert.x-eventloop-thread-0] INFO io.opentelemetry.exporter.logging.LoggingSpanExporter - 'GET /test/vertx/virtualThread/executeBlocking' : 0d649d214ef3200cb4de7c30b1d4125f 9025554fa2e3557e SERVER [tracer: io.opentelemetry.netty-4.1:2.22.0-alpha] AttributesMap{data={network.peer.port=64026, url.scheme=http, thread.name=vert.x-eventloop-thread-0, user_agent.original=ApacheBench/2.3, network.protocol.version=1, server.port=8080, http.response.status_code=200, thread.id=37, url.path=/test/vertx/virtualThread/executeBlocking, server.address=localhost, client.address=0:0:0:0:0:0:0:1, network.peer.address=0:0:0:0:0:0:0:1, http.route=/test/vertx/virtualThread/executeBlocking, http.request.method=GET}, capacity=128, totalAddedValues=14}
   2025-12-26 14:55:15.797 INFO  [virtual-105] mylog.VirtualThreadExecuteBlocking - traceId=441ec234ed15278c543e638704422fc2 spanId=8b54a513d9dc6a7c - VT THREAD3: VirtualThread[#105]/runnable@ForkJoinPool-1-worker-12
   2025-12-26 14:55:15.798 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecuteBlocking - traceId=441ec234ed15278c543e638704422fc2 spanId=8b54a513d9dc6a7c - VT THREAD4: Thread[#37,vert.x-eventloop-thread-0,5,main]
   [otel.javaagent 2025-12-26 14:55:15:799 +0700] [vert.x-eventloop-thread-0] INFO io.opentelemetry.exporter.logging.LoggingSpanExporter - 'GET /test/vertx/virtualThread/executeBlocking' : 441ec234ed15278c543e638704422fc2 8b54a513d9dc6a7c SERVER [tracer: io.opentelemetry.netty-4.1:2.22.0-alpha] AttributesMap{data={network.peer.port=64028, url.scheme=http, thread.name=vert.x-eventloop-thread-0, user_agent.original=ApacheBench/2.3, network.protocol.version=1, server.port=8080, http.response.status_code=200, thread.id=37, url.path=/test/vertx/virtualThread/executeBlocking, server.address=localhost, client.address=0:0:0:0:0:0:0:1, network.peer.address=0:0:0:0:0:0:0:1, http.route=/test/vertx/virtualThread/executeBlocking, http.request.method=GET}, capacity=128, totalAddedValues=14}
   2025-12-26 14:55:15.804 INFO  [virtual-103] mylog.VirtualThreadExecuteBlocking - traceId=b5a17dd70d0406db2468e02cd7d82fd7 spanId=d5960c8c6b273078 - VT THREAD3: VirtualThread[#103]/runnable@ForkJoinPool-1-worker-7
   2025-12-26 14:55:15.805 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecuteBlocking - traceId=b5a17dd70d0406db2468e02cd7d82fd7 spanId=d5960c8c6b273078 - VT THREAD4: Thread[#37,vert.x-eventloop-thread-0,5,main]
   [otel.javaagent 2025-12-26 14:55:15:806 +0700] [vert.x-eventloop-thread-0] INFO io.opentelemetry.exporter.logging.LoggingSpanExporter - 'GET /test/vertx/virtualThread/executeBlocking' : b5a17dd70d0406db2468e02cd7d82fd7 d5960c8c6b273078 SERVER [tracer: io.opentelemetry.netty-4.1:2.22.0-alpha] AttributesMap{data={network.peer.port=64027, url.scheme=http, thread.name=vert.x-eventloop-thread-0, user_agent.original=ApacheBench/2.3, network.protocol.version=1, server.port=8080, http.response.status_code=200, thread.id=37, url.path=/test/vertx/virtualThread/executeBlocking, server.address=localhost, client.address=0:0:0:0:0:0:0:1, network.peer.address=0:0:0:0:0:0:0:1, http.route=/test/vertx/virtualThread/executeBlocking, http.request.method=GET}, capacity=128, totalAddedValues=14}
   2025-12-26 14:55:15.807 INFO  [virtual-107] mylog.VirtualThreadExecuteBlocking - traceId=e1bf9a41c7c77c73a151347e0ed5be22 spanId=b67c1e4affef483a - VT THREAD3: VirtualThread[#107]/runnable@ForkJoinPool-1-worker-8
   2025-12-26 14:55:15.807 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecuteBlocking - traceId=e1bf9a41c7c77c73a151347e0ed5be22 spanId=b67c1e4affef483a - VT THREAD4: Thread[#37,vert.x-eventloop-thread-0,5,main]
   [otel.javaagent 2025-12-26 14:55:15:808 +0700] [vert.x-eventloop-thread-0] INFO io.opentelemetry.exporter.logging.LoggingSpanExporter - 'GET /test/vertx/virtualThread/executeBlocking' : e1bf9a41c7c77c73a151347e0ed5be22 b67c1e4affef483a SERVER [tracer: io.opentelemetry.netty-4.1:2.22.0-alpha] AttributesMap{data={network.peer.port=64029, url.scheme=http, thread.name=vert.x-eventloop-thread-0, user_agent.original=ApacheBench/2.3, network.protocol.version=1, server.port=8080, http.response.status_code=200, thread.id=37, url.path=/test/vertx/virtualThread/executeBlocking, server.address=localhost, client.address=0:0:0:0:0:0:0:1, network.peer.address=0:0:0:0:0:0:0:1, http.route=/test/vertx/virtualThread/executeBlocking, http.request.method=GET}, capacity=128, totalAddedValues=14}
   2025-12-26 14:55:15.820 INFO  [virtual-109] mylog.VirtualThreadExecuteBlocking - traceId=3451d6125cc6af1d1713918d7435931a spanId=6d9403d8cdb75db7 - VT THREAD3: VirtualThread[#109]/runnable@ForkJoinPool-1-worker-9
   2025-12-26 14:55:15.821 INFO  [vert.x-eventloop-thread-0] mylog.VirtualThreadExecuteBlocking - traceId=3451d6125cc6af1d1713918d7435931a spanId=6d9403d8cdb75db7 - VT THREAD4: Thread[#37,vert.x-eventloop-thread-0,5,main]
   [otel.javaagent 2025-12-26 14:55:15:821 +0700] [vert.x-eventloop-thread-0] INFO io.opentelemetry.exporter.logging.LoggingSpanExporter - 'GET /test/vertx/virtualThread/executeBlocking' : 3451d6125cc6af1d1713918d7435931a 6d9403d8cdb75db7 SERVER [tracer: io.opentelemetry.netty-4.1:2.22.0-alpha] AttributesMap{data={network.peer.port=64030, url.scheme=http, thread.name=vert.x-eventloop-thread-0, user_agent.original=ApacheBench/2.3, network.protocol.version=1, server.port=8080, http.response.status_code=200, thread.id=37, url.path=/test/vertx/virtualThread/executeBlocking, server.address=localhost, client.address=0:0:0:0:0:0:0:1, network.peer.address=0:0:0:0:0:0:0:1, http.route=/test/vertx/virtualThread/executeBlocking, http.request.method=GET}, capacity=128, totalAddedValues=14}
   2025-12-26 14:55:44.387 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - Before cleanup stats (total=5, active=0, idle=5, waiting=0)
   2025-12-26 14:55:44.397 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - After cleanup  stats (total=5, active=0, idle=5, waiting=0)
   2025-12-26 14:55:44.397 DEBUG [HikariPool-1 housekeeper] com.zaxxer.hikari.pool.HikariPool - traceId= spanId= - HikariPool-1 - Fill pool skipped, pool is at sufficient level.

   ```
6. ab -n 4 -c 2 http://localhost:8080/test/vertx/virtualThread/organic/repository