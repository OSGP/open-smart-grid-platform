/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects;

import java.util.Objects;

/**
 * A value object, containing the relevant information for setting up a connection to a device:
 *
 * <ul>
 *   <li>Device Identification
 *   <li>IP Address
 *   <li>Common Address
 *   <li>Port
 * </ul>
 */
public class ConnectionParameters {

  private static final String DEFAULT_IP_ADDRESS = "localhost";
  private static final String DEFAULT_DEVICE_IDENTIFICATION = null;
  private static final int DEFAULT_COMMON_ADDRESS = 0;
  private static final int DEFAULT_PORT = 2404;

  final String ipAddress;
  final String deviceIdentification;
  final int commonAddress;
  final Integer port;

  private ConnectionParameters(final Builder builder) {
    this.ipAddress = builder.ipAddress;
    this.deviceIdentification = builder.deviceIdentification;
    this.commonAddress = builder.commonAddress;
    this.port = builder.port;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof ConnectionParameters)) {
      return false;
    }
    final ConnectionParameters that = (ConnectionParameters) obj;
    return Objects.equals(this.ipAddress, that.ipAddress)
        && Objects.equals(this.deviceIdentification, that.deviceIdentification)
        && Objects.equals(this.commonAddress, that.commonAddress)
        && Objects.equals(this.port, that.port);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.ipAddress, this.deviceIdentification, this.commonAddress, this.port);
  }

  @Override
  public String toString() {
    return "ConnectionParameters [ipAddress="
        + this.ipAddress
        + ", deviceIdentification="
        + this.deviceIdentification
        + ", commonAddress="
        + this.commonAddress
        + ", port="
        + this.port
        + "]";
  }

  public static class Builder {
    private String ipAddress = DEFAULT_IP_ADDRESS;
    private String deviceIdentification = DEFAULT_DEVICE_IDENTIFICATION;
    private int commonAddress = DEFAULT_COMMON_ADDRESS;
    private Integer port = DEFAULT_PORT;

    public Builder ipAddress(final String ipAddress) {
      this.ipAddress = ipAddress;
      return this;
    }

    public Builder deviceIdentification(final String deviceIdentification) {
      this.deviceIdentification = deviceIdentification;
      return this;
    }

    public Builder commonAddress(final int commonAddress) {
      this.commonAddress = commonAddress;
      return this;
    }

    public Builder port(final Integer port) {
      this.port = port;
      return this;
    }

    public ConnectionParameters build() {
      return new ConnectionParameters(this);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public String getIpAddress() {
    return this.ipAddress;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public int getCommonAddress() {
    return this.commonAddress;
  }

  public Integer getPort() {
    return this.port;
  }
}
