package org.osgp.adapter.protocol.dlms.domain.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;
import org.osgp.adapter.protocol.dlms.domain.factories.DeviceConnector;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ImageTransfer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageTransfer.class);
    private static final int LOGGER_PERCENTAGE_STEP = 5;

    private static final String EXCEPTION_MSG_WAITING_FOR_IMAGE_ACTIVATION = "An error occurred while waiting for image activation status to change.";
    private static final String EXCEPTION_MSG_IMAGE_VERIFY_NOT_CALLED = "Image verify could not be called.";
    private static final String EXCEPTION_MSG_IMAGE_NOT_VERIFIED = "The image could not be verified. Status: ";
    private static final String EXCEPTION_MSG_IMAGE_BLOCK_SIZE_NOT_READ = "Image block size could not be read.";
    private static final String EXCEPTION_MSG_IMAGE_TRANSFER_ENABLED_NOT_READ = "Image transfer enabled could not be read.";
    private static final String EXCEPTION_MSG_IMAGE_TRANSFER_STATUS_NOT_READ = "Image transfer status could not be read.";
    private static final String EXCEPTION_MSG_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER_NOT_READ = "Image first not transferred block number could not be read.";
    private static final String EXCEPTION_MSG_IMAGE_TRANSFER_NOT_INITIATED = "Image transfer has not been initiated.";
    private static final String EXCEPTION_MSG_IMAGE_TO_ACTIVATE_NOT_OK = "Properties of image to activate or not as excepted.";
    private static final String EXCEPTION_MSG_ACTIVATION_TAKING_TOO_LONG = "Activation is taking too long.";
    private static final String EXCEPTION_MSG_IMAGE_ACTIVATE_NOT_SUCCESS = "Image activate could not be called successfully.";

    private static final int CLASS_ID = 18;
    private static final ObisCode OBIS_CODE = new ObisCode("0.0.44.0.0.255");
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

    private final ImageTranferProperties properties;
    private final String imageIdentifier;
    private final byte[] imageData;
    private final CosemObjectAccessor imageTransferCosem;
    private int imageBlockSize;
    private boolean imageBlockSizeReadFlag;

    public ImageTransfer(final DeviceConnector connector, ImageTranferProperties properties,
            final String imageIdentifier, final byte[] imageData) throws ProtocolAdapterException {
        this.properties = properties;
        this.imageIdentifier = imageIdentifier;
        this.imageData = imageData;
        this.imageBlockSizeReadFlag = false;
        this.imageTransferCosem = new CosemObjectAccessor(connector, OBIS_CODE, CLASS_ID);
    }

    /**
     * Call before initiating transfer to make sure the process is not already
     * enabled and might be resumed.
     * 
     * @return Should initiate transfer
     * @throws ProtocolAdapterException
     */
    public boolean shouldInitiateTransfer() throws ProtocolAdapterException {
        return !this.isImageTransferStatusIn(ImageTransferStatus.INITIATED, ImageTransferStatus.VERIFICATION_INITIATED,
                ImageTransferStatus.ACTIVATION_INITIATED);
    }

    public boolean shouldTransferImage() throws ProtocolAdapterException {
        return this.isImageTransferStatusIn(ImageTransferStatus.INITIATED);
    }

    public boolean imageIsVerified() throws ProtocolAdapterException {
        return this.isImageTransferStatusIn(ImageTransferStatus.VERIFICATION_SUCCESSFUL,
                ImageTransferStatus.ACTIVATION_INITIATED, ImageTransferStatus.ACTIVATION_SUCCESSFUL,
                ImageTransferStatus.ACTIVATION_FAILED);
    }

    /**
     * Check image transfer enabled value of the COSEM server.
     *
     * @return image transfer enabled
     * @throws ProtocolAdapterException
     */
    public boolean imageTransferEnabled() throws ProtocolAdapterException {
        final DataObject transferEnabled = this.imageTransferCosem.readAttribute(Attribute.IMAGE_TRANSFER_ENABLED
                .getValue());
        if (transferEnabled == null || !transferEnabled.isBoolean()) {
            throw new ProtocolAdapterException(EXCEPTION_MSG_IMAGE_TRANSFER_ENABLED_NOT_READ);
        }

        return (Boolean) transferEnabled.getValue();
    }

    public void setImageTransferEnabled(final boolean enabled) throws ProtocolAdapterException {
        final DataObject transferEnabled = DataObject.newBoolData(enabled);
        this.imageTransferCosem.writeAttribute(Attribute.IMAGE_TRANSFER_ENABLED.getValue(), transferEnabled);
    }

    /**
     * Initiates Image transfer.After a successful initiation, the value of the
     * image_transfer_status attribute is (1) Image transfer initiated and the
     * COSEM server is prepared to accept ImageBlocks.
     *
     * @throws ProtocolAdapterException
     */
    public void initiateImageTransfer() throws ProtocolAdapterException {
        final List<DataObject> params = new ArrayList<>();
        params.add(DataObject.newOctetStringData(this.imageIdentifier.getBytes()));
        params.add(DataObject.newUInteger32Data(this.getImageSize()));

        this.imageTransferCosem.callMethod(Method.IMAGE_TRANSFER_INITIATE.getValue(),
                DataObject.newStructureData(params));
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
        if (!this.shouldTransferImage()) {
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
    public void transferMissingImageBlocks() throws ProtocolAdapterException {
        int blockNumber;
        while ((blockNumber = this.getImageFirstNotTransferredBlockNumber()) < this.numberOfBlocks()) {
            LOGGER.info("Retransferring block {}.", blockNumber);
            this.imageBlockTransfer(blockNumber);
        }
    }

    /**
     * The Image is verified. This is done by invoking the image_verify method
     * by the client and testing the image transfer status.
     */
    public void verifyImage() throws ProtocolAdapterException {
        final MethodResultCode verified = this.imageTransferCosem.callMethod(Method.IMAGE_VERIFY.getValue(),
                DataObject.newInteger8Data((byte) 0));
        if (verified == null) {
            throw new ProtocolAdapterException(EXCEPTION_MSG_IMAGE_VERIFY_NOT_CALLED);
        }

        if (verified == MethodResultCode.OTHER_REASON) {
            // If activation was triggered the device will not verify again.
            if (this.imageIsVerified()) {
                return;
            }
            final int status = this.getImageTransferStatus();
            throw new ProtocolAdapterException(EXCEPTION_MSG_IMAGE_NOT_VERIFIED + status);
        }

        if (verified == MethodResultCode.TEMPORARY_FAILURE) {
            final Future<Integer> newStatus = EXECUTOR_SERVICE.submit(new ImageTransferStatusChangeWatcher(
                    ImageTransferStatus.VERIFICATION_INITIATED, properties.getVerificationStatusCheckInterval(),
                    properties.verificationStatusCheckTimeout));

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
        final DataObject imageTransferStatusData = this.imageTransferCosem
                .readAttribute(Attribute.IMAGE_TO_ACTIVATE_INFO.getValue());

        if (imageTransferStatusData.getType() != Type.ARRAY) {
            throw new ProtocolAdapterException(EXCEPTION_MSG_IMAGE_TO_ACTIVATE_NOT_OK);
        }

        boolean imageWasReturned = false;
        @SuppressWarnings("unchecked")
        final List<DataObject> images = (List<DataObject>) imageTransferStatusData.getValue();
        for (final DataObject image : images) {
            @SuppressWarnings("unchecked")
            List<DataObject> imageData = (List<DataObject>) image.getValue();

            // Match image by signature.
            if (this.isSignature((byte[]) imageData.get(2).getValue())) {
                imageWasReturned = true;
                // Check image_size
                if ((Long) imageData.get(0).getValue() != this.imageData.length) {
                    return false;
                }
            }
        }

        return imageWasReturned;
    }

    /**
     * The image is activated.
     *
     * @throws ProtocolAdapterException
     */
    public void activateImage() throws ProtocolAdapterException {
        final MethodResultCode imageActivate = this.imageTransferCosem.callMethod(Method.IMAGE_ACTIVATE.getValue(),
                DataObject.newInteger8Data((byte) 0));
        if (imageActivate == null) {
            throw new ProtocolAdapterException(EXCEPTION_MSG_IMAGE_VERIFY_NOT_CALLED);
        }

        if (imageActivate == MethodResultCode.TEMPORARY_FAILURE) {
            final Future<Integer> newStatus = EXECUTOR_SERVICE.submit(new ImageTransferStatusChangeWatcher(
                    ImageTransferStatus.ACTIVATION_INITIATED, properties.getActivationStatusCheckInterval(), properties
                            .getActivationStatusCheckTimeout(), true));

            int status;
            try {
                status = newStatus.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new ProtocolAdapterException(EXCEPTION_MSG_WAITING_FOR_IMAGE_ACTIVATION, e);
            }

            if (status == ImageTransferStatus.ACTIVATION_FAILED.getValue()) {
                throw new ProtocolAdapterException(EXCEPTION_MSG_IMAGE_TO_ACTIVATE_NOT_OK);
            }

            if (status == ImageTransferStatus.ACTIVATION_INITIATED.getValue()) {
                throw new ProtocolAdapterException(EXCEPTION_MSG_ACTIVATION_TAKING_TOO_LONG);
            }

            return;
        }

        if (imageActivate != MethodResultCode.SUCCESS) {
            throw new ProtocolAdapterException(EXCEPTION_MSG_IMAGE_ACTIVATE_NOT_SUCCESS);
        }
    }

    private int getImageSize() {
        return this.imageData.length;
    }

    private int numberOfBlocks() throws ProtocolAdapterException {
        int blocks = (int) Math.ceil((double) this.getImageSize() / this.getImageBlockSize());
        LOGGER.info("Calculated number of blocks: {} / {} = {}", this.getImageSize(), this.getImageBlockSize(), blocks);
        return blocks;
    }

    private int readImageBlockSize() throws ProtocolAdapterException {
        final DataObject imageBlockSizeData = this.imageTransferCosem.readAttribute(Attribute.IMAGE_BLOCK_SIZE
                .getValue());
        if (imageBlockSizeData == null || !imageBlockSizeData.isNumber()) {
            throw new ProtocolAdapterException(EXCEPTION_MSG_IMAGE_BLOCK_SIZE_NOT_READ);
        }

        this.imageBlockSize = ((Long) imageBlockSizeData.getValue()).intValue();
        this.imageBlockSizeReadFlag = true;
        return this.imageBlockSize;
    }

    private int getImageBlockSize() throws ProtocolAdapterException {
        if (!this.imageBlockSizeReadFlag) {
            return this.readImageBlockSize();
        }
        return this.imageBlockSize;
    }

    private int getImageFirstNotTransferredBlockNumber() throws ProtocolAdapterException {
        final DataObject imageFirstNotReadBlockNumberData = this.imageTransferCosem
                .readAttribute(Attribute.IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER.getValue());
        if (imageFirstNotReadBlockNumberData == null || !imageFirstNotReadBlockNumberData.isNumber()) {
            throw new ProtocolAdapterException(EXCEPTION_MSG_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER_NOT_READ);
        }

        return ((Long) imageFirstNotReadBlockNumberData.getValue()).intValue();
    }

    private boolean isImageTransferStatusIn(ImageTransferStatus... statuses) throws ProtocolAdapterException {
        int currentStatus = this.getImageTransferStatus();
        for (ImageTransferStatus status : statuses) {
            if (currentStatus == status.getValue()) {
                return true;
            }
        }

        return false;
    }

    private int getImageTransferStatus() throws ProtocolAdapterException {
        final DataObject imageTransferStatusData = this.imageTransferCosem
                .readAttribute(Attribute.IMAGE_TRANSFER_STATUS.getValue());
        if (imageTransferStatusData == null || !imageTransferStatusData.isNumber()) {
            throw new ProtocolAdapterException(EXCEPTION_MSG_IMAGE_TRANSFER_STATUS_NOT_READ);
        }

        return (Integer) imageTransferStatusData.getValue();
    }

    private void imageBlockTransfer(final int blockNumber) throws ProtocolAdapterException {
        final int imageBlockSize = this.getImageBlockSize();
        final int startIndex = imageBlockSize * blockNumber;

        int endIndex = startIndex + imageBlockSize;
        // Do not transfer data with padded 0 bytes.
        endIndex = (endIndex <= this.imageData.length) ? endIndex : this.imageData.length;
        byte[] transferData = Arrays.copyOfRange(this.imageData, startIndex, endIndex);

        final List<DataObject> params = new ArrayList<>();
        params.add(DataObject.newUInteger32Data(blockNumber));
        params.add(DataObject.newOctetStringData(transferData));

        this.imageTransferCosem.callMethod(Method.IMAGE_BLOCK_TRANSFER.getValue(), DataObject.newStructureData(params));
    }

    private void logUploadPercentage(final int block, final int totalBlocks) {
        final int step = (int) Math.round((double) totalBlocks / (100 / LOGGER_PERCENTAGE_STEP));
        if (step != 0 && block % step == 0) {
            LOGGER.info("Firmware upload progress {}%. ({} / {})", (block / step) * LOGGER_PERCENTAGE_STEP, block,
                    totalBlocks);
        }
    }

    /**
     * Compare the signature with the first bytes of the image data.
     * 
     * @param signature
     *            Signature retrieved from device.
     * @return Signature matches.
     */
    private boolean isSignature(final byte[] signature) {
        for (int i = 0; i < signature.length; i++) {
            if (signature[i] != this.imageData[i]) {
                return false;
            }
        }

        return true;
    }

    static class ImageTranferProperties {
        private int verificationStatusCheckInterval;
        private int verificationStatusCheckTimeout;
        private int activationStatusCheckInterval;
        private int activationStatusCheckTimeout;

        public int getVerificationStatusCheckInterval() {
            return verificationStatusCheckInterval;
        }

        public void setVerificationStatusCheckInterval(int verificationStatusCheckInterval) {
            this.verificationStatusCheckInterval = verificationStatusCheckInterval;
        }

        public int getVerificationStatusCheckTimeout() {
            return verificationStatusCheckTimeout;
        }

        public void setVerificationStatusCheckTimeout(int verificationStatusCheckTimeout) {
            this.verificationStatusCheckTimeout = verificationStatusCheckTimeout;
        }

        public int getActivationStatusCheckInterval() {
            return activationStatusCheckInterval;
        }

        public void setActivationStatusCheckInterval(int activationStatusCheckInterval) {
            this.activationStatusCheckInterval = activationStatusCheckInterval;
        }

        public int getActivationStatusCheckTimeout() {
            return activationStatusCheckTimeout;
        }

        public void setActivationStatusCheckTimeout(int activationStatusCheckTimeout) {
            this.activationStatusCheckTimeout = activationStatusCheckTimeout;
        }
    }

    private class ImageTransferStatusChangeWatcher implements Callable<Integer> {
        private final ImageTransferStatus imageTransferStatusWaitingFor;
        private final int pollingInterval;
        private final int timeout;
        private final boolean disconnectWhileWaiting;
        private int slept = 0;

        public ImageTransferStatusChangeWatcher(final ImageTransferStatus imageTransferStatus, int pollingInterval,
                int timeout) {
            this(imageTransferStatus, pollingInterval, timeout, false);
        }

        public ImageTransferStatusChangeWatcher(final ImageTransferStatus imageTransferStatusWaitingFor,
                int pollingInterval, int timeout, boolean disconnectWhileWaiting) {
            this.imageTransferStatusWaitingFor = imageTransferStatusWaitingFor;
            this.pollingInterval = pollingInterval;
            this.timeout = timeout;
            this.disconnectWhileWaiting = disconnectWhileWaiting;
        }

        @Override
        public Integer call() throws Exception {
            int status = 0;
            while (this.slept < this.timeout) {
                status = ImageTransfer.this.getImageTransferStatus();
                if (status != this.imageTransferStatusWaitingFor.getValue()) {
                    return status;
                }

                if (disconnectWhileWaiting) {
                    this.disconnect();
                }

                LOGGER.info("Waiting for status change.");
                int doSleep = (this.slept + this.pollingInterval < this.timeout) ? this.pollingInterval : this.timeout
                        - this.slept;
                Thread.sleep(doSleep);
                this.slept += doSleep;

                if (disconnectWhileWaiting) {
                    // Always return in connected state.
                    this.connect();
                }
            }

            return status;
        }

        private void connect() throws ProtocolAdapterException {
            ImageTransfer.this.imageTransferCosem.connect();
        }

        private void disconnect() throws ProtocolAdapterException {
            ImageTransfer.this.imageTransferCosem.disconnect();
        }
    }

    /**
     * Possible values of Attribute IMAGE_TRANSFER_STATUS(6).
     *
     */
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

    /**
     * Attributes of Image transfer IC definition.
     *
     */
    private enum Attribute {
        LOGICAL_NAME(1),
        IMAGE_BLOCK_SIZE(2),
        IMAGE_TRANSFERRED_BLOCKS_STATUS(3),
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

    /**
     * Methods of Image transfer IC definition.
     *
     */
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
