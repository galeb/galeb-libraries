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

package io.galeb.core.model;

import com.google.gson.annotations.Expose;

public class Backend extends Entity {

    private static final long serialVersionUID = 1L;

    public static final String PROP_ACTIVECONN = "activeConn";

    public enum Health {
        HEALTHY,
        DEADY,
        UNKNOWN
    }

    @Expose private Health health = Health.HEALTHY;

    @Expose private int connections = 0;

    public Health getHealth() {
        return health;
    }

    public final Backend setHealth(Health health) {
        this.health = health;
        return this;
    }

    public int getConnections() {
        return connections;
    }

    public Backend setConnections(int connections) {
        this.connections = connections;
        return this;
    }

    @Override
    public Entity copy() {
        Entity newEntity = super.copy();
        ((Backend) newEntity).setHealth(getHealth()).setConnections(getConnections());
        return newEntity;
    }

}
