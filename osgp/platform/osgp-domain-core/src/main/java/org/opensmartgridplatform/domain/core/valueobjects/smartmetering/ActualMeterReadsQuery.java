// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

/**
 * request actual reads for E or GAS meters
 *
 * @author dev
 */
public class ActualMeterReadsQuery implements Serializable {
  private static final long serialVersionUID = 3751586818507193990L;

  private final boolean mbusDevice;

  public ActualMeterReadsQuery() {
    this(false);
  }

  public ActualMeterReadsQuery(final boolean mbusDevice) {
    this.mbusDevice = mbusDevice;
  }

  public boolean isMbusDevice() {
    return this.mbusDevice;
  }
}
