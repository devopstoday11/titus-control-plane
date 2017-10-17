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

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.List;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IOExtTest {

    @Test
    public void testReadLines() throws Exception {
        File file = File.createTempFile("junit", ".txt", new File("build"));
        try (Writer fwr = new FileWriter(file)) {
            fwr.write("line1\n");
            fwr.write("line2");
        }

        List<String> lines = IOExt.readLines(file);
        assertThat(lines).containsExactly("line1", "line2");
    }
}