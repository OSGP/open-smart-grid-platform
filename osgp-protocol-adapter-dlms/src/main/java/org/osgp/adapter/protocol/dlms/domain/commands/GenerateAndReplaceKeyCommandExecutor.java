/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;

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
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.security.EncryptionService;

/**
 * Some code may look odd, specifically in the execute() method. The reason is
 * that the device may (sometimes) return NOT_OK after a replacekeys request but
 * was in fact successful! Actually the situation is that (sometimes) the device
 * returns NOT_OK but does replace the keys. So the key that was sent to the
 * device that received the status NOT_OK should be saved, so in case the
 * supposedly valid key (the key that was on the device before replace keys was
 * executed) does not work anymore the new (but supposedly NOT_OK) key can be
 * tried. This key is recognized because both: valid_to=null and valid_from=null
 * ! If that key works we know the device gave the wrong response and this key
 * should be made valid. See also DlmsDevice: discardInvalidKeys,
 * promoteInvalidKeys, get/hasNewSecurityKey.
 *
 */
@Component
public class GenerateAndReplaceKeyCommandExecutor
extends AbstractCommandExecutor<ActionRequestDto, ActionResponseDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateAndReplaceKeyCommandExecutor.class);

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private ReplaceKeyCommandExecutor replaceKeyCommandExecutor;

    public static final int AES_GMC_128_KEY_SIZE = 128;

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
            final ActionRequestDto actionRequestDto)
                    throws ProtocolAdapterException, FunctionalException {

        final SetKeysRequestDto setKeysRequestDto = this.generateKeys();

        return this.replaceKeyCommandExecutor.executeBundleAction(conn, device, setKeysRequestDto);
    }

    private SetKeysRequestDto generateKeys() throws FunctionalException {
        final byte[] authenticationKey = this.generateKey();
        final byte[] encryptionKey = this.generateKey();

        final byte[] encryptedAuthenticationKey = this.encryptionService.encrypt(authenticationKey);
        final byte[] encryptedEncryptionKey = this.encryptionService.encrypt(encryptionKey);

        return new SetKeysRequestDto(encryptedAuthenticationKey, encryptedEncryptionKey);
    }

    private final byte[] generateKey() {
        try {
            final KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(AES_GMC_128_KEY_SIZE);
            return keyGenerator.generateKey().getEncoded();
        } catch (final NoSuchAlgorithmException e) {
            throw new AssertionError("Expected AES algorithm to be available for key generation.", e);
        }
    }
}
