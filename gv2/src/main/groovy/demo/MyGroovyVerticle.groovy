package demo

import block.Pi
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
    def mmm
    def eb = vertx.eventBus()
    JaegerTraceExporter.createAndRegister("http://127.0.0.1:14268/api/traces", "m1");
    LoggingTraceExporter.register();
    first()

    def consumer = eb.consumer("somebody.do.job")
    consumer.handler({ message ->
      println("I have received a message: ${message.body().toString()}")
      SpanContext spanContext = textFormat.extract(message.body(), GETTER);
      Span span = tracer.spanBuilderWithRemoteParent("message recieved", spanContext).
          setRecordEvents(true).setSampler(Samplers.alwaysSample()).startSpan();
      // do something
      Scope scope = tracer.withSpan(span)
      scope.withCloseable {
       tracer.getCurrentSpan().addAnnotation("Start work");
        try {
          Thread.sleep(new Random().nextInt(1000)+1);
        }
          catch (InterruptedException e) {
        }
        Span span2 = tracer.spanBuilderWithRemoteParent("start difficult process", span.context).
            setRecordEvents(true).setSampler(Samplers.alwaysSample()).startSpan();
        Scope scope2 = tracer.withSpan(span2)
        scope2.withCloseable {
          def pi1 = new Pi().calculate(5000)
          tracer.getCurrentSpan().addAnnotation(pi1 as String);
        span2.end()
        tracer.getCurrentSpan().addAnnotation("End of proccess");
      span.end();
      //Independent process
      Span span3 = tracer.spanBuilderWithRemoteParent("more one diffucult", span.context).setRecordEvents(true).setSampler(Samplers.alwaysSample()).startSpan();
      Scope scope3 = tracer.withSpan(span3)
      scope3.withCloseable {
        def pi2 = new Pi().calculate(50000)
        tracer.getCurrentSpan().addAnnotation(pi2 as String);
      span3.end()

      mmm = ['value1': pi1, 'value2': pi2]


      textFormat.inject( tracer.getCurrentSpan().getContext(), mmm, SETTER);
      eb.publish("somebody.deliver.result", mmm)

        } } }

    })
  }

  public void first() {
      Scope ignored = tracer.spanBuilder("root").setRecordEvents(true).setSampler(Samplers.alwaysSample()).startScopedSpan()
      ignored.withCloseable {
        println "RUNING"
        // Do nothing
      }

  }
}