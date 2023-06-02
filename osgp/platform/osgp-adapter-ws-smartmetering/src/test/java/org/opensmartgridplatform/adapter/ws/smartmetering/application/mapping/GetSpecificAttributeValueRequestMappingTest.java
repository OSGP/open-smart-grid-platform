//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ObisCodeValues;

public class GetSpecificAttributeValueRequestMappingTest {

  private final AdhocMapper mapper = new AdhocMapper();

  private GetSpecificAttributeValueRequest makeRequest() {
    final GetSpecificAttributeValueRequest result = new GetSpecificAttributeValueRequest();
    final ObisCodeValues obiscode = new ObisCodeValues();
    obiscode.setA((short) 1);
    result.setObisCode(obiscode);
    result.setDeviceIdentification("12345");
    return result;
  }

  @Test
  public void test() {
    final GetSpecificAttributeValueRequest req1 = this.makeRequest();
    final Object obj1 =
        this.mapper.map(
            req1,
            org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                .SpecificAttributeValueRequest.class);

    assertThat(
            (obj1 != null)
                && (obj1
                    instanceof
                    org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                        .SpecificAttributeValueRequest))
        .isTrue();

    final Object obj2 = this.mapper.map(obj1, GetSpecificAttributeValueRequest.class);
    assertThat((obj2 != null) && (obj2 instanceof GetSpecificAttributeValueRequest)).isTrue();

    final GetSpecificAttributeValueRequest req2 = (GetSpecificAttributeValueRequest) obj2;
    assertThat(req1.getDeviceIdentification().equals(req2.getDeviceIdentification())).isTrue();
  }
}
