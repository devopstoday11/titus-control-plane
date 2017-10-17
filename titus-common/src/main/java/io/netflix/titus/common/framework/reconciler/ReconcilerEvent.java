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

package io.netflix.titus.common.framework.reconciler;

import java.util.Optional;

/**
 */
public interface ReconcilerEvent {

    enum EventType {
        ChangeRequest,
        Changed,
        ChangeError,
        ModelInitial,
        ModelUpdated,
        ModelUpdateError
    }

    interface ReconcileEventFactory {
        ReconcilerEvent newChangeEvent(ReconcilerEvent.EventType eventType, ChangeAction changeAction, Optional<Throwable> error);

        ReconcilerEvent newModelUpdateEvent(ReconcilerEvent.EventType eventType,
                                            ModelUpdateAction modelUpdateAction,
                                            Optional<EntityHolder> changedEntityHolder,
                                            Optional<EntityHolder> previousEnityHolder,
                                            Optional<Throwable> error);
    }
}
