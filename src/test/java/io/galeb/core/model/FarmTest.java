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

import static org.assertj.core.api.Assertions.assertThat;
import io.galeb.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class FarmTest {

    Farm farm;

    VirtualHost virtualhostNull = null;
    String virtualHostId = "test.localhost";
    String virtualHostId2 = "test2.localhost";
    JsonObject virtualHostIdJson;
    JsonObject virtualHostIdJson2;

    BackendPool backendPoolNull = null;
    String backendPoolId = "backendpool";
    String backendPoolId2 = "backendpool2";
    JsonObject backendPoolIdJson;
    JsonObject backendPoolIdJson2;

    Backend nullBackend = null;
    String backendId = "http://0.0.0.0:00";
    String backendId2 = "http://1.1.1.1:11";
    JsonObject backendIdJson;
    JsonObject backendIdJson2;

    String ruleId = "/";
    String ruleId2 = "/test";
    JsonObject ruleIdJson;
    JsonObject ruleIdJson2;

    @org.junit.Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() {
        farm = new Farm();
        virtualHostIdJson = JsonObject.toJsonObject(new VirtualHost().setId(virtualHostId));
        virtualHostIdJson2 = JsonObject.toJsonObject(new VirtualHost().setId(virtualHostId2));

        backendPoolIdJson = JsonObject.toJsonObject(new BackendPool().setId(backendPoolId));
        backendPoolIdJson2 = JsonObject.toJsonObject(new BackendPool().setId(backendPoolId2));

        backendIdJson = JsonObject.toJsonObject(new Backend().setId(backendId).setParentId(backendPoolId));
        backendIdJson2 = JsonObject.toJsonObject(new Backend().setId(backendId2));

        ruleIdJson = JsonObject.toJsonObject(new Rule().setId(ruleId).setParentId(virtualHostId));
        ruleIdJson2 = JsonObject.toJsonObject(new Rule().setId(ruleId2));
    }

    @Test
    public void optionsDefaultIsEmptyAtFarm() {
        assertThat(farm.options.isEmpty()).isTrue();
    }

    @Test
    public void setOptionsAtFarm() {
        final Map<String, String> newOptions = new HashMap<>();
        newOptions.put("key", "value");
        farm.setOptions(newOptions);
        assertThat(farm.options.get("key")).isEqualTo("value");
    }

    @Test
    public void getEntityMapDefaultIsEmptyAtFarm() {
        assertThat(farm.getEntityMap()).isEmpty();
    }

    @Test
    public void getVirtualHostsAtFarm() {
        assertThat(farm.getVirtualHosts()).isEmpty();
    }

    @Test
    public void containVirtualHostIsFalseInDefaultVirtualHostsAtFalse() {
        assertThat(farm.containVirtualHost(virtualHostIdJson)).isFalse();
    }

    @Test
    public void containVirtualHostWithStringIsFalseInDefaultVirtualHostsAtFalse() {
        assertThat(farm.containVirtualHost(virtualHostId)).isFalse();
    }

    @Test
    public void containVirtualHostIsTrueAfterAddVirtualHostAtFarm() {
        farm.addVirtualHost(virtualHostIdJson);
        assertThat(farm.containVirtualHost(virtualHostIdJson)).isTrue();
    }

    @Test
    public void containVirtualHostWithStringIsTrueAfterAddVirtualHostAtFarm() {
        farm.addVirtualHost(virtualHostIdJson);
        assertThat(farm.containVirtualHost(virtualHostId)).isTrue();
    }

    @Test
    public void clearVirtualHostAtFarm() {
        farm.addVirtualHost(virtualHostIdJson);
        assertThat(farm.getVirtualHosts()).hasSize(1);
        farm.clearVirtualHosts();
        assertThat(farm.getVirtualHosts()).isEmpty();
    }

    @Test
    public void getSingleVirtualHostAtFarm() {
        farm.addVirtualHost(virtualHostIdJson);
        farm.addVirtualHost(virtualHostIdJson2);
        assertThat(farm.getVirtualHost(virtualHostIdJson)).isInstanceOf(VirtualHost.class);
    }

    @Test
    public void delVirtualHostAtFarm() {
        farm.addVirtualHost(virtualHostIdJson);
        assertThat(farm.getVirtualHosts()).hasSize(1);
        farm.delVirtualHost(virtualHostIdJson);
        assertThat(farm.getVirtualHosts()).isEmpty();
    }

    @Test
    public void delVirtualHostWithStringAtFarm() {
        farm.addVirtualHost(virtualHostIdJson);
        assertThat(farm.getVirtualHosts()).hasSize(1);
        farm.delVirtualHost(virtualHostId);
        assertThat(farm.getVirtualHosts()).isEmpty();
    }

    @Test
    public void getBackendPoolsAtFarm() {
        assertThat(farm.getBackendPools()).isEmpty();
    }

    @Test
    public void containBackendPoolIsFalseInDefaultBackendPoolsAtFalse() {
        assertThat(farm.containBackendPool(backendPoolIdJson)).isFalse();
    }

    @Test
    public void containBackendPoolWithStringIsFalseInDefaultBackendPoolsAtFalse() {
        assertThat(farm.containBackendPool(backendPoolId)).isFalse();
    }

    @Test
    public void containBackendPoolIsTrueAfterAddBackendPoolAtFarm() {
        farm.addBackendPool(backendPoolIdJson);
        assertThat(farm.containBackendPool(backendPoolIdJson)).isTrue();
    }

    @Test
    public void containBackendPoolWithStringIsTrueAfterAddBackendPoolAtFarm() {
        farm.addBackendPool(backendPoolIdJson);
        assertThat(farm.containBackendPool(backendPoolId)).isTrue();
    }

    @Test
    public void clearBackendPoolAtFarm() {
        farm.addBackendPool(backendPoolIdJson);
        assertThat(farm.getBackendPools()).hasSize(1);
        farm.clearBackendPool();
        assertThat(farm.getBackendPools()).isEmpty();
    }

    @Test
    public void getSingleBackendPoolAtFarm() {
        farm.addBackendPool(backendPoolIdJson);
        farm.addBackendPool(backendPoolIdJson2);
        assertThat(farm.getBackendPool(backendPoolIdJson)).isInstanceOf(BackendPool.class);
    }

    @Test
    public void delBackendPoolAtFarm() {
        farm.addBackendPool(backendPoolIdJson);
        assertThat(farm.getBackendPools()).hasSize(1);
        farm.delBackendPool(backendPoolIdJson);
        assertThat(farm.getBackendPools()).isEmpty();
    }

    @Test
    public void delBackendPoolWithStringAtFarm() {
        farm.addBackendPool(backendPoolIdJson);
        assertThat(farm.getBackendPools()).hasSize(1);
        farm.delBackendPool(backendPoolId);
        assertThat(farm.getBackendPools()).isEmpty();
    }

    @Test
    public void getBackendsAtFarm() {
        assertThat(farm.getBackends()).isEmpty();
    }

    @Test
    public void getBackendsAfterAddBackendsAtFarm() {
        farm.addBackendPool(backendPoolIdJson);
        farm.addBackend(backendIdJson);
        assertThat(farm.getBackends()).hasSize(1);
    }

    @Test
    public void getSingleBackendWithoutBackendAtFarm() {
        assertThat(farm.getBackends(backendId)).isEmpty();
    }

    @Test
    public void getSingleBackendAtFarm() {
        farm.addBackendPool(backendPoolIdJson);
        farm.addBackend(backendIdJson);
        assertThat(farm.getBackends(backendId)).hasSize(1);
        farm.addBackend(backendIdJson2);
        assertThat(farm.getBackends(backendId)).hasSize(1);
    }

    @Test
    public void delBackendAtFarm() {
        farm.addBackendPool(backendPoolIdJson);
        farm.addBackend(backendIdJson);
        assertThat(farm.getBackends(backendId)).hasSize(1);
        farm.delBackend(backendIdJson);
        assertThat(farm.getBackends(backendId)).isEmpty();
    }

    @Test
    public void delBackendWithNullBackendPoolAtFarm() {
        farm.delBackend(backendIdJson2);
        assertThat(farm.getBackends(backendId2)).isEmpty();
    }

    @Test
    public void getRulesAtFarm() {
        assertThat(farm.getRules()).isEmpty();
    }

    @Test
    public void getRulesAfterAddRulesAtFarm() {
        farm.addVirtualHost(virtualHostIdJson);
        farm.addRule(ruleIdJson);
        assertThat(farm.getRules()).hasSize(1);
    }

    @Test
    public void getSingleRuleWithoutVirtualHostAtFarm() {
        assertThat(farm.getRules(virtualHostId)).isEmpty();
    }

    @Test
    public void getSingleRuleAtFarm() {
        farm.addVirtualHost(virtualHostIdJson);
        farm.addRule(ruleIdJson);
        farm.addRule(ruleIdJson2);
        assertThat(farm.getRules(ruleId)).hasSize(1);
    }

    @Test
    public void delRuleAtFarm() {
        farm.addVirtualHost(virtualHostIdJson);
        farm.addRule(ruleIdJson);
        assertThat(farm.getRules(ruleId)).hasSize(1);
        farm.delRule(ruleIdJson);
        assertThat(farm.getRules(ruleId)).isEmpty();
    }

    @Test
    public void delRuleWithObjectAtFarm() {
        farm.addVirtualHost(virtualHostIdJson);
        farm.addRule(ruleIdJson);
        assertThat(farm.getRules(ruleId)).hasSize(1);
        farm.delRule((Rule) new Rule().setId(ruleId).setParentId(virtualHostId));
        assertThat(farm.getRules(ruleId)).isEmpty();
    }

    @Test
    public void delRuleWithNullVirtualHostAtFarm() {
        farm.addRule(ruleIdJson2);
        assertThat(farm.getRules(ruleId)).isEmpty();
    }

    @Test
    public void getRootHandlerDefaultIsNullAtFarm() {
        assertThat(farm.getRootHandler()).isNull();
    }

}
