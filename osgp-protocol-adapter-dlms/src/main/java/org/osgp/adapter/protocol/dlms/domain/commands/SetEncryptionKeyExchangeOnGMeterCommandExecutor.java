/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.LnClientConnection;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component()
public class SetEncryptionKeyExchangeOnGMeterCommandExecutor implements CommandExecutor<String, AccessResultCode> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetEncryptionKeyExchangeOnGMeterCommandExecutor.class);

    private static final int CLASS_ID = 20;// TODO replace
    private static final ObisCode OBIS_CODE = new ObisCode("0.0.13.0.0.255"); // TODO
    // replace
    private static final int ATTRIBUTE_ID = 6; // TODO replace

    @Autowired
    private DlmsHelperService dlmsHelperService;

    @Override
    public AccessResultCode execute(final LnClientConnection conn, final String keyToSet) throws IOException,
    ProtocolAdapterException {
        LOGGER.debug("SetEncryptionKeyExchangeOnGMeterCommandExecutor.execute called");

        final AttributeAddress calendarNamePassive = new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID);
        final DataObject value = DataObject.newOctetStringData(keyToSet.getBytes());
        final SetParameter setParameter = new SetParameter(calendarNamePassive, value);

        // TODO uncomment the next line. Not executing just yet. First test the
        // complete round trip
        final List<AccessResultCode> resultCode = Arrays.asList(AccessResultCode.SUCCESS);// =
        // conn.set(setParameter);

        if (!AccessResultCode.SUCCESS.equals(resultCode)) {
            throw new ProtocolAdapterException(
                    "SetEncryptionKeyExchangeOnGMeterCommandExecutor: Request with code failed: " + resultCode);
        }

        LOGGER.info("Finished calling conn.set");

        return AccessResultCode.SUCCESS;
    }

}
