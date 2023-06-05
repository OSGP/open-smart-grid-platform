// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ObisCodeValues;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SpecificAttributeValueRequestData;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SpecificAttributeValueRequestDto;

public class GetSpecificAttributeValueMapperTest {

  private final CommonMapper commonMapper = new CommonMapper();

  @Test
  public void test() {
    final SpecificAttributeValueRequestData reqData1 = this.makeRequestData();
    final Object obj = this.commonMapper.map(reqData1, SpecificAttributeValueRequestDto.class);
    assertThat(obj != null && obj instanceof SpecificAttributeValueRequestDto).isTrue();
    final Object reqData2 = this.commonMapper.map(obj, SpecificAttributeValueRequestData.class);
    assertThat(reqData2 != null && reqData2 instanceof SpecificAttributeValueRequestData).isTrue();
    assertThat(reqData1.equals(reqData2)).isTrue();
  }

  private SpecificAttributeValueRequestData makeRequestData() {
    final ObisCodeValues obiscode =
        new ObisCodeValues((byte) 2, (byte) 3, (byte) 4, (byte) 5, (byte) 6, (byte) 7);
    return new SpecificAttributeValueRequestData(21, 1, obiscode);
  }
}
