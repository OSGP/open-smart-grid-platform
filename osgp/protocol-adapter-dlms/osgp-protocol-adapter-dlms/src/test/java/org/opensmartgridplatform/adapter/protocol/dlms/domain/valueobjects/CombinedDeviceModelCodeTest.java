// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.valueobjects;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.valueobjects.CombinedDeviceModelCode.CombinedDeviceModelCodeBuilder;

@ExtendWith(MockitoExtension.class)
class CombinedDeviceModelCodeTest {
  private static final String GATEWAYCODE = "gwcode";
  private static final String CH1_DEVICE_MODEL_CODE = "ch1DeviceModelCode";
  private static final String CH2_DEVICE_MODEL_CODE = "ch2DeviceModelCode";
  private static final String CH3_DEVICE_MODEL_CODE = "ch3DeviceModelCode";
  private static final String CH4_DEVICE_MODEL_CODE = "ch4DeviceModelCode";

  private static final String DEVICE_MODEL_CODE_STRING =
      String.format(
          "%s,%s,%s,%s,%s",
          GATEWAYCODE,
          CH1_DEVICE_MODEL_CODE,
          CH2_DEVICE_MODEL_CODE,
          CH3_DEVICE_MODEL_CODE,
          CH4_DEVICE_MODEL_CODE);

  @Test
  void parseShouldSucceed() {

    final CombinedDeviceModelCode combinedDeviceModelCode =
        CombinedDeviceModelCode.parse(DEVICE_MODEL_CODE_STRING);

    assertEquals(GATEWAYCODE, combinedDeviceModelCode.getGatewayDeviceModelCode());
    assertEquals(CH1_DEVICE_MODEL_CODE, combinedDeviceModelCode.getCodeFromChannel(1));
    assertEquals(CH2_DEVICE_MODEL_CODE, combinedDeviceModelCode.getCodeFromChannel(2));
    assertEquals(CH3_DEVICE_MODEL_CODE, combinedDeviceModelCode.getCodeFromChannel(3));
    assertEquals(CH4_DEVICE_MODEL_CODE, combinedDeviceModelCode.getCodeFromChannel(4));
  }

  @Test
  void parseShouldReturnEmptyCombinedDeviceModelCode() {
    final CombinedDeviceModelCode combinedDeviceModelCode =
        CombinedDeviceModelCode.parse("invalid");

    Assertions.assertEquals("", combinedDeviceModelCode.getGatewayDeviceModelCode());
    assertNull(combinedDeviceModelCode.getCodeFromChannel(1));
    assertNull(combinedDeviceModelCode.getCodeFromChannel(2));
    assertNull(combinedDeviceModelCode.getCodeFromChannel(3));
    assertNull(combinedDeviceModelCode.getCodeFromChannel(4));
  }

  @Test
  void builderShouldSucceed() {
    final CombinedDeviceModelCode deviceModelCode =
        new CombinedDeviceModelCodeBuilder()
            .gatewayDeviceModelCode(GATEWAYCODE)
            .channelBasedDeviceModelCode(1, CH1_DEVICE_MODEL_CODE)
            .channelBasedDeviceModelCode(2, CH2_DEVICE_MODEL_CODE)
            .channelBasedDeviceModelCode(3, CH3_DEVICE_MODEL_CODE)
            .channelBasedDeviceModelCode(4, CH4_DEVICE_MODEL_CODE)
            .build();

    assertEquals(DEVICE_MODEL_CODE_STRING, deviceModelCode.toString());
  }
}
