/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;

import org.joda.time.DateTime;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.ClientConnection;
import org.openmuc.jdlms.DataObject;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.RequestParameterFactory;
import org.openmuc.jdlms.SetRequestParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component()
public class SynchronizeTimeCommandExecutor implements CommandExecutor<DataObject, AccessResultCode> {

    private static final int CLASS_ID = 8;
    private static final ObisCode OBIS_CODE = new ObisCode("0.0.1.0.0.255");
    private static final int ATTRIBUTE_ID = 2;

    @Autowired
    private DlmsHelperService dlmsHelperService;

    @Override
    public AccessResultCode execute(final ClientConnection conn, final DataObject object) throws IOException {
        final RequestParameterFactory factory = new RequestParameterFactory(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID);

        final DateTime dt = DateTime.now();

        final DataObject obj = this.dlmsHelperService.asDataObject(dt);

        final SetRequestParameter request = factory.createSetRequestParameter(obj);

        return conn.set(request).get(0);
    }
}
