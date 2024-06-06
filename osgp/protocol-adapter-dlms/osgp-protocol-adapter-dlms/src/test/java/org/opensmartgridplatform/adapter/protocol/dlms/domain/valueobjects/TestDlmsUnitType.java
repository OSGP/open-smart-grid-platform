// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.valueobjects;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsUnitTypeDto;

public class TestDlmsUnitType {

  @Test
  public void testGetUnit() {
    final String result = DlmsUnitTypeDto.getUnit(1);
    assertThat(result).isEqualTo("Y");
  }

  @Test
  public void testGetWh() {
    final String result = DlmsUnitTypeDto.getUnit(30);
    assertThat(result).isEqualTo("WH");
  }

  @Test
  public void testGetUndefined() {
    final String result = DlmsUnitTypeDto.getUnit(0);
    assertThat(result).isEqualTo("UNDEFINED");
  }
}
