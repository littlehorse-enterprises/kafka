package org.apache.kafka.streams.errors;

import org.apache.kafka.common.TopicPartition;

import java.util.Set;

public class StreamsProducerException extends StreamsException {
    private final Set<TopicPartition> partitions;

    public StreamsProducerException(String message, Set<TopicPartition> partitions, Exception e) {
        super(message, e);
        this.partitions = partitions;
    }
}
