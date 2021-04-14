/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.microgrids;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SetDataRequestDto implements Serializable {

  private static final long serialVersionUID = 2354993345497992666L;

  private List<SetDataSystemIdentifierDto> setDataSystemIdentifiers;

  public SetDataRequestDto(final List<SetDataSystemIdentifierDto> setDataSystemIdentifiers) {
    this.setDataSystemIdentifiers = new ArrayList<>(setDataSystemIdentifiers);
  }

  public List<SetDataSystemIdentifierDto> getSetDataSystemIdentifiers() {
    return Collections.unmodifiableList(this.setDataSystemIdentifiers);
  }
}
