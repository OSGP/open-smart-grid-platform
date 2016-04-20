package org.osgp.adapter.protocol.dlms.domain.commands.stub;

import org.openmuc.jdlms.ClientConnection;
import org.osgp.adapter.protocol.dlms.domain.commands.SetConfigurationObjectBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetConfigurationObjectRequestDataDto;

public class SetConfigurationObjectBundleCommandExecutorStub extends AbstractCommandExecutorStub implements SetConfigurationObjectBundleCommandExecutor {

    @Override
    public ActionResponseDto execute(ClientConnection conn, DlmsDevice device,
            SetConfigurationObjectRequestDataDto object) throws ProtocolAdapterException {
        return doExecute(conn, device, object);
    }

}
