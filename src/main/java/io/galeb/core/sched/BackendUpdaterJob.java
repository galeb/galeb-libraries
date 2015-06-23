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

import io.galeb.core.model.Backend;
import io.galeb.core.model.Entity;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@DisallowConcurrentExecution
public class BackendUpdaterJob extends AbstractJob {

    private static final long TTL = 10000L;

    private void changeConnections(Entity backend, int conn) {
        ((Backend) backend).setConnections(conn);
        backend.setVersion(farm.getVersion());
        distributedMap.getMap(Backend.class.getName()).put(backend.getId(), backend);
    }

    private void cleanUpConnectionsInfo() {
        farm.getCollection(Backend.class).stream()
            .filter(backendWithTTL -> ((Backend) backendWithTTL).getConnections()>0 &&
                    backendWithTTL.getModifiedAt()<(System.currentTimeMillis()-TTL))
            .forEach(backendWithTTL -> changeConnections(backendWithTTL, 0));
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        setEnvironment(context.getJobDetail().getJobDataMap());
        cleanUpConnectionsInfo();

        eventBus.getMapReduce().reduce().forEach((key, value) -> {
            farm.getCollection(Backend.class).stream()
                .filter(backend -> backend.getId().equals(key))
                .forEach(backend -> changeConnections(backend, value));
        });

        logger.trace(String.format("Job %s done.", this.getClass().getSimpleName()));
    }



}
