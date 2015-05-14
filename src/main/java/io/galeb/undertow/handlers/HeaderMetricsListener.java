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

import io.galeb.core.eventbus.IEventBus;
import io.galeb.core.logging.Logger;
import io.galeb.core.model.Metrics;
import io.undertow.server.ExchangeCompletionListener;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderValues;

import java.util.NoSuchElementException;

class HeaderMetricsListener implements ExchangeCompletionListener {

    private Logger logger;
    private IEventBus eventBus;

    @Override
    public void exchangeEvent(final HttpServerExchange exchange, final NextListener nextListener) {
        try {
            final HeaderValues headerXProxyHost = exchange.getRequestHeaders().get(BackendSelector.X_PROXY_HOST);
            if (headerXProxyHost==null) {
                return;
            }
            final Metrics metrics = new Metrics();
            metrics.setParentId(exchange.getHostName());
            metrics.setId(headerXProxyHost.getFirst());
            metrics.putProperty(Metrics.PROP_STATUSCODE, exchange.getResponseCode());
            final long requestTimeNano = System.nanoTime() - exchange.getRequestStartTime();
            metrics.putProperty(Metrics.PROP_REQUESTTIME, requestTimeNano/1000000L);

            eventBus.onRequestMetrics(metrics);
        } catch (NoSuchElementException e1) {
            logger.debug(e1);
        } catch (final RuntimeException e2) {
            logger.error(e2);
        } finally {
            nextListener.proceed();
        }
    }

    public HeaderMetricsListener setEventBus(IEventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.getQueueManager();
        return this;
    }

    public HeaderMetricsListener setLogger(Logger logger) {
        this.logger = logger;
        return this;
    }

}