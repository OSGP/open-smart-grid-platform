package org.osgp.adapter.protocol.dlms.domain.commands.stub;

import org.openmuc.jdlms.ClientConnection;
import org.osgp.adapter.protocol.dlms.domain.commands.SetAdministrativeStatusBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AdministrativeStatusTypeDataDto;

public class SetAdministrativeStatusBundleCommandExecutorStub extends AbstractCommandExecutorStub implements SetAdministrativeStatusBundleCommandExecutor{
    @Override
    public ActionResponseDto execute(ClientConnection conn, DlmsDevice device, AdministrativeStatusTypeDataDto object)
            throws ProtocolAdapterException {
        return doExecute(conn, device, object);
    }

}
