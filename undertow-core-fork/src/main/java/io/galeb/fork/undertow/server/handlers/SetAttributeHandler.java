/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.galeb.fork.undertow.server.handlers;

import io.galeb.fork.undertow.attribute.ExchangeAttribute;
import io.galeb.fork.undertow.attribute.ExchangeAttributeParser;
import io.galeb.fork.undertow.attribute.ExchangeAttributes;
import io.galeb.fork.undertow.server.HttpHandler;
import io.galeb.fork.undertow.server.HttpServerExchange;

/**
 * Handler that can set an arbitrary attribute on the exchange. Both the attribute and the
 * value to set are expressed as exchange attributes.
 *
 *
 * @author Stuart Douglas
 */
public class SetAttributeHandler implements HttpHandler {

    private final HttpHandler next;
    private final ExchangeAttribute attribute;
    private final ExchangeAttribute value;

    public SetAttributeHandler(HttpHandler next, ExchangeAttribute attribute, ExchangeAttribute value) {
        this.next = next;
        this.attribute = attribute;
        this.value = value;
    }

    public SetAttributeHandler(HttpHandler next, final String attribute, final String value) {
        this.next = next;
        ExchangeAttributeParser parser = ExchangeAttributes.parser(getClass().getClassLoader());
        this.attribute = parser.parseSingleToken(attribute);
        this.value = parser.parse(value);
    }

    public SetAttributeHandler(HttpHandler next, final String attribute, final String value, final ClassLoader classLoader) {
        this.next = next;
        ExchangeAttributeParser parser = ExchangeAttributes.parser(classLoader);
        this.attribute = parser.parseSingleToken(attribute);
        this.value = parser.parse(value);
    }
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        attribute.writeAttribute(exchange, value.readAttribute(exchange));
        next.handleRequest(exchange);
    }
}
