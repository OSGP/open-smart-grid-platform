/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.dlms.application.mapping.InstallationMapper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SmartMeteringDeviceDto;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

@ExtendWith(MockitoExtension.class)
public class InstallationServiceTest {
    @InjectMocks
    InstallationService testService;
    @Mock
    SecretManagementService secretManagementService;
    @Mock
    DlmsDeviceRepository dlmsDeviceRepository;
    @Mock
    InstallationMapper installationMapper;
    @Mock
    EncryptionHelperService encryptionHelperService;

    @Test
    void addEMeter() throws FunctionalException {
        // GIVEN
        final SmartMeteringDeviceDto deviceDto = new SmartMeteringDeviceDto();
        deviceDto.setDeviceIdentification("Test");
        deviceDto.setMasterKey(new byte[16]);
        deviceDto.setAuthenticationKey(new byte[16]);
        deviceDto.setGlobalEncryptionUnicastKey(new byte[16]);
        final DlmsDevice dlmsDevice = new DlmsDevice();
        when(this.installationMapper.map(deviceDto, DlmsDevice.class)).thenReturn(dlmsDevice);
        when(this.dlmsDeviceRepository.save(dlmsDevice)).thenReturn(dlmsDevice);
        when(this.encryptionHelperService.rsaDecrypt(any())).thenReturn(new byte[16]);
        // WHEN
        this.testService.addMeter(deviceDto);
        //THEN
        verify(this.secretManagementService, times(1)).storeNewKeys(any(), any());
        verify(this.secretManagementService, times(1)).activateNewKeys(any(), any());
    }

    @Test
    void addGMeter() throws FunctionalException {
        // GIVEN
        final SmartMeteringDeviceDto deviceDto = new SmartMeteringDeviceDto();
        deviceDto.setDeviceIdentification("Test");
        deviceDto.setMbusDefaultKey(new byte[16]);
        final DlmsDevice dlmsDevice = new DlmsDevice();
        when(this.installationMapper.map(deviceDto, DlmsDevice.class)).thenReturn(dlmsDevice);
        when(this.dlmsDeviceRepository.save(dlmsDevice)).thenReturn(dlmsDevice);
        when(this.encryptionHelperService.rsaDecrypt(any())).thenReturn(new byte[16]);
        // WHEN
        this.testService.addMeter(deviceDto);
        //THEN
        verify(this.secretManagementService, times(1)).storeNewKeys(any(), any());
        verify(this.secretManagementService, times(1)).activateNewKeys(any(), any());
    }

    @Test
    void addMeterNoKeys() {
        // GIVEN
        final SmartMeteringDeviceDto deviceDto = new SmartMeteringDeviceDto();
        deviceDto.setDeviceIdentification("Test");
        // WHEN
        Assertions.assertThatExceptionOfType(FunctionalException.class)
                  .isThrownBy(() -> this.testService.addMeter(deviceDto));
    }

    @Test
    void addMeterRedundantKeys() {
        // GIVEN
        final SmartMeteringDeviceDto deviceDto = new SmartMeteringDeviceDto();
        deviceDto.setDeviceIdentification("Test");
        deviceDto.setMasterKey(new byte[16]);
        deviceDto.setAuthenticationKey(new byte[16]);
        deviceDto.setGlobalEncryptionUnicastKey(new byte[16]);
        deviceDto.setMbusDefaultKey(new byte[16]);
        // WHEN
        Assertions.assertThatExceptionOfType(FunctionalException.class)
                  .isThrownBy(() -> this.testService.addMeter(deviceDto));
    }

    @Test
    void addMeterNoDeviceIdentification() {
        // GIVEN
        final SmartMeteringDeviceDto deviceDto = new SmartMeteringDeviceDto();
        deviceDto.setMbusDefaultKey(new byte[16]);
        // WHEN
        Assertions.assertThatExceptionOfType(FunctionalException.class)
                  .isThrownBy(() -> this.testService.addMeter(deviceDto));
    }
}
