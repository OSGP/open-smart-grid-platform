/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.mqtt;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class MqttNotification implements Serializable {

    private static final long serialVersionUID = 7659422285786844390L;

    public enum Type {
        MEASUREMENT,
        CONGESTION
    }

    public static class Builder {

        //        Meting:
        //        ean_code; voltage_L1; voltage_L2; voltage_L3; stroom_in_L1; stroom_in_L2; stroom_in_L3;
        //        stroom_teruglevering_L1; stroom_teruglevering_L2; stroom_terugleving_L3;
        //
        //        Congestie-signaal:
        //        ean_code; stroom_in_L1; stroom_in_L2; stroom_in_L3; stroom_teruglevering_L1;
        //        stroom_teruglevering_L2; stroom_terugleving_L3;
        //
        private String equipmentIdentifier;
        private Type type;
        private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        public Builder appendByte(final byte b) {
            this.baos.write(b);
            return this;
        }

        public Builder appendBytes(final byte[] bytes) {
            if (bytes != null) {
                for (final byte b : bytes) {
                    this.baos.write(b);
                }
            }
            return this;
        }

        public Builder withEquipmentIdentifier(final String equipmentIdentifier) {
            this.equipmentIdentifier = equipmentIdentifier;
            return this;
        }

        public Builder withType(final Type type) {
            this.type = type;
            return this;
        }

        public MqttNotification build() {
            return new MqttNotification(this.equipmentIdentifier, this.type);
        }
    }

    private final String equipmentIdentifier;
    private final Type type;

    public MqttNotification(final String equipmentIdentifier, final Type type) {
        this.equipmentIdentifier = equipmentIdentifier;
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("MqttNotification [ean = %s, type = %s]", this.equipmentIdentifier, this.type);
    }

    public String getEquipmentIdentifier() {
        return this.equipmentIdentifier;
    }

    public Type getType() {
        return this.type;
    }

}
