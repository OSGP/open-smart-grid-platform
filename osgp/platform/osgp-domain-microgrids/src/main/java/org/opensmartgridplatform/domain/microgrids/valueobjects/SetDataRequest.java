/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.microgrids.valueobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SetDataRequest implements Serializable {

  private static final long serialVersionUID = -6528597730317108512L;

  private final List<SetDataSystemIdentifier> setDataSystemIdentifiers;

  public SetDataRequest(final List<SetDataSystemIdentifier> setDataSystemIdentifiers) {
    this.setDataSystemIdentifiers = new ArrayList<>(setDataSystemIdentifiers);
  }

  public List<SetDataSystemIdentifier> getSetDataSystemIdentifiers() {
    return new ArrayList<>(this.setDataSystemIdentifiers);
  }
}
