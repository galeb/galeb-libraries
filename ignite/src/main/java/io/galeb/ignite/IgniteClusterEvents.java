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

package io.galeb.ignite;

import io.galeb.core.cluster.ClusterEvents;
import io.galeb.core.cluster.ClusterListener;
import org.apache.ignite.events.*;
import org.apache.ignite.lang.*;

import static io.galeb.ignite.IgniteInstance.INSTANCE;

public class IgniteClusterEvents implements ClusterEvents {
    @Override
    public void registerListener(ClusterListener clusterListener) {
        IgnitePredicate<? extends Event> predicate = null;
        INSTANCE.events().localListen(predicate);
    }

    @Override
    public boolean isReady() {
        return INSTANCE.cluster().localNode().isLocal();
    }
}
