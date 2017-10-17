/*
 * Copyright 2017 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.netflix.titus.master.agent.store;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;

import io.netflix.titus.api.agent.model.AgentInstance;
import io.netflix.titus.api.agent.model.AgentInstanceGroup;
import io.netflix.titus.api.agent.store.AgentStore;
import io.netflix.titus.common.util.guice.annotation.Activator;
import io.netflix.titus.common.util.rx.ObservableExt;
import io.netflix.titus.common.util.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Completable;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * Removes {@link AgentInstanceGroup} entities tagged with {@link AgentStoreReaper#ATTR_REMOVED} from the store,
 * including their associated instances.
 */
@Singleton
public class AgentStoreReaper {

    private static final Logger logger = LoggerFactory.getLogger(AgentStoreReaper.class);

    public static final String ATTR_REMOVED = "instanceGroupRemoved";

    private static final long EXPIRED_DATA_RETENCY_PERIOD_MS = 3600_000;

    private static final long STORAGE_GC_INTERVAL_MS = 600_000;

    private final AgentStore agentStore;
    private final Scheduler scheduler;

    private Subscription reaperSubscription;

    @Inject
    public AgentStoreReaper(AgentStore agentStore) {
        this(agentStore, Schedulers.computation());
    }

    public AgentStoreReaper(AgentStore agentStore, Scheduler scheduler) {
        this.agentStore = agentStore;
        this.scheduler = scheduler;
    }

    @Activator
    public void enterActiveMode() {
        this.reaperSubscription = ObservableExt.schedule(
                doStoreGarbageCollection(),
                STORAGE_GC_INTERVAL_MS, STORAGE_GC_INTERVAL_MS, TimeUnit.MILLISECONDS,
                scheduler
        ).subscribe(next -> next.ifPresent(e -> logger.warn("Store garbage collection error", e)));
    }

    @PreDestroy
    public void shutdown() {
        ObservableExt.safeUnsubscribe(reaperSubscription);
    }

    private Completable doStoreGarbageCollection() {
        return Observable.zip(
                agentStore.retrieveAgentInstanceGroups().toList(),
                agentStore.retrieveAgentInstances().toList(),
                Pair::of
        ).flatMap(pair -> {
                    Pair<List<String>, List<String>> recordsToRemove = findRecordsToRemove(pair);
                    return toRemoveActions(recordsToRemove.getLeft(), recordsToRemove.getRight()).toObservable();
                }
        ).toCompletable();
    }

    private boolean shouldBeRemoved(AgentInstanceGroup sg) {
        String removedTimestampStr = sg.getAttributes().get(ATTR_REMOVED);
        if (removedTimestampStr == null) {
            return false;
        }
        try {
            long removedTimestamp = Long.parseLong(removedTimestampStr);
            return (removedTimestamp + EXPIRED_DATA_RETENCY_PERIOD_MS) < scheduler.now();
        } catch (Exception e) {
            logger.warn("Invalid {} value={} found for instance group record {}", ATTR_REMOVED, removedTimestampStr, sg.getId());
            return true;
        }
    }

    private Pair<List<String>, List<String>> findRecordsToRemove(Pair<List<AgentInstanceGroup>, List<AgentInstance>> pair) {
        List<AgentInstanceGroup> instanceGroups = pair.getLeft();
        List<AgentInstance> instances = pair.getRight();

        List<AgentInstanceGroup> instanceGroupsToRemove = instanceGroups.stream().filter(this::shouldBeRemoved).collect(Collectors.toList());
        List<String> instanceGroupIdsToRemove = instanceGroupsToRemove.stream().map(AgentInstanceGroup::getId).collect(Collectors.toList());

        List<AgentInstance> instancesToRemove = instances.stream()
                .filter(i -> instanceGroupIdsToRemove.contains(i.getInstanceGroupId()))
                .collect(Collectors.toList());
        List<String> instanceIdsToRemove = instancesToRemove.stream().map(AgentInstance::getId).collect(Collectors.toList());

        logger.info("Removing records of non-existing instance groups: {}", instanceGroupIdsToRemove);
        logger.info("Removing agent instance records of non-existing instance groups: {}", instanceIdsToRemove);

        return Pair.of(instanceGroupIdsToRemove, instanceIdsToRemove);
    }

    private Completable toRemoveActions(List<String> instanceGroupIds, List<String> instanceIds) {
        return agentStore.removeAgentInstances(instanceIds).concatWith(agentStore.removeAgentInstanceGroups(instanceGroupIds));
    }
}
