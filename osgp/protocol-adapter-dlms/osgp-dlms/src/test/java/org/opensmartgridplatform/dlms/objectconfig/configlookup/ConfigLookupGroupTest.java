// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.objectconfig.configlookup;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class ConfigLookupGroupTest {

  @Test
  void getType() {
    final ConfigLookupType configLookupTypeG4G6 = new ConfigLookupType();
    configLookupTypeG4G6.type = "G4_G6";
    configLookupTypeG4G6.match = List.of("G4", "G6");
    final ConfigLookupType configLookupTypeG10G25 = new ConfigLookupType();
    configLookupTypeG10G25.type = "G10_G25";
    configLookupTypeG10G25.match = List.of("G10", "G16", "G25");

    final ConfigLookupGroup group = new ConfigLookupGroup();
    group.defaulttype = "Unmatched";
    group.configlookuptypes = List.of(configLookupTypeG4G6, configLookupTypeG10G25);

    assertThat(group.getMatchingType("SmallMeterModelTypeG4")).isEqualTo(configLookupTypeG4G6.type);
    assertThat(group.getMatchingType("BigMeterG25")).isEqualTo(configLookupTypeG10G25.type);
    assertThat(group.getMatchingType("G25meterWithG4inName"))
        .isEqualTo(configLookupTypeG10G25.type);
    assertThat(group.getMatchingType("UnknownModel")).isEqualTo("Unmatched");
  }
}
