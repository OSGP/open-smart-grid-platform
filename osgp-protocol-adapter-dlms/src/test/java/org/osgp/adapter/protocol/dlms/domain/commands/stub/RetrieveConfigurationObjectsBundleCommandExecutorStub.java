package org.osgp.adapter.protocol.dlms.domain.commands.stub;

import org.openmuc.jdlms.ClientConnection;
import org.osgp.adapter.protocol.dlms.domain.commands.RetrieveConfigurationObjectsBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetConfigurationRequestDataDto;

public class RetrieveConfigurationObjectsBundleCommandExecutorStub extends AbstractCommandExecutorStub implements RetrieveConfigurationObjectsBundleCommandExecutor {

    @Override
    public ActionResponseDto execute(ClientConnection conn, DlmsDevice device, GetConfigurationRequestDataDto object)
            throws ProtocolAdapterException {
        return doExecute(conn, device, object);
    }

}
