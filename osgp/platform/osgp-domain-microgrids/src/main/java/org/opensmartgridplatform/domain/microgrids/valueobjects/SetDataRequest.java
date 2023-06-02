//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
