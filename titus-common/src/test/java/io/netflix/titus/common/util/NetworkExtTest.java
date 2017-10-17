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

package io.netflix.titus.common.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NetworkExtTest {

    @Test
    public void testToIPv4() throws Exception {
        long address = (((((1L << 8) | 1) << 8) | 1) << 8) | 1;
        assertThat(NetworkExt.toIPv4(address)).isEqualTo("1.1.1.1");
    }
}