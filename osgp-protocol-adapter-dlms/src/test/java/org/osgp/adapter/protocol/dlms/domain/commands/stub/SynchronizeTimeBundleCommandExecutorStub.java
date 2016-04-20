package org.osgp.adapter.protocol.dlms.domain.commands.stub;

import org.openmuc.jdlms.ClientConnection;
import org.osgp.adapter.protocol.dlms.domain.commands.SynchronizeTimeBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SynchronizeTimeRequestDataDto;

public class SynchronizeTimeBundleCommandExecutorStub extends AbstractCommandExecutorStub implements SynchronizeTimeBundleCommandExecutor {

    @Override
    public ActionResponseDto execute(ClientConnection conn, DlmsDevice device, SynchronizeTimeRequestDataDto object)
            throws ProtocolAdapterException {
        return doExecute(conn, device, object);
    }
}
