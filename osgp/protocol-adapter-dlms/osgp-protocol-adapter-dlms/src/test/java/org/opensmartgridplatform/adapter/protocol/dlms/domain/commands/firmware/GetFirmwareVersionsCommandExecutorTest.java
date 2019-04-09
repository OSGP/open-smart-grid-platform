/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub.DlmsConnectionManagerStub;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub.DlmsConnectionStub;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.dto.valueobjects.FirmwareModuleType;
import org.opensmartgridplatform.dto.valueobjects.FirmwareVersionDto;

@RunWith(MockitoJUnitRunner.class)
public class GetFirmwareVersionsCommandExecutorTest {
    private static final int CLASS_ID = 1;
    private static final int ATTRIBUTE_ID = 2;

    private static final ObisCode OBIS_CODE_ACTIVE_FIRMWARE_VERSION = new ObisCode("1.0.0.2.0.255");
    private static final ObisCode OBIS_CODE_MODULE_ACTIVE_FIRMWARE_VERSION = new ObisCode("1.1.0.2.0.255");
    private static final ObisCode OBIS_CODE_COMMUNICATION_MODULE_ACTIVE_FIRMWARE_VERSION = new ObisCode(
            "1.2.0.2.0.255");
    private static final ObisCode OBIS_CODE_MBUS_DRIVER_ACTIVE_FIRMWARE_VERSION = new ObisCode("1.4.0.2.0.255");

    private GetFirmwareVersionsCommandExecutor executor;

    private DlmsConnectionManagerStub connectionHolder;
    private DlmsConnectionStub connectionStub;

    @Before
    public void setUp() {
        this.executor = new GetFirmwareVersionsCommandExecutor();
        this.connectionStub = new DlmsConnectionStub();
        this.connectionHolder = new DlmsConnectionManagerStub(this.connectionStub);
    }

    @Test
    public void returns3FirmwareVersionsForDsmr422Device() throws Exception {
        final DlmsDevice device = new DlmsDevice();

        // Set return values in DLMS connection stub
        this.connectionStub
                .addReturnValue(new AttributeAddress(CLASS_ID, OBIS_CODE_ACTIVE_FIRMWARE_VERSION, ATTRIBUTE_ID),
                        DataObject.newOctetStringData("string1".getBytes()));
        this.connectionStub
                .addReturnValue(new AttributeAddress(CLASS_ID, OBIS_CODE_MODULE_ACTIVE_FIRMWARE_VERSION, ATTRIBUTE_ID),
                        DataObject.newOctetStringData("string2".getBytes()));
        this.connectionStub.addReturnValue(
                new AttributeAddress(CLASS_ID, OBIS_CODE_COMMUNICATION_MODULE_ACTIVE_FIRMWARE_VERSION, ATTRIBUTE_ID),
                DataObject.newOctetStringData("string3".getBytes()));

        // Execute command
        final List<FirmwareVersionDto> result = this.executor.execute(this.connectionHolder, device, null);

        // Check return values
        Assertions.assertThat(result).usingRecursiveFieldByFieldElementComparator()
                .containsExactly(new FirmwareVersionDto(FirmwareModuleType.ACTIVE_FIRMWARE, "string1"),
                        new FirmwareVersionDto(FirmwareModuleType.MODULE_ACTIVE, "string2"),
                        new FirmwareVersionDto(FirmwareModuleType.COMMUNICATION, "string3"));
    }

    @Test
    public void returns4FirmwareVersionsForSmr51Device() throws Exception {
        final DlmsDevice device = new DlmsDevice().setProtocol("SMR", "5.1");

        // Set return values in DLMS connection stub
        this.connectionStub
                .addReturnValue(new AttributeAddress(CLASS_ID, OBIS_CODE_ACTIVE_FIRMWARE_VERSION, ATTRIBUTE_ID),
                        DataObject.newOctetStringData("string1".getBytes()));
        this.connectionStub
                .addReturnValue(new AttributeAddress(CLASS_ID, OBIS_CODE_MODULE_ACTIVE_FIRMWARE_VERSION, ATTRIBUTE_ID),
                        DataObject.newOctetStringData("string2".getBytes()));
        this.connectionStub.addReturnValue(
                new AttributeAddress(CLASS_ID, OBIS_CODE_COMMUNICATION_MODULE_ACTIVE_FIRMWARE_VERSION, ATTRIBUTE_ID),
                DataObject.newOctetStringData("string3".getBytes()));
        this.connectionStub.addReturnValue(
                new AttributeAddress(CLASS_ID, OBIS_CODE_MBUS_DRIVER_ACTIVE_FIRMWARE_VERSION, ATTRIBUTE_ID),
                DataObject.newOctetStringData("string4".getBytes()));

        // Execute command
        final List<FirmwareVersionDto> result = this.executor.execute(this.connectionHolder, device, null);

        // Check return values
        Assertions.assertThat(result).usingRecursiveFieldByFieldElementComparator()
                .containsExactly(new FirmwareVersionDto(FirmwareModuleType.ACTIVE_FIRMWARE, "string1"),
                        new FirmwareVersionDto(FirmwareModuleType.MODULE_ACTIVE, "string2"),
                        new FirmwareVersionDto(FirmwareModuleType.COMMUNICATION, "string3"),
                        new FirmwareVersionDto(FirmwareModuleType.M_BUS_DRIVER_ACTIVE, "string4"));
    }
}

