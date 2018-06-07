======================================================
vert.x with opencensus and jaerger trace demo (groovy)
======================================================

This project shows how to use vertx with opencensus in groovy.
Software exports tracers to Jaeger by JaegerTraceExporter
It works also with Zipkin (tested)

Flow

   1. gv1 generate periodicaly a message and send them to the event bus
   2. gv2 recieve the messages and do some job
   3. publish the result to the event bus back
   4. g1 recieve the message and deliver(print out and log) it to customer. After that finish the span scope


Software requirements:

    java version "1.8.0_161"
    Groovy Version: 2.4.15 JVM
    Jaeger tracing or Zipkin
        
        https://www.jaegertracing.io/docs/getting-started/
        https://github.com/openzipkin/docker-zipkin/blob/master/README.md
        

How to start
============

Start Jaeger like:

:: 

    $ docker run -d -e \
        COLLECTOR_ZIPKIN_HTTP_PORT=9411 \
        -p 5775:5775/udp \
        -p 6831:6831/udp \
        -p 6832:6832/udp \
        -p 5778:5778 \
        -p 16686:16686 \
        -p 14268:14268 \
        -p 9411:9411 \
        jaegertracing/all-in-one:latest


Run the execution verticle

::

   $ cd gv2
   $ ./gradlew run

   
Run the generator verticle

::

   $ cd gv1
   $ ./gredlew run

have fun.

Checkout the expected result in the doc folder 
