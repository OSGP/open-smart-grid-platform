package org.osgp.adapter.protocol.dlms.domain.commands.stub;

import org.openmuc.jdlms.ClientConnection;
import org.osgp.adapter.protocol.dlms.domain.commands.SetPushSetupAlarmBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetPushSetupAlarmRequestDataDto;

public class SetPushSetupAlarmBundleCommandExecutorStub extends AbstractCommandExecutorStub implements SetPushSetupAlarmBundleCommandExecutor {

    @Override
    public ActionResponseDto execute(ClientConnection conn, DlmsDevice device, SetPushSetupAlarmRequestDataDto object)
            throws ProtocolAdapterException {
        return doExecute(conn, device, object);
    }

}
