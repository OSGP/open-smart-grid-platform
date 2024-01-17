// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RelayConfiguration implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = 7065046304131439924L;

  @Valid
  @Size(min = 1, max = 6)
  @NotNull
  private List<RelayMap> relayMap;

  public RelayConfiguration(final List<RelayMap> relayMap) {
    this.relayMap = new ArrayList<>(relayMap);
  }

  public List<RelayMap> getRelayMap() {
    return this.relayMap;
  }
}
