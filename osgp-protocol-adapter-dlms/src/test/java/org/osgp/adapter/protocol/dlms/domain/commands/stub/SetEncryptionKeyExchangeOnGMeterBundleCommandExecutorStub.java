package org.osgp.adapter.protocol.dlms.domain.commands.stub;

import org.openmuc.jdlms.ClientConnection;
import org.osgp.adapter.protocol.dlms.domain.commands.SetEncryptionKeyExchangeOnGMeterBundleCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GMeterInfoDto;

public class SetEncryptionKeyExchangeOnGMeterBundleCommandExecutorStub extends AbstractCommandExecutorStub implements SetEncryptionKeyExchangeOnGMeterBundleCommandExecutor {

    @Override
    public ActionResponseDto execute(ClientConnection conn, DlmsDevice device, GMeterInfoDto object)
            throws ProtocolAdapterException {
        return doExecute(conn, device, object);
    }

}
