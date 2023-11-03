// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.objectconfig.configlookup;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ConfigLookupGroup {
  protected String name;
  protected String defaulttype;
  protected List<ConfigLookupType> configlookuptypes;

  public String getMatchingType(final String deviceModel) {
    String matchingType = this.defaulttype;

    for (final ConfigLookupType configLookupType : this.configlookuptypes) {
      if (configLookupType.matches(deviceModel)) {
        matchingType = configLookupType.type;
      }
    }

    return matchingType;
  }
}
