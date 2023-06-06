// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.microgrids.valueobjects;

import java.io.Serializable;

public class SystemIdentifier implements Serializable {

  /** */
  private static final long serialVersionUID = -3313598698244220718L;

  private final int id;
  private final String systemType;

  public SystemIdentifier(final int id, final String systemType) {
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
