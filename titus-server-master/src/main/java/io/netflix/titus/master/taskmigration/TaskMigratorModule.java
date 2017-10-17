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

package io.netflix.titus.master.taskmigration;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.netflix.archaius.ConfigProxyFactory;
import io.netflix.titus.master.taskmigration.job.DefaultTaskMigrationManagerFactory;
import io.netflix.titus.master.taskmigration.job.ServiceJobTaskMigrator;
import io.netflix.titus.master.taskmigration.job.ServiceJobTaskMigratorConfig;

public class TaskMigratorModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(TaskMigrator.class).to(ServiceJobTaskMigrator.class);
        bind(TaskMigrationManagerFactory.class).to(DefaultTaskMigrationManagerFactory.class);
    }

    @Provides
    @Singleton
    public ServiceJobTaskMigratorConfig getServiceJobTaskMigratorConfig(ConfigProxyFactory factory) {
        return factory.newProxy(ServiceJobTaskMigratorConfig.class);
    }

}
