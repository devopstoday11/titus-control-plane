/*
 * Copyright 2019 Netflix, Inc.
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

package com.netflix.titus.runtime.endpoint.admission;

import java.util.Set;
import java.util.function.UnaryOperator;

import com.netflix.titus.api.jobmanager.model.job.JobDescriptor;
import com.netflix.titus.common.model.admission.AdmissionSanitizer;
import com.netflix.titus.common.model.admission.AdmissionValidator;
import com.netflix.titus.common.model.sanitizer.ValidationError;
import reactor.core.publisher.Mono;

class EmptyValidator implements AdmissionValidator<JobDescriptor>, AdmissionSanitizer<JobDescriptor> {
    @Override
    public Mono<Set<ValidationError>> validate(JobDescriptor entity) {
        return Mono.empty();
    }

    @Override
    public ValidationError.Type getErrorType() {
        return ValidationError.Type.HARD;
    }

    @Override
    public Mono<UnaryOperator<JobDescriptor>> sanitize(JobDescriptor entity) {
        return Mono.empty();
    }
}
