/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.E_METER_AUTHENTICATION;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.E_METER_MASTER;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.G_METER_ENCRYPTION;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.G_METER_MASTER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.ArrayUtils;
import org.opensmartgridplatform.adapter.protocol.dlms.application.mapping.InstallationMapper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus.CoupleMBusDeviceCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus.CoupleMbusDeviceByChannelCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus.DeCoupleMBusDeviceCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CoupleMbusDeviceByChannelRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CoupleMbusDeviceByChannelResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DeCoupleMbusDeviceDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DeCoupleMbusDeviceResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MbusChannelElementsDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MbusChannelElementsResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SmartMeteringDeviceDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "dlmsInstallationService")
public class InstallationService {

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepository;

    @Autowired
    private InstallationMapper installationMapper;

    @Autowired
    private EncryptionHelperService encryptionService;

    @Autowired
    private SecretManagementService secretManagementService;

    @Autowired
    private CoupleMBusDeviceCommandExecutor coupleMBusDeviceCommandExecutor;

    @Autowired
    private DeCoupleMBusDeviceCommandExecutor deCoupleMBusDeviceCommandExecutor;

    @Autowired
    private CoupleMbusDeviceByChannelCommandExecutor coupleMbusDeviceByChannelCommandExecutor;

    public void addMeter(final SmartMeteringDeviceDto smartMeteringDevice) throws FunctionalException {
        this.storeNewKeys(smartMeteringDevice);
        final DlmsDevice dlmsDevice = this.installationMapper.map(smartMeteringDevice, DlmsDevice.class);
        this.dlmsDeviceRepository.save(dlmsDevice);
    }

    private void storeNewKeys(final SmartMeteringDeviceDto deviceDto) throws FunctionalException {
        Map<SecurityKeyType, byte[]> keysByType = new EnumMap<>(SecurityKeyType.class);
        List<SecurityKeyType> keyTypesToStore = this.determineKeyTypesToStore(deviceDto);
        for (SecurityKeyType keyType : keyTypesToStore) {
            byte[] key = this.getKeyFromDeviceDto(deviceDto, keyType);
            if (key != null && ArrayUtils.isNotEmpty(key)) {
                keysByType.put(keyType, this.encryptionService.rsaDecrypt(key));
            } else {
                Exception rootCause = new NoSuchElementException(keyType.name());
                throw new FunctionalException(FunctionalExceptionType.KEY_NOT_PRESENT, ComponentType.PROTOCOL_DLMS,
                        rootCause);
            }
        }
        this.secretManagementService.storeNewKeys(deviceDto.getDeviceIdentification(), keysByType);
    }

    private List<SecurityKeyType> determineKeyTypesToStore(SmartMeteringDeviceDto deviceDto) {
        List<SecurityKeyType> keyTypesToStore = new ArrayList<>(Arrays.asList(E_METER_MASTER, E_METER_AUTHENTICATION));
        boolean deviceIsGMeter = this.getKeyFromDeviceDto(deviceDto, G_METER_MASTER) != null
                || this.getKeyFromDeviceDto(deviceDto, G_METER_ENCRYPTION) != null;
        if (deviceIsGMeter) {
            keyTypesToStore.addAll(Arrays.asList(G_METER_MASTER, G_METER_ENCRYPTION));
        }
        return keyTypesToStore;
    }

    private byte[] getKeyFromDeviceDto(SmartMeteringDeviceDto deviceDto, SecurityKeyType keyType) {
        switch (keyType) {
        case E_METER_MASTER:
            return deviceDto.getMasterKey();
        case E_METER_AUTHENTICATION:
            return deviceDto.getAuthenticationKey();
        case G_METER_ENCRYPTION:
            return deviceDto.getGlobalEncryptionUnicastKey();
        case G_METER_MASTER:
            return deviceDto.getMbusDefaultKey();
        default:
            throw new IllegalArgumentException("Unknown type " + keyType);
        }
    }

    public MbusChannelElementsResponseDto coupleMbusDevice(final DlmsConnectionManager conn, final DlmsDevice device,
            final MbusChannelElementsDto mbusChannelElements) throws ProtocolAdapterException {
        return this.coupleMBusDeviceCommandExecutor.execute(conn, device, mbusChannelElements);
    }

    public CoupleMbusDeviceByChannelResponseDto coupleMbusDeviceByChannel(final DlmsConnectionManager conn,
            final DlmsDevice device, final CoupleMbusDeviceByChannelRequestDataDto requestDto)
            throws ProtocolAdapterException {
        return this.coupleMbusDeviceByChannelCommandExecutor.execute(conn, device, requestDto);
    }

    public DeCoupleMbusDeviceResponseDto deCoupleMbusDevice(final DlmsConnectionManager conn, final DlmsDevice device,
            final DeCoupleMbusDeviceDto deCoupleMbusDeviceDto) throws ProtocolAdapterException {
        return this.deCoupleMBusDeviceCommandExecutor.execute(conn, device, deCoupleMbusDeviceDto);
    }
}
