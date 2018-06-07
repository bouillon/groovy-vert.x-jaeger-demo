package vav

import io.opencensus.common.Scope;
import io.opencensus.trace.Span;
import io.opencensus.exporter.trace.jaeger.JaegerTraceExporter;
import io.opencensus.exporter.trace.logging.LoggingTraceExporter;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.samplers.Samplers;
import io.opencensus.trace.SpanContext;
import io.opencensus.trace.propagation.TextFormat;
import io.opencensus.trace.propagation.SpanContextParseException
import io.vertx.core.AbstractVerticle
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpServerRequest;
//import io.vertx.core.eventbus.Message


/*vertx.createHttpServer().requestHandler({ req ->
    req.response().putHeader("content-type", "text/html").end("<html><body><h1>Hello from vert.x!</h1></body></html>")
}).listen(8080)*/

class MyGroovyVerticle extends AbstractVerticle {
  private static final Tracer tracer = Tracing.getTracer();

  private static final TextFormat textFormat = Tracing.getPropagationComponent().getB3Format();
  private static final TextFormat.Setter<Map> SETTER = new TextFormat.Setter<Map>() {
    public void put(Map carrier, String key, String value) {
      carrier.put(key, value);
    }
  };
  private static final TextFormat.Getter<Map> GETTER = new TextFormat.Getter<Map>() {
    public String get(Map carrier, String key) {
      return carrier.get(key);
    }
  };

  @Override
  void start() throws Exception {
    def eb = vertx.eventBus()
    JaegerTraceExporter.createAndRegister("http://127.0.0.1:14268/api/traces", "m1");
    LoggingTraceExporter.register();
    first()

    def mmm

    vertx.setPeriodic(5000, { id ->
      // This handler will get called every second
      Scope ignored = tracer.spanBuilder("start INIT process").setRecordEvents(true).setSampler(Samplers.alwaysSample()).startScopedSpan()
      ignored.withCloseable {
        tracer.getCurrentSpan().addAnnotation("start job");
        //HttpRequest<Buffer> b = WebClient.create(vertx).get(secondPort, "localhost", "/");
        //textFormat.inject( tracer.getCurrentSpan().getContext(), b, SETTER);
        mmm = ['a': 'b', 'c': 'd']
        textFormat.inject( tracer.getCurrentSpan().getContext(), mmm, SETTER);
        eb.publish("somebody.do.job", mmm)
        tracer.getCurrentSpan().addAnnotation("sended  message to BUS");
      }

    })


    def consumer = eb.consumer("somebody.deliver.result")
    consumer.handler({ message ->
      println("I have received a message: ${message.body().toString()}")
      SpanContext spanContext = textFormat.extract(message.body(), GETTER);
      System.out.println(spanContext.toString());
      Span span = tracer.spanBuilderWithRemoteParent("second", spanContext).setRecordEvents(true).setSampler(Samplers.alwaysSample()).startSpan();

      Scope ignored = tracer.withSpan(span)
      ignored.withCloseable {
        //
        tracer.getCurrentSpan().addAnnotation("End of Proccess. Result Delivered to the customer" + message.body().toString());
      }
      span.end();


    })




  }


  public void first() {
    //logger.info("Starting the First HTTP server");

      Scope ignored = tracer.spanBuilder("root").setRecordEvents(true).setSampler(Samplers.alwaysSample()).startScopedSpan()
      ignored.withCloseable {
        tracer.getCurrentSpan().addAnnotation("start");
        tracer.getCurrentSpan().addAnnotation("sended");
        println "RUNING"
        //HttpRequest<Buffer> b = WebClient.create(vertx).get(secondPort, "localhost", "/");
        //textFormat.inject( tracer.getCurrentSpan().getContext(), b, SETTER);
      }

  }
}