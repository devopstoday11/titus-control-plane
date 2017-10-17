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

package io.netflix.titus.runtime.endpoint.v3.grpc.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.netflix.titus.grpc.protogen.JobDescriptor;
import com.netflix.titus.grpc.protogen.TaskStatus;
import io.netflix.titus.api.jobmanager.model.job.Job;
import io.netflix.titus.api.jobmanager.model.job.Task;
import io.netflix.titus.api.jobmanager.model.job.TaskState;
import io.netflix.titus.common.util.tuple.Pair;
import io.netflix.titus.runtime.endpoint.JobQueryCriteria;
import io.netflix.titus.runtime.endpoint.v3.grpc.V3GrpcModelConverters;

public class V3TaskQueryCriteriaEvaluator extends V3AbstractQueryCriteriaEvaluator<Task> {

    public V3TaskQueryCriteriaEvaluator(JobQueryCriteria<TaskStatus.TaskState, JobDescriptor.JobSpecCase> criteria) {
        super(createTaskPredicates(criteria), criteria);
    }

    private static List<Predicate<Pair<Job<?>, Task>>> createTaskPredicates(JobQueryCriteria<TaskStatus.TaskState, JobDescriptor.JobSpecCase> criteria) {
        List<Predicate<Pair<Job<?>, Task>>> predicates = new ArrayList<>();
        applyJobIds(criteria.getJobIds()).ifPresent(predicates::add);
        applyTaskIds(criteria.getTaskIds()).ifPresent(predicates::add);
        applyTaskStates(criteria.getTaskStates()).ifPresent(predicates::add);
        return predicates;
    }

    private static Optional<Predicate<Pair<Job<?>, Task>>> applyJobIds(Set<String> jobIds) {
        if (jobIds.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(jobAndTask -> jobIds.contains(jobAndTask.getLeft().getId()));
    }

    private static Optional<Predicate<Pair<Job<?>, Task>>> applyTaskIds(Set<String> taskIds) {
        if (taskIds.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(jobAndTask -> taskIds.contains(jobAndTask.getRight().getId()));
    }

    private static Optional<Predicate<Pair<Job<?>, Task>>> applyTaskStates(Set<TaskStatus.TaskState> taskStates) {
        if (taskStates.isEmpty()) {
            return Optional.empty();
        }
        Set<TaskState> coreTaskStates = taskStates.stream().map(V3GrpcModelConverters::toCoreTaskState).collect(Collectors.toSet());
        return Optional.of(jobAndTasks -> {
            Task task = jobAndTasks.getRight();
            return coreTaskStates.contains(task.getStatus().getState());
        });
    }
}
