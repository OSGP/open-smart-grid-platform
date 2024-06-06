// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.objectconfig.configlookup;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ConfigLookupType {
  protected String type;
  protected List<String> match;

  public boolean matches(final String deviceModel) {
    return this.match.stream().anyMatch(deviceModel::contains);
  }
}
