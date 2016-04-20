package org.osgp.adapter.protocol.dlms.domain.commands.stub;

import org.openmuc.jdlms.ClientConnection;
import org.osgp.adapter.protocol.dlms.domain.commands.GetActualMeterReadsBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsDataDto;

public class GetActualMeterReadsBundleCommandExecutorStub extends AbstractCommandExecutorStub implements GetActualMeterReadsBundleCommandExecutor {

    @Override
    public ActionResponseDto execute(ClientConnection conn, DlmsDevice device, ActualMeterReadsDataDto object)
            throws ProtocolAdapterException {
        return doExecute(conn, device, object);
    }

}
