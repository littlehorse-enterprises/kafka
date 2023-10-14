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
package org.apache.kafka.test;

import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.streams.processor.StandbyUpdateListener;

import java.util.HashMap;
import java.util.Map;

public class MockStandbyUpdateListener implements StandbyUpdateListener {

    public final Map<String, String> storeNameCalledUpdate = new HashMap<>();
    public static final String UPDATE_BATCH = "update_batch";

    @Override
    public void onUpdateStart(TopicPartition topicPartition, String storeName, long earliestOffset, long startingOffset, long currentEndOffset) {

    }

    @Override
    public void onBatchUpdated(TopicPartition topicPartition, String storeName, long batchEndOffset, long numRestored, long currentEndOffset) {
        storeNameCalledUpdate.put(UPDATE_BATCH, storeName);
    }

    @Override
    public void onUpdateSuspended(TopicPartition topicPartition, String storeName, long storeOffset, long currentEndOffset, SuspendReason reason) {

    }
}
