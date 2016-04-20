package org.osgp.adapter.protocol.dlms.domain.commands.stub;

import org.openmuc.jdlms.ClientConnection;
import org.osgp.adapter.protocol.dlms.domain.commands.SetAlarmNotificationsBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetAlarmNotificationsRequestDataDto;

public class SetAlarmNotificationsBundleCommandExecutorStub extends AbstractCommandExecutorStub implements SetAlarmNotificationsBundleCommandExecutor {

    @Override
    public ActionResponseDto execute(ClientConnection conn, DlmsDevice device,
            SetAlarmNotificationsRequestDataDto object) throws ProtocolAdapterException {
        return doExecute(conn, device, object);
    }

}
