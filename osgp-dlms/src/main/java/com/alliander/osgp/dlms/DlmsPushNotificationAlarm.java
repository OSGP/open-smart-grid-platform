/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dlms;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import org.apache.cxf.common.util.StringUtils;

import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmType;

public class DlmsPushNotificationAlarm implements Serializable {

    private static final long serialVersionUID = 1408450287084256721L;

    public static class Builder {

        private String equipmentIdentifier;
        private EnumSet<AlarmType> alarms;
        private ByteArrayOutputStream baos = new ByteArrayOutputStream();

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

        public Builder withAlarms(final Set<AlarmType> alarms) {
            if (alarms == null || alarms.isEmpty()) {
                this.alarms = EnumSet.noneOf(AlarmType.class);
            } else {
                this.alarms = EnumSet.copyOf(alarms);
            }
            return this;
        }

        public DlmsPushNotificationAlarm build() {
            return new DlmsPushNotificationAlarm(this.baos.toByteArray(), this.equipmentIdentifier, this.alarms);
        }
    }

    private final byte[] bytes;
    private final String equipmentIdentifier;
    private final EnumSet<AlarmType> alarms;

    public DlmsPushNotificationAlarm(final String equipmentIdentifier, final Set<AlarmType> alarms) {
        this(new byte[0], equipmentIdentifier, alarms);
    }

    public DlmsPushNotificationAlarm(final byte[] bytes, final String equipmentIdentifier, final Set<AlarmType> alarms) {
        if (bytes == null) {
            this.bytes = new byte[0];
        } else {
            this.bytes = Arrays.copyOf(bytes, bytes.length);
        }
        this.equipmentIdentifier = equipmentIdentifier;
        if (alarms == null || alarms.isEmpty()) {
            this.alarms = EnumSet.noneOf(AlarmType.class);
        } else {
            this.alarms = EnumSet.copyOf(alarms);
        }
    }

    public boolean isValid() {
        return !StringUtils.isEmpty(this.equipmentIdentifier) && !this.alarms.isEmpty();
    }

    @Override
    public String toString() {
        return String.format("DlmsPushNotificationAlarm[device=%s, alarms=%s]", this.equipmentIdentifier, this.alarms);
    }

    public byte[] toByteArray() {
        return Arrays.copyOf(this.bytes, this.bytes.length);
    }

    public int getSize() {
        return this.bytes.length;
    }

    public String getEquipmentIdentifier() {
        return this.equipmentIdentifier;
    }

    public Set<AlarmType> getAlarms() {
        return EnumSet.copyOf(this.alarms);
    }
}
