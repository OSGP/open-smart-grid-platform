/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.openmuc.jdlms.LnClientConnection;
import org.openmuc.jdlms.MethodParameter;
import org.openmuc.jdlms.MethodResult;
import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SecurityUtils;
import org.openmuc.jdlms.datatypes.DataObject;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component()
public class SetEncryptionKeyExchangeOnGMeterCommandExecutor implements
CommandExecutor<HashMap<String, String>, MethodResultCode> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetEncryptionKeyExchangeOnGMeterCommandExecutor.class);

    private static final int CLASS_ID = 72;
    private static final ObisCode OBIS_CODE = new ObisCode("0.0.24.1.0.255");

    private static final int SET_ENCRYPTION_KEY_ATTRIBUTE_ID = 7;
    private static final int TRANSFER_KEY_ATTRIBUTE_ID = 8;

    @Autowired
    private DlmsHelperService dlmsHelperService;

    @Override
    public MethodResultCode execute(final LnClientConnection conn, final HashMap<String, String> keys)
            throws IOException, ProtocolAdapterException {
        LOGGER.debug("SetEncryptionKeyExchangeOnGMeterCommandExecutor.execute called");

        final byte[] encryptedKey = SecurityUtils.aesRFC3394KeyWrap(keys.get("masterKey").getBytes(), keys
                .get("newKey").getBytes());

        final DataObject keyToSetvalueDataObject = DataObject.newOctetStringData(encryptedKey);

        // Transfer Key
        final MethodParameter transferKeyMethod = new MethodParameter(CLASS_ID, OBIS_CODE, TRANSFER_KEY_ATTRIBUTE_ID,
                keyToSetvalueDataObject);
        List<MethodResult> methodResultCode = conn.action(transferKeyMethod);

        if (methodResultCode == null || methodResultCode.isEmpty() || methodResultCode.get(0) == null
                || !MethodResultCode.SUCCESS.equals(methodResultCode.get(0).resultCode())) {
            throw new IOException("Error while executing TRANSFER_KEY_ATTRIBUTE_ID");
        }

        // Set Encryption Key
        final MethodParameter setEncryptionKeyMethod = new MethodParameter(CLASS_ID, OBIS_CODE,
                SET_ENCRYPTION_KEY_ATTRIBUTE_ID, keyToSetvalueDataObject);
        methodResultCode = conn.action(setEncryptionKeyMethod);

        if (methodResultCode == null || methodResultCode.isEmpty() || methodResultCode.get(0) == null
                || !MethodResultCode.SUCCESS.equals(methodResultCode.get(0).resultCode())) {
            throw new IOException("Error while executing SET_ENCRYPTION_KEY_ATTRIBUTE_ID");
        }

        LOGGER.info("Finished calling conn.set");

        return MethodResultCode.SUCCESS;
    }
}
