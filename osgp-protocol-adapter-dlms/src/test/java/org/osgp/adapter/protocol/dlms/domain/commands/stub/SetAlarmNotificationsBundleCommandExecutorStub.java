/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands.stub;

import org.openmuc.jdlms.DlmsConnection;
import org.osgp.adapter.protocol.dlms.domain.commands.SetAlarmNotificationsBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetAlarmNotificationsRequestDto;

public class SetAlarmNotificationsBundleCommandExecutorStub extends AbstractCommandExecutorStub implements
        SetAlarmNotificationsBundleCommandExecutor {

    @Override
    public ActionResponseDto execute(final DlmsConnection conn, final DlmsDevice device,
            final SetAlarmNotificationsRequestDto object) throws ProtocolAdapterException {
        return this.doExecute(conn, device, object);
    }

}
