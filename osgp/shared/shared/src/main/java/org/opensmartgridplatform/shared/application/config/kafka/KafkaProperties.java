/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.application.config.kafka;

import java.util.HashMap;
import java.util.Map;

public class KafkaProperties {

  private KafkaProperties() {
    // hide implicit constructor
  }

  public static Map<String, Class<?>> commonProperties() {
    final HashMap<String, Class<?>> map = new HashMap<>();
    map.put("bootstrap.servers", String.class);

    map.put("security.protocol", String.class);
    map.put("ssl.truststore.location", String.class);
    map.put("ssl.truststore.password", String.class);
    map.put("ssl.keystore.location", String.class);
    map.put("ssl.keystore.password", String.class);
    map.put("ssl.key.password", String.class);
    map.put("ssl.endpoint.identification.algorithm", String.class);
    map.put("ssl.enabled.protocols", String.class);
    map.put("ssl.keystore.type", String.class);
    map.put("ssl.protocol", String.class);
    map.put("ssl.provider", String.class);
    map.put("ssl.truststore.type", String.class);
    map.put("sasl.client.callback.handler.class", String.class);
    map.put("sasl.jaas.config", String.class);
    map.put("sasl.kerberos.service.name", String.class);
    map.put("sasl.login.callback.handler.class", String.class);
    map.put("sasl.login.class", String.class);
    map.put("sasl.mechanism", String.class);

    return map;
  }

  public static Map<String, Class<?>> producerProperties() {
    final HashMap<String, Class<?>> map = new HashMap<>();
    map.put("key.serializer", String.class);
    map.put("value.serializer", String.class);
    map.put("acks", String.class);
    map.put("buffer.memory", Long.class);
    map.put("compression.type", String.class);
    map.put("retries", Integer.class);
    map.put("batch.size", Integer.class);
    map.put("client.id", String.class);
    map.put("delivery.timeout.ms", Integer.class);
    map.put("linger.ms", Integer.class);
    map.put("max.block.ms", Long.class);
    map.put("max.request.size", Integer.class);
    map.put("partitioner.class", String.class);
    map.put("client.dns.lookup", String.class);
    map.put("connections.max.idle.ms", Long.class);
    map.put("receive.buffer.bytes", Integer.class);
    map.put("request.timeout.ms", Integer.class);
    return map;
  }

  public static Map<String, Class<?>> consumerProperties() {
    final HashMap<String, Class<?>> map = new HashMap<>();
    map.put("key.deserializer", String.class);
    map.put("value.deserializer", String.class);
    map.put("group.id", String.class);
    map.put("fetch.min.bytes", Integer.class);
    map.put("heartbeat.interval.ms", Integer.class);
    map.put("max.partition.fetch.bytes", Integer.class);
    map.put("session.timeout.ms", Integer.class);
    map.put("allow.auto.create.topics", Boolean.class);
    map.put("auto.offset.reset", String.class);
    map.put("default.api.timeout.ms", Integer.class);
    map.put("enable.auto.commit", Boolean.class);
    map.put("exclude.internal.topics", Boolean.class);
    map.put("fetch.max.bytes", Integer.class);
    map.put("group.instance.id", String.class);
    map.put("isolation.level", String.class);
    map.put("max.poll.interval.ms", Integer.class);
    map.put("max.poll.records", Integer.class);
    map.put("partition.assignment.strategy", String.class);
    map.put("send.buffer.bytes", Integer.class);
    map.put("client.dns.lookup", String.class);
    map.put("connections.max.idle.ms", Long.class);
    map.put("receive.buffer.bytes", Integer.class);
    map.put("request.timeout.ms", Integer.class);
    return map;
  }
}
