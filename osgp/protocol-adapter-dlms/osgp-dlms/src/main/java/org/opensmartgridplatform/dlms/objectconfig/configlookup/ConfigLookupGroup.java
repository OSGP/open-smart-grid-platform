// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.objectconfig.configlookup;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@NoArgsConstructor
@Slf4j
public class ConfigLookupGroup {
  protected String name;
  protected String defaulttype;
  protected List<ConfigLookupType> configlookuptypes;

  public String getMatchingType(final String deviceModel) {
    String lastMatchingType = null;
    for (final ConfigLookupType configLookupType : this.configlookuptypes) {
      if (configLookupType.matches(deviceModel)) {
        lastMatchingType = configLookupType.type;
      }
    }
    if (lastMatchingType != null) {
      return lastMatchingType;
    }

    log.warn(
        "Could not determine matching type for device model: {}. This model should be added to the configuration, to prevent inefficient communication with meter",
        deviceModel);
    return this.defaulttype;
  }
}
