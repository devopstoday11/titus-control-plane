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

package io.netflix.titus.master.endpoint.v2.rest.provider;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.netflix.spectator.api.Registry;
import com.sun.jersey.spi.container.ResourceMethodDispatchAdapter;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;
import io.netflix.titus.master.endpoint.v2.rest.RestConfig;
import io.netflix.titus.master.endpoint.v2.rest.metric.InstrumentedResourceMethodDispatchProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class InstrumentedResourceMethodDispatchAdapter implements ResourceMethodDispatchAdapter {

    private static final Logger logger = LoggerFactory.getLogger(InstrumentedResourceMethodDispatchAdapter.class);

    private final RestConfig config;
    private final Registry registry;

    @Inject
    public InstrumentedResourceMethodDispatchAdapter(RestConfig config, Registry registry) {
        this.config = config;
        this.registry = registry;
    }

    @Override
    public ResourceMethodDispatchProvider adapt(ResourceMethodDispatchProvider provider) {
        return new InstrumentedResourceMethodDispatchProvider(config, registry, provider);
    }
}
