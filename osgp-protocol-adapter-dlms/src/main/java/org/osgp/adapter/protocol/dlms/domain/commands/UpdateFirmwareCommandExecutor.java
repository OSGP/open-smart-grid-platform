package org.osgp.adapter.protocol.dlms.domain.commands;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.DlmsConnection;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.stereotype.Component;

@Component
public class UpdateFirmwareCommandExecutor extends AbstractCommandExecutor<String, AccessResultCode> {

    @Override
    public AccessResultCode execute(final DlmsConnection conn, final DlmsDevice device, final String firmwareIdentifier)
            throws ProtocolAdapterException {

        return AccessResultCode.OBJECT_UNDEFINED;
    }

}
