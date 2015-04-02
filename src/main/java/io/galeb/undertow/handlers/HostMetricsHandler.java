/*
 *
 */

package io.galeb.undertow.handlers;


import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import io.undertow.server.ExchangeCompletionListener;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.builder.HandlerBuilder;
import io.undertow.util.HeaderValues;

public class HostMetricsHandler implements HttpHandler {

    private final HttpHandler next;
    private final ExchangeCompletionListener exchangeCompletionListener = new MetricCompletionListener();

    private final AtomicBoolean enabled = new AtomicBoolean(true);

    public HostMetricsHandler(final HttpHandler next) {
        this.next = next;
    }

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {
        exchange.addExchangeCompleteListener(exchangeCompletionListener);
        next.handleRequest(exchange);
    }

    private class MetricCompletionListener implements ExchangeCompletionListener {
        @Override
        public void exchangeEvent(final HttpServerExchange exchange, final NextListener nextListener) {
            try {
                if (enabled.get()) {

                    HeaderValues host = exchange.getRequestHeaders().get("X-Proxy-Host");
                    if (host==null) {
                        return;
                    }
                    showMetric("Virtual Host", exchange.getHostName());
                    showMetric("Real Host", host.getFirst());
                    showMetric("Http Status", exchange.getResponseCode());
                    HeaderValues startTime = exchange.getRequestHeaders().get("X-Start-Time");
                    if (startTime!=null) {
                        showMetric("Request Time", System.nanoTime()/1000000 - Long.valueOf(startTime.getFirst())/1000000);
                    }
                }
            } finally {
                nextListener.proceed();
            }
        }
    }

    private void showMetric(String metric, String dataMetric) {
        System.out.println(String.format("%s: %s", metric, dataMetric));
    }

    private void showMetric(String metric, int dataMetric) {
        System.out.println(String.format("%s: %d", metric, dataMetric));
    }

    private void showMetric(String metric, long dataMetric) {
        System.out.println(String.format("%s: %d", metric, dataMetric));
    }

    @SuppressWarnings("unused")
    private void showMetric(String metric, double dataMetric) {
        System.out.println(String.format("%s: %f", metric, dataMetric));
    }

    public synchronized HttpHandler enabled(boolean flag) {
        enabled.set(flag);
        return this;
    }

    @Override
    public String toString() {
        return "HostMetricsHandler{}";
    }



    public static class Builder implements HandlerBuilder {

        @Override
        public String name() {
            return "host-metrics";
        }

        @Override
        public Map<String, Class<?>> parameters() {
            return Collections.emptyMap();
        }

        @Override
        public Set<String> requiredParameters() {
            return Collections.emptySet();
        }

        @Override
        public String defaultParameter() {
            return "";
        }

        @Override
        public HandlerWrapper build(Map<String, Object> config) {
            return new Wrapper();
        }

    }

    private static class Wrapper implements HandlerWrapper {

        @Override
        public HttpHandler wrap(HttpHandler handler) {
            return new HostMetricsHandler(handler);
        }
    }
}
