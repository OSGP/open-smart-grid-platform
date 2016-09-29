package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;

import org.bouncycastle.util.Arrays;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.MethodParameter;
import org.openmuc.jdlms.MethodResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.internal.asn1.cosem.Action_Result;
import org.openmuc.jdlms.internal.asn1.cosem.Data_Access_Result;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ConnectionException;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.UpdateFirmwareRequestDto;

@Component
public class UpdateFirmwareCommandExecutor extends AbstractCommandExecutor<String, Boolean> {

    private static final String EXCEPTION_MSG_IMAGE_TO_ACTIVATE_NOT_OK = "Properties of image to activate or not as excepted.";
    private static final int CLASS_ID = 18;
    private static final ObisCode OBIS_CODE = new ObisCode("0.0.44.0.0.255");

    public UpdateFirmwareCommandExecutor() {
        super(UpdateFirmwareRequestDto.class);
    }

    @Override
    public Boolean execute(final DlmsConnection conn, final DlmsDevice device, final String firmwareIdentifier)
            throws ProtocolAdapterException {

        final CosemObject cosemObject = new CosemObject(conn, OBIS_CODE, CLASS_ID);
        final ImageTransfer transfer = new ImageTransfer(cosemObject, firmwareIdentifier,
                this.getImageData(firmwareIdentifier));

        try {
            if (!transfer.imageTransferEnabled()) {
                return false;
            }

            transfer.initiateImageTransfer();
            transfer.transferImageBlocks();
            transfer.checkCompleteness();
            transfer.verifyImage();

            if (transfer.imageToActivateOk()) {
                final int result = transfer.activateImage();
                if (result != Data_Access_Result.SUCCESS) {
                    throw new ProtocolAdapterException(EXCEPTION_MSG_IMAGE_TO_ACTIVATE_NOT_OK);
                }

                return true;
            }
        } catch (final ProtocolAdapterException e) {
            throw e;
        }

        return false;
    }

    private byte[] getImageData(final String firmwareIdentifier) {
        // TODO: READ IMAGE DATA FROM WEB.
        return new byte[10];
    }

    @Override
    public String fromBundleRequestInput(final ActionRequestDto bundleInput) throws ProtocolAdapterException {
        // TODO Auto-generated method stub
        return ((UpdateFirmwareRequestDto) bundleInput).getFirmwareIdentifier();
    }

    @Override
    public ActionResponseDto asBundleResponse(final Boolean executionResult) throws ProtocolAdapterException {
        if (executionResult) {
            return new ActionResponseDto("Update firmware was successful");
        } else {
            throw new ProtocolAdapterException("Update firmware was not successful");
        }
    }

    private class ImageTransfer {

        private static final String EXCEPTION_MSG_IMAGE_VERIFY_NOT_CALLED = "Image verify could not be called.";
        private static final String EXCEPTION_MSG_IMAGE_NOT_VERIFIED = "The image could not be verified.";
        private static final String EXCEPTION_MSG_IMAGE_BLOCK_SIZE_NOT_READ = "Image block size could not be read.";
        private static final String EXCEPTION_MSG_IMAGE_TRANSFER_ENABLED_NOT_READ = "Image transfer enabled could not be read.";
        private static final String EXCEPTION_MSG_IMAGE_TRANSFER_STATUS_NOT_READ = "Image transfer status could not be read.";
        private static final String EXCEPTION_MSG_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER_NOT_READ = "Image first not transferred block number could not be read.";
        private static final String EXCEPTION_MSG_IMAGE_TRANSFER_NOT_INITIATED = "Image transfer has not been initiated.";

        private static final int IMAGE_TRANSFER_STATUS_INITIATED = 1;
        private static final int IMAGE_TRANSFER_STATUS_VERIFICATION_INITIATED = 2;
        private static final int IMAGE_TRANSFER_STATUS_VERIFICATION_FAILED = 4;

        private static final int ATTRIBUTE_IMAGE_BLOCK_SIZE = 2;
        private static final int ATTRIBUTE_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER = 4;
        private static final int ATTRIBUTE_IMAGE_TRANSFER_ENABLED = 5;
        private static final int ATTRIBUTE_IMAGE_TRANSFER_STATUS = 6;
        private static final int ATTRIBUTE_IMAGE_TO_ACTIVATE_INFO = 7;

        private static final int METHOD_IMAGE_VERIFY = 3;

