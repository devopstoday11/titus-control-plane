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

package io.netflix.titus.master;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.netflix.titus.api.model.v2.JsonType;
import io.netflix.titus.api.model.v2.WorkerAssignments;

public class JobSchedulingInfo implements JsonType {

    private String jobId;
    private Map<Integer, WorkerAssignments> workerAssignments; // index by stage num

    @JsonCreator
    @JsonIgnoreProperties(ignoreUnknown = true)
    public JobSchedulingInfo(@JsonProperty("jobId") String jobId,
                             @JsonProperty("workerAssignments")
                                     Map<Integer, WorkerAssignments> workerAssignments) {
        this.jobId = jobId;
        this.workerAssignments = workerAssignments;
    }

    public String getJobId() {
        return jobId;
    }

    public Map<Integer, WorkerAssignments> getWorkerAssignments() {
        return workerAssignments;
    }

    @Override
    public String toString() {
        return "SchedulingChange [jobId=" + jobId + ", workerAssignments="
                + workerAssignments + "]";
    }
}
