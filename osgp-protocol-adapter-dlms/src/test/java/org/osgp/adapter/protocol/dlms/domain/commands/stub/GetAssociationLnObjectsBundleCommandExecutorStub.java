/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands.stub;

import org.openmuc.jdlms.ClientConnection;
import org.osgp.adapter.protocol.dlms.domain.commands.GetAssociationLnObjectsBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetAssociationLnObjectsRequestDto;

public class GetAssociationLnObjectsBundleCommandExecutorStub extends AbstractCommandExecutorStub implements
GetAssociationLnObjectsBundleCommandExecutor {

    @Override
    public ActionResponseDto execute(final ClientConnection conn, final DlmsDevice device,
            final GetAssociationLnObjectsRequestDto object) throws ProtocolAdapterException {
        return this.doExecute(conn, device, object);
    }

}
