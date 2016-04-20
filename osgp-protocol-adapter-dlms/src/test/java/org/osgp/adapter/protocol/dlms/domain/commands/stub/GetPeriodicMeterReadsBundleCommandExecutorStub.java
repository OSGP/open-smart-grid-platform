package org.osgp.adapter.protocol.dlms.domain.commands.stub;

import org.openmuc.jdlms.ClientConnection;
import org.osgp.adapter.protocol.dlms.domain.commands.GetPeriodicMeterReadsBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDataDto;

public class GetPeriodicMeterReadsBundleCommandExecutorStub extends AbstractCommandExecutorStub implements GetPeriodicMeterReadsBundleCommandExecutor {

    @Override
    public ActionResponseDto execute(ClientConnection conn, DlmsDevice device, PeriodicMeterReadsRequestDataDto object)
            throws ProtocolAdapterException {
        return doExecute(conn, device, object);
    }
    
}
