package io.galeb.core.loadbalance.impl;

import static org.assertj.core.api.Assertions.assertThat;
import io.galeb.core.json.JsonObject;
import io.galeb.core.model.Backend;
import io.galeb.core.model.BackendPool;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class RoundRobinPolicyTest {

    int numBackends = 10;
    RoundRobinPolicy roundRobinPolicy;
    BackendPool backendPool;

    @Before
    public void setUp() throws URISyntaxException{
        backendPool = new BackendPool();
        roundRobinPolicy = new RoundRobinPolicy();
        final List<URI> uris = new LinkedList<>();

        for (int x=0; x<numBackends; x++) {
            final String backendId = String.format("http://0.0.0.0:%s", x);
            backendPool.addBackend(JsonObject.toJsonString(new Backend().setId(backendId)));
            uris.add(new URI(backendId));
        }
        roundRobinPolicy.mapOfHosts(uris);
    }


    @Test
    public void backendsChosenInSequence() {
        final LinkedList<Object> controlList = new LinkedList<>();
        for (int counter=0; counter<numBackends*99; counter++) {
            controlList.add(roundRobinPolicy.getChoice());
        }

        roundRobinPolicy.reset();
        int lastChoice = roundRobinPolicy.getLastChoice();
        int currentChoice;

        for (int counter=0; counter<numBackends*99; counter++) {
            currentChoice = (int) controlList.poll();
            assertThat(currentChoice).isNotEqualTo(lastChoice);
            assertThat(roundRobinPolicy.getChoice()).isEqualTo(currentChoice);
            lastChoice = currentChoice;
        }
    }

}
