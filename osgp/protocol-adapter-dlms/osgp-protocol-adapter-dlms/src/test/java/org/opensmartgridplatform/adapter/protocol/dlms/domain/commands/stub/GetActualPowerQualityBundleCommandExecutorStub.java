package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;

public class GetActualPowerQualityBundleCommandExecutorStub extends AbstractCommandExecutorStub {

    @Override
    public ActionResponseDto executeBundleAction(final DlmsConnectionManager conn, final DlmsDevice device,
            final ActionRequestDto actionRequestDto) throws ProtocolAdapterException {
        return this.doExecute(conn, device, actionRequestDto);
    }
}
