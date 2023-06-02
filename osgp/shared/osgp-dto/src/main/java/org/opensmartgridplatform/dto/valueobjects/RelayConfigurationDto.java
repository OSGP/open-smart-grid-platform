//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dto.valueobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RelayConfigurationDto implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = -108654955320491314L;

  private List<RelayMapDto> relayMap;

  public RelayConfigurationDto(final List<RelayMapDto> relayMap) {
    this.relayMap = new ArrayList<>(relayMap);
  }

  public List<RelayMapDto> getRelayMap() {
    return this.relayMap;
  }
}
