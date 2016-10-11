package org.osgp.adapter.protocol.dlms.domain.commands;

import java.util.concurrent.ExecutorService;

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

    @Autowired
    private ExecutorService executorService;

    public UpdateFirmwareCommandExecutor() {
        super(UpdateFirmwareRequestDto.class);
    }

    @Override
    public Boolean execute(final DlmsConnection conn, final DlmsDevice device, final String firmwareIdentification)
            throws ProtocolAdapterException {

        final ImageTransfer transfer = new ImageTransfer(this.executorService, conn, firmwareIdentification,
                this.getImageData(firmwareIdentification));

        try {
            if (!transfer.imageTransferEnabled()) {
                transfer.setImageTransferEnabled(true);
            }
            
            if (transfer.shouldInitiateTransfer()) {
                transfer.initiateImageTransfer();
            }

            if (transfer.shouldTransferImage()) {
                transfer.transferImageBlocks();
                transfer.transferMissingImageBlocks();
            }
            
            if (!transfer.imageIsVerified()) {
                transfer.verifyImage();
            }
            
            if (!transfer.isImageTransferActivated() && transfer.imageIsVerified() && transfer.imageToActivateOk()) {
                transfer.activateImage();
                transfer.setImageTransferEnabled(false);
                return true;
            }
            else {
                // Image data is not correct.
                return false;
            }
        } catch (final ProtocolAdapterException e) {
            throw e;
        } 
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
