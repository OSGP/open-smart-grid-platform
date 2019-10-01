package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SetParameter;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.GetConfigurationObjectService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.ProtocolServiceLookup;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.SetConfigurationObjectService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagsDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationObjectDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetRandomisationSettingsRequestDataDto;

/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
@RunWith(MockitoJUnitRunner.class)
public class SetRandomisationSettingsCommandExecutorTest {

    @Mock
    private DlmsObjectConfigService dlmsObjectConfigService;

    @Mock
    private ProtocolServiceLookup protocolServiceLookup;

    @Mock
    private GetConfigurationObjectService getConfigurationObjectService;

    @Mock
    private SetConfigurationObjectService setConfigurationObjectService;

    @InjectMocks
    private SetRandomisationSettingsCommandExecutor executor;

    @Mock
    private DlmsConnection dlmsConnection;

    @Mock
    private DlmsConnectionManager dlmsConnectionManager;

    private SetRandomisationSettingsRequestDataDto dataDto;
    private DlmsDevice device;

    @Before
    public void init() throws ProtocolAdapterException, IOException {

        // SETUP
        Protocol smr51 = Protocol.SMR_5_1;
        device = createDlmsDevice(smr51);

        AttributeAddress address = new AttributeAddress(1, new ObisCode("0.1.94.31.12.255"), 1);

        dataDto = new SetRandomisationSettingsRequestDataDto(0, 1, 1, 1);

        ConfigurationFlagsDto currentConfigurationFlagsDto = new ConfigurationFlagsDto(getFlags());
        ConfigurationObjectDto currentConfigurationObjectDto = new ConfigurationObjectDto(currentConfigurationFlagsDto);

        when(protocolServiceLookup.lookupGetService(smr51)).thenReturn(getConfigurationObjectService);
        when(protocolServiceLookup.lookupSetService(smr51)).thenReturn(setConfigurationObjectService);
        when(getConfigurationObjectService.getConfigurationObject(dlmsConnectionManager)).thenReturn(
                currentConfigurationObjectDto);
        when(setConfigurationObjectService.setConfigurationObject(any(DlmsConnectionManager.class),
                any(ConfigurationObjectDto.class), any(ConfigurationObjectDto.class))).thenReturn(
                AccessResultCode.SUCCESS);
        when(dlmsObjectConfigService.findAttributeAddress(device, DlmsObjectType.RANDOMISATION_SETTINGS,
                null)).thenReturn(Optional.of(address));

        when(dlmsConnectionManager.getConnection()).thenReturn(dlmsConnection);
        when(dlmsConnection.set(any(SetParameter.class))).thenReturn(AccessResultCode.SUCCESS);
    }

    @Test
    public void testExecuteSuccess() throws ProtocolAdapterException {

        // CALL
        AccessResultCode resultCode = executor.execute(dlmsConnectionManager, device, dataDto);

        // ASSERT
        assertThat(resultCode).isEqualTo(AccessResultCode.SUCCESS);
    }

    @Test(expected = ProtocolAdapterException.class)
    public void testExecuteFailConfiguration() throws ProtocolAdapterException {

        // SETUP
        when(setConfigurationObjectService.setConfigurationObject(any(DlmsConnectionManager.class),
                any(ConfigurationObjectDto.class), any(ConfigurationObjectDto.class))).thenReturn(
                AccessResultCode.OTHER_REASON);

        // CALL
        executor.execute(dlmsConnectionManager, device, dataDto);
    }

    @Test(expected = ProtocolAdapterException.class)
    public void testExecuteFailSetRandomisationSettings() throws ProtocolAdapterException, IOException {

        // SETUP
        when(dlmsConnection.set(any(SetParameter.class))).thenReturn(AccessResultCode.OTHER_REASON);

        // CALL
        executor.execute(dlmsConnectionManager, device, dataDto);
    }

    @Test(expected = ProtocolAdapterException.class)
    public void testUnknownAttribute() throws ProtocolAdapterException {

        // SETUP
        when(dlmsObjectConfigService.findAttributeAddress(device, DlmsObjectType.RANDOMISATION_SETTINGS,
                null)).thenReturn(Optional.empty());

        // CALL
        executor.execute(dlmsConnectionManager, device, dataDto);
    }

    private DlmsDevice createDlmsDevice(final Protocol protocol) {
        final DlmsDevice device = new DlmsDevice();
        device.setProtocol(protocol);
        device.setSelectiveAccessSupported(true);
        return device;
    }

    private List<ConfigurationFlagDto> getFlags() {

        return Arrays.stream(ConfigurationFlagTypeDto.values()).map(
                flagType -> new ConfigurationFlagDto(flagType, true)).collect(Collectors.toList());

    }
}
