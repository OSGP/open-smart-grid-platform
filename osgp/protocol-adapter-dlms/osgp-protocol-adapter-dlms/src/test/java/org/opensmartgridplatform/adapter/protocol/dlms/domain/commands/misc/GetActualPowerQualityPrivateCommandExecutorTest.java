package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.GetResultImpl;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualPowerQualityPrivateResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualPowerQualityRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfidentialityTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsUnitTypeDto;
import org.opensmartgridplatform.shared.utils.datehelpers.DateHelper;

/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GetActualPowerQualityPrivateCommandExecutorTest {

    @Spy
    private DlmsHelper dlmsHelper;

    @Mock
    private DlmsDevice dlmsDevice;

    @Mock
    private DlmsConnectionManager conn;

    @Mock
    private DlmsMessageListener dlmsMessageListener;

    private ActualPowerQualityRequestDto actualPowerQualityRequestDto;

    @BeforeEach
    public void before() throws ProtocolAdapterException, IOException {
        this.actualPowerQualityRequestDto = new ActualPowerQualityRequestDto(ConfidentialityTypeDto.PRIVATE);

        when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
        doReturn(generateMockedResult(AccessResultCode.SUCCESS)).when(this.dlmsHelper).getAndCheck(eq(this.conn), eq(this.dlmsDevice),
                eq("retrieve actual power quality"), any(AttributeAddress.class));
    }

    @Test
    void testRetrieval() throws ProtocolAdapterException {
        final GetActualPowerQualityPrivateCommandExecutor executor =
                new GetActualPowerQualityPrivateCommandExecutor(this.dlmsHelper);

        final ActualPowerQualityPrivateResponseDto responseDto = executor.execute(this.conn, this.dlmsDevice,
                this.actualPowerQualityRequestDto);

        assertThat(responseDto.getLogTime()).isEqualTo(DateHelper.dateFromIsoString("2019-01-01T00:00:00+01:00"));

        assertThat(responseDto.getInstantaneousCurrentL1().getValue()).isEqualTo(BigDecimal.valueOf(10.0));
        assertThat(responseDto.getInstantaneousCurrentL1().getDlmsUnit()).isEqualTo(DlmsUnitTypeDto.UNDEFINED);

        assertThat(responseDto.getInstantaneousCurrentL2().getValue()).isEqualTo(BigDecimal.valueOf(20.0));
        assertThat(responseDto.getInstantaneousCurrentL3().getValue()).isEqualTo(BigDecimal.valueOf(30.0));
        assertThat(responseDto.getInstantaneousActivePowerImport().getValue()).isEqualTo(BigDecimal.valueOf(40.0));
        assertThat(responseDto.getInstantaneousActivePowerExport().getValue()).isEqualTo(BigDecimal.valueOf(50.0));
        assertThat(responseDto.getInstantaneousActivePowerImportL1().getValue()).isEqualTo(BigDecimal.valueOf(60.0));
        assertThat(responseDto.getInstantaneousActivePowerImportL2().getValue()).isEqualTo(BigDecimal.valueOf(70.0));
        assertThat(responseDto.getInstantaneousActivePowerImportL3().getValue()).isEqualTo(BigDecimal.valueOf(80.0));
        assertThat(responseDto.getInstantaneousActivePowerExportL1().getValue()).isEqualTo(BigDecimal.valueOf(90.0));
        assertThat(responseDto.getInstantaneousActivePowerExportL2().getValue()).isEqualTo(BigDecimal.valueOf(100.0));
        assertThat(responseDto.getInstantaneousActivePowerExportL3().getValue()).isEqualTo(BigDecimal.valueOf(110.0));
        assertThat(responseDto.getAverageCurrentL1().getValue()).isEqualTo(BigDecimal.valueOf(120.0));
        assertThat(responseDto.getAverageCurrentL2().getValue()).isEqualTo(BigDecimal.valueOf(130.0));
        assertThat(responseDto.getAverageCurrentL3().getValue()).isEqualTo(BigDecimal.valueOf(140.0));
        assertThat(responseDto.getAverageActivePowerImportL1().getValue()).isEqualTo(BigDecimal.valueOf(150.0));
        assertThat(responseDto.getAverageActivePowerImportL2().getValue()).isEqualTo(BigDecimal.valueOf(160.0));
        assertThat(responseDto.getAverageActivePowerImportL3().getValue()).isEqualTo(BigDecimal.valueOf(170.0));
        assertThat(responseDto.getAverageActivePowerExportL1().getValue()).isEqualTo(BigDecimal.valueOf(180.0));
        assertThat(responseDto.getAverageActivePowerExportL2().getValue()).isEqualTo(BigDecimal.valueOf(190.0));
        assertThat(responseDto.getAverageActivePowerExportL3().getValue()).isEqualTo(BigDecimal.valueOf(200.0));

        assertThat(responseDto.getAverageReactivePowerImportL1().getValue()).isEqualTo(BigDecimal.valueOf(210.0));
        assertThat(responseDto.getAverageReactivePowerImportL2().getValue()).isEqualTo(BigDecimal.valueOf(220.0));
        assertThat(responseDto.getAverageReactivePowerImportL3().getValue()).isEqualTo(BigDecimal.valueOf(230.0));
        assertThat(responseDto.getAverageReactivePowerExportL1().getValue()).isEqualTo(BigDecimal.valueOf(240.0));
        assertThat(responseDto.getAverageReactivePowerExportL2().getValue()).isEqualTo(BigDecimal.valueOf(250.0));
        assertThat(responseDto.getAverageReactivePowerExportL3().getValue()).isEqualTo(BigDecimal.valueOf(260.0));
        assertThat(responseDto.getInstantaneousActiveCurrentTotalOverAllPhases().getValue()).isEqualTo(BigDecimal.valueOf(270.0));
    }

    @Test
    void testOtherReasonResult() throws ProtocolAdapterException {
        doReturn(generateMockedResult(AccessResultCode.OTHER_REASON)).when(this.dlmsHelper).getAndCheck(eq(this.conn), eq(this.dlmsDevice),
                eq("retrieve actual power quality"), any(AttributeAddress.class));

        assertThatExceptionOfType(ProtocolAdapterException.class).isThrownBy(() -> {
            new GetActualPowerQualityPrivateCommandExecutor(this.dlmsHelper).execute(this.conn,
                    this.dlmsDevice, this.actualPowerQualityRequestDto);
        });
    }

    @Test
    void testFromBundleRequestInput() throws ProtocolAdapterException {
        GetActualPowerQualityPrivateCommandExecutor executor =
                new GetActualPowerQualityPrivateCommandExecutor(this.dlmsHelper);

        ActualPowerQualityRequestDto actionRequestDto = new ActualPowerQualityRequestDto();
        ActualPowerQualityRequestDto requestDto = executor.fromBundleRequestInput(actionRequestDto);

        assertThat(requestDto).isInstanceOf(ActualPowerQualityRequestDto.class);
        assertThat(requestDto.isPublic()).isFalse();
    }

    private List<GetResult> generateMockedResult(AccessResultCode resultCode) {
        return generateMockedResult(resultCode, DataObject.newDateTimeData(new CosemDateTime(2018, 12, 31, 23, 0, 0,
                0)));
    }
    private List<GetResult> generateMockedResult(AccessResultCode resultCode, DataObject dateTimeDataObject) {
        List<GetResult> results = new ArrayList<>();

        // INDEX_TIME = 0;
        results.add(new GetResultImpl(dateTimeDataObject, resultCode));
        // Values
        for (int i = 1;i<=27;i++) {
            results.add(new GetResultImpl(DataObject.newInteger64Data(i),
                    resultCode));
        }

        // Scaler units
        for (int i = 28;i<=54;i++) {
            List<DataObject> scalerUnit = new ArrayList<>();
            scalerUnit.add(DataObject.newInteger64Data(1));
            scalerUnit.add(DataObject.newInteger64Data(DlmsUnitTypeDto.UNDEFINED.getIndex()));
            results.add(new GetResultImpl(DataObject.newArrayData(scalerUnit),
                    resultCode));
        }
        return results;
    }

}
