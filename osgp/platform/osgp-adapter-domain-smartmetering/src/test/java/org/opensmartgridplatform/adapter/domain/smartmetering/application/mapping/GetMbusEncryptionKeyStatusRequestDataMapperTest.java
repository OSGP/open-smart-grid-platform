/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetMbusEncryptionKeyStatusRequestData;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetMbusEncryptionKeyStatusRequestDto;

public class GetMbusEncryptionKeyStatusRequestDataMapperTest {

  private static final String MAPPED_OBJECT_VALUE_MESSAGE = "Mapped object should not be null.";
  private static final String MAPPED_FIELD_VALUE_MESSAGE =
      "Mapped field should have the same value.";

  private static final String MBUS_DEVICE_IDENTIFICATION = "TestMbusDevice";
  private static final Short CHANNEL = 1;

  private final ConfigurationMapper mapper = new ConfigurationMapper();

  @Test
  public void shouldConvertValueObjectToDto() {
    final GetMbusEncryptionKeyStatusRequestData source = this.makeRequest();
    final GetMbusEncryptionKeyStatusRequestDto result =
        this.mapper.map(source, GetMbusEncryptionKeyStatusRequestDto.class);

    assertThat(result).withFailMessage(MAPPED_OBJECT_VALUE_MESSAGE).isNotNull();
    assertThat(result.getMbusDeviceIdentification())
        .withFailMessage(MAPPED_FIELD_VALUE_MESSAGE)
        .isEqualTo(source.getMbusDeviceIdentification());
    assertThat(result.getChannel())
        .withFailMessage(MAPPED_FIELD_VALUE_MESSAGE)
        .isEqualTo(source.getChannel());
  }

  private GetMbusEncryptionKeyStatusRequestData makeRequest() {
    final GetMbusEncryptionKeyStatusRequestData request =
        new GetMbusEncryptionKeyStatusRequestData(MBUS_DEVICE_IDENTIFICATION);
    request.setChannel(CHANNEL);
    return request;
  }
}
