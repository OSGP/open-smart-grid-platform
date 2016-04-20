package org.osgp.adapter.protocol.dlms.domain.commands.stub;

import org.openmuc.jdlms.ClientConnection;
import org.osgp.adapter.protocol.dlms.domain.commands.SetActivityCalendarBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActivityCalendarDataDto;

public class SetActivityCalendarBundleCommandExecutorStub extends AbstractCommandExecutorStub implements SetActivityCalendarBundleCommandExecutor {

    @Override
    public ActionResponseDto execute(ClientConnection conn, DlmsDevice device, ActivityCalendarDataDto object)
            throws ProtocolAdapterException {
        return doExecute(conn, device, object);
    }

}
