/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.redis;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

/**
 * @author OSGP
 *
 */
public class AddMeterPublisher {
    private final RedisTemplate<String, Object> template;
    private final ChannelTopic topic;
    private final AtomicLong counter = new AtomicLong(0);

    public AddMeterPublisher(final RedisTemplate<String, Object> template, final ChannelTopic topic) {
        this.template = template;
        this.topic = topic;
    }

    // @Scheduled(initialDelay = 6000, fixedDelay = 5000)
    // public void publish() {
    // this.template.convertAndSend(this.topic.getTopic(), "Message " +
    // this.counter.incrementAndGet() + ", "
    // + Thread.currentThread().getName());
    // }

    public void publish(final String message) {
        this.template.convertAndSend(this.topic.getTopic(), message);
    }
}
