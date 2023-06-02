//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.microgrids.valueobjects;

import java.io.Serializable;

public class MeasurementFilter extends NodeIdentifier implements Serializable {

  private static final long serialVersionUID = -5169545289993816729L;

  private final boolean all;

  public MeasurementFilter(final int id, final String node, final boolean all) {
    super(id, node);
    this.all = all;
  }

  public boolean isAll() {
    return this.all;
  }
}
