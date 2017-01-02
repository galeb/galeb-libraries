/*
 * Copyright (c) 2014-2015 Globo.com - ATeam
 * All rights reserved.
 *
 * This source is subject to the Apache License, Version 2.0.
 * Please see the LICENSE file for more information.
 *
 * Authors: See AUTHORS file
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.galeb.undertow.handlers;

import io.galeb.fork.undertow.attribute.ExchangeAttribute;
import io.galeb.fork.undertow.attribute.ExchangeAttributes;
import io.galeb.fork.undertow.attribute.ResponseTimeAttribute;
import io.galeb.fork.undertow.attribute.SubstituteEmptyWrapper;
import io.galeb.fork.undertow.server.ExchangeCompletionListener;
import io.galeb.fork.undertow.server.HttpHandler;
import io.galeb.fork.undertow.server.HttpServerExchange;
import io.galeb.fork.undertow.server.handlers.accesslog.AccessLogReceiver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccessLogExtendedHandler implements HttpHandler, ProcessorLocalStatusCode {

    private static final Logger LOGGER = LogManager.getLogger();

    public static final String REAL_DEST = "#REAL_DEST#";
    public static final String UNKNOWN = "UNKNOWN";

    private final ResponseTimeAttribute responseTimeAttribute = new ResponseTimeAttribute(TimeUnit.MILLISECONDS);
    private final ExchangeCompletionListener exchangeCompletionListener = new AccessLogCompletionListener();
    private final AccessLogReceiver accessLogReceiver;
    private final ExchangeAttribute tokens;
    private final HttpHandler next;
    private int maxRequestTime = Integer.MAX_VALUE - 1;

    public AccessLogExtendedHandler(HttpHandler next,
                                    AccessLogReceiver accessLogReceiver,
                                    String formatString,
                                    ClassLoader classLoader) {
        this.next = next;
        this.accessLogReceiver = accessLogReceiver;
        tokens = ExchangeAttributes.parser(classLoader, new SubstituteEmptyWrapper("-")).parse(formatString);
        LOGGER.info("AccessLogExtendedHandler enabled");
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        exchange.addExchangeCompleteListener(exchangeCompletionListener);
        next.handleRequest(exchange);
    }

    private class AccessLogCompletionListener implements ExchangeCompletionListener {

        @Override
        public void exchangeEvent(HttpServerExchange exchange, NextListener nextListener) {
            try {
                final String tempRealDest = exchange.getAttachment(BackendSelector.REAL_DEST);
                String realDest = tempRealDest != null ? tempRealDest : UNKNOWN;
                String message = tokens.readAttribute(exchange);
                int realStatus = exchange.getStatusCode();
                long responseBytesSent = exchange.getResponseBytesSent();
                final Integer responseTime = Math.round(Float.parseFloat(responseTimeAttribute.readAttribute(exchange)));
                int fakeStatusCode = getFakeStatusCode(tempRealDest, realStatus, responseBytesSent, responseTime, maxRequestTime);
                if (fakeStatusCode != NOT_MODIFIED) {
                    message = message.replaceAll("^(.*Local:\t)\\d{3}(\t.*Proxy:\t)\\d{3}(\t.*)$",
                            "$1" + String.valueOf(fakeStatusCode) + "$2" + String.valueOf(fakeStatusCode) + "$3");
                }
                Pattern compile = Pattern.compile("([^\\t]*\\t[^\\t]*\\t)([^\\t]+)(\\t.*)$");
                Matcher match = compile.matcher(message);
                if (match.find()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(match.group(1)).append(match.group(2).replace(" ", "\t")).append(match.group(3));
                    message = sb.toString();
                }
                accessLogReceiver.logMessage(message.replaceAll(REAL_DEST, realDest));
            } catch (Exception e) {
                LOGGER.error(e);
            } finally {
                nextListener.proceed();
            }
        }
    }

    public AccessLogExtendedHandler setMaxRequestTime(int maxRequestTime) {
        this.maxRequestTime = maxRequestTime;
        return this;
    }
}
