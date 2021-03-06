/*
 *  Galeb - Load Balance as a Service Plataform
 *
 *  Copyright (C) 2014-2016 Globo.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.galeb.core.cluster;

import java.util.concurrent.ConcurrentMap;

public interface DistributedMap<K, V> {

    public static final String BACKEND_CONNECTIONS = "backendConnections";

    default ConcurrentMap<K, V> getMap(String key) {
        throw new UnsupportedOperationException();
    }

    default void remove(String key) {
        throw new UnsupportedOperationException();
    }

    default void registerListener(final DistributedMapListener distributedMapListener) {
        throw new UnsupportedOperationException();
    }

    default void unregisterListener(final DistributedMapListener distributedMapListener) {
        throw new UnsupportedOperationException();
    }

    default DistributedMapStats getStats() {
        throw new UnsupportedOperationException();
    }

}
