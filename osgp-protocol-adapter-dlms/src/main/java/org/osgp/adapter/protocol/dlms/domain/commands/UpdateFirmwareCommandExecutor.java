package org.osgp.adapter.protocol.dlms.domain.commands;

import org.openmuc.jdlms.DlmsConnection;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.FirwareImageFactory;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.UpdateFirmwareRequestDto;

@Component
public class UpdateFirmwareCommandExecutor extends AbstractCommandExecutor<String, Boolean> {

    @Autowired
    private FirwareImageFactory firmwareImageFactory;

    public UpdateFirmwareCommandExecutor() {
        super(UpdateFirmwareRequestDto.class);
    }

    @Override
    public Boolean execute(final DlmsConnection conn, final DlmsDevice device, final String firmwareIdentification)
            throws ProtocolAdapterException {

        final ImageTransfer transfer = new ImageTransfer(conn, firmwareIdentification,
                this.getImageData(firmwareIdentification));

        try {
            if (!transfer.imageTransferEnabled()) {
                return false;
            }

            transfer.initiateImageTransfer();
            transfer.transferImageBlocks();
            transfer.checkCompleteness();
            transfer.verifyImage();

            if (transfer.imageToActivateOk()) {
                transfer.activateImage();
                return true;
            }
        } catch (final ProtocolAdapterException e) {
            throw e;
        }

        return false;
    }

    private byte[] getImageData(final String firmwareIdentification) throws ProtocolAdapterException {
        return this.firmwareImageFactory.getFirmwareImage(firmwareIdentification);
    }

    @Override
    public String fromBundleRequestInput(final ActionRequestDto bundleInput) throws ProtocolAdapterException {
        return ((UpdateFirmwareRequestDto) bundleInput).getFirmwareIdentification();
    }

    @Override
    public ActionResponseDto asBundleResponse(final Boolean executionResult) throws ProtocolAdapterException {
        if (executionResult) {
            return new ActionResponseDto("Update firmware was successful");
        } else {
            throw new ProtocolAdapterException("Update firmware was not successful");
        }
    }
}
