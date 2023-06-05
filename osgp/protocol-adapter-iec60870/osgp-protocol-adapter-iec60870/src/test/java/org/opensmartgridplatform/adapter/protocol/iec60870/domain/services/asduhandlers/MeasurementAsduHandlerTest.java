// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.asduhandlers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.TimeZone;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.opensmartgridplatform.adapter.protocol.iec60870.application.mapping.Iec60870Mapper;
import org.opensmartgridplatform.adapter.protocol.iec60870.application.services.DistributionAutomationDeviceResponseService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.LogItemFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.ResponseMetadataFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.AsduConverterService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.DeviceResponseServiceRegistry;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.LoggingService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceType;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata;
import org.opensmartgridplatform.adapter.protocol.iec60870.testutils.factories.AsduFactory;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportDto;

@ExtendWith(MockitoExtension.class)
public class MeasurementAsduHandlerTest {

  private static final String DEVICE_IDENTIFICATION = "TEST-DEVICE-1";
  private static final DeviceType DEVICE_TYPE = DeviceType.DISTRIBUTION_AUTOMATION_DEVICE;
  private static final String ORGANISATION_IDENTIFICATION = "TEST-ORG-1";
  private static final String CORRELATION_UID = "TEST-CORR-1";

  @InjectMocks private ShortFloatWithTime56MeasurementAsduHandler asduHandler;

  @Mock private ResponseMetadataFactory responseMetadataFactory;

  @Mock private DeviceResponseServiceRegistry deviceResponseServiceRegistry;

  @Mock private AsduConverterService converter;

  @Mock private LogItemFactory logItemFactory;

  @Mock private DistributionAutomationDeviceResponseService deviceResponseService;

  @Mock private LoggingService loggingService;

  private final Iec60870Mapper mapper = new Iec60870Mapper(TimeZone.getTimeZone("Europe/Paris"));

  @Test
  void shouldSendMeasurementReportWhenHandlingAsdu() throws Exception {
    // Arrange
    final ASdu asdu = AsduFactory.ofType(ASduType.M_ME_TF_1);
    final MeasurementReportDto measurementReportDto =
        this.mapper.map(asdu, MeasurementReportDto.class);
    final ResponseMetadata responseMetadata =
        new ResponseMetadata.Builder()
            .withCorrelationUid(CORRELATION_UID)
            .withDeviceIdentification(DEVICE_IDENTIFICATION)
            .withDeviceType(DEVICE_TYPE)
            .withOrganisationIdentification(ORGANISATION_IDENTIFICATION)
            .build();

    when(this.converter.convert(asdu)).thenReturn(measurementReportDto);
    when(this.deviceResponseServiceRegistry.forDeviceType(DEVICE_TYPE))
        .thenReturn(this.deviceResponseService);
    // Act
    this.asduHandler.handleAsdu(asdu, responseMetadata);

    // Assert
    verify(this.deviceResponseService).process(measurementReportDto, responseMetadata);
  }
}
