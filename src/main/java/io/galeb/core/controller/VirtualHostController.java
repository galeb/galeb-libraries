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

package io.galeb.core.controller;

import io.galeb.core.json.JsonObject;
import io.galeb.core.model.Farm;
import io.galeb.core.model.VirtualHost;
import io.galeb.core.model.collections.VirtualHostCollection;

public class VirtualHostController implements EntityController {

    private final Farm farm;

    private final VirtualHostCollection virtualHostCollection;

    public VirtualHostController(final Farm farm) {
        this.farm = farm;
        this.virtualHostCollection = (VirtualHostCollection) farm.getVirtualHosts();
    }

    @Override
    public EntityController add(JsonObject json) throws Exception{
        final VirtualHost virtualHost = (VirtualHost) json.instanceOf(VirtualHost.class);
        virtualHostCollection.add(virtualHost);
        farm.setVersion(virtualHost.getVersion());
        return this;
    }

    @Override
    public EntityController del(JsonObject json) throws Exception {
        final VirtualHost virtualHost = (VirtualHost) json.instanceOf(VirtualHost.class);
        virtualHostCollection.remove(virtualHost);
        farm.setVersion(virtualHost.getVersion());
        return this;
    }

    @Override
    public EntityController delAll() throws Exception {
        virtualHostCollection.clear();
        return this;
    }

    @Override
    public EntityController change(JsonObject json) throws Exception {
        final VirtualHost virtualHost = (VirtualHost) json.instanceOf(VirtualHost.class);
        virtualHostCollection.change(virtualHost);
        return this;
    }

    @Override
    public String get(String id) {
        if (id != null && !"".equals(id)) {
            return JsonObject.toJsonString(virtualHostCollection.stream()
                    .filter(virtualHost -> virtualHost.getId().equals(id)));
        } else {
            return JsonObject.toJsonString(virtualHostCollection);
        }
    }

}
