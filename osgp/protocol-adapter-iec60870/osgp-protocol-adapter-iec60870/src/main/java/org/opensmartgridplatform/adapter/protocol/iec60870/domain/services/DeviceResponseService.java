// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services;

import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportDto;

public interface DeviceResponseService {

  void process(MeasurementReportDto measurementReportDto, ResponseMetadata responseMetadata);

  void processEvent(MeasurementReportDto measurementReportDto, ResponseMetadata responseMetadata);
}
