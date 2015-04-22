package io.galeb.core.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

public class BackendPoolTest {

    BackendPool backendPool;
    Backend backend;
    Backend nullBackend = null;
    String backendId = "http://0.0.0.0:00";
    String backendIdAsJson = String.format("{'id':'%s'}", backendId);
    String backendId2AsJson = "{'id':'http://1.1.1.1:11'}";

    @Before
    public void setUp() {
        backendPool = new BackendPool();
        backend = (Backend) new Backend().setId(backendId);
    }

    @Test
    public void getBackendsAtBackendPool() {
        assertThat(backendPool.getBackends()).isEmpty();
    }

    @Test
    public void clearBackendsAtBackendPool() {
        backendPool.addBackend(backendIdAsJson);
        assertThat(backendPool.getBackends()).hasSize(1);
        backendPool.clearBackends();
        assertThat(backendPool.getBackends()).isEmpty();
    }

    @Test
    public void containBackendIsFalseInDefaultBackendsAtBackendPool() {
        assertThat(backendPool.containBackend(backendId)).isFalse();
    }

    @Test
    public void containBackendIsTrueAfterAddBackendsAtBackendPool() {
        backendPool.addBackend(backendIdAsJson);
        assertThat(backendPool.containBackend(backendId)).isTrue();
    }

    @Test
    public void delBackendAtBackendPool() {
        backendPool.addBackend(backendIdAsJson);
        assertThat(backendPool.getBackends()).hasSize(1);
        backendPool.delBackend(backendId);
        assertThat(backendPool.getBackends()).isEmpty();
    }

    @Test
    public void delNullBackendAtBackendPool() {
        backendPool.addBackend(backendIdAsJson);
        assertThat(backendPool.getBackends()).hasSize(1);
        backendPool.delBackend(nullBackend);
        assertThat(backendPool.getBackends()).hasSize(1);
    }

    @Test
    public void getSingleBackendAtBackendPool() {
        backendPool.addBackend(backendIdAsJson);
        backendPool.addBackend(backendId2AsJson);
        assertThat(backendPool.getBackend(backendId)).isInstanceOf(Backend.class);
    }

}
