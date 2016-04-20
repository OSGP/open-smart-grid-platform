package org.osgp.adapter.protocol.dlms.domain.commands.stub;

import org.openmuc.jdlms.ClientConnection;
import org.osgp.adapter.protocol.dlms.domain.commands.RetrieveEventsBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.FindEventsQueryDto;

public class RetrieveEventsBundleCommandExecutorStub extends AbstractCommandExecutorStub implements RetrieveEventsBundleCommandExecutor {

    @Override
    public ActionResponseDto execute(ClientConnection conn, DlmsDevice device, FindEventsQueryDto object)
            throws ProtocolAdapterException {
        return doExecute(conn, device, object);
    }

}
