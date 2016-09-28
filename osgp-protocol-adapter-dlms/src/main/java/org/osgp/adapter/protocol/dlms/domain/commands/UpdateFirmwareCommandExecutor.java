package org.osgp.adapter.protocol.dlms.domain.commands;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.DlmsConnection;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.UpdateFirmwareRequestDto;

@Component
public class UpdateFirmwareCommandExecutor extends AbstractCommandExecutor<String, AccessResultCode> {

    public UpdateFirmwareCommandExecutor() {
        super(UpdateFirmwareRequestDto.class);
    }

    @Override
    public AccessResultCode execute(final DlmsConnection conn, final DlmsDevice device, final String firmwareIdentifier)
            throws ProtocolAdapterException {

        return AccessResultCode.OBJECT_UNDEFINED;
    }

    @Override
    public String fromBundleRequestInput(final ActionRequestDto bundleInput) throws ProtocolAdapterException {
        // TODO Auto-generated method stub
        return ((UpdateFirmwareRequestDto) bundleInput).getFirmwareIdentifier();
    }

    @Override
    public ActionResponseDto asBundleResponse(final AccessResultCode executionResult) throws ProtocolAdapterException {
        this.checkAccessResultCode(executionResult);

        return new ActionResponseDto("Update firmware was successful");
    }
}
