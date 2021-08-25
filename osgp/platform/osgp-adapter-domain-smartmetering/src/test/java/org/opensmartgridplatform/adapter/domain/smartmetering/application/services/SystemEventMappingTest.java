/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SystemEvent;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SystemEventDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SystemEventTypeDto;

class SystemEventMappingTest {

  private final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

  // Test if mapping a SystemEvent object succeeds with null
  // variables, although it
  @Test
  void testWithNullVariables() {
    // build test data
    final String deviceId = null;
    final SystemEventTypeDto systemEventType = null;
    final Date timestamp = null;
    final String reason = null;
    final SystemEventDto systemEventDto =
        new SystemEventDto(deviceId, systemEventType, timestamp, reason);
    // actual mapping
    final SystemEvent systemEvent =
        this.mapperFactory.getMapperFacade().map(systemEventDto, SystemEvent.class);
    // test mapping
    assertThat(systemEvent).isNotNull();
    assertThat(systemEvent.getDeviceIdentification()).isNull();
    assertThat(systemEvent.getSystemEventType()).isNull();
    assertThat(systemEvent.getTimestamp()).isNull();
    assertThat(systemEvent.getReason()).isNull();
  }

  // is not empty
  @Test
  void testWithNonEmptyValue() {
    // build test data
    final String deviceId = "deviceId";
    final SystemEventTypeDto systemEventType =
        SystemEventTypeDto.INVOCATION_COUNTER_THRESHOLD_REACHED;
    final Date timestamp = new Date();
    final String reason = "reason123";
    final SystemEventDto systemEventDto =
        new SystemEventDto(deviceId, systemEventType, timestamp, reason);
    // actual mapping
    final SystemEvent systemEvent =
        this.mapperFactory.getMapperFacade().map(systemEventDto, SystemEvent.class);
    // test mapping
    assertThat(systemEvent).isNotNull();
    assertThat(systemEvent.getDeviceIdentification()).isEqualTo(deviceId);
    assertThat(systemEvent.getSystemEventType().name()).isEqualTo(systemEventType.name());
    assertThat(systemEvent.getTimestamp()).isEqualTo(timestamp);
    assertThat(systemEvent.getReason()).isEqualTo(reason);
  }
}