        private final String imageIdentifier;
        private final byte[] data;
        private final CosemObject cosemObject;

        private int imageBlockSize;
        private boolean imageBlockSizeRead;

        public ImageTransfer(final CosemObject cosemObject, final String imageIdentifier, final byte[] data) {
            this.imageIdentifier = imageIdentifier;
            this.data = data;
            this.imageBlockSizeRead = false;
            this.cosemObject = cosemObject;
        }

        private int getImageSize() {
            return this.data.length;
        }

        private int numberOfBlocks() throws ProtocolAdapterException {
            return this.getImageSize() / this.getImageBlockSize();
        }

        private int readImageBlockSize() throws ProtocolAdapterException {
            final DataObject imageBlockSizeData = this.cosemObject.readAttribute(ATTRIBUTE_IMAGE_BLOCK_SIZE);
            if (imageBlockSizeData == null || !imageBlockSizeData.isNumber()) {
                throw new ProtocolAdapterException(EXCEPTION_MSG_IMAGE_BLOCK_SIZE_NOT_READ);
            }

            this.imageBlockSize = (Integer) imageBlockSizeData.getValue();
            this.imageBlockSizeRead = true;
            return this.imageBlockSize;
        }

        private int getImageBlockSize() throws ProtocolAdapterException {
            if (!this.imageBlockSizeRead) {
                return this.readImageBlockSize();
            }
            return this.imageBlockSize;
        }

        private int getImageFirstNotTransferredBlockNumber() throws ProtocolAdapterException {
            final DataObject imageFirstNotReadBlockNumberData = this.cosemObject
                    .readAttribute(ATTRIBUTE_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER);
            if (imageFirstNotReadBlockNumberData == null && !imageFirstNotReadBlockNumberData.isNumber()) {
                throw new ProtocolAdapterException(EXCEPTION_MSG_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER_NOT_READ);
            }

            return (Integer) imageFirstNotReadBlockNumberData.getValue();
        }

        private boolean isImageTransferInitiated() throws ProtocolAdapterException {
            return (this.getImageTransferStatus() == IMAGE_TRANSFER_STATUS_INITIATED);
        }

        private int getImageTransferStatus() throws ProtocolAdapterException {
            final DataObject imageTransferStatusData = this.cosemObject.readAttribute(ATTRIBUTE_IMAGE_TRANSFER_STATUS);
            if (imageTransferStatusData == null || !imageTransferStatusData.isNumber()) {
                throw new ProtocolAdapterException(EXCEPTION_MSG_IMAGE_TRANSFER_STATUS_NOT_READ);
            }

            return (Integer) imageTransferStatusData.getValue();
        }

        private void imageBlockTransfer(final int blockNumber) throws ProtocolAdapterException {
            final int imageBlockSize = this.getImageBlockSize();
            final int index = imageBlockSize * blockNumber;

            final byte[] transferData = Arrays.copyOfRange(this.data, index, index + imageBlockSize);
            // TODO: CALL METHOD ON METER
        }

        /**
         * Check image transfer enabled value of the COSEM server.
         *
         * @return image transfer enabled
         * @throws ProtocolAdapterException
         */
        public boolean imageTransferEnabled() throws ProtocolAdapterException {
            final DataObject transferEnabled = this.cosemObject.readAttribute(ATTRIBUTE_IMAGE_TRANSFER_ENABLED);
            if (transferEnabled == null || !transferEnabled.isBoolean()) {
                throw new ProtocolAdapterException(EXCEPTION_MSG_IMAGE_TRANSFER_ENABLED_NOT_READ);
            }

            return (Boolean) transferEnabled.getValue();
        }

        /**
         * Initiates Image transfer.After a successful initiation, the value of
         * the image_transfer_status attribute is (1) Image transfer initiated
         * and the COSEM server is prepared to accept ImageBlocks.
         */
        public void initiateImageTransfer() {
            // TODO: CALL METHOD ON METER
        }

        /**
         * Transfers all image blocks.
         *
         * ImageBlocks are accepted only by those COSEM servers, in which the
         * Image transfer process has been successfully initiated. Other servers
         * silently discard any ImageBlocks received.
         *
         * @throws ProtocolAdapterException
         */
        public void transferImageBlocks() throws ProtocolAdapterException {
            if (!this.isImageTransferInitiated()) {
                throw new ProtocolAdapterException(EXCEPTION_MSG_IMAGE_TRANSFER_NOT_INITIATED);
            }

            final int blocks = this.numberOfBlocks();
            for (int i = 0; i < blocks; i++) {
                this.imageBlockTransfer(i);
            }
        }

