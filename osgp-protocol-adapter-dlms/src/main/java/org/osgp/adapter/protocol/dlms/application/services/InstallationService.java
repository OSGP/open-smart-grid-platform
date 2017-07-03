/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.services;

import org.osgp.adapter.protocol.dlms.application.mapping.InstallationMapper;
import org.osgp.adapter.protocol.dlms.domain.commands.CoupleMBusDeviceCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.DeCoupleMBusDeviceCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.dto.valueobjects.smartmetering.DecoupleMbusDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.MbusChannelElementsDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.MbusChannelElementsResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SmartMeteringDeviceDto;

@Service(value = "dlmsInstallationService")
public class InstallationService {

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepository;

    @Autowired
    private InstallationMapper installationMapper;

    @Autowired
    private ReEncryptionService reEncryptionService;

    @Autowired
    private CoupleMBusDeviceCommandExecutor coupleMBusDeviceCommandExecutor;

    @Autowired
    private DeCoupleMBusDeviceCommandExecutor deCoupleMBusDeviceCommandExecutor;

    // === ADD METER ===
    public void addMeter(final SmartMeteringDeviceDto smartMeteringDevice) throws ProtocolAdapterException {
        this.reEncryptKeys(smartMeteringDevice);
        final DlmsDevice dlmsDevice = this.installationMapper.map(smartMeteringDevice, DlmsDevice.class);
        this.dlmsDeviceRepository.save(dlmsDevice);
    }

    private void reEncryptKeys(final SmartMeteringDeviceDto smartMeteringDevice) throws ProtocolAdapterException {
        this.reEncryptMasterKey(smartMeteringDevice);
        this.reEncryptAuthenticationKey(smartMeteringDevice);
        this.reEncryptEncryptionKey(smartMeteringDevice);
    }

    private void reEncryptMasterKey(final SmartMeteringDeviceDto smartMeteringDevice) throws ProtocolAdapterException {
        final byte[] reEncryptedMasterKey = this.reEncryptionService.reEncryptKey(smartMeteringDevice.getMasterKey(),
                SecurityKeyType.E_METER_MASTER);
        smartMeteringDevice.setMasterKey(reEncryptedMasterKey);
    }

    private void reEncryptAuthenticationKey(final SmartMeteringDeviceDto smartMeteringDevice)
            throws ProtocolAdapterException {
        final byte[] reEncryptedAuthenticationKey = this.reEncryptionService
                .reEncryptKey(smartMeteringDevice.getAuthenticationKey(), SecurityKeyType.E_METER_AUTHENTICATION);
        smartMeteringDevice.setAuthenticationKey(reEncryptedAuthenticationKey);
    }

    private void reEncryptEncryptionKey(final SmartMeteringDeviceDto smartMeteringDevice)
            throws ProtocolAdapterException {
        final byte[] reEncryptedEncryptionKey = this.reEncryptionService
                .reEncryptKey(smartMeteringDevice.getGlobalEncryptionUnicastKey(), SecurityKeyType.E_METER_ENCRYPTION);
        smartMeteringDevice.setGlobalEncryptionUnicastKey(reEncryptedEncryptionKey);
    }

    public MbusChannelElementsResponseDto coupleMbusDevice(final DlmsConnectionHolder conn, final DlmsDevice device,
            final MbusChannelElementsDto mbusChannelElements) throws ProtocolAdapterException {

        return this.coupleMBusDeviceCommandExecutor.execute(conn, device, mbusChannelElements);
    }

    public DecoupleMbusDto deCoupleMbusDevice(final DlmsConnectionHolder conn, final DlmsDevice device,
            final DecoupleMbusDto decoupleMbusDto) throws ProtocolAdapterException {

        return this.deCoupleMBusDeviceCommandExecutor.execute(conn, device, decoupleMbusDto);
    }
}
