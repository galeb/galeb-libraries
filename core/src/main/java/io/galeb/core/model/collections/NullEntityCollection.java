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

package io.galeb.core.model.collections;

import io.galeb.core.json.JsonObject;
import io.galeb.core.model.Entity;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class NullEntityCollection implements Collection<Entity, Entity> {

    Set<Entity> nullSet = Collections.emptySet();

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Iterator<Entity> iterator() {
        return nullSet.iterator();
    }

    @Override
    public Object[] toArray() {
        return nullSet.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return nullSet.toArray(a);
    }

    @Override
    public boolean add(Entity e) {
        return false;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(java.util.Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(java.util.Collection<? extends Entity> c) {
        return false;
    }

    @Override
    public boolean retainAll(java.util.Collection<?> c) {
        return false;
    }

    @Override
    public boolean removeAll(java.util.Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {
        // NULL
    }

    @Override
    public List<Entity> getListByID(String entityId) {
        return Collections.emptyList();
    }

    @Override
    public List<Entity> getListByJson(JsonObject json) {
        return Collections.emptyList();
    }

    @Override
    public Collection<Entity, Entity> change(Entity entity) {
        return this;
    }

}
