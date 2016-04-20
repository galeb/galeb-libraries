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

import io.undertow.attribute.ExchangeAttribute;
import io.undertow.attribute.ExchangeAttributes;
import io.undertow.attribute.SubstituteEmptyWrapper;
import io.undertow.server.ExchangeCompletionListener;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.accesslog.AccessLogReceiver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AccessLogExtendedHandler implements HttpHandler {

    private static final Logger LOGGER = LogManager.getLogger();

    public static final String REAL_DEST = "#REAL_DEST#";
    public static final String UNKNOWN = "UNKNOWN";

    private final ExchangeCompletionListener exchangeCompletionListener = new AccessLogCompletionListener();
    private final AccessLogReceiver accessLogReceiver;
    private final ExchangeAttribute tokens;
    private final HttpHandler next;

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
                int realStatus = exchange.getResponseCode();
                if (UNKNOWN.equals(realDest)) {
                    message = message.replaceAll("^([^\t]*\t[^\t]*\t[^\t]*\t[^\t]*\t[^\t]*\t)[^\t]+(\t.*)$", "$1" +
                            String.valueOf(realStatus + 400) + "$2");
                }
                accessLogReceiver.logMessage(message.replaceAll(REAL_DEST, realDest));
            } finally {
                nextListener.proceed();
            }
        }
    }

}
