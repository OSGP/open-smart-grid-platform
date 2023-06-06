// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
