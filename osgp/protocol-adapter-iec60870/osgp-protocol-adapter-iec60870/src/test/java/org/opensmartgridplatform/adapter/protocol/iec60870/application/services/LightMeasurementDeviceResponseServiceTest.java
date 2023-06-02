//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec60870.application.services;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.entities.Iec60870Device;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.repositories.Iec60870DeviceRepository;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.LightMeasurementService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.PendingRequestsQueue;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata;
import org.opensmartgridplatform.adapter.protocol.iec60870.testutils.factories.Iec60870DeviceFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.testutils.factories.MeasurementReportFactory;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportDto;

@ExtendWith(MockitoExtension.class)
class LightMeasurementDeviceResponseServiceTest {

  private static final String ORGANISATION_IDENTIFICATION = "TEST-ORG-1";
  private static final String CORRELATION_UID = "TEST-CORR-1";

  @InjectMocks private LightSensorDeviceResponseService lightMeasurementDeviceResponseService;

  @Mock private Iec60870DeviceRepository iec60870DeviceRepository;

  @Mock private LightMeasurementService lightMeasurementService;

  @Mock private PendingRequestsQueue pendingRequestsQueue;

  @Test
  void sendLightSensorStatusResponseShouldDequeueCorrelationUid() {

    final MeasurementReportDto measurementReportDto =
        MeasurementReportFactory.getMeasurementReportDto();
    final ResponseMetadata responseMetadata =
        new ResponseMetadata.Builder()
            .withCorrelationUid(CORRELATION_UID)
            .withDeviceIdentification(Iec60870DeviceFactory.LMD_1_DEVICE_IDENTIFICATION)
            .withOrganisationIdentification(ORGANISATION_IDENTIFICATION)
            .build();
    final Iec60870Device lightMeasurementDevice1 =
        Iec60870DeviceFactory.getLightMeasurementDevice1();
    final Iec60870Device lightMeasurementDevice2 =
        Iec60870DeviceFactory.getLightMeasurementDevice2();
    when(this.iec60870DeviceRepository.findByGatewayDeviceIdentification(
            Iec60870DeviceFactory.GATEWAY_DEVICE_IDENTIFICATION))
        .thenReturn(Arrays.asList(lightMeasurementDevice1, lightMeasurementDevice2));

    this.lightMeasurementDeviceResponseService.sendLightSensorStatusResponse(
        measurementReportDto, lightMeasurementDevice1, responseMetadata, "");

    verify(this.pendingRequestsQueue)
        .dequeue(eq(Iec60870DeviceFactory.LMD_1_DEVICE_IDENTIFICATION));
  }
}
