//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class ProfileEntryValue implements Serializable {

  private static final long serialVersionUID = 991045734132231709L;

  private final Serializable value;

  public ProfileEntryValue(final Serializable value) {
    this.value = value;
  }

  public Serializable getValue() {
    return this.value;
  }
}
