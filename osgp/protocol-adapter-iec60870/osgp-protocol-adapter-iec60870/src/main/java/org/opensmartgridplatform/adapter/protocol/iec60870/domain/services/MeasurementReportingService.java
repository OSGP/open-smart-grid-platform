// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services;

import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportDto;

@FunctionalInterface
public interface MeasurementReportingService {
  /**
   * Send a measurement report.
   *
   * @param measurementReportDto The {@link MeasurementReportDto} instance to send.
   * @param responseMetadata The {@link ResponseMetadata} instance.
   */
  void send(MeasurementReportDto measurementReportDto, ResponseMetadata responseMetadata);
}
