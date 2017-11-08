/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import org.osgp.adapter.protocol.dlms.application.services.SecurityKeyService;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GenerateAndReplaceKeysRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetKeysRequestDto;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.EncrypterException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;

@Component
public class GenerateAndReplaceKeyCommandExecutor extends AbstractCommandExecutor<ActionRequestDto, ActionResponseDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateAndReplaceKeyCommandExecutor.class);

    @Autowired
    private ReplaceKeyCommandExecutor replaceKeyCommandExecutor;

    @Autowired
    private SecurityKeyService securityKeyService;

    public GenerateAndReplaceKeyCommandExecutor() {
        super(GenerateAndReplaceKeysRequestDataDto.class);
    }

    @Override
    public ActionResponseDto executeBundleAction(final DlmsConnectionHolder conn, final DlmsDevice device,
            final ActionRequestDto actionRequestDto) throws ProtocolAdapterException, FunctionalException {

        return this.execute(conn, device, actionRequestDto);
    }

    @Override
    public ActionResponseDto execute(final DlmsConnectionHolder conn, final DlmsDevice device,
            final ActionRequestDto actionRequestDto) throws ProtocolAdapterException, FunctionalException {
        LOGGER.info("Generate new keys for device {}", device.getDeviceIdentification());
        final SetKeysRequestDto setKeysRequest = this.generateSetKeysRequest();
        return this.replaceKeyCommandExecutor.executeBundleAction(conn, device, setKeysRequest);
    }

    private SetKeysRequestDto generateSetKeysRequest() throws FunctionalException {
        try {
            final byte[] encryptedAuthenticationKey = this.securityKeyService.generateAndEncryptKey();
            final byte[] encryptedEncryptionKey = this.securityKeyService.generateAndEncryptKey();
            final SetKeysRequestDto setKeysRequest = new SetKeysRequestDto(encryptedAuthenticationKey,
                    encryptedEncryptionKey);
            setKeysRequest.setGeneratedKeys(true);
            return setKeysRequest;
        } catch (final EncrypterException e) {
            throw new FunctionalException(FunctionalExceptionType.ENCRYPTION_EXCEPTION, ComponentType.PROTOCOL_DLMS, e);
        }
    }
}
