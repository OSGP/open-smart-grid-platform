package org.osgp.adapter.protocol.dlms.domain.commands;

import java.util.List;

import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DeviceConnector;
import org.osgp.adapter.protocol.dlms.domain.factories.FirwareImageFactory;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.FirmwareVersionDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.UpdateFirmwareRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.UpdateFirmwareResponseDto;

@Component
public class UpdateFirmwareCommandExecutor extends AbstractCommandExecutor<String, List<FirmwareVersionDto>> {

    @Autowired
    private FirwareImageFactory firmwareImageFactory;

    @Autowired
    GetFirmwareVersionsCommandExecutor getFirmwareVersionsCommandExecutor;

    public UpdateFirmwareCommandExecutor() {
        super(UpdateFirmwareRequestDto.class);
    }

    @Override
    public List<FirmwareVersionDto> execute(final DeviceConnector conn, final DlmsDevice device,
            final String firmwareIdentification) throws ProtocolAdapterException {

        final ImageTransfer transfer = new ImageTransfer(conn, firmwareIdentification,
                this.getImageData(firmwareIdentification));

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

        if (transfer.imageIsVerified() && transfer.imageToActivateOk()) {
            transfer.activateImage();
            transfer.setImageTransferEnabled(false);
            return getFirmwareVersionsCommandExecutor.execute(conn, device, null);
        } else {
            // Image data is not correct.
            transfer.setImageTransferEnabled(false);
            throw new ProtocolAdapterException("An unknown error occurred while updating firmware.");
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
    public ActionResponseDto asBundleResponse(final List<FirmwareVersionDto> executionResult)
            throws ProtocolAdapterException {

        return new UpdateFirmwareResponseDto(executionResult);
    }
}
