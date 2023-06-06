// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.microgrids;

import java.io.Serializable;

public class SystemIdentifierDto implements Serializable {

  private static final long serialVersionUID = -8592667499461927077L;

  private int id;
  private String systemType;

  public SystemIdentifierDto(final int id, final String systemType) {
    this.id = id;
    this.systemType = systemType;
  }

  public int getId() {
    return this.id;
  }

  public String getSystemType() {
    return this.systemType;
  }
}
