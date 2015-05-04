package io.galeb.core.sched;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import io.galeb.core.controller.BackendPoolController;
import io.galeb.core.controller.EntityController.Action;
import io.galeb.core.eventbus.EventBusListener;
import io.galeb.core.eventbus.IEventBus;
import io.galeb.core.json.JsonObject;
import io.galeb.core.logging.Logger;
import io.galeb.core.model.Backend;
import io.galeb.core.model.BackendPool;
import io.galeb.core.model.Entity;
import io.galeb.core.model.Farm;
import io.galeb.core.model.Metrics;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class BackendPoolUpdaterJobTest {

    private JobExecutionContext jobExecutionContext = mock(JobExecutionContext.class);

    private final Farm farm = new Farm();

    private class FakeEventBus implements IEventBus {
        @Override
        public void publishEntity(Entity entity, String entityType, Action action) {
            BackendPoolController backendPoolController = new BackendPoolController(farm);
            BackendPool backendPool = ((BackendPool)entity);
            try {
                backendPoolController.change(JsonObject.toJsonObject(backendPool));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onRequestMetrics(Metrics metrics) {
            // Fake
        }

        @Override
        public void onConnectionsMetrics(Metrics metrics) {
            // Fake
        }

        @Override
        public IEventBus setEventBusListener(EventBusListener eventBusListener) {
            // Fake
            return this;
        }

        @Override
        public void start() {
            // Fake
        }
    }

    @Before
    public void setUp() {
        Logger logger = mock(Logger.class);
        IEventBus eventBus = new FakeEventBus();
        doNothing().when(logger).error(any(Throwable.class));
        doNothing().when(logger).debug(any(Throwable.class));

        JobDetail jobDetail = mock(JobDetail.class);
        when(jobExecutionContext.getJobDetail()).thenReturn(jobDetail);

        JobDataMap jobdataMap = new JobDataMap();
        jobdataMap.put("farm", farm);
        jobdataMap.put("logger", logger);
        jobdataMap.put("eventbus", eventBus);
        when(jobDetail.getJobDataMap()).thenReturn(jobdataMap);
    }

    @Test
    public void executeTest() throws JobExecutionException {
        final String backendPoolId = "pool1";
        int numBackends = 10;
        int maxConn = 1000;
        int minConn = maxConn;

        farm.addBackendPool(((BackendPool)new BackendPool().setId(backendPoolId)));

        for (int x=0; x<=numBackends;x++) {
            int numConn = (int) (Math.random() * (maxConn - Float.MIN_VALUE));
            minConn = numConn < minConn ? numConn : minConn;

            Backend backend = (Backend)new Backend().setConnections(numConn)
                                                    .setParentId(backendPoolId)
                                                    .setId(UUID.randomUUID().toString());
            farm.addBackend(backend);
        }

        new BackendPoolUpdaterJob().execute(jobExecutionContext);
        BackendPool backendPool = farm.getBackendPool(backendPoolId);
        Backend backendWithLeastConn = backendPool.getBackendWithLeastConn();

        assertThat(backendWithLeastConn.getConnections()).isEqualTo(minConn);
    }

}
