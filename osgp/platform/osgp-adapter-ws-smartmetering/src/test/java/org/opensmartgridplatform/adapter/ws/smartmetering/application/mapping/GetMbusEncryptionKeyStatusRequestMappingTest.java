// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetMbusEncryptionKeyStatusRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetMbusEncryptionKeyStatusRequestData;

public class GetMbusEncryptionKeyStatusRequestMappingTest {

  private static final String MAPPED_OBJECT_NULL_MESSAGE = "Mapped object should not be null.";
  private static final String MAPPED_FIELD_VALUE_MESSAGE =
      "Mapped field should have the same value.";

  private static final String MBUS_DEVICE_IDENTIFICATION = "TestMbusDevice";

  private final ConfigurationMapper mapper = new ConfigurationMapper();

  private GetMbusEncryptionKeyStatusRequest makeRequest() {

    final GetMbusEncryptionKeyStatusRequest result = new GetMbusEncryptionKeyStatusRequest();
    result.setMbusDeviceIdentification(MBUS_DEVICE_IDENTIFICATION);
    return result;
  }

  @Test
  public void shouldConvertGetMbusEncryptionKeyStatusDataRequest() {
    final GetMbusEncryptionKeyStatusRequest source = this.makeRequest();
    final GetMbusEncryptionKeyStatusRequestData result =
        this.mapper.map(source, GetMbusEncryptionKeyStatusRequestData.class);
    assertThat(result).as(MAPPED_OBJECT_NULL_MESSAGE).isNotNull();
    assertThat(result.getMbusDeviceIdentification())
        .as(MAPPED_FIELD_VALUE_MESSAGE)
        .isEqualTo(source.getMbusDeviceIdentification());
  }
}
