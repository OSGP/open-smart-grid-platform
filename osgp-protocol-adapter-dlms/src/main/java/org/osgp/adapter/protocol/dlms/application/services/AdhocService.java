/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.services;

import org.openmuc.jdlms.ClientConnection;
import org.osgp.adapter.protocol.dlms.domain.commands.GetAssociationLnObjectsCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.RetrieveConfigurationObjectsCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.SynchronizeTimeCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.dto.valueobjects.smartmetering.AssociationLnListTypeDto;

@Service(value = "dlmsAdhocService")
public class AdhocService {

    @Autowired
    private SynchronizeTimeCommandExecutor synchronizeTimeCommandExecutor;

    @Autowired
    private RetrieveConfigurationObjectsCommandExecutor retrieveConfigurationObjectsCommandExecutor;

    @Autowired
    private GetAssociationLnObjectsCommandExecutor getAssociationLnObjectsCommandExecutor;

    // === REQUEST Synchronize Time DATA ===

    public void synchronizeTime(final ClientConnection conn, final DlmsDevice device) throws ProtocolAdapterException {
        this.synchronizeTimeCommandExecutor.execute(conn, device, null);
    }

    public String retrieveConfigurationObjects(final ClientConnection conn, final DlmsDevice device)
            throws ProtocolAdapterException {

        return this.retrieveConfigurationObjectsCommandExecutor.execute(conn, device, null);
    }

    public AssociationLnListTypeDto getAssociationLnObjects(final ClientConnection conn, final DlmsDevice device)
            throws ProtocolAdapterException {
        return this.getAssociationLnObjectsCommandExecutor.execute(conn, device, null);
    }
}
