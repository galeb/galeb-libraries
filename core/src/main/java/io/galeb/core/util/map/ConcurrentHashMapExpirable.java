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

package io.galeb.core.util.map;

import static java.util.Collections.unmodifiableCollection;
import static java.util.Collections.unmodifiableSet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class ConcurrentHashMapExpirable<K, V> implements ConcurrentMap<K, V> {

    private final ConcurrentHashMap<K, ValueWithTimeStamp<V>> realMap;
    private final long ttl;


    private static class ValueWithTimeStamp<V> {
        private final V value;
        private long timestamp = System.nanoTime();

        private ValueWithTimeStamp(final V value) {
            this.value = value;
        }

        private V getValue() {
            return value;
        }

        private int getValueInt() {
            return (Integer)value;
        }

        private ValueWithTimeStamp setTimestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        boolean expired(long ttl) {
            return (System.nanoTime() - this.timestamp) > ttl;
        }

        @Override
        public boolean equals(Object obj) {
            boolean result = false;
            if (obj instanceof ValueWithTimeStamp) {
                try {
                    ValueWithTimeStamp valueWithTimeStamp = (ValueWithTimeStamp) obj;
                    result = this.value.equals(valueWithTimeStamp.getValue());
                } catch (Exception ignore){
                    return false;
                }
            }
            return result;
        }
    }

    public ConcurrentHashMapExpirable(long ttl, TimeUnit timeUnit) {
        this.ttl = timeUnit.toNanos(ttl);
        realMap = new ConcurrentHashMap<>();
    }

    public ConcurrentHashMapExpirable(long ttl, TimeUnit timeUnit, int initialCapacity, float loadFactor, int concurrencyLevel) {
        this.ttl = timeUnit.toNanos(ttl);
        realMap = new ConcurrentHashMap<>(initialCapacity, loadFactor, concurrencyLevel);
    }


    public ConcurrentHashMapExpirable(long ttlInNanoSec) {
        this(ttlInNanoSec, TimeUnit.NANOSECONDS);
    }

    @Override
    public V get(Object key) {
        final ValueWithTimeStamp<V> valueWithTimeStamp = realMap.get(key);

        if (valueWithTimeStamp != null && valueWithTimeStamp.expired(ttl)) {
            realMap.remove(key);
            return null;
        } else {
            return valueWithTimeStamp.getValue();
        }
    }

    @Override
    public V put(K key, V value) {
        ValueWithTimeStamp<V> valueWithTimeStampStored = realMap.put(key, new ValueWithTimeStamp<>(value));
        if (valueWithTimeStampStored!=null) {
            return valueWithTimeStampStored.value;
        } else {
            return null;
        }
    }

    @Override
    public int size() {
        return realMap.size();
    }

    @Override
    public boolean isEmpty() {
        return realMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return realMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object aValue) {
        V value = (V) aValue;
        ValueWithTimeStamp<V> valueWithTimeStamp = new ValueWithTimeStamp<>(value);
        return realMap.containsValue(valueWithTimeStamp);
    }

    @Override
    public V remove(Object key) {
        ValueWithTimeStamp<V> valueWithTimeStampStored = realMap.remove(key);
        return valueWithTimeStampStored.getValue();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        realMap.clear();
    }

    @Override
    public Set<K> keySet() {
        clearExpired();
        return unmodifiableSet(realMap.keySet());
    }

    private Map<K, V> extractRealMap() {
        final Map<K, V> mapExtracted = new HashMap<>();
        realMap.forEach((key, valueWithTimeStamp) -> mapExtracted.put(key, valueWithTimeStamp.getValue()));
        return mapExtracted;
    }

    @Override
    public Collection<V> values() {
        clearExpired();
        return unmodifiableCollection(extractRealMap().values());
    }

    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        clearExpired();
        return unmodifiableSet(extractRealMap().entrySet());
    }

    public final void clearExpired() {
        realMap.forEach((k, v) -> get(k));
    }

    public final void renewAll() {
        realMap.forEach((k, v) -> v.setTimestamp(System.nanoTime()));
    }

    @Override
    public V putIfAbsent(K key, V value) {
        ValueWithTimeStamp<V> valueWithTimeStampStored = realMap.putIfAbsent(key, new ValueWithTimeStamp<>(value));
        if (valueWithTimeStampStored!=null) {
            return valueWithTimeStampStored.value;
        }
        return null;
    }

    @Override
    public boolean remove(Object key, Object value) {
        return realMap.remove(key, value);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return realMap.replace(key, new ValueWithTimeStamp<>(oldValue), new ValueWithTimeStamp<>(newValue));
    }

    @Override
    public V replace(K key, V value) {
        ValueWithTimeStamp<V> valueReturned = realMap.replace(key, new ValueWithTimeStamp<>(value));
        if (valueReturned!=null) {
            try {
                return valueReturned.getValue();
            } catch (Exception ignore) {
                return null;
            }
        }
        return null;
    }

    public int reduceValuesToInt() {
        return realMap.reduceValuesToInt(10L, ValueWithTimeStamp::getValueInt, 0, (x, y) -> x + y);
    }

}
