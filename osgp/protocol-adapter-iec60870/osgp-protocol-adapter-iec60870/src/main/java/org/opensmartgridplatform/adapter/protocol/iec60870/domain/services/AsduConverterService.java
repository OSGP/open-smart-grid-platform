// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services;

import org.openmuc.j60870.ASdu;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportDto;

@FunctionalInterface
public interface AsduConverterService {
  MeasurementReportDto convert(ASdu asdu);
}
