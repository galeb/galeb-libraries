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
import io.galeb.core.model.BackendPool;
import io.galeb.core.model.Farm;

public class BackendPoolController extends EntityController {

    public BackendPoolController(final Farm farm) {
        super(farm);
    }

    @Deprecated @Override
    public EntityController add(JsonObject json) throws Exception {
        final BackendPool backendPool = (BackendPool) json.instanceOf(BackendPool.class);
        return add(backendPool);
    }

    @Deprecated @Override
    public EntityController del(JsonObject json) throws Exception {
        final BackendPool backendPool = (BackendPool) json.instanceOf(BackendPool.class);
        return del(backendPool);
    }

    @Override
    public EntityController delAll() throws Exception {
        delAll(BackendPool.class);
        return null;
    }

    @Deprecated @Override
    public EntityController change(JsonObject json) throws Exception {
        final BackendPool backendPool = (BackendPool) json.instanceOf(BackendPool.class);
        return change(backendPool);
    }

    @Override
    public String get(String id) {
        return get(BackendPool.class, id);
    }

}
