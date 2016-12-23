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

package io.galeb.core.loadbalance.impl;

import io.galeb.core.loadbalance.LoadBalancePolicy;
import io.galeb.core.model.Backend;
import io.galeb.core.model.BackendPool;
import io.galeb.core.model.Entity;
import io.galeb.core.model.Farm;
import io.galeb.core.model.collections.BackendCollection;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class LeastConnPolicy extends LoadBalancePolicy {

    public static final String PROP_CUTTING_LINE = "lbCuttingLine";

    private Farm farm = null;
    private String backendPoolId = null;
    private ConcurrentLinkedQueue<String> backends = new ConcurrentLinkedQueue<>();
    private double cuttingLine = 0.666;

    @Override
    public int getChoice() {
        if (farm!=null && backendPoolId!=null && backends.isEmpty()) {

            Comparator<? super Entity> backendComparator = Comparator.comparingInt(b -> ((Backend) b).getConnections());

            backends.addAll(((BackendCollection)farm.getCollection(Backend.class)).streamHealthy()
                                .filter(backend -> backend.getParentId().equals(backendPoolId))
                                .sorted(backendComparator)
                                .limit(Integer.toUnsignedLong((int) ((uris.size()*cuttingLine) - Float.MIN_VALUE)))
                                .map(Entity::getId)
                                .collect(Collectors.toCollection(LinkedList::new)));
        }

        int pos = 0;
        String choice = backends.poll();
        if (choice!=null) {
            pos = expirableURIS.stream().map(e -> e.getUri().toString()).collect(Collectors.toCollection(LinkedList::new)).indexOf(choice);
        }
        return pos >= 0 ? pos : 0;
    }

    @Override
    public LoadBalancePolicy setCriteria(Map<String, Object> criteria) {
        super.setCriteria(criteria);
        final Object farmObj = loadBalancePolicyCriteria.get(Farm.class.getSimpleName());
        if (farmObj!=null && farmObj instanceof Farm) {
            farm = (Farm)farmObj;
            backendPoolId = (String) loadBalancePolicyCriteria.get(BackendPool.class.getSimpleName());
            Double limitObj = (Double) loadBalancePolicyCriteria.get(PROP_CUTTING_LINE);
            if (limitObj!=null) {
                cuttingLine = limitObj;
            }
        }
        return this;
    }

}
