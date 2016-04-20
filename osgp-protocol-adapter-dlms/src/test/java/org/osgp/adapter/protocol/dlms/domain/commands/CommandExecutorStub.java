package org.osgp.adapter.protocol.dlms.domain.commands;

import org.openmuc.jdlms.ClientConnection;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsQueryDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.MeterReadsDto;

// Dit werkt niet.

public class CommandExecutorStub implements CommandExecutor<ActualMeterReadsQueryDto, MeterReadsDto>  {

    @Override
    public MeterReadsDto execute(ClientConnection conn, DlmsDevice device, ActualMeterReadsQueryDto object)
            throws ProtocolAdapterException {
        // TODO Auto-generated method stub
        return null;
    }

}
