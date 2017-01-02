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

package io.galeb.fork.undertow.server.handlers.builder;

import io.galeb.fork.undertow.attribute.ExchangeAttribute;
import io.galeb.fork.undertow.attribute.ExchangeAttributes;
import io.galeb.fork.undertow.server.HandlerWrapper;
import io.galeb.fork.undertow.server.HttpHandler;
import io.galeb.fork.undertow.server.handlers.SetAttributeHandler;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author Stuart Douglas
 */
public class RewriteHandlerBuilder implements HandlerBuilder {
    @Override
    public String name() {
        return "rewrite";
    }

    @Override
    public Map<String, Class<?>> parameters() {
        return Collections.<String, Class<?>>singletonMap("value", ExchangeAttribute.class);
    }

    @Override
    public Set<String> requiredParameters() {
        return Collections.singleton("value");
    }

    @Override
    public String defaultParameter() {
        return "value";
    }

    @Override
    public HandlerWrapper build(final Map<String, Object> config) {
        final ExchangeAttribute value = (ExchangeAttribute) config.get("value");

        return new HandlerWrapper() {
            @Override
            public HttpHandler wrap(HttpHandler handler) {
                return new SetAttributeHandler(handler, ExchangeAttributes.relativePath(), value);
            }
        };
    }
}
