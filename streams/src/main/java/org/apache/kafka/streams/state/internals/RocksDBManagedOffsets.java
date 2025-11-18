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
import org.apache.kafka.streams.query.Position;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RocksDBManagedOffsets {

    private final ConcurrentHashMap<TopicPartition, Long> offsets;

    public RocksDBManagedOffsets() {
        this.offsets = new ConcurrentHashMap<>();
    }

    public RocksDBManagedOffsets(final Map<TopicPartition, Long> offsets) {
        this.offsets = new ConcurrentHashMap<>(offsets);
    }

    public Long get(final TopicPartition tp) {
        return offsets.get(tp);
    }

    public Long remove(final TopicPartition tp) {
        return offsets.remove(tp);
    }

    public void put(final TopicPartition tp, final long offset) {
        offsets.put(tp, offset);
    }

    public Set<Map.Entry<TopicPartition, Long>> listAll() {
        return offsets.entrySet();
    }

    public Position toPosition() {
        final Map<String, Map<Integer, Long>> all = new ConcurrentHashMap<>();
        offsets.forEach((tp, offset) -> {
            final Map<Integer, Long> topicOffsets = all.computeIfAbsent(tp.topic(), k -> new ConcurrentHashMap<>());
            topicOffsets.put(tp.partition(), offset);
        });
        return Position.fromMap(all);
    }

    public Map<TopicPartition, Long> getOffsets() {
        return Collections.unmodifiableMap(offsets);
    }
}
