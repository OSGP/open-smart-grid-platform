// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;

class ManufacturerIdTest {

  String manufacturerIdentificationLgb = "LGB";
  int manufacturerIdLgb = 12514;

  @Test
  void testManufacturerIdFromIdentification() {
    final ManufacturerId lgb =
        ManufacturerId.fromIdentification(this.manufacturerIdentificationLgb);
    assertThat(lgb.getId())
        .withFailMessage("manufacturer_id value")
        .isEqualTo(this.manufacturerIdLgb);
  }

  @Test
  void testManufacturerIdFromIdentificationInvalidLength() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () -> {
              ManufacturerId.fromIdentification("LGBA");
            });
  }

  @Test
  void testManufacturerIdFromIdentificationInvalidCharacters() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () -> {
              ManufacturerId.fromIdentification("LG1");
            });
  }

  @Test
  void testManufacturerIdFromId() {
    final ManufacturerId lgb = ManufacturerId.fromId(this.manufacturerIdLgb);
    assertThat(lgb.getIdentification())
        .withFailMessage("manufacturer_id code")
        .isEqualTo(this.manufacturerIdentificationLgb);
  }

  @Test
  void testManufacturerIdFromIdZero() {
    final ManufacturerId lgb = ManufacturerId.fromId(0);
    assertThat(lgb.getIdentification()).withFailMessage("manufacturer_id code").isNull();
  }

  @Test
  void testManufacturerIdFromIdTooLow() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () -> {
              ManufacturerId.fromId(1);
            });
  }
}
