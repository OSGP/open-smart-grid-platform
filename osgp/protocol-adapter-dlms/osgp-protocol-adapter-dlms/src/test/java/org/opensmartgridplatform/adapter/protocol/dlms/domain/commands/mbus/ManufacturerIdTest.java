/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class ManufacturerIdTest {

  String manufacturerIdentificationLgb = "LGB";
  int manufacturerIdLgb = 12514;

  @Test
  public void testManufacturerIdFromIdentification() {
    final ManufacturerId lgb =
        ManufacturerId.fromIdentification(this.manufacturerIdentificationLgb);
    assertThat(lgb.getId())
        .withFailMessage("manufacturer_id value")
        .isEqualTo(this.manufacturerIdLgb);
  }

  @Test
  public void testManufacturerIdFromId() {
    final ManufacturerId lgb = ManufacturerId.fromId(this.manufacturerIdLgb);
    assertThat(lgb.getIdentification())
        .withFailMessage("manufacturer_id code")
        .isEqualTo(this.manufacturerIdentificationLgb);
  }
}
