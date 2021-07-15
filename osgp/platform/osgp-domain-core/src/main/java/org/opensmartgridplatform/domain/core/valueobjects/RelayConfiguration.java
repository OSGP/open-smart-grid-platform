/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
