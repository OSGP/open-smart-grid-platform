/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import org.openmuc.jdlms.ClientConnection;
import org.openmuc.jdlms.MethodResultCode;
import org.osgp.adapter.protocol.dlms.application.models.ProtocolMeterInfo;
import org.osgp.adapter.protocol.dlms.application.services.DomainHelperService;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionValueObjectResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GMeterInfoDto;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;

@Component()
public class SetEncryptionKeyExchangeOnGMeterBundleCommandExecutor implements
CommandExecutor<GMeterInfoDto, ActionValueObjectResponseDto> {

    @Autowired
    private SetEncryptionKeyExchangeOnGMeterCommandExecutor setEncryptionKeyExchangeOnGMeterCommandExecutor;

    @Autowired
    private DomainHelperService domainHelperService;

    @Override
    public ActionValueObjectResponseDto execute(final ClientConnection conn, final DlmsDevice device,
            final GMeterInfoDto gMeterInfo) {

        DlmsDevice gMeterDevice;
        try {
            gMeterDevice = this.domainHelperService.findDlmsDevice(gMeterInfo.getDeviceIdentification());
            final ProtocolMeterInfo protocolMeterInfo = new ProtocolMeterInfo(gMeterInfo.getChannel(),
                    gMeterInfo.getDeviceIdentification(), gMeterDevice.getValidSecurityKey(
                            SecurityKeyType.G_METER_ENCRYPTION).getKey(), gMeterDevice.getValidSecurityKey(
                                    SecurityKeyType.G_METER_MASTER).getKey());

            final MethodResultCode methodResultCode = this.setEncryptionKeyExchangeOnGMeterCommandExecutor.execute(
                    conn, device, protocolMeterInfo);
            if (MethodResultCode.SUCCESS.equals(methodResultCode)) {
                return new ActionValueObjectResponseDto("Setting encryption key exchange on Gas meter "
                        + gMeterInfo.getDeviceIdentification() + " was successful");
            } else {
                return new ActionValueObjectResponseDto("Setting encryption key exchange on Gas meter "
                        + gMeterInfo.getDeviceIdentification() + " was not successful and returned with: "
                        + methodResultCode);
            }
        } catch (final ProtocolAdapterException e) {
            return new ActionValueObjectResponseDto(e, "Error while setting encryption key exchange on Gas meter "
                    + gMeterInfo.getDeviceIdentification());
        } catch (final FunctionalException e) {
            return new ActionValueObjectResponseDto(e, "Error while looking up G-Meter "
                    + gMeterInfo.getDeviceIdentification());
        }

    }
}
