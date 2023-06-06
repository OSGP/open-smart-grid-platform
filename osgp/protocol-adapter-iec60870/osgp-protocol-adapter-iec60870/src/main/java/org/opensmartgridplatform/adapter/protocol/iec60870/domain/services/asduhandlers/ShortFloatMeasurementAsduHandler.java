// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.asduhandlers;

import org.openmuc.j60870.ASduType;
import org.springframework.stereotype.Component;

/**
 * Measurement ASDU Handler for ASDUs with type identification M_ME_NC_1:
 *
 * <ul>
 *   <li>Measured value, short floating point number (without time tag)
 * </ul>
 */
@Component
public class ShortFloatMeasurementAsduHandler extends MeasurementAsduHandler {

  public ShortFloatMeasurementAsduHandler() {
    super(ASduType.M_ME_NC_1);
  }
}
