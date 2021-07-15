/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.security;

import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.G_METER_ENCRYPTION;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.G_METER_MASTER;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.openmuc.jdlms.MethodParameter;
import org.openmuc.jdlms.MethodResult;
import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.SecretManagementService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.interfaceclass.method.MBusClientMethod;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GMeterInfoDto;
import org.opensmartgridplatform.shared.exceptionhandling.EncrypterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component()
public class SetEncryptionKeyExchangeOnGMeterCommandExecutor
        extends AbstractCommandExecutor<GMeterInfoDto, MethodResultCode> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetEncryptionKeyExchangeOnGMeterCommandExecutor.class);

    private static final int CLASS_ID = 72;
    private static final ObisCode OBIS_CODE_INTERVAL_MBUS_1 = new ObisCode("0.1.24.1.0.255");
    private static final ObisCode OBIS_CODE_INTERVAL_MBUS_2 = new ObisCode("0.2.24.1.0.255");
    private static final ObisCode OBIS_CODE_INTERVAL_MBUS_3 = new ObisCode("0.3.24.1.0.255");
    private static final ObisCode OBIS_CODE_INTERVAL_MBUS_4 = new ObisCode("0.4.24.1.0.255");

    private static final Map<Integer, ObisCode> OBIS_HASHMAP = new HashMap<>();

    static {
        OBIS_HASHMAP.put(1, OBIS_CODE_INTERVAL_MBUS_1);
        OBIS_HASHMAP.put(2, OBIS_CODE_INTERVAL_MBUS_2);
        OBIS_HASHMAP.put(3, OBIS_CODE_INTERVAL_MBUS_3);
        OBIS_HASHMAP.put(4, OBIS_CODE_INTERVAL_MBUS_4);
    }

    @Autowired
    private SecretManagementService secretManagementService;

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepository;

    public SetEncryptionKeyExchangeOnGMeterCommandExecutor() {
        super(GMeterInfoDto.class);
    }

    @Override
    public ActionResponseDto asBundleResponse(final MethodResultCode executionResult) throws ProtocolAdapterException {
        this.checkMethodResultCode(executionResult);
        return new ActionResponseDto("M-Bus User key exchange on Gas meter was successful");
    }

    @Override
    public MethodResultCode execute(final DlmsConnectionManager conn, final DlmsDevice device,
            final GMeterInfoDto gMeterInfo) throws ProtocolAdapterException {
        try {
            LOGGER.debug("SetEncryptionKeyExchangeOnGMeterCommandExecutor.execute called");

            final String mbusDeviceIdentification = gMeterInfo.getDeviceIdentification();
            final int channel = gMeterInfo.getChannel();
            final ObisCode obisCode = OBIS_HASHMAP.get(channel);
            final byte[] gMeterEncryptionKey = this.secretManagementService
                    .generate128BitsKeyAndStoreAsNewKey(mbusDeviceIdentification, G_METER_ENCRYPTION);

            MethodResult methodResultCode = this
                    .transferKey(conn, mbusDeviceIdentification, channel, gMeterEncryptionKey);
            this.checkMethodResultCode(methodResultCode, "M-Bus Setup transfer_key", obisCode);

            methodResultCode = this.setEncryptionKey(conn, channel, gMeterEncryptionKey);
            this.checkMethodResultCode(methodResultCode, "M-Bus Setup set_encryption_key", obisCode);

            this.secretManagementService.activateNewKey(mbusDeviceIdentification, G_METER_ENCRYPTION);
            return MethodResultCode.SUCCESS;
        } catch (final IOException e) {
            throw new ConnectionException(e);
        } catch (final EncrypterException e) {
            throw new ProtocolAdapterException(
                    "Unexpected exception during decryption of security keys, reason = " + e.getMessage(), e);
        }
    }

    private MethodResult setEncryptionKey(final DlmsConnectionManager conn, final int channel,
            final byte[] encryptionKey)
            throws IOException {
        final MethodParameter methodSetEncryptionKey = this
                .getSetEncryptionKeyMethodParameter(OBIS_HASHMAP.get(channel), encryptionKey);
        conn.getDlmsMessageListener().setDescription("SetEncryptionKeyExchangeOnGMeter for channel " + channel
                + ", call M-Bus Setup set_encryption_key method: " + JdlmsObjectToStringUtil
                .describeMethod(methodSetEncryptionKey));
        return conn.getConnection().action(methodSetEncryptionKey);
    }

    private MethodResult transferKey(final DlmsConnectionManager conn, final String mbusDeviceIdentification, final int channel,
            final byte[] encryptionKey) throws ProtocolAdapterException, IOException {
        final MethodParameter methodTransferKey = this
                .getTransferKeyMethodParameter(mbusDeviceIdentification, channel, encryptionKey);
        conn.getDlmsMessageListener().setDescription(
                "SetEncryptionKeyExchangeOnGMeter for channel " + channel + ", call M-Bus Setup transfer_key method: "
                        + JdlmsObjectToStringUtil.describeMethod(methodTransferKey));

        return conn.getConnection().action(methodTransferKey);
    }

    private MethodParameter getTransferKeyMethodParameter(final String mbusDeviceIdentification, final int channel,
            final byte[] gMeterUserKey) throws ProtocolAdapterException {
        final DlmsDevice mbusDevice = this.dlmsDeviceRepository.findByDeviceIdentification(mbusDeviceIdentification);
        if (mbusDevice == null) {
            throw new ProtocolAdapterException("Unknown M-Bus device: " + mbusDeviceIdentification);
        }
        final byte[] mbusDefaultKey = this.secretManagementService.getKey(mbusDeviceIdentification, G_METER_MASTER);
        final byte[] encryptedUserKey = this.encryptMbusUserKey(mbusDefaultKey, gMeterUserKey);
        final DataObject methodParameter = DataObject.newOctetStringData(encryptedUserKey);
        final MBusClientMethod method = MBusClientMethod.TRANSFER_KEY;
        return new MethodParameter(method.getInterfaceClass().id(), OBIS_HASHMAP.get(channel), method.getMethodId(),
                methodParameter);
    }

    private void checkMethodResultCode(final MethodResult methodResultCode, final String methodParameterName,
            final ObisCode obisCode) throws ProtocolAdapterException {
        if (methodResultCode == null || !MethodResultCode.SUCCESS.equals(methodResultCode.getResultCode())) {
            String message = "Error while executing " + methodParameterName + ".";
            if (methodResultCode != null) {
                message += " Reason = " + methodResultCode.getResultCode();
            }
            throw new ProtocolAdapterException(message);
        } else {
            LOGGER.info("Successfully invoked '{}' method: class_id {} obis_code {}", methodParameterName, CLASS_ID,
                    obisCode);
        }
    }

    private MethodParameter getSetEncryptionKeyMethodParameter(final ObisCode obisCode, final byte[] encryptionKey) {
        final DataObject methodParameter = DataObject.newOctetStringData(encryptionKey);
        final MBusClientMethod method = MBusClientMethod.SET_ENCRYPTION_KEY;
        return new MethodParameter(method.getInterfaceClass().id(), obisCode, method.getMethodId(), methodParameter);
    }

    /**
     * Encrypts a new M-Bus User key with the M-Bus Default key for use as M-Bus
     * Client Setup transfer_key parameter.
     * <p>
     * Note that the specifics of the encryption of the M-Bus User key depend on
     * the M-Bus version the devices support. This method should be appropriate
     * for use with DSMR 4 M-Bus devices.
     * <p>
     * The encryption is performed by applying an AES/CBC/NoPadding cipher
     * initialized for encryption with the given mbusDefaultKey and an
     * initialization vector of 16 zero-bytes to the given mbusUserKey.
     *
     * @return the properly wrapped User key for a DSMR 4 M-Bus User key change.
     */
    private byte[] encryptMbusUserKey(final byte[] mbusDefaultKey, final byte[] mbusUserKey)
            throws ProtocolAdapterException {

        final Key secretkeySpec = new SecretKeySpec(mbusDefaultKey, "AES");

        try {
            final Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            final IvParameterSpec params = new IvParameterSpec(new byte[16]);
            cipher.init(Cipher.ENCRYPT_MODE, secretkeySpec, params);
            return cipher.doFinal(mbusUserKey);

        } catch (final NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            final String message = "Error encrypting M-Bus User key with M-Bus Default key for transfer.";
            LOGGER.error(message, e);
            throw new ProtocolAdapterException(message);
        }
    }
}
