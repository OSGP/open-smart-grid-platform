// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.objectconfig.configlookup;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class ConfigLookupTypeTest {

  @Test
  void match() {
    final ConfigLookupType configLookupType = new ConfigLookupType();
    configLookupType.match = List.of("G4", "G6");

    assertThat(configLookupType.matches("SmallMeterModelTypeG4")).isTrue();
    assertThat(configLookupType.matches("BiggerMeterG6Type")).isTrue();
    assertThat(configLookupType.matches("UnknownMeterType")).isFalse();
  }
}
