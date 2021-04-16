/*
 * Copyright 2020 Smart Society Services B.V.
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
 * ASDU Handler for ASDUs with type identification M_SP_NA_1:.
 *
 * <ul>
 *   <li>Single-point information with quality
 * </ul>
 */
@Component
public class SinglePointWithQualityAsduHandler extends MeasurementAsduHandler {

  public SinglePointWithQualityAsduHandler() {
    super(ASduType.M_SP_NA_1);
  }
}
