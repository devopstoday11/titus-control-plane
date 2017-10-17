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

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.netflix.archaius.ConfigProxyFactory;
import com.netflix.governator.guice.jersey.GovernatorJerseySupportModule;
import io.netflix.titus.common.runtime.TitusRuntime;
import io.netflix.titus.common.runtime.internal.DefaultTitusRuntime;
import io.netflix.titus.master.agent.AgentModule;
import io.netflix.titus.master.agent.endpoint.AgentEndpointModule;
import io.netflix.titus.master.appscale.endpoint.v3.AutoScalingModule;
import io.netflix.titus.master.audit.service.AuditModule;
import io.netflix.titus.master.cluster.DefaultLeaderActivator;
import io.netflix.titus.master.cluster.DefaultLeaderElector;
import io.netflix.titus.master.cluster.LeaderActivator;
import io.netflix.titus.master.cluster.LeaderElector;
import io.netflix.titus.master.config.MasterConfiguration;
import io.netflix.titus.master.endpoint.EndpointModule;
import io.netflix.titus.master.endpoint.common.ContextResolver;
import io.netflix.titus.master.endpoint.common.EmptyContextResolver;
import io.netflix.titus.master.endpoint.v2.rest.JerseyModule;
import io.netflix.titus.master.endpoint.v2.rest.caller.CallerIdResolver;
import io.netflix.titus.master.endpoint.v2.rest.caller.HttpCallerIdResolver;
import io.netflix.titus.master.endpoint.v2.validator.ValidatorConfiguration;
import io.netflix.titus.master.job.JobModule;
import io.netflix.titus.master.jobmanager.endpoint.v3.V3EndpointModule;
import io.netflix.titus.master.jobmanager.service.V3JobManagerModule;
import io.netflix.titus.master.master.MasterDescription;
import io.netflix.titus.master.master.MasterMonitor;
import io.netflix.titus.master.master.ZookeeperMasterMonitor;
import io.netflix.titus.master.mesos.MesosModule;
import io.netflix.titus.master.scheduler.SchedulerModule;
import io.netflix.titus.master.service.management.ManagementModule;
import io.netflix.titus.master.store.StoreModule;
import io.netflix.titus.master.taskmigration.TaskMigratorModule;
import io.netflix.titus.master.zookeeper.ZookeeperPaths;
import io.netflix.titus.runtime.TitusEntitySanitizerModule;
import io.netflix.titus.runtime.endpoint.common.EmptyLogStorageInfo;

/**
 * Main TitusMaster guice module.
 */
public class TitusMasterModule extends AbstractModule {

    @Override
    protected void configure() {
        // Configuration
        bind(CoreConfiguration.class).to(MasterConfiguration.class);

        bind(TitusRuntime.class).to(DefaultTitusRuntime.class);

        install(new TitusEntitySanitizerModule());

        // Mesos
        install(new MesosModule());

        // TitusMaster monitor / leader election
        bind(MasterMonitor.class).to(ZookeeperMasterMonitor.class);
        bind(LeaderElector.class).to(DefaultLeaderElector.class).asEagerSingleton();
        bind(LeaderActivator.class).to(DefaultLeaderActivator.class);

        // Storage
        install(new StoreModule());

        // Service
        install(new AuditModule());
        install(new AgentModule());
        install(new SchedulerModule());
        install(new JobModule());
        install(new V3JobManagerModule());
        install(new ManagementModule());

        // Remaining services
        bind(ApiOperations.class).to(ApiOperationsImpl.class);

        // REST/GRPC
        bind(JerseyModule.V2_LOG_STORAGE_INFO).toInstance(EmptyLogStorageInfo.INSTANCE);
        bind(V3EndpointModule.V3_LOG_STORAGE_INFO).toInstance(EmptyLogStorageInfo.INSTANCE);
        bind(ContextResolver.class).toInstance(EmptyContextResolver.INSTANCE);

        install(new GovernatorJerseySupportModule());

        // This should be in JerseyModule, but overrides get broken if we do that (possibly Governator bug).
        bind(CallerIdResolver.class).to(HttpCallerIdResolver.class);
        install(new JerseyModule());

        install(new EndpointModule());
        install(new V3EndpointModule());
        install(new AgentEndpointModule());
        install(new AutoScalingModule());

        install(new TaskMigratorModule());
    }

    @Provides
    @Singleton
    public MasterConfiguration getMasterConfiguration(ConfigProxyFactory factory) {
        return factory.newProxy(MasterConfiguration.class);
    }

    @Provides
    @Singleton
    public ValidatorConfiguration getValidatorConfiguration(ConfigProxyFactory factory) {
        return factory.newProxy(ValidatorConfiguration.class);
    }

    @Provides
    @Singleton
    public MasterDescription getMasterDescription(MasterConfiguration configuration) {
        return MasterDescriptions.create(configuration);
    }

    @Provides
    @Singleton
    public ZookeeperPaths getZookeeperPaths(MasterConfiguration configuration) {
        return new ZookeeperPaths(configuration.getZkRoot());
    }
}
