/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dlms;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto;

public class DlmsPushNotification implements Serializable {

  private static final long serialVersionUID = 1408450287084256721L;

  public static class Builder {

    private String equipmentIdentifier;
    private String triggerType;
    private EnumSet<AlarmTypeDto> alarms = EnumSet.noneOf(AlarmTypeDto.class);
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

    public Builder withTriggerType(final String triggerType) {
      this.triggerType = triggerType;
      return this;
    }

    public Builder withAlarms(final Set<AlarmTypeDto> alarms) {
      if (alarms == null || alarms.isEmpty()) {
        this.alarms = EnumSet.noneOf(AlarmTypeDto.class);
      } else {
        this.alarms = EnumSet.copyOf(alarms);
      }
      return this;
    }

    public Builder addAlarms(final Set<AlarmTypeDto> alarms) {
      if (alarms != null) {
        this.alarms.addAll(EnumSet.copyOf(alarms));
      }
      return this;
    }

    public DlmsPushNotification build() {
      return new DlmsPushNotification(
          this.baos.toByteArray(), this.equipmentIdentifier, this.triggerType, this.alarms);
    }
  }

  private final String equipmentIdentifier;
  private final String triggerType;
  private final EnumSet<AlarmTypeDto> alarms;
  private final byte[] bytes;

  public DlmsPushNotification(
      final byte[] bytes,
      final String equipmentIdentifier,
      final String triggerType,
      final Set<AlarmTypeDto> alarms) {
    if (bytes == null) {
      this.bytes = new byte[0];
    } else {
      this.bytes = Arrays.copyOf(bytes, bytes.length);
    }
    this.equipmentIdentifier = equipmentIdentifier;
    this.triggerType = triggerType;
    if (alarms == null || alarms.isEmpty()) {
      this.alarms = EnumSet.noneOf(AlarmTypeDto.class);
    } else {
      this.alarms = EnumSet.copyOf(alarms);
    }
  }

  public boolean isValid() {
    return !StringUtils.isEmpty(this.equipmentIdentifier)
        && (!this.alarms.isEmpty() || !"".equals(this.triggerType));
  }

  @Override
  public String toString() {
    return String.format(
        "DlmsPushNotification [device = %s, trigger type = %s, alarms=%s]",
        this.equipmentIdentifier, this.triggerType, this.alarms);
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

  public String getTriggerType() {
    return this.triggerType;
  }

  public Set<AlarmTypeDto> getAlarms() {
    return EnumSet.copyOf(this.alarms);
  }
}
