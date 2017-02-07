/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.services;

import org.osgp.adapter.protocol.dlms.application.mapping.InstallationMapper;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.dto.valueobjects.smartmetering.SmartMeteringDeviceDto;
import com.alliander.osgp.shared.security.EncryptionService;
import com.alliander.osgp.shared.security.RsaEncryptionService;

@Service(value = "dlmsInstallationService")
public class InstallationService {

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepository;

    @Autowired
    private InstallationMapper installationMapper;

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private RsaEncryptionService rsaEncryptionService;

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
        final byte[] reEncryptedMasterKey = this.reEncryptKey(smartMeteringDevice.getMasterKey(),
                SecurityKeyType.E_METER_MASTER);
        smartMeteringDevice.setMasterKey(reEncryptedMasterKey);
    }

    private void reEncryptAuthenticationKey(final SmartMeteringDeviceDto smartMeteringDevice)
            throws ProtocolAdapterException {
        final byte[] reEncryptedAuthenticationKey = this.reEncryptKey(smartMeteringDevice.getAuthenticationKey(),
                SecurityKeyType.E_METER_AUTHENTICATION);
        smartMeteringDevice.setAuthenticationKey(reEncryptedAuthenticationKey);
    }

    private void reEncryptEncryptionKey(final SmartMeteringDeviceDto smartMeteringDevice)
            throws ProtocolAdapterException {
        final byte[] reEncryptedEncryptionKey = this.reEncryptKey(smartMeteringDevice.getGlobalEncryptionUnicastKey(),
                SecurityKeyType.E_METER_ENCRYPTION);
        smartMeteringDevice.setGlobalEncryptionUnicastKey(reEncryptedEncryptionKey);
    }

    private byte[] reEncryptKey(final byte[] asymmetricEncryptedKey, final SecurityKeyType keyType)
            throws ProtocolAdapterException {

        if (asymmetricEncryptedKey == null) {
            return null;
        }

        try {
            /*
             * Replace the asymmetric encryption for which the public key is
             * shared with web service callers by a faster symmetric encryption
             * for use inside the protocol adapter only.
             */
            final byte[] decryptedKeyBytes = this.rsaEncryptionService.decrypt(asymmetricEncryptedKey);
            return this.encryptionService.encrypt(decryptedKeyBytes);
        } catch (final Exception e) {
            throw new ProtocolAdapterException("Error processing " + keyType + " key", e);
        }
    }
}
