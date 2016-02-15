/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.util.encoders.Hex;
import org.openmuc.jdlms.LnClientConnection;
import org.openmuc.jdlms.MethodParameter;
import org.openmuc.jdlms.MethodResult;
import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SecurityUtils;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.interfaceclass.method.MBusClientMethod;
import org.osgp.adapter.protocol.dlms.application.models.ProtocolMeterInfo;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component()
public class SetEncryptionKeyExchangeOnGMeterCommandExecutor implements
CommandExecutor<ProtocolMeterInfo, MethodResultCode> {

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

    @Override
    public MethodResultCode execute(final LnClientConnection conn, final DlmsDevice device,
            final ProtocolMeterInfo protocolMeterInfo) throws IOException, ProtocolAdapterException {
        LOGGER.debug("SetEncryptionKeyExchangeOnGMeterCommandExecutor.execute called");

        final byte[] unencryptedEncryptionKey = Hex.decode(protocolMeterInfo.getEncryptionKey());
        final byte[] masterKey = Hex.decode(protocolMeterInfo.getMasterKey());

        final ObisCode obisCode = OBIS_HASHMAP.get(protocolMeterInfo.getChannel());

        try {
            final MethodParameter methodTransferKey = this.transferKeyToMBus(obisCode, masterKey,
                    unencryptedEncryptionKey);

            final List<MethodResult> methodResultCode = conn.action(methodTransferKey);

            if (!MethodResultCode.SUCCESS.equals(methodResultCode.get(0).resultCode())) {
                throw new IOException("Error while executing transferKeyToMBus. Reason = "
                        + methodResultCode.get(0).resultCode());
            }

            LOGGER.info("Success!: Finished calling transferKeyToMBus class_id {} obis_code {}", CLASS_ID, obisCode);

        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
                | BadPaddingException e) {
            throw new ProtocolAdapterException(e.getMessage());
        }
        final MethodParameter methodSetEncryptionKey = this.setEncryptionKey(obisCode, unencryptedEncryptionKey);
        final List<MethodResult> methodResultCode = conn.action(methodSetEncryptionKey);
        if (!MethodResultCode.SUCCESS.equals(methodResultCode.get(0).resultCode())) {
            throw new IOException("Error while executing setEncryptionKey. Reason = "
                    + methodResultCode.get(0).resultCode());
        }

        LOGGER.info("Success!: Finished calling setEncryptionKey class_id {} obis_code {}", CLASS_ID, obisCode);
        return MethodResultCode.SUCCESS;
    }

    private MethodParameter transferKeyToMBus(final ObisCode obisCode, final byte[] defaultMBusKey,
            final byte[] encryptionKey) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        byte[] encryptedEncryptionkey;
        encryptedEncryptionkey = SecurityUtils.aes128Ciphering(defaultMBusKey, encryptionKey);

        final DataObject methodParameter = DataObject.newOctetStringData(encryptedEncryptionkey);
        final MethodParameter ret = new MethodParameter(MBusClientMethod.TRANSFER_KEY, obisCode, methodParameter);

        return ret;
    }

    private MethodParameter setEncryptionKey(final ObisCode obisCode, final byte[] encryptionKey) throws IOException {
        final DataObject methodParameter = DataObject.newOctetStringData(encryptionKey);
        final MethodParameter ret = new MethodParameter(MBusClientMethod.SET_ENCRYPTION_KEY, obisCode, methodParameter);

        return ret;
    }

}
