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
import org.osgp.adapter.protocol.dlms.domain.commands.CoupleMbusDeviceByChannelCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.DeCoupleMBusDeviceCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.dto.valueobjects.smartmetering.CoupleMbusDeviceByChannelRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.CoupleMbusDeviceByChannelResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.DeCoupleMbusDeviceDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.DeCoupleMbusDeviceResponseDto;
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
    private SecurityKeyService securityKeyService;

    @Autowired
    private CoupleMBusDeviceCommandExecutor coupleMBusDeviceCommandExecutor;

    @Autowired
    private DeCoupleMBusDeviceCommandExecutor deCoupleMBusDeviceCommandExecutor;

    @Autowired
    private CoupleMbusDeviceByChannelCommandExecutor coupleMbusDeviceByChannelCommandExecutor;

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
        this.reEncryptMbusDefaultKey(smartMeteringDevice);
    }

    private void reEncryptMasterKey(final SmartMeteringDeviceDto smartMeteringDevice) throws ProtocolAdapterException {
        if (smartMeteringDevice.getMasterKey() == null || smartMeteringDevice.getMasterKey().length <= 0) {
            return;
        }
        final byte[] reEncryptedMasterKey = this.securityKeyService.reEncryptKey(smartMeteringDevice.getMasterKey(),
                SecurityKeyType.E_METER_MASTER);
        smartMeteringDevice.setMasterKey(reEncryptedMasterKey);
    }

    private void reEncryptAuthenticationKey(final SmartMeteringDeviceDto smartMeteringDevice)
            throws ProtocolAdapterException {

        if (smartMeteringDevice.getAuthenticationKey() == null
                || smartMeteringDevice.getAuthenticationKey().length <= 0) {
            return;
        }
        final byte[] reEncryptedAuthenticationKey = this.securityKeyService
                .reEncryptKey(smartMeteringDevice.getAuthenticationKey(), SecurityKeyType.E_METER_AUTHENTICATION);
        smartMeteringDevice.setAuthenticationKey(reEncryptedAuthenticationKey);
    }

    private void reEncryptEncryptionKey(final SmartMeteringDeviceDto smartMeteringDevice)
            throws ProtocolAdapterException {

        if (smartMeteringDevice.getGlobalEncryptionUnicastKey() == null
                || smartMeteringDevice.getGlobalEncryptionUnicastKey().length <= 0) {
            return;
        }
        final byte[] reEncryptedEncryptionKey = this.securityKeyService
                .reEncryptKey(smartMeteringDevice.getGlobalEncryptionUnicastKey(), SecurityKeyType.E_METER_ENCRYPTION);
        smartMeteringDevice.setGlobalEncryptionUnicastKey(reEncryptedEncryptionKey);
    }

    private void reEncryptMbusDefaultKey(final SmartMeteringDeviceDto smartMeteringDevice)
            throws ProtocolAdapterException {

        if (smartMeteringDevice.getMbusDefaultKey() == null || smartMeteringDevice.getMbusDefaultKey().length <= 0) {
            return;
        }

        final byte[] reEncryptedMbusDefaultKey = this.securityKeyService
                .reEncryptKey(smartMeteringDevice.getMbusDefaultKey(), SecurityKeyType.G_METER_MASTER);
        smartMeteringDevice.setMbusDefaultKey(reEncryptedMbusDefaultKey);
    }

    public MbusChannelElementsResponseDto coupleMbusDevice(final DlmsConnectionHolder conn, final DlmsDevice device,
            final MbusChannelElementsDto mbusChannelElements) throws ProtocolAdapterException {
        return this.coupleMBusDeviceCommandExecutor.execute(conn, device, mbusChannelElements);
    }

    public CoupleMbusDeviceByChannelResponseDto coupleMbusDeviceByChannel(final DlmsConnectionHolder conn,
            final DlmsDevice device, final CoupleMbusDeviceByChannelRequestDataDto requestDto)
            throws ProtocolAdapterException {
        return this.coupleMbusDeviceByChannelCommandExecutor.execute(conn, device, requestDto);
    }

    public DeCoupleMbusDeviceResponseDto deCoupleMbusDevice(final DlmsConnectionHolder conn, final DlmsDevice device,
            final DeCoupleMbusDeviceDto deCoupleMbusDeviceDto) throws ProtocolAdapterException {
        return this.deCoupleMBusDeviceCommandExecutor.execute(conn, device, deCoupleMbusDeviceDto);
    }
}
