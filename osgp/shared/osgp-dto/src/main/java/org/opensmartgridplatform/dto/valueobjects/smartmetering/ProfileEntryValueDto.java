// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class ProfileEntryValueDto implements Serializable {

  private static final long serialVersionUID = 2123390296585369209L;

  private final Serializable value;

  public ProfileEntryValueDto(final Serializable value) {
    this.value = value;
  }

  public Serializable getValue() {
    return this.value;
  }
}
