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
import java.util.Map;

import org.openmuc.jdlms.LnClientConnection;
import org.openmuc.jdlms.MethodParameter;
import org.openmuc.jdlms.MethodResult;
import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SecurityUtils;
import org.openmuc.jdlms.datatypes.DataObject;
import org.osgp.adapter.protocol.dlms.application.models.ProtocolMeterInfo;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final Map<Integer, ObisCode> OBIS_HASHMAP = new HashMap();;
    static {
        OBIS_HASHMAP.put(1, OBIS_CODE_INTERVAL_MBUS_1);
        OBIS_HASHMAP.put(2, OBIS_CODE_INTERVAL_MBUS_2);
        OBIS_HASHMAP.put(3, OBIS_CODE_INTERVAL_MBUS_3);
        OBIS_HASHMAP.put(4, OBIS_CODE_INTERVAL_MBUS_4);
    }

    private enum AttributeEnum {
        SET_ENCRYPTION_KEY_ATTRIBUTE_ID(7),
        TRANSFER_KEY_ATTRIBUTE_ID(8);

        private int attrValue;

        private AttributeEnum(final int attrValue) {
            this.attrValue = attrValue;
        }

        public int getAttrValue() {
            return this.attrValue;
        }
    }

    @Autowired
    private DlmsHelperService dlmsHelperService;

    @Override
    public MethodResultCode execute(final LnClientConnection conn, final DlmsDevice device,
            final ProtocolMeterInfo protocolMeterInfo) throws IOException, ProtocolAdapterException {
        LOGGER.debug("SetEncryptionKeyExchangeOnGMeterCommandExecutor.execute called");

        final byte[] encryptedKey = SecurityUtils.aesRFC3394KeyWrap(
                device.getValidSecurityKey(SecurityKeyType.E_METER_MASTER).getKey().getBytes(), protocolMeterInfo
                        .getEncryptionKey().getBytes());
        final DataObject keyToSetDataObject = DataObject.newOctetStringData(encryptedKey);

        final ObisCode obisCode = OBIS_HASHMAP.get(protocolMeterInfo.getChannel());

        this.performKeyAction(conn, keyToSetDataObject, obisCode, AttributeEnum.TRANSFER_KEY_ATTRIBUTE_ID);
        this.performKeyAction(conn, keyToSetDataObject, obisCode, AttributeEnum.SET_ENCRYPTION_KEY_ATTRIBUTE_ID);

        return MethodResultCode.SUCCESS;
    }

    private void performKeyAction(final LnClientConnection conn, final DataObject keyToSetDataObject,
            final ObisCode obisCode, final AttributeEnum attribute) throws IOException {
        final MethodParameter setEncryptionKeyMethod = new MethodParameter(CLASS_ID, obisCode,
                attribute.getAttrValue(), keyToSetDataObject);
        final List<MethodResult> methodResultCode = conn.action(setEncryptionKeyMethod);

        if (!MethodResultCode.SUCCESS.equals(methodResultCode.get(0).resultCode())) {
            throw new IOException("Error while executing for attribute " + attribute + " Reason = "
                    + methodResultCode.get(0).resultCode());
        }

        LOGGER.info("Success!: Finished calling performKeyAction class_id {} obis_code {} attribute{}", CLASS_ID,
                obisCode, attribute);
    }

}
