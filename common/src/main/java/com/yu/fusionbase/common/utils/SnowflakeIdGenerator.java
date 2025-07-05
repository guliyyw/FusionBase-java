package com.yu.fusionbase.common.utils;

import org.springframework.stereotype.Component;

@Component
public class SnowflakeIdGenerator implements IdGenerator {
    private final long datacenterId;
    private final long machineId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SnowflakeIdGenerator() {
        this.datacenterId = 1L;
        this.machineId = 1L;
    }

    public SnowflakeIdGenerator(long datacenterId, long machineId) {
        this.datacenterId = datacenterId;
        this.machineId = machineId;
    }

    @Override
    public synchronized String nextId() {
        long currentTimestamp = System.currentTimeMillis();

        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id");
        }

        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & 4095;
            if (sequence == 0) {
                currentTimestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = currentTimestamp;

        return String.valueOf(((currentTimestamp - 1288834974657L) << 22)
                | (datacenterId << 17)
                | (machineId << 12)
                | sequence);
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}