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

package io.netflix.titus.common.framework.reconciler.internal;

import java.util.List;
import java.util.function.Function;

import io.netflix.titus.common.framework.reconciler.ChangeAction;
import io.netflix.titus.common.framework.reconciler.EntityHolder;
import io.netflix.titus.common.framework.reconciler.ReconciliationEngine.DifferenceResolver;

public class DispatchingDifferenceResolver implements DifferenceResolver {

    private final Function<EntityHolder, DifferenceResolver> dispatcherFun;

    public DispatchingDifferenceResolver(Function<EntityHolder, DifferenceResolver> dispatcherFun) {
        this.dispatcherFun = dispatcherFun;
    }

    @Override
    public List<ChangeAction> apply(EntityHolder referenceModel, EntityHolder runningModel, EntityHolder storeModel) {
        return dispatcherFun.apply(referenceModel).apply(referenceModel, runningModel, storeModel);
    }
}
