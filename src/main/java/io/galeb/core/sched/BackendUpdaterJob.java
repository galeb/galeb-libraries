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

package io.galeb.core.sched;

import io.galeb.core.controller.EntityController.Action;
import io.galeb.core.mapreduce.MapReduce;
import io.galeb.core.model.Backend;

import java.util.Map;
import java.util.Map.Entry;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@DisallowConcurrentExecution
public class BackendUpdaterJob extends AbstractJob {

    private static final long TTL = 10000L;

    private MapReduce mapReduce;

    private final String entityType = Backend.class.getSimpleName().toLowerCase();

    private void cleanUpConnectionsInfo() {
        for (Backend backendWithTTL: farm.getBackends()) {
            long now = System.currentTimeMillis();
            if (backendWithTTL.getConnections()>0 &&  backendWithTTL.getModifiedAt()<(now-TTL)) {
                backendWithTTL.setConnections(0);
                eventBus.publishEntity(backendWithTTL, entityType, Action.CHANGE);
            }
        }
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        setEnvironment(context.getJobDetail().getJobDataMap());
        cleanUpConnectionsInfo();

        mapReduce = eventBus.getMapReduce();
        Map<String, Integer> backEndMap = mapReduce.reduce();

        for (Entry<String, Integer> entry: backEndMap.entrySet()) {
            String backendId = entry.getKey();
            for (Backend backend: farm.getBackends(backendId)) {
                backend.setConnections(entry.getValue());
                eventBus.publishEntity(backend, entityType, Action.CHANGE);
            }
        }

        logger.debug(String.format("Job %s done.", this.getClass().getSimpleName()));
    }

}
