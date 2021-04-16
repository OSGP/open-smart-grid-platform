/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
  public void testGetKwh() {
    final String result = DlmsUnitTypeDto.getUnit(30);
    assertThat(result).isEqualTo("KWH");
  }

  @Test
  public void testGetUndefined() {
    final String result = DlmsUnitTypeDto.getUnit(0);
    assertThat(result).isEqualTo("UNDEFINED");
  }
}
