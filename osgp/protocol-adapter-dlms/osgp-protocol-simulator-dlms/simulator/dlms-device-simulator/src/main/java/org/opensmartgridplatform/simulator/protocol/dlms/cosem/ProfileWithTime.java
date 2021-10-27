/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
