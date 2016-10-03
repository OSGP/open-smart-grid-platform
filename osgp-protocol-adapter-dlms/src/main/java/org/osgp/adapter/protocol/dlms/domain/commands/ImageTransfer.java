package org.osgp.adapter.protocol.dlms.domain.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.bouncycastle.util.Arrays;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;
import org.openmuc.jdlms.internal.asn1.cosem.Action_Result;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ImageTransfer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageTransfer.class);
    private static final int LOGGER_PERCENTAGE_STEP = 5;

    private static final String EXCEPTION_MSG_IMAGE_VERIFY_NOT_CALLED = "Image verify could not be called.";
    private static final String EXCEPTION_MSG_IMAGE_NOT_VERIFIED = "The image could not be verified. Status: ";
    private static final String EXCEPTION_MSG_IMAGE_BLOCK_SIZE_NOT_READ = "Image block size could not be read.";
    private static final String EXCEPTION_MSG_IMAGE_TRANSFER_ENABLED_NOT_READ = "Image transfer enabled could not be read.";
    private static final String EXCEPTION_MSG_IMAGE_TRANSFER_STATUS_NOT_READ = "Image transfer status could not be read.";
    private static final String EXCEPTION_MSG_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER_NOT_READ = "Image first not transferred block number could not be read.";
    private static final String EXCEPTION_MSG_IMAGE_TRANSFER_NOT_INITIATED = "Image transfer has not been initiated.";
    private static final String EXCEPTION_MSG_IMAGE_TO_ACTIVATE_NOT_OK = "Properties of image to activate or not as excepted.";

    private static final int CLASS_ID = 18;
    private static final ObisCode OBIS_CODE = new ObisCode("0.0.44.0.0.255");

    private final ExecutorService executorService;
    private final String imageIdentifier;
    private final byte[] data;
    private final CosemObjectAccessor cosemObject;
    private int imageBlockSize;
    private boolean imageBlockSizeRead;

    public ImageTransfer(final ExecutorService executor, final DlmsConnection conn, final String imageIdentifier,
            final byte[] data) {
        this.executorService = executor;
        this.imageIdentifier = imageIdentifier;
        this.data = data;
        this.imageBlockSizeRead = false;
        this.cosemObject = new CosemObjectAccessor(conn, OBIS_CODE, CLASS_ID);
    }

    private int getImageSize() {
        return this.data.length;
    }

    private int numberOfBlocks() throws ProtocolAdapterException {
        return (int) Math.ceil((double) this.getImageSize() / this.getImageBlockSize());
    }

    private int readImageBlockSize() throws ProtocolAdapterException {
        final DataObject imageBlockSizeData = this.cosemObject.readAttribute(Attribute.IMAGE_BLOCK_SIZE.getValue());
        if (imageBlockSizeData == null || !imageBlockSizeData.isNumber()) {
            throw new ProtocolAdapterException(EXCEPTION_MSG_IMAGE_BLOCK_SIZE_NOT_READ);
        }

        this.imageBlockSize = ((Long) imageBlockSizeData.getValue()).intValue();
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
                .readAttribute(Attribute.IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER.getValue());
        if (imageFirstNotReadBlockNumberData == null || !imageFirstNotReadBlockNumberData.isNumber()) {
            throw new ProtocolAdapterException(EXCEPTION_MSG_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER_NOT_READ);
        }

        return ((Long) imageFirstNotReadBlockNumberData.getValue()).intValue();
    }

    private boolean isImageTransferInitiated() throws ProtocolAdapterException {
        return (this.getImageTransferStatus() == ImageTransferStatus.INITIATED.getValue());
    }

    private int getImageTransferStatus() throws ProtocolAdapterException {
        final DataObject imageTransferStatusData = this.cosemObject.readAttribute(Attribute.IMAGE_TRANSFER_STATUS
                .getValue());
        if (imageTransferStatusData == null || !imageTransferStatusData.isNumber()) {
            throw new ProtocolAdapterException(EXCEPTION_MSG_IMAGE_TRANSFER_STATUS_NOT_READ);
        }

        return (Integer) imageTransferStatusData.getValue();
    }

    private void imageBlockTransfer(final int blockNumber) throws ProtocolAdapterException {
        final int imageBlockSize = this.getImageBlockSize();
        final int index = imageBlockSize * blockNumber;

        final byte[] transferData = Arrays.copyOfRange(this.data, index, index + imageBlockSize);

        final List<DataObject> params = new ArrayList<>();
        params.add(DataObject.newUInteger32Data(blockNumber));
        params.add(DataObject.newOctetStringData(transferData));

        final MethodResultCode result = this.cosemObject.callMethod(Method.IMAGE_BLOCK_TRANSFER.getValue(),
                DataObject.newStructureData(params));
    }

    private void logUploadPercentage(final int block, final int totalBlocks) {
        final int step = (int) Math.round((double) totalBlocks / (100 / LOGGER_PERCENTAGE_STEP));
        if (block % step == 0) {
            LOGGER.info("Firmware upload progress " + (block / step) * LOGGER_PERCENTAGE_STEP + "%.");
        }
    }

    /**
     * Check image transfer enabled value of the COSEM server.
     *
     * @return image transfer enabled
     * @throws ProtocolAdapterException
     */
    public boolean imageTransferEnabled() throws ProtocolAdapterException {
        final DataObject transferEnabled = this.cosemObject.readAttribute(Attribute.IMAGE_TRANSFER_ENABLED.getValue());
        if (transferEnabled == null || !transferEnabled.isBoolean()) {
            throw new ProtocolAdapterException(EXCEPTION_MSG_IMAGE_TRANSFER_ENABLED_NOT_READ);
        }

        return (Boolean) transferEnabled.getValue();
    }

    /**
     * Initiates Image transfer.After a successful initiation, the value of the
     * image_transfer_status attribute is (1) Image transfer initiated and the
     * COSEM server is prepared to accept ImageBlocks.
     */
    public void initiateImageTransfer() {
        final List<DataObject> params = new ArrayList<>();
        params.add(DataObject.newOctetStringData(this.imageIdentifier.getBytes()));
        params.add(DataObject.newUInteger32Data(this.getImageSize()));

        final MethodResultCode result = this.cosemObject.callMethod(Method.IMAGE_TRANSFER_INITIATE.getValue(),
                DataObject.newStructureData(params));

        LOGGER.info("Initatiate result: " + result.getCode());
    }

    /**
     * Transfers all image blocks.
     *
     * ImageBlocks are accepted only by those COSEM servers, in which the Image
     * transfer process has been successfully initiated. Other servers silently
     * discard any ImageBlocks received.
     *
     * @throws ProtocolAdapterException
     */
    public void transferImageBlocks() throws ProtocolAdapterException {
        if (!this.isImageTransferInitiated()) {
            throw new ProtocolAdapterException(EXCEPTION_MSG_IMAGE_TRANSFER_NOT_INITIATED);
        }

        final int blocks = this.numberOfBlocks();
        for (int i = 0; i < blocks; i++) {
            this.logUploadPercentage(i, blocks);
            this.imageBlockTransfer(i);
        }
    }

    /**
     * Checks the completeness of the Image transferred. If the Image is not
     * complete, it transfers the ImageBlocks not (yet) transferred. This is an
     * iterative process, continued until the whole Image is successfully
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
     * The Image is verified. This is done by invoking the image_verify method
     * by the client and testing the image transfer status.
     */
    public void verifyImage() throws ProtocolAdapterException {
        final MethodResultCode verified = this.cosemObject.callMethod(Method.IMAGE_VERIFY.getValue(),
                DataObject.newInteger8Data((byte) 0));
        if (verified == null) {
            throw new ProtocolAdapterException(EXCEPTION_MSG_IMAGE_VERIFY_NOT_CALLED);
        }

        if (verified.getCode() == Action_Result.OTHER_REASON) {
            final int status = this.getImageTransferStatus();
            throw new ProtocolAdapterException(EXCEPTION_MSG_IMAGE_NOT_VERIFIED + status);
        }

        if (verified.getCode() == Action_Result.TEMPORARY_FAILURE) {
            final Future<Integer> newStatus = this.executorService.submit(new ImageTransferStatusChanged(
                    ImageTransferStatus.VERIFICATION_INITIATED));

            int status;
            try {
                status = newStatus.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new ProtocolAdapterException("", e);
            }

            if (status == ImageTransferStatus.VERIFICATION_FAILED.getValue()) {
                throw new ProtocolAdapterException(EXCEPTION_MSG_IMAGE_NOT_VERIFIED);
            }

            return;
        }
    }

    /**
     * Checks the information on the Image transferred to see if it is OK to
     * activate the image. This information is held by the
     * image_to_activate_info attribute of the Image transfer object and it is
     * generated as a result of the Image verification.
     *
     * @return OK to activate image
     * @throws ProtocolAdapterException
     */
    public boolean imageToActivateOk() throws ProtocolAdapterException {
        final DataObject imageTransferStatusData = this.cosemObject.readAttribute(Attribute.IMAGE_TO_ACTIVATE_INFO
                .getValue());

        if (imageTransferStatusData.getType() != Type.ARRAY) {
            throw new ProtocolAdapterException(EXCEPTION_MSG_IMAGE_TO_ACTIVATE_NOT_OK);
        }

        final List<DataObject> images = (List<DataObject>) imageTransferStatusData.getValue();
        for (final DataObject image : images) {
            // TODO: check values

        }

        return true;
    }

    /**
     * The image is activated.
     *
     * @return
     * @throws ProtocolAdapterException
     */
    public void activateImage() throws ProtocolAdapterException {
        final MethodResultCode imageActivate = this.cosemObject.callMethod(Method.IMAGE_ACTIVATE.getValue(),
                DataObject.newInteger8Data((byte) 0));
        if (imageActivate == null) {
            throw new ProtocolAdapterException(EXCEPTION_MSG_IMAGE_VERIFY_NOT_CALLED);
        }

        if (imageActivate.getCode() == Action_Result.TEMPORARY_FAILURE) {
            final Future<Integer> newStatus = this.executorService.submit(new ImageTransferStatusChanged(
                    ImageTransferStatus.ACTIVATION_INITIATED));

            int status;
            try {
                status = newStatus.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new ProtocolAdapterException("", e);
            }

            if (status == ImageTransferStatus.ACTIVATION_FAILED.getValue()) {
                throw new ProtocolAdapterException(EXCEPTION_MSG_IMAGE_TO_ACTIVATE_NOT_OK);
            }

            return;
        }

        if (imageActivate.getCode() != Action_Result.SUCCESS) {
            throw new ProtocolAdapterException(EXCEPTION_MSG_IMAGE_TO_ACTIVATE_NOT_OK);
        }
    }

    private class ImageTransferStatusChanged implements Callable<Integer> {
        private final ImageTransferStatus imageTransferStatus;

        public ImageTransferStatusChanged(final ImageTransferStatus imageTransferStatus) {
            this.imageTransferStatus = imageTransferStatus;
        }

        @Override
        public Integer call() throws Exception {
            int status = 0;
            while ((status = ImageTransfer.this.getImageTransferStatus()) == this.imageTransferStatus.getValue()) {
                LOGGER.info("Waiting for status change.");
                Thread.sleep(20000);
            }

            return status;
        }
    }

    private enum ImageTransferStatus {
        NOT_INITIATED(0),
        INITIATED(1),
        VERIFICATION_INITIATED(2),
        VERIFICATION_SUCCESSFUL(3),
        VERIFICATION_FAILED(4),
        ACTIVATION_INITIATED(5),
        ACTIVATION_SUCCESSFUL(6),
        ACTIVATION_FAILED(7);

        private final int imageTransferStatus;

        private ImageTransferStatus(final int imageTransferStatus) {
            this.imageTransferStatus = imageTransferStatus;
        }

        private int getValue() {
            return this.imageTransferStatus;
        }
    }

    private enum Attribute {
        IMAGE_BLOCK_SIZE(2),
        IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER(4),
        IMAGE_TRANSFER_ENABLED(5),
        IMAGE_TRANSFER_STATUS(6),
        IMAGE_TO_ACTIVATE_INFO(7);

        private final int attributeId;

        private Attribute(final int attributeId) {
            this.attributeId = attributeId;
        }

        private int getValue() {
            return this.attributeId;
        }
    }

    private enum Method {
        IMAGE_TRANSFER_INITIATE(1),
        IMAGE_BLOCK_TRANSFER(2),
        IMAGE_VERIFY(3),
        IMAGE_ACTIVATE(4);

        private final int methodId;

        private Method(final int methodId) {
            this.methodId = methodId;
        }

        private int getValue() {
            return this.methodId;
        }
    };
}
