// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetMbusEncryptionKeyStatusResponseData;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EncryptionKeyStatusTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetMbusEncryptionKeyStatusResponseDto;

public class GetMbusEncryptionKeyStatusResponseDataMapperTest {

  private static final String MAPPED_OBJECT_VALUE_MESSAGE = "Mapped object should not be null.";
  private static final String MAPPED_FIELD_VALUE_MESSAGE =
      "Mapped field should have the same value.";

  private static final String MBUS_DEVICE_IDENTIFICATION = "TestMbusDevice";
  private static final EncryptionKeyStatusTypeDto ENCRYPTION_KEY_STATUS =
      EncryptionKeyStatusTypeDto.ENCRYPTION_KEY_IN_USE;

  private final ConfigurationMapper mapper = new ConfigurationMapper();

  @Test
  public void shouldConvertDtoToValueObject() {
    final GetMbusEncryptionKeyStatusResponseDto source = this.makeResponse();
    final GetMbusEncryptionKeyStatusResponseData result =
        this.mapper.map(source, GetMbusEncryptionKeyStatusResponseData.class);

    assertThat(result).withFailMessage(MAPPED_OBJECT_VALUE_MESSAGE).isNotNull();
    assertThat(result.getMbusDeviceIdentification())
        .withFailMessage(MAPPED_FIELD_VALUE_MESSAGE)
        .isEqualTo(source.getMbusDeviceIdentification());
    assertThat(result.getEncryptionKeyStatus().name())
        .withFailMessage(MAPPED_FIELD_VALUE_MESSAGE)
        .isEqualTo(source.getEncryptionKeyStatus().name());
  }

  private GetMbusEncryptionKeyStatusResponseDto makeResponse() {
    final GetMbusEncryptionKeyStatusResponseDto response =
        new GetMbusEncryptionKeyStatusResponseDto(
            MBUS_DEVICE_IDENTIFICATION, ENCRYPTION_KEY_STATUS);
    return response;
  }
}
