/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetMbusEncryptionKeyStatusResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.EncryptionKeyStatusType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetMbusEncryptionKeyStatusResponseData;

public class GetMbusEncryptionKeyStatusResponseMappingTest {

  private static final String MAPPED_OBJECT_NULL_MESSAGE = "Mapped object should not be null.";
  private static final String MAPPED_FIELD_VALUE_MESSAGE =
      "Mapped field should have the same value.";

  private static final String MBUS_DEVICE_IDENTIFICATION = "TestMbusDevice";
  private static final EncryptionKeyStatusType ENCRYPTION_KEY_STATUS =
      EncryptionKeyStatusType.ENCRYPTION_KEY_IN_USE;

  private final ConfigurationMapper mapper = new ConfigurationMapper();

  private GetMbusEncryptionKeyStatusResponseData makeResponse() {

    return new GetMbusEncryptionKeyStatusResponseData(
        MBUS_DEVICE_IDENTIFICATION, ENCRYPTION_KEY_STATUS);
  }

  @Test
  public void shouldConvertGetMbusEncryptionKeyStatusResponse() {
    final GetMbusEncryptionKeyStatusResponseData source = this.makeResponse();
    final GetMbusEncryptionKeyStatusResponse result =
        this.mapper.map(source, GetMbusEncryptionKeyStatusResponse.class);
    assertThat(result).as(MAPPED_OBJECT_NULL_MESSAGE).isNotNull();
    assertThat(result.getMbusDeviceIdentification())
        .as(MAPPED_FIELD_VALUE_MESSAGE)
        .isEqualTo(source.getMbusDeviceIdentification());
    assertThat(result.getEncryptionKeyStatus().name())
        .as(MAPPED_FIELD_VALUE_MESSAGE)
        .isEqualTo(source.getEncryptionKeyStatus().name());
  }
}
