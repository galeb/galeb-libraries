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

package io.galeb.undertow.loaders;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import io.galeb.core.controller.EntityController.Action;
import io.galeb.core.logging.Logger;
import io.galeb.core.model.Entity;
import io.galeb.core.model.Farm;
import io.galeb.core.model.VirtualHost;
import io.galeb.undertow.handlers.PathGlobHandler;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.IPAddressAccessControlHandler;
import io.undertow.server.handlers.NameVirtualHostHandler;
import io.undertow.util.StatusCodes;

public class VirtualHostLoader implements Loader {

    private HttpHandler virtualHostHandler;
    private Optional<Logger> optionalLogger = Optional.empty();
    private Loader ruleLoader;

    public VirtualHostLoader setRuleLoader(final Loader ruleLoader) {
        this.ruleLoader = ruleLoader;
        return this;
    }

    public VirtualHostLoader setVirtualHostHandler(final HttpHandler virtualHostHandler) {
        this.virtualHostHandler = virtualHostHandler;
        return this;
    }

    @Override
    public Loader setLogger(Logger logger) {
        optionalLogger = Optional.ofNullable(logger);
        return this;
    }

    @Override
    public void from(Entity entity, Action action) {
        if (action.equals(Action.DEL_ALL)) {
            final Farm farm = (Farm) entity;
            farm.getCollection(VirtualHost.class).stream()
                    .forEach(virtualhost -> from(virtualhost, Action.DEL));
            return;
        }

        final String virtualhostId = entity.getId();
        boolean isOk = false;

        final NameVirtualHostHandler nameVirtualHostHandler = (NameVirtualHostHandler) virtualHostHandler;
        switch (action) {
            case ADD:
                if (!nameVirtualHostHandler.getHosts().containsKey(virtualhostId)) {
                    final HttpHandler pathHandler = new PathGlobHandler();
                    final AtomicReference<HttpHandler> nextHandler = new AtomicReference<>(pathHandler);
                    Map<String, Object> properties = entity.getProperties();
                    if (properties != null) {
                        String ipACL = (String) properties.get(VirtualHost.ALLOW_PROPERTY);
                        if (ipACL != null) {
                            nextHandler.set(new IPAddressAccessControlHandler(pathHandler, StatusCodes.FORBIDDEN));
                            Arrays.asList(ipACL.split(",")).stream().forEach(ip -> {
                                ((IPAddressAccessControlHandler) nextHandler.get()).addAllow(ip);
                            });
                        }
                    }
                    nameVirtualHostHandler.addHost(virtualhostId, nextHandler.get());
                    isOk = true;
                }
                break;

            case DEL:
                if (nameVirtualHostHandler.getHosts().containsKey(virtualhostId)) {
                    ((VirtualHost) entity).getRules().forEach(r -> ruleLoader.from(r, Action.DEL));
                    nameVirtualHostHandler.removeHost(virtualhostId);
                    isOk = true;
                }
                break;

            case CHANGE:
                if (nameVirtualHostHandler.getHosts().containsKey(virtualhostId)) {
                    from(entity, Action.DEL);
                    from(entity, Action.ADD);
                    isOk = true;
                }
                break;

            default:
                optionalLogger.ifPresent(logger -> logger.error(action.toString()+" NOT FOUND"));
        }
        if (isOk) {
            optionalLogger.ifPresent(logger -> logger.debug("Action "+action.toString()+" applied: "+virtualhostId+" ("+entity.getEntityType()+")"));
        }
    }

    @Override
    public void changeIfNecessary(List<Entity> oldEntities, Entity entity) {
        from(entity, Action.CHANGE);
    }

}
