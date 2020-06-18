/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.security;

import java.io.IOException;

import org.openmuc.jdlms.MethodParameter;
import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.SecurityUtils;
import org.openmuc.jdlms.SecurityUtils.KeyId;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.SecurityKeyService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetKeysRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.EncrypterException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

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
 */
@Component
public class ReplaceKeyCommandExecutor
        extends AbstractCommandExecutor<ReplaceKeyCommandExecutor.KeyWrapper, DlmsDevice> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReplaceKeyCommandExecutor.class);

    private static final String REPLACE_KEYS = "Replace keys for device: ";
    private static final String WAS_SUCCESFULL = " was successful";

    @Autowired
    @Qualifier("secretMangementService")
    private SecurityKeyService securityKeyService;

    static class KeyWrapper {
        private final byte[] bytes;
        private final KeyId keyId;
        private final SecurityKeyType securityKeyType;

        public KeyWrapper(final byte[] bytes, final KeyId keyId, final SecurityKeyType securityKeyType) {
            this.bytes = bytes;
            this.keyId = keyId;
            this.securityKeyType = securityKeyType;
        }

        public byte[] getBytes() {
            return this.bytes;
        }

        public KeyId getKeyId() {
            return this.keyId;
        }

        public SecurityKeyType getSecurityKeyType() {
            return this.securityKeyType;
        }
    }

    public ReplaceKeyCommandExecutor() {
        super(SetKeysRequestDto.class);
    }

    public static KeyWrapper wrap(final byte[] bytes, final KeyId keyId, final SecurityKeyType securityKeyType) {
        return new KeyWrapper(bytes, keyId, securityKeyType);
    }

    @Override
    public ActionResponseDto executeBundleAction(final DlmsConnectionManager conn, final DlmsDevice device,
            final ActionRequestDto actionRequestDto) throws OsgpException {

        this.checkActionRequestType(actionRequestDto);

        LOGGER.info("Keys set on device :{}", device.getDeviceIdentification());

        SetKeysRequestDto setKeysRequestDto = (SetKeysRequestDto) actionRequestDto;
        if (!setKeysRequestDto.isGeneratedKeys()) {
            setKeysRequestDto = this.reEncryptKeys((SetKeysRequestDto) actionRequestDto);
        }

        final DlmsDevice devicePostSave = this.execute(conn, device, ReplaceKeyCommandExecutor
                .wrap(setKeysRequestDto.getAuthenticationKey(), KeyId.AUTHENTICATION_KEY,
                        SecurityKeyType.E_METER_AUTHENTICATION));

        this.execute(conn, devicePostSave, ReplaceKeyCommandExecutor
                .wrap(setKeysRequestDto.getEncryptionKey(), KeyId.GLOBAL_UNICAST_ENCRYPTION_KEY,
                        SecurityKeyType.E_METER_ENCRYPTION));

        return new ActionResponseDto(REPLACE_KEYS + device.getDeviceIdentification() + WAS_SUCCESFULL);
    }

    private SetKeysRequestDto reEncryptKeys(final SetKeysRequestDto setKeysRequestDto) throws FunctionalException {
        final byte[] reEncryptedAuthenticationKey = this.securityKeyService
                .reEncryptKey(setKeysRequestDto.getAuthenticationKey(), SecurityKeyType.E_METER_AUTHENTICATION);
        final byte[] reEncryptedEncryptionKey = this.securityKeyService
                .reEncryptKey(setKeysRequestDto.getEncryptionKey(), SecurityKeyType.E_METER_ENCRYPTION);

        return new SetKeysRequestDto(reEncryptedAuthenticationKey, reEncryptedEncryptionKey);
    }

    @Override
    public DlmsDevice execute(final DlmsConnectionManager conn, final DlmsDevice device,
            final ReplaceKeyCommandExecutor.KeyWrapper keyWrapper) throws OsgpException {

        final DlmsDevice devicePostSave = this.securityKeyService
                .storeNewKey(device, keyWrapper.getBytes(), keyWrapper.getSecurityKeyType());
        this.sendToDevice(conn, devicePostSave, keyWrapper);
        return this.securityKeyService.validateNewKey(devicePostSave, keyWrapper.getSecurityKeyType());
    }

    /**
     * Send the key to the device.
     *
     * @param conn
     *         jDLMS connection.
     * @param device
     *         Device instance
     * @param keyWrapper
     *         Key data
     */
    private void sendToDevice(final DlmsConnectionManager conn, final DlmsDevice device,
            final ReplaceKeyCommandExecutor.KeyWrapper keyWrapper) throws ProtocolAdapterException {

        try {
            final byte[] decryptedKey = this.securityKeyService
                    .decryptKey(keyWrapper.getBytes(), keyWrapper.securityKeyType);
            final byte[] decryptedMasterKey = this.securityKeyService
                    .getDlmsMasterKey(device.getDeviceIdentification());

            final MethodParameter methodParameterAuth = SecurityUtils
                    .keyChangeMethodParamFor(decryptedMasterKey, decryptedKey, keyWrapper.getKeyId());

            conn.getDlmsMessageListener().setDescription(
                    "ReplaceKey for " + keyWrapper.securityKeyType + " " + keyWrapper.getKeyId() + ", call method: "
                            + JdlmsObjectToStringUtil.describeMethod(methodParameterAuth));

            final MethodResultCode methodResultCode = conn.getConnection().action(methodParameterAuth).getResultCode();

            if (!MethodResultCode.SUCCESS.equals(methodResultCode)) {
                throw new ProtocolAdapterException(
                        "AccessResultCode for replace keys was not SUCCESS: " + methodResultCode);
            }

            if (keyWrapper.securityKeyType == SecurityKeyType.E_METER_AUTHENTICATION) {
                conn.getConnection().changeClientGlobalAuthenticationKey(decryptedKey);
            } else if (keyWrapper.securityKeyType == SecurityKeyType.E_METER_ENCRYPTION) {
                conn.getConnection().changeClientGlobalEncryptionKey(decryptedKey);
            }
        } catch (final IOException e) {
            throw new ConnectionException(e);
        } catch (final EncrypterException e) {
            LOGGER.error("Unexpected exception during decryption of security keys", e);
            throw new ProtocolAdapterException(
                    "Unexpected exception during decryption of security keys, reason = " + e.getMessage());
        }
    }
}
