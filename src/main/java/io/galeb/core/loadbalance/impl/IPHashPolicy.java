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
import io.galeb.core.util.consistenthash.ConsistentHash;
import io.galeb.core.util.consistenthash.HashAlgorithm;
import io.galeb.core.util.consistenthash.HashAlgorithm.HashType;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class IPHashPolicy extends LoadBalancePolicy {

    public static final String HASH_ALGORITHM = "HashAlgorithm";

    public static final String NUM_REPLICAS   = "NumReplicas";

    private final Set<Integer> listPos = new LinkedHashSet<>();

    private HashAlgorithm hashAlgorithm = new HashAlgorithm(HashType.SIP24);

    private int numReplicas = 1;

    private final ConsistentHash<Integer> consistentHash =
            new ConsistentHash<Integer>(hashAlgorithm, numReplicas, new ArrayList<Integer>());

    private volatile String sourceIP = "127.0.0.1";

    @Override
    public int getChoice() {
        if (isReseted()) {
            consistentHash.rebuild(hashAlgorithm, numReplicas, listPos);
            rebuilt();
        }
        final int chosen = consistentHash.get(sourceIP);
        last.lazySet(chosen);
        return chosen;
    }

    @Override
    public synchronized LoadBalancePolicy setCriteria(final Map<String, Object> criteria) {
        final String hashAlgorithmStr = (String) criteria.get(HASH_ALGORITHM);
        if (hashAlgorithmStr!=null && HashAlgorithm.hashTypeFromString(hashAlgorithmStr)!=null) {
            hashAlgorithm = new HashAlgorithm(hashAlgorithmStr);
        }
        final String numReplicaStr = (String) criteria.get(NUM_REPLICAS);
        if (numReplicaStr!=null) {
            numReplicas = Integer.valueOf(numReplicaStr);
        }
        sourceIP = (String) criteria.get(SOURCE_IP_CRITERION);

        listPos.clear();
        for (final URI uri: uris) {
            listPos.add(uris.indexOf(uri));
        }

        return this;
    }

    @Override
    public boolean needSourceIP() {
        return true;
    }

}