        /**
         * Checks the completeness of the Image transferred. If the Image is not
         * complete, it transfers the ImageBlocks not (yet) transferred. This is
         * an iterative process, continued until the whole Image is successfully
         * transferred.
         *
         * @throws ProtocolAdapterException
         */
        public void checkCompleteness() throws ProtocolAdapterException {
            int blockNumber;
            while ((blockNumber = this.getImageFirstNotTransferredBlockNumber()) < this.numberOfBlocks()) {
                this.imageBlockTransfer(blockNumber);
            }
        }

        /**
         * The Image is verified. This is done by invoking the image_verify
         * method by the client and testing the image transfer status.
         */
        public void verifyImage() throws ProtocolAdapterException {
            final DataObject imageVerify = this.cosemObject.callMethod(METHOD_IMAGE_VERIFY);
            if (imageVerify == null || !imageVerify.isNumber()) {
                throw new ProtocolAdapterException(EXCEPTION_MSG_IMAGE_VERIFY_NOT_CALLED);
            }

            final int verified = (Integer) imageVerify.getValue();

            if (verified == Action_Result.OTHER_REASON) {
                throw new ProtocolAdapterException(EXCEPTION_MSG_IMAGE_NOT_VERIFIED);
            }

            if (verified == Action_Result.TEMPORARY_FAILURE) {
                int attempt = 0;
                int status;

                while ((status = this.getImageTransferStatus()) == IMAGE_TRANSFER_STATUS_VERIFICATION_INITIATED
                        && attempt < 3) {
                    attempt++;
                }

                if (status == IMAGE_TRANSFER_STATUS_VERIFICATION_FAILED) {
                    throw new ProtocolAdapterException(EXCEPTION_MSG_IMAGE_NOT_VERIFIED);
                }
            }
        }

        /**
         * Checks the information on the Image transferred to see if it is OK to
         * activate the image. This information is held by the
         * image_to_activate_info attribute of the Image transfer object and it
         * is generated as a result of the Image verification.
         *
         * @return OK to activate image
         * @throws ProtocolAdapterException
         */
        public boolean imageToActivateOk() throws ProtocolAdapterException {
            final DataObject imageTransferStatusData = this.cosemObject.readAttribute(ATTRIBUTE_IMAGE_TO_ACTIVATE_INFO);
            // TODO: check values

            return false;
        }

        /**
         * The image is activated.
         *
         * @return
         * @throws ProtocolAdapterException
         */
        public int activateImage() throws ProtocolAdapterException {
            // TODO: CALL METHOD ON METER
            return Data_Access_Result.OTHER_REASON;
        }
    }

    private class CosemObject {

        private final DlmsConnection conn;
        private final ObisCode obisCode;
        private final int classId;

        public CosemObject(final DlmsConnection conn, final ObisCode obisCode, final int classId) {
            this.conn = conn;
            this.obisCode = obisCode;
            this.classId = classId;
        }

        private AttributeAddress createAttributeAddress(final int attributeId) {
            return new AttributeAddress(this.classId, this.obisCode, attributeId);
        }

        private MethodParameter createMethodParameter(final int methodId, final DataObject dataObject) {
            return new MethodParameter(this.classId, this.obisCode, methodId, dataObject);
        }

        private MethodParameter createMethodParameter(final int methodId) {
            return new MethodParameter(this.classId, this.obisCode, methodId);
        }

        public DataObject readAttribute(final int attributeId) throws ProtocolAdapterException {
            GetResult getResult = null;
            try {
                getResult = this.conn.get(this.createAttributeAddress(attributeId));
            } catch (final IOException e) {
                throw new ConnectionException(e);
            }

            if (getResult == null) {
                throw new ProtocolAdapterException("No GetResult received while retrieving attribute " + attributeId
                        + ".");
            }

            return getResult.getResultData();
        }

        public DataObject callMethod(final int methodId) {
            final MethodParameter methodParameter = this.createMethodParameter(methodId);

            MethodResult result = null;
            try {
                result = this.conn.action(methodParameter);
            } catch (final IOException e) {
                throw new ConnectionException(e);
            }

            if (result == null) {

            }

            return result.getResultData();
        }
    }
}
