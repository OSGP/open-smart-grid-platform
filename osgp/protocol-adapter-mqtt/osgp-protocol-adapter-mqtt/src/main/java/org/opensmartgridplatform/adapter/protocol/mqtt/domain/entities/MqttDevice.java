/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.mqtt.domain.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;

@Entity
@Table(name = "mqtt_device")
public class MqttDevice extends AbstractEntity {

  private static final long serialVersionUID = 182081847594067712L;

  @Column(unique = true, nullable = false, length = 40)
  private String deviceIdentification;

  @Column private String host;

  @Column private Integer port;

  @Column private String topics;

  @Column private String qos;

  public MqttDevice() {
    // Default constructor
  }

  public MqttDevice(final String deviceIdentification) {
    this.deviceIdentification = deviceIdentification;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public String getHost() {
    return this.host;
  }

  public void setHost(final String host) {
    this.host = host;
  }

  public Integer getPort() {
    return this.port;
  }

  public void setPort(final Integer port) {
    this.port = port;
  }

  public String[] getTopics() {
    if (StringUtils.isBlank(this.topics)) {
      return new String[] {};
    }
    return StringUtils.split(this.topics, ',');
  }

  public void setTopics(final String[] topics) {
    if (ArrayUtils.isEmpty(topics)) {
      this.topics = null;
    }
    this.topics = StringUtils.join(topics, ',');
  }

  public String getQos() {
    return this.qos;
  }

  public void setQos(final String qos) {
    this.qos = qos;
  }

  @Override
  public String toString() {
    return String.format(
        "MqttDevice[deviceId=%s, host=%s, port=%s, , topic=%s]",
        this.deviceIdentification, this.host, this.port, this.topics);
  }
}
