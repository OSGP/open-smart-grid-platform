/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance 
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc.GetActualPowerQualityCommandExecutor.ActualPowerQualityLogicalName;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.GetResultImpl;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualPowerQualityRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualPowerQualityResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualValueDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CaptureObjectDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsUnitTypeDto;

@ExtendWith(MockitoExtension.class)
public class GetActualPowerQualityCommandExecutorTest {
    private static final int CLASS_ID_DATA = 1;
    private static final int CLASS_ID_REGISTER = 3;
    private static final int CLASS_ID_CLOCK = 8;

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
        when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    }

    @Test
    void testRetrieval() throws ProtocolAdapterException {
        executeAndAssert("PRIVATE", GetActualPowerQualityCommandExecutor.getLogicalNamesPrivate());
        executeAndAssert("PUBLIC", GetActualPowerQualityCommandExecutor.getLogicalNamesPublic());
    }

    @Test
    void testOtherReasonResult() throws ProtocolAdapterException {
        this.actualPowerQualityRequestDto = new ActualPowerQualityRequestDto("PRIVATE");

        final List<GetActualPowerQualityCommandExecutor.ActualPowerQualityLogicalName> logicalNames =
                GetActualPowerQualityCommandExecutor.getLogicalNamesPrivate();

        doReturn(generateMockedResult(logicalNames, AccessResultCode.OTHER_REASON)).when(this.dlmsHelper).getAndCheck(eq(this.conn), eq(this.dlmsDevice),
                eq("retrieve actual power quality"), any(AttributeAddress.class));

        assertThatExceptionOfType(ProtocolAdapterException.class).isThrownBy(() -> {
            new GetActualPowerQualityCommandExecutor(this.dlmsHelper).execute(this.conn,
                    this.dlmsDevice, this.actualPowerQualityRequestDto);
        });
    }

    void executeAndAssert(String profileType,
            List<GetActualPowerQualityCommandExecutor.ActualPowerQualityLogicalName> logicalNames) throws ProtocolAdapterException {
        this.actualPowerQualityRequestDto = new ActualPowerQualityRequestDto(profileType);

        doReturn(generateMockedResult(logicalNames, AccessResultCode.SUCCESS)).when(this.dlmsHelper).getAndCheck(eq(this.conn), eq(this.dlmsDevice),
                eq("retrieve actual power quality"), any(AttributeAddress.class));

        final GetActualPowerQualityCommandExecutor executor =
                new GetActualPowerQualityCommandExecutor(this.dlmsHelper);


        final ActualPowerQualityResponseDto responseDto = executor.execute(this.conn, this.dlmsDevice,
                this.actualPowerQualityRequestDto);

        assertThat(responseDto.getActualPowerQualityData().getActualValues().size()).isEqualTo(logicalNames.size());
        assertThat(responseDto.getActualPowerQualityData().getCaptureObjects().size()).isEqualTo(logicalNames.size());

        for (int i=0;i<logicalNames.size();i++) {
            final GetActualPowerQualityCommandExecutor.ActualPowerQualityLogicalName logicalName = logicalNames.get(i);

            Serializable expectedValue;
            String expectedUnit = null;
            switch(logicalName.getClassId()) {
                case CLASS_ID_CLOCK:     expectedValue = DateTime.parse("2018-12-31T23:00:00Z").toDate();
                                         break;
                case CLASS_ID_REGISTER:  expectedValue = BigDecimal.valueOf(i * 10.0);
                                         expectedUnit = DlmsUnitTypeDto.VOLT.getUnit();
                                         break;
                case CLASS_ID_DATA:      expectedValue = BigDecimal.valueOf(i);
                                         break;
                default:                 expectedValue = null;
                
            }

            final CaptureObjectDto captureObject = responseDto.getActualPowerQualityData().getCaptureObjects().get(i);
            assertThat(captureObject.getLogicalName()).isEqualTo(logicalName.getObisCode());
            assertThat(captureObject.getClassId()).isEqualTo(logicalName.getClassId());
            assertThat(captureObject.getAttributeIndex()).isEqualTo(logicalName.getAttributeIdValue().longValue());
            assertThat(captureObject.getUnit()).isEqualTo(expectedUnit);

            final ActualValueDto actualValue = responseDto.getActualPowerQualityData().getActualValues().get(i);
            assertThat(actualValue.getValue()).isEqualTo(expectedValue);
        }
    }

    private List<GetResult> generateMockedResult(
            List<GetActualPowerQualityCommandExecutor.ActualPowerQualityLogicalName> logicalNames, AccessResultCode resultCode) {
        return generateMockedResult(logicalNames, resultCode, DataObject.newDateTimeData(new CosemDateTime(2018, 12,
                31, 23, 0, 0,
                0)));
    }

    private List<GetResult> generateMockedResult(
            List<GetActualPowerQualityCommandExecutor.ActualPowerQualityLogicalName> logicalNames,
            AccessResultCode resultCode, DataObject dateTimeDataObject) {
        List<GetResult> results = new ArrayList<>();

        int idx = 1;
        for (ActualPowerQualityLogicalName logicalName : logicalNames) {
            if (logicalName.getClassId() == CLASS_ID_CLOCK) {
                results.add(new GetResultImpl(dateTimeDataObject, resultCode));
            } else {
                results.add(new GetResultImpl(DataObject.newInteger64Data(idx++), resultCode));
                if (logicalName.getClassId() == CLASS_ID_REGISTER) {
                    List<DataObject> scalerUnit = new ArrayList<>();
                    scalerUnit.add(DataObject.newInteger64Data(1));
                    scalerUnit.add(DataObject.newInteger64Data(DlmsUnitTypeDto.VOLT.getIndex()));
                    results.add(new GetResultImpl(DataObject.newArrayData(scalerUnit), resultCode));
                }
            }
        }
        return results;
    }

}
