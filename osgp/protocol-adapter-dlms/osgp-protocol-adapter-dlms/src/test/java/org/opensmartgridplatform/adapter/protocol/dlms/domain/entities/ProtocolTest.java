// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.entities;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class ProtocolTest {

  @Test
  public void testProtocolForDevice() {
    // SETUP
    final DlmsDevice device = new DlmsDevice();
    final Protocol protocol = Protocol.DSMR_4_2_2;
    device.setProtocol(protocol);

    // CALL
    final Protocol result = Protocol.forDevice(device);

    // VERIFY
    assertThat(result).isEqualTo(protocol);
  }

  @Test
  public void testProtocolForDeviceDoesNotExist() {
    // SETUP
    final DlmsDevice device = new DlmsDevice();
    device.setProtocol("XHP", "544");

    // CALL
    final Protocol result = Protocol.forDevice(device);

    // VERIFY
    assertThat(result).isEqualTo(Protocol.OTHER_PROTOCOL);
  }

  @Test
  public void testProtocolWithNameAndVersion() {
    assertThat(Protocol.withNameAndVersion("DSMR", "4.2.2")).isEqualTo(Protocol.DSMR_4_2_2);
    assertThat(Protocol.withNameAndVersion("SMR", "5.0.0")).isEqualTo(Protocol.SMR_5_0_0);
    assertThat(Protocol.withNameAndVersion("SMR", "5.1")).isEqualTo(Protocol.SMR_5_1);
    assertThat(Protocol.withNameAndVersion("SMR", "5.2")).isEqualTo(Protocol.SMR_5_2);
    assertThat(Protocol.withNameAndVersion("other", "0.1")).isEqualTo(Protocol.OTHER_PROTOCOL);
  }

  @Test
  public void testIsSelectingValuesSupported() {
    assertThat(Protocol.DSMR_4_2_2.isSelectValuesInSelectiveAccessSupported()).isEqualTo(true);
    assertThat(Protocol.SMR_5_0_0.isSelectValuesInSelectiveAccessSupported()).isEqualTo(true);
    assertThat(Protocol.SMR_5_1.isSelectValuesInSelectiveAccessSupported()).isEqualTo(true);
    assertThat(Protocol.SMR_5_2.isSelectValuesInSelectiveAccessSupported()).isEqualTo(true);
    assertThat(Protocol.OTHER_PROTOCOL.isSelectValuesInSelectiveAccessSupported()).isEqualTo(true);
  }

  @Test
  public void testIsSMR5() {
    assertThat(Protocol.DSMR_4_2_2.isSmr5()).isEqualTo(false);
    assertThat(Protocol.SMR_5_0_0.isSmr5()).isEqualTo(true);
    assertThat(Protocol.SMR_5_1.isSmr5()).isEqualTo(true);
    assertThat(Protocol.SMR_5_2.isSmr5()).isEqualTo(true);
    assertThat(Protocol.OTHER_PROTOCOL.isSmr5()).isEqualTo(false);
  }
}
