package org.osgp.adapter.protocol.dlms.domain.commands.stub;

import org.openmuc.jdlms.ClientConnection;
import org.osgp.adapter.protocol.dlms.domain.commands.GetFirmwareVersionsBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetFirmwareVersionRequestDataDto;

public class GetFirmwareVersionsBundleCommandExecutorStub extends AbstractCommandExecutorStub implements GetFirmwareVersionsBundleCommandExecutor {

    @Override
    public ActionResponseDto execute(ClientConnection conn, DlmsDevice device, GetFirmwareVersionRequestDataDto object)
            throws ProtocolAdapterException {
        return doExecute(conn, device, object);
    }

    
 }
