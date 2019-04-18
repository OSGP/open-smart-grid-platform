/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.responses;

import org.opensmartgridplatform.dto.da.measurements.MeasurementReportDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

public class MeasurementResponse extends DeviceResponse {

    private final MeasurementReportDto measurementReportDto;

    public MeasurementResponse(final MessageMetadata messageMetadata, final MeasurementReportDto measurementReportDto) {
        super(messageMetadata);
        this.measurementReportDto = measurementReportDto;
    }

    public MeasurementReportDto getMeasurementReportDto() {
        return this.measurementReportDto;
    }

}
