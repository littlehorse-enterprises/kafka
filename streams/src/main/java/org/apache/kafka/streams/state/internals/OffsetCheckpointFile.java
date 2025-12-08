/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.streams.state.internals;

import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.utils.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.Collections;
import java.util.Map;

public class OffsetCheckpointFile extends OffsetCheckpoint {

    private final File file;
    private static final Logger LOG = LoggerFactory.getLogger(OffsetCheckpointFile.class);

    public OffsetCheckpointFile(final File file) {
        super(LOG);
        this.file = file;
    }

    @Override
    public void write(final Map<TopicPartition, Long> offsets) throws IOException {
        // if there are no offsets, skip writing the file to save disk IOs
        // but make sure to delete the existing file if one exists
        if (offsets.isEmpty()) {
            Utils.delete(file);
            return;
        }

        synchronized (lock) {
            // write to temp file and then swap with the existing file
            final File temp = new File(file.getAbsolutePath() + ".tmp");
            LOG.trace("Writing tmp checkpoint file {}", temp.getAbsolutePath());

            try (final FileOutputStream fileOutputStream = new FileOutputStream(temp)) {
                writeBuffer(offsets, fileOutputStream);
                fileOutputStream.flush();
            }

            LOG.trace("Swapping tmp checkpoint file {} {}", temp.toPath(), file.toPath());
            Utils.atomicMoveWithFallback(temp.toPath(), file.toPath());
        }
    }

    /**
     * Reads the offsets from the local checkpoint file, skipping any negative offsets it finds.
     *
     * @throws IOException if any file operation fails with an IO exception
     * @throws IllegalArgumentException if the offset checkpoint version is unknown
     */
    public Map<TopicPartition, Long> read() throws IOException {
        synchronized (lock) {
            try (final BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
                return readBuffer(reader);
            } catch (final NoSuchFileException e) {
                return Collections.emptyMap();
            }
        }
    }

    /**
     * @throws IOException if there is any IO exception during delete
     */
    public void delete() throws IOException {
        Files.deleteIfExists(file.toPath());
    }

    @Override
    public String toString() {
        return file.getAbsolutePath();
    }
}
