// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.cosem;

import java.util.Calendar;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.CaptureObjectDefinitionCollection;

public abstract class ProfileWithTime extends ProfileGeneric {

  /** Every 5 minutes. */
  protected static final int CAPTURE_PERIOD = 300;

  protected Calendar time;

  public ProfileWithTime(final Calendar time, final int objectName, final String instanceId) {
    super(objectName, instanceId);

    this.time = time;
  }

  @Override
  protected abstract CaptureObjectDefinitionCollection getCaptureObjectDefinitionCollection();

  protected Calendar getDateTime() {
    return (Calendar) this.time.clone();
  }

  protected void forwardTime() {
    this.time.add(Calendar.SECOND, CAPTURE_PERIOD);
  }
}
