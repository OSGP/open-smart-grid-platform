package org.osgp.adapter.protocol.dlms.domain.commands.stub;

import org.openmuc.jdlms.ClientConnection;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;

public abstract class AbstractCommandExecutorStub {

    private ActionResponseDto actionResponse;
    private ProtocolAdapterException protocolAdapterException;
    
    protected ActionResponseDto doExecute(ClientConnection conn, DlmsDevice device, ActionDto object)
            throws ProtocolAdapterException {

        if (this.protocolAdapterException != null) {
            throw protocolAdapterException; 
        } else if (actionResponse == null) {
            return new ActionResponseDto();
        } else {
            return actionResponse;
        }
    }
    
    public ActionResponseDto getActionResponse() {
        return actionResponse;
    }

    public void setActionResponse(ActionResponseDto actionResponse) {
        this.actionResponse = actionResponse;
    }
    
    public void failWith(final ProtocolAdapterException protocolAdapterException) {
        this.protocolAdapterException = protocolAdapterException;
    }

    public ProtocolAdapterException getProtocolAdapterException() {
        return protocolAdapterException;
    }

    public void setProtocolAdapterException(ProtocolAdapterException protocolAdapterException) {
        this.protocolAdapterException = protocolAdapterException;
    }
    
    
}
