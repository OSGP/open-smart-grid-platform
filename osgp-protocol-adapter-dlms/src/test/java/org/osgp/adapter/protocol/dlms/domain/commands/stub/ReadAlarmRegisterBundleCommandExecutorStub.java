package org.osgp.adapter.protocol.dlms.domain.commands.stub;

import org.openmuc.jdlms.ClientConnection;
import org.osgp.adapter.protocol.dlms.domain.commands.ReadAlarmRegisterBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ReadAlarmRegisterDataDto;

public class ReadAlarmRegisterBundleCommandExecutorStub extends AbstractCommandExecutorStub implements ReadAlarmRegisterBundleCommandExecutor{

    @Override
    public ActionResponseDto execute(ClientConnection conn, DlmsDevice device, ReadAlarmRegisterDataDto object)
            throws ProtocolAdapterException {
        return doExecute(conn, device, object);
    }

}
