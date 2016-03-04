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

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Entity implements Serializable, Comparable<Entity> {

    private static final long serialVersionUID = 1L;

    public static final String SEP_COMPOUND_ID = "#";

    /** internal use **/
    private String compoundId = "";

    /** The primary key */
    @Expose private int                        pk            = -1;

    /** The id. */
    @Expose private String                     id            = "";

    /** The parent id. */
    @Expose private String                     parentId      = "";

    /** The created at. */
    @SerializedName("_created_at")
    @Expose private final Long                 createdAt     = System.currentTimeMillis();

    /** The modified at. */
    @SerializedName("_modified_at")
    @Expose private Long                       modifiedAt    = System.currentTimeMillis();

    /** The properties. */
    @Expose private final Map<String, Object>  properties    = new ConcurrentHashMap<>(16, 0.9f, 1);

    /** The entity type. */
    @SerializedName("_entity_type")
    @Expose private String                     entityType    = this.getClass().getSimpleName().toLowerCase();

    /** The hash. */
    @SerializedName("_etag")
    @Expose private String                     etag          = "0";

    /** The version */
    @Expose private Integer                    version       = 0;

    public Entity() {
        // default
    }

    public Entity(Entity entity) {
        this();
        setId(entity.getId());
        setParentId(entity.getParentId());
        setEntityType(entity.getEntityType());
        setPk(entity.getPk());
        setProperties(entity.getProperties());
        setVersion(entity.getVersion());
    }

    public int getPk() {
        return pk;
    }

    public Entity setPk(int pk) {
        this.pk = pk;
        return this;
    }

    public String getId() {
        return id;
    }

    public final Entity setId(String id) {
        this.id = id;
        updateCompoundId();
        return this;
    }

    public String getParentId() {
        return parentId;
    }

    public final Entity setParentId(String parentId) {
        this.parentId = parentId;
        updateCompoundId();
        return this;
    }

    public Long getModifiedAt() {
        return modifiedAt;
    }

    public final Entity updateModifiedAt() {
        modifiedAt = System.currentTimeMillis();
        return this;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public final Entity setProperties(final Map<String, Object> myProperties) {
        properties.clear();
        properties.putAll(myProperties);
        return this;
    }

    public final Entity putProperty(String key, Object value) {
        properties.put(key, value);
        return this;
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }

    public void clearProperties() {
        properties.clear();
    }

    public String getEntityType() {
        return entityType;
    }

    public final Entity setEntityType(String entityType) {
        this.entityType = entityType;
        return this;
    }

    public String getETag() {
        return etag;
    }

    public final Entity setETag(String hash) {
        etag = hash;
        return this;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public final Entity updateETag() {
        etag = "0";
        return this;
    }

    public int getVersion() {
        return version;
    }

    public final Entity setVersion(int version) {
        this.version = version;
        return this;
    }

    public Entity copy() {
        return new Entity(this);
    }

    public String compoundId() {
        return compoundId;
    }

    private void updateCompoundId() {
        compoundId = getId()+SEP_COMPOUND_ID+getParentId();
    }

    @Override
    public int hashCode() {
        return (compoundId()).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Entity
               && ((Entity) obj).compoundId().equals(compoundId());
    }

    @Override
    public int compareTo(Entity o) {
        return o != null ? compoundId().compareTo(o.compoundId()) : -1;
    }

}
