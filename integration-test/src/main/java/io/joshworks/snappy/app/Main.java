package io.joshworks.snappy.app;

import io.joshworks.snappy.Config;
import io.joshworks.snappy.SnappyServer;
import io.joshworks.snappy.metric.Metrics;


/**
 * Created by josh on 3/10/17.
 */
public class Main {

    public static void main(String[] args) {
        SnappyServer server = new SnappyServer(new Config().enableTracer().enableHttpMetrics());

        server.get("/echo", (exchange) -> {
                    exchange.response().send("{}");
                    Metrics.addMetric("Yolo", 1);
                });

        server.start();
    }
}