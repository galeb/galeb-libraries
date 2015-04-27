/*
 *
 */

package io.galeb.undertow.handlers;


import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class MonitorHeadersHandler implements HttpHandler {

    private final HttpHandler next;
    private final HeaderMetricsListener headerMetricsListener = new HeaderMetricsListener();

    public MonitorHeadersHandler(final HttpHandler next) {
        this.next = next;
    }

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {
        exchange.addExchangeCompleteListener(headerMetricsListener);
        next.handleRequest(exchange);
    }

}
