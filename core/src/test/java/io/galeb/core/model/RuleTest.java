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

import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RuleTest {

    @Test
    public void newRuleInstance() {
        assertThat(new Rule()).isInstanceOf(Rule.class);
    }

    @Test
    public void rulesAreEquals() {
        String targetId1 = "aTargetId";
        int ruleOrder1 = 1;
        final Map<String, Object> properties1 = new HashMap<>();
        properties1.put(Rule.PROP_TARGET_ID, targetId1);
        properties1.put(Rule.PROP_DEFAULT, String.valueOf(true));
        properties1.put(Rule.PROP_RULE_ORDER, String.valueOf(ruleOrder1));

        String targetId2 = "otherTargetId";
        int ruleOrder2 = 2;
        final Map<String, Object> properties2 = new HashMap<>();
        properties2.put(Rule.PROP_TARGET_ID, targetId2);
        properties2.put(Rule.PROP_DEFAULT, String.valueOf(false));
        properties2.put(Rule.PROP_RULE_ORDER, String.valueOf(ruleOrder2));

        Rule rule1 = new Rule();
        rule1.setParentId("sameParent").setId("aRule").setProperties(properties1);
        Rule rule2 = new Rule();
        rule2.setParentId("sameParent").setId("aRule").setProperties(properties2);

        assertThat(rule1).isEqualTo(rule2);
    }

    @Test
    public void rulesAreDifferentsBecauseParents() {
        Rule rule1 = new Rule();
        rule1.setParentId("sameParent").setId("aRule");
        Rule rule2 = new Rule();
        rule2.setParentId("otherParent").setId("aRule");

        assertThat(rule1).isNotEqualTo(rule2);
    }

    @Test
    public void rulesAreDifferentsBecauseId() {
        Rule rule1 = new Rule();
        rule1.setParentId("sameParent").setId("aRule");
        Rule rule2 = new Rule();
        rule2.setParentId("sameParent").setId("otherRule");

        assertThat(rule1).isNotEqualTo(rule2);
    }

    @Test
    public void rulesIsOrderedByRuleOrder() {
        int ruleOrder1 = 2;
        final Map<String, Object> properties1 = new HashMap<>();
        properties1.put(Rule.PROP_RULE_ORDER, String.valueOf(ruleOrder1));

        int ruleOrder2 = 1;
        final Map<String, Object> properties2 = new HashMap<>();
        properties2.put(Rule.PROP_RULE_ORDER, String.valueOf(ruleOrder2));

        int ruleOrder3 = 3;
        final Map<String, Object> properties3 = new HashMap<>();
        properties3.put(Rule.PROP_RULE_ORDER, String.valueOf(ruleOrder3));

        Rule rule1 = new Rule();
        rule1.setParentId("sameParent").setId("aRule").setProperties(properties1);
        Rule rule2 = new Rule();
        rule2.setParentId("sameParent").setId("otherRule").setProperties(properties2);
        Rule rule3 = new Rule();
        rule3.setParentId("sameParent").setId("otherOtherRule").setProperties(properties3);

        assertThat(rule2).isLessThan(rule3);
        assertThat(rule1).isGreaterThan(rule2);
        assertThat(rule1).isLessThan(rule3);
    }

    @Test
    public void rulesInSortedList() {
        LinkedList<Rule> rules = IntStream.rangeClosed(1, 5).parallel().boxed().map(aInteger -> {
            HashMap<String, Object> properties = new HashMap<>();
            properties.put(Rule.PROP_RULE_ORDER, aInteger);
            Rule rule = new Rule();
            rule.setId(aInteger.toString())
                .setParentId("sameParent")
                .setProperties(properties);
            return rule;
        }).sorted().collect(Collectors.toCollection(LinkedList<Rule>::new));
        assertThat(rules).hasSize(5);
        assertThat(rules).isSorted();
    }

    @Test
    public void rulesInSortedSet() {
        Set<Rule> rules = new ConcurrentSkipListSet<>();
        rules.addAll(IntStream.rangeClosed(1, 5).boxed().map(aInteger -> {
            HashMap<String, Object> properties = new HashMap<>();
            properties.put(Rule.PROP_RULE_ORDER, aInteger);
            Rule rule = new Rule();
            rule.setId(aInteger.toString())
                .setParentId("sameParent")
                .setProperties(properties);
            return rule;
        }).collect(Collectors.toSet()));
        assertThat(rules).hasSize(5);
    }

    @Test
    public void rulesInSortedSetWithSameId() {
        Set<Rule> rules = IntStream.rangeClosed(1, 5).boxed().map(aInteger -> {
            HashMap<String, Object> properties = new HashMap<>();
            properties.put(Rule.PROP_RULE_ORDER, aInteger);
            Rule rule = new Rule();
            rule.setId("sameId")
                .setParentId("sameParent")
                .setProperties(properties);
            return rule;
        }).sorted().collect(Collectors.toSet());
        assertThat(rules).hasSize(1);
    }

    @Test
    public void rulesInSortedSetWithDiffParent() {
        Set<Rule> rules = IntStream.rangeClosed(1, 5).boxed().map(aInteger -> {
            HashMap<String, Object> properties = new HashMap<>();
            properties.put(Rule.PROP_RULE_ORDER, aInteger);
            Rule rule = new Rule();
            rule.setId(aInteger.toString())
                .setParentId(aInteger.toString())
                .setProperties(properties);
            return rule;
        }).sorted().collect(Collectors.toSet());
        assertThat(rules).hasSize(5);
    }

    @Test
    public void rulesInSortedSetWithSameOrder() {
        Set<Rule> rules = IntStream.rangeClosed(1, 5).boxed().map(aInteger -> {
            HashMap<String, Object> properties = new HashMap<>();
            properties.put(Rule.PROP_RULE_ORDER, 1);
            Rule rule = new Rule();
            rule.setId(aInteger.toString())
                .setParentId(aInteger.toString())
                .setProperties(properties);
            return rule;
        }).sorted().collect(Collectors.toSet());
        assertThat(rules).hasSize(5);
    }


    @Test
    public void rulesInSortedMap() {
        Map<String, Rule> rules = new ConcurrentSkipListMap<>();
        rules.putAll(IntStream.rangeClosed(1, 5).boxed().map(aInteger -> {
            HashMap<String, Object> properties = new HashMap<>();
            properties.put(Rule.PROP_RULE_ORDER, aInteger);
            Rule rule = new Rule();
            rule.setId(aInteger.toString())
                .setParentId("sameParent")
                .setProperties(properties);
            return rule;
        }).collect(Collectors.toMap(Rule::compoundId, Rule::new)));
        assertThat(rules).hasSize(5);
    }

    @Test
    public void rulesInSortedMapWithSameId() {
        Map<Rule, Rule> rules = new TreeMap<>();
        IntStream.rangeClosed(1, 5).boxed().map(aInteger -> {
            HashMap<String, Object> properties = new HashMap<>();
            properties.put(Rule.PROP_RULE_ORDER, aInteger);
            Rule rule = new Rule();
            rule.setId("sameId")
                .setParentId("sameParent")
                .setProperties(properties);
            return rule;
        }).forEach(rule -> {
            rules.put(rule, rule);
        });
        assertThat(rules).hasSize(1);
    }

    @Test
    public void rulesInSortedMapWithDiffParent() {
        Map<Rule, Rule> rules = new ConcurrentSkipListMap<>();
        IntStream.rangeClosed(1, 5).boxed().map(aInteger -> {
            HashMap<String, Object> properties = new HashMap<>();
            properties.put(Rule.PROP_RULE_ORDER, aInteger);
            Rule rule = new Rule();
            rule.setId("sameId")
                .setParentId(aInteger.toString())
                .setProperties(properties);
            return rule;
        }).forEach(rule -> {
            rules.put(rule, rule);
        });
        assertThat(rules).hasSize(5);
    }

    @Test
    public void rulesInSortedMapWithSameOrder() {
        Map<Rule, Rule> rules = new ConcurrentSkipListMap<>();
        IntStream.rangeClosed(1, 5).boxed().map(aInteger -> {
            HashMap<String, Object> properties = new HashMap<>();
            properties.put(Rule.PROP_RULE_ORDER, 1);
            Rule rule = new Rule();
            rule.setId(aInteger.toString())
                .setParentId(aInteger.toString())
                .setProperties(properties);
            return rule;
        }).forEach(rule -> {
            rules.put(rule, rule);
        });
        assertThat(rules).hasSize(5);
    }
}
