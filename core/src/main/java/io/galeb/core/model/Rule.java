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

public class Rule extends Entity {

    private static final long serialVersionUID = 1L;

    public static final String CLASS_NAME     = "Rule";

    public static final String PROP_TARGET_ID = "targetId";

    public static final String PROP_MATCH     = "match";

    public Rule() {
        super();
    }

    public Rule(Rule rule) {
        super(rule);
        updateETag();
    }

    @Override
    public Entity copy() {
        return new Rule(this);
    }
}
