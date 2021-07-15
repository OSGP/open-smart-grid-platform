/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.asduhandlers;

import org.openmuc.j60870.ASduType;
import org.springframework.stereotype.Component;

/**
 * Measurement ASDU Handler for ASDUs with type identification M_ME_TF_1:
 *
 * <ul>
 *   <li>Measured value, short floating point number with time tag CP56Time2a
 * </ul>
 */
@Component
public class ShortFloatWithTime56MeasurementAsduHandler extends MeasurementAsduHandler {

  public ShortFloatWithTime56MeasurementAsduHandler() {
    super(ASduType.M_ME_TF_1);
  }
}
