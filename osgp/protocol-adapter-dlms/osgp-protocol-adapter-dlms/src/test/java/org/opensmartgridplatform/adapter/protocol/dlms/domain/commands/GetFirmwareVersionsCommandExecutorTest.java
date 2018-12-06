package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.refEq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.when;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dto.valueobjects.FirmwareModuleType;
import org.opensmartgridplatform.dto.valueobjects.FirmwareVersionDto;

@RunWith(MockitoJUnitRunner.class)
public class GetFirmwareVersionsCommandExecutorTest {
    private static final int CLASS_ID = 1;
    private static final int ATTRIBUTE_ID = 2;

    private static final ObisCode OBIS_CODE_ACTIVE_FIRMWARE_VERSION = new ObisCode("1.0.0.2.0.255");
    private static final ObisCode OBIS_CODE_MODULE_ACTIVE_FIRMWARE_VERSION = new ObisCode("1.1.0.2.0.255");
    private static final ObisCode OBIS_CODE_COMMUNICATION_MODULE_ACTIVE_FIRMWARE_VERSION = new ObisCode("1.2.0.2.0.255");
    private static final ObisCode OBIS_CODE_MBUS_DRIVER_ACTIVE_FIRMWARE_VERSION = new ObisCode("1.4.0.2.0.255");

    private GetFirmwareVersionsCommandExecutor executor;

    @Mock
    private DlmsMessageListener listener;

    @Mock
    private DlmsHelperService helperService;

    private DlmsConnectionHolder connectionHolder;

    @Before
    public void setUp() throws Exception {
        executor = new GetFirmwareVersionsCommandExecutor(helperService);
        connectionHolder = new DlmsConnectionHolder(null, null, listener, null);
    }

    @Test
    public void returns3FirmwareVersionsForDlms1Device() throws Exception {
        DlmsDevice device = new DlmsDevice();

        GetResult getResult1 = new GetResultBuilder().build();
        GetResult getResult2 = new GetResultBuilder().build();
        GetResult getResult3 = new GetResultBuilder().build();

        when(this.helperService.getAndCheck(same(connectionHolder),
                same(device),
                eq("retrieve firmware versions"),
                refEq(new AttributeAddress(CLASS_ID, OBIS_CODE_ACTIVE_FIRMWARE_VERSION, ATTRIBUTE_ID)),
                refEq(new AttributeAddress(CLASS_ID, OBIS_CODE_MODULE_ACTIVE_FIRMWARE_VERSION, ATTRIBUTE_ID)),
                refEq(new AttributeAddress(CLASS_ID, OBIS_CODE_COMMUNICATION_MODULE_ACTIVE_FIRMWARE_VERSION, ATTRIBUTE_ID))))
                .thenReturn(asList(getResult1, getResult2, getResult3));
        when(helperService.readString(getResult1.getResultData(), FirmwareModuleType.ACTIVE_FIRMWARE.getDescription()))
                .thenReturn("string1");
        when(helperService.readString(getResult2.getResultData(), FirmwareModuleType.MODULE_ACTIVE.getDescription()))
                .thenReturn("string2");
        when(helperService.readString(getResult3.getResultData(), FirmwareModuleType.COMMUNICATION.getDescription()))
                .thenReturn("string3");

        List<FirmwareVersionDto> result = executor.execute(connectionHolder, device, null);

        Assertions.assertThat(result).usingRecursiveFieldByFieldElementComparator().containsExactly(
                new FirmwareVersionDto(FirmwareModuleType.ACTIVE_FIRMWARE, "string1"),
                new FirmwareVersionDto(FirmwareModuleType.MODULE_ACTIVE, "string2"),
                new FirmwareVersionDto(FirmwareModuleType.COMMUNICATION, "string3"));
    }

    @Test
    public void returns4FirmwareVersionsForSMR51Device() throws Exception {
        DlmsDevice device = new DlmsDevice().setProtocol("SMR", "5.1");

        GetResult getResult1 = new GetResultBuilder().build();
        GetResult getResult2 = new GetResultBuilder().build();
        GetResult getResult3 = new GetResultBuilder().build();
        GetResult getResult4 = new GetResultBuilder().build();

        when(this.helperService.getAndCheck(same(connectionHolder),
                same(device),
                eq("retrieve firmware versions"),
                refEq(new AttributeAddress(CLASS_ID, OBIS_CODE_ACTIVE_FIRMWARE_VERSION, ATTRIBUTE_ID)),
                refEq(new AttributeAddress(CLASS_ID, OBIS_CODE_MODULE_ACTIVE_FIRMWARE_VERSION, ATTRIBUTE_ID)),
                refEq(new AttributeAddress(CLASS_ID, OBIS_CODE_COMMUNICATION_MODULE_ACTIVE_FIRMWARE_VERSION, ATTRIBUTE_ID)),
                refEq(new AttributeAddress(CLASS_ID, OBIS_CODE_MBUS_DRIVER_ACTIVE_FIRMWARE_VERSION, ATTRIBUTE_ID))))
                .thenReturn(asList(getResult1, getResult2, getResult3, getResult4));
        when(helperService.readString(getResult1.getResultData(), FirmwareModuleType.ACTIVE_FIRMWARE.getDescription()))
                .thenReturn("string1");
        when(helperService.readString(getResult2.getResultData(), FirmwareModuleType.MODULE_ACTIVE.getDescription()))
                .thenReturn("string2");
        when(helperService.readString(getResult3.getResultData(), FirmwareModuleType.COMMUNICATION.getDescription()))
                .thenReturn("string3");
        when(helperService.readString(getResult4.getResultData(), FirmwareModuleType.M_BUS_DRIVER_ACTIVE.getDescription()))
                .thenReturn("string4");

        List<FirmwareVersionDto> result = executor.execute(connectionHolder, device, null);

        Assertions.assertThat(result).usingRecursiveFieldByFieldElementComparator().containsExactly(
                new FirmwareVersionDto(FirmwareModuleType.ACTIVE_FIRMWARE, "string1"),
                new FirmwareVersionDto(FirmwareModuleType.MODULE_ACTIVE, "string2"),
                new FirmwareVersionDto(FirmwareModuleType.COMMUNICATION, "string3"),
                new FirmwareVersionDto(FirmwareModuleType.M_BUS_DRIVER_ACTIVE, "string4"));
    }
}

