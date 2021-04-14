/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.asduhandlers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.ie.IeSinglePointWithQuality;
import org.openmuc.j60870.ie.InformationObject;
import org.opensmartgridplatform.adapter.protocol.iec60870.application.services.Iec60870AsduConverterService;
import org.opensmartgridplatform.adapter.protocol.iec60870.application.services.LightMeasurementRtuDeviceResponseService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.LogItemFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.ResponseMetadataFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.DeviceResponseServiceRegistry;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.LoggingService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceType;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata;
import org.opensmartgridplatform.adapter.protocol.iec60870.testutils.builders.AsduBuilder;
import org.opensmartgridplatform.dto.da.measurements.MeasurementDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementElementDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementGroupDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportHeaderDto;
import org.opensmartgridplatform.dto.da.measurements.elements.BitmaskMeasurementElementDto;

@ExtendWith(MockitoExtension.class)
class SinglePointWithQualityAsduHandlerTest {

  private static final String GATEWAY_DEVICE_IDENTIFICATION = "TEST-GATEWAY-1";
  private static final DeviceType GATEWAY_DEVICE_TYPE = DeviceType.LIGHT_MEASUREMENT_RTU;
  private static final int LMD_1_IOA = 1;
  private static final boolean LMD_1_ON = true;
  private static final int LMD_2_IOA = 2;
  private static final boolean LMD_2_ON = false;
  private static final String MEASUREMENT_TYPE = ASduType.M_SP_NA_1.name();
  private static final String MEASUREMENT_REASON =
      CauseOfTransmission.INTERROGATED_BY_STATION.name();
  private static final int MEASUREMENT_ORIGINATOR_ADDRESS = 0;
  private static final int MEASUREMENT_COMMON_ADDRESS = 0;
  private static final String ORGANISATION_IDENTIFICATION = "TEST-ORG-1";
  private static final String CORRELATION_UID = "TEST-CORR-1";

  @InjectMocks private SinglePointWithQualityAsduHandler asduHandler;

  @Mock private Iec60870AsduConverterService converterService;

  @Mock private ResponseMetadataFactory responseMetadataFactory;

  @Mock private LogItemFactory logItemFactory;

  @Mock private LightMeasurementRtuDeviceResponseService deviceResponseService;

  @Mock private DeviceResponseServiceRegistry deviceResponseServiceMap;

  @Mock private LoggingService loggingService;

  /**
   * Test method for {@link
   * org.opensmartgridplatform.adapter.protocol.iec60870.domain.lightmeasurement.asduhandlers.SinglePointWithQualityAsduHandler#handleAsdu(org.openmuc.j60870.ASdu,
   * org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata)}.
   *
   * @throws Exception
   */
  @Test
  void testHandleAsduShouldSendMeasurementReport() throws Exception {
    // Arrange
    when(this.deviceResponseServiceMap.forDeviceType(DeviceType.LIGHT_MEASUREMENT_RTU))
        .thenReturn(this.deviceResponseService);

    final ASdu asdu = this.createAsdu();
    final ResponseMetadata responseMetadata = this.createResponseMetadata();
    final MeasurementReportDto measurementReport = this.createMeasurementReportDto();
    when(this.converterService.convert(asdu)).thenReturn(measurementReport);
    when(this.deviceResponseServiceMap.forDeviceType(GATEWAY_DEVICE_TYPE))
        .thenReturn(this.deviceResponseService);

    // Act
    this.asduHandler.handleAsdu(asdu, responseMetadata);

    // Assert
    verify(this.deviceResponseService)
        .process(any(MeasurementReportDto.class), eq(responseMetadata));
  }

  private ASdu createAsdu() {
    final InformationObject ioLmd1 = this.createInformationObject(LMD_1_IOA, LMD_1_ON);
    final InformationObject ioLmd2 = this.createInformationObject(LMD_2_IOA, LMD_2_ON);

    return AsduBuilder.ofType(ASduType.M_SP_NA_1)
        .withCauseOfTransmission(CauseOfTransmission.INTERROGATED_BY_STATION)
        .withInformationObjects(ioLmd1, ioLmd2)
        .build();
  }

  private InformationObject createInformationObject(final int ioa, final boolean on) {
    return new InformationObject(ioa, new IeSinglePointWithQuality(on, false, false, false, false));
  }

  private ResponseMetadata createResponseMetadata() {
    return new ResponseMetadata.Builder()
        .withCorrelationUid(CORRELATION_UID)
        .withDeviceIdentification(GATEWAY_DEVICE_IDENTIFICATION)
        .withDeviceType(GATEWAY_DEVICE_TYPE)
        .withOrganisationIdentification(ORGANISATION_IDENTIFICATION)
        .build();
  }

  private MeasurementReportDto createMeasurementReportDto() {
    final MeasurementReportHeaderDto mrh =
        new MeasurementReportHeaderDto(
            MEASUREMENT_TYPE,
            MEASUREMENT_REASON,
            MEASUREMENT_ORIGINATOR_ADDRESS,
            MEASUREMENT_COMMON_ADDRESS);
    final MeasurementGroupDto mg1 =
        this.createMeasurementGroup(Integer.toString(LMD_1_IOA), LMD_1_ON);
    final MeasurementGroupDto mg2 =
        this.createMeasurementGroup(Integer.toString(LMD_2_IOA), LMD_2_ON);
    return new MeasurementReportDto(mrh, Arrays.asList(mg1, mg2));
  }

  private MeasurementGroupDto createMeasurementGroup(
      final String identification, final boolean value) {
    final byte b = value ? (byte) 1 : (byte) 0;
    final MeasurementElementDto me = new BitmaskMeasurementElementDto(b);
    final MeasurementDto m = new MeasurementDto(Arrays.asList(me));
    return new MeasurementGroupDto(identification, Arrays.asList(m));
  }
}
