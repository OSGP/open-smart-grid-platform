/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects;

public class DeviceConnectionParameters {

    final String ipAddress;
    final String deviceIdentification;
    final int commonAddress;
    final int port;

    public DeviceConnectionParameters(final Builder builder) {
        this.ipAddress = builder.ipAddress;
        this.deviceIdentification = builder.deviceIdentification;
        this.commonAddress = builder.commonAddress;
        this.port = builder.port;
    }

    public static class Builder {
        private String ipAddress = null;
        private String deviceIdentification = null;
        private int commonAddress;
        private int port;

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

        public Builder port(final int port) {
            this.port = port;
            return this;
        }

        public DeviceConnectionParameters build() {
            return new DeviceConnectionParameters(this);
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

    public int getPort() {
        return this.port;
    }

}