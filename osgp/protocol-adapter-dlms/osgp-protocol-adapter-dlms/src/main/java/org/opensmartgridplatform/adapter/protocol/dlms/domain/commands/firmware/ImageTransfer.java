// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware;

import static com.google.common.util.concurrent.MoreExecutors.shutdownAndAwaitTermination;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.CosemObjectAccessor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ImageTransferException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.NotSupportedByProtocolException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ImageTransferAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.method.ImageTransferMethod;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;

@Slf4j
public class ImageTransfer {
  private static final long AWAIT_TERMINATION_IN_SEC = 5;

  private static final double LOGGER_PERCENTAGE_STEP = 5.0;

  private static final String EXCEPTION_MSG_IMAGE_VERIFY_NOT_CALLED =
      "Image verify could not be called.";
  private static final String EXCEPTION_MSG_IMAGE_VERIFICATION_ERROR =
      "The image could not be verified. Left in firmware transfer status %s with method result code %s}";
  private static final String EXCEPTION_MSG_IMAGE_NOT_VERIFIED =
      "The image could not be verified. Status: ";
  private static final String EXCEPTION_MSG_IMAGE_BLOCK_SIZE_NOT_READ =
      "Image block size could not be read.";
  private static final String EXCEPTION_MSG_IMAGE_TRANSFER_ENABLED_NOT_READ =
      "Image transfer enabled could not be read.";
  private static final String EXCEPTION_MSG_IMAGE_TRANSFER_STATUS_NOT_READ =
      "Image transfer status could not be read.";
  private static final String EXCEPTION_MSG_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER_NOT_READ =
      "Image first not transferred block number could not be read.";
  private static final String EXCEPTION_MSG_IMAGE_TRANSFER_NOT_INITIATED =
      "Image transfer has not been initiated.";
  private static final String EXCEPTION_MSG_IMAGE_ACTIVATE_NOT_CALLED =
      "Image activate could not be called.";
  private static final String EXCEPTION_MSG_IMAGE_TO_ACTIVATE_NOT_OK =
      "Properties of image to activate are not as expected.";

  private final ImageTransferProperties properties;
  private final byte[] imageIdentifier;
  private final byte[] imageData;
  private final DlmsConnectionManager connector;
  private final CosemObjectAccessor imageTransferCosem;
  private int imageBlockSize;
  private boolean imageBlockSizeReadFlag;

  public ImageTransfer(
      final DlmsConnectionManager connector,
      final ImageTransferProperties properties,
      final byte[] imageIdentifier,
      final byte[] imageData,
      final ObjectConfigServiceHelper objectConfigServiceHelper,
      final Protocol protocol)
      throws NotSupportedByProtocolException {
    this.properties = properties;
    this.imageIdentifier = imageIdentifier;
    this.imageData = imageData;
    this.imageBlockSizeReadFlag = false;
    this.connector = connector;
    this.imageTransferCosem =
        new CosemObjectAccessor(
            connector, objectConfigServiceHelper, DlmsObjectType.IMAGE_TRANSFER, protocol);
  }

  public boolean shouldTransferImage() throws OsgpException {
    return this.isImageTransferStatusIn(ImageTransferStatus.INITIATED);
  }

  public boolean isInitiated() throws OsgpException {
    return this.getImageTransferStatus() == ImageTransferStatus.INITIATED.getValue();
  }

  public boolean imageIsVerified() throws OsgpException {
    return this.isImageTransferStatusIn(
        ImageTransferStatus.VERIFICATION_SUCCESSFUL,
        ImageTransferStatus.ACTIVATION_INITIATED,
        ImageTransferStatus.ACTIVATION_SUCCESSFUL,
        ImageTransferStatus.ACTIVATION_FAILED);
  }

  /**
   * Check image transfer enabled value of the COSEM server.
   *
   * @return image transfer enabled
   */
  public boolean imageTransferEnabled() throws ProtocolAdapterException {
    this.connector
        .getDlmsMessageListener()
        .setDescription(
            "ImageTransfer read image_transfer_enabled, read attribute: "
                + JdlmsObjectToStringUtil.describeAttributes(
                    this.imageTransferCosem.createAttributeAddress(
                        ImageTransferAttribute.IMAGE_TRANSFER_ENABLED)));

    final DataObject transferEnabled =
        this.imageTransferCosem.readAttribute(ImageTransferAttribute.IMAGE_TRANSFER_ENABLED);
    if (transferEnabled == null || !transferEnabled.isBoolean()) {
      throw new ProtocolAdapterException(EXCEPTION_MSG_IMAGE_TRANSFER_ENABLED_NOT_READ);
    }

    return (Boolean) transferEnabled.getValue();
  }

  public void setImageTransferEnabled(final boolean enabled) throws ProtocolAdapterException {
    this.connector
        .getDlmsMessageListener()
        .setDescription(
            "ImageTransfer set image_transfer_enabled to "
                + (enabled ? "TRUE" : "FALSE")
                + ", write attribute: "
                + JdlmsObjectToStringUtil.describeAttributes(
                    this.imageTransferCosem.createAttributeAddress(
                        ImageTransferAttribute.IMAGE_TRANSFER_ENABLED)));

    final DataObject transferEnabled = DataObject.newBoolData(enabled);
    this.imageTransferCosem.writeAttribute(
        ImageTransferAttribute.IMAGE_TRANSFER_ENABLED, transferEnabled);
  }

  /**
   * Initiates Image transfer.After a successful initiation, the value of the image_transfer_status
   * attribute is (1) Image transfer initiated and the COSEM server is prepared to accept
   * ImageBlocks.
   */
  public void initiateImageTransfer() throws ProtocolAdapterException {
    final List<DataObject> params = new ArrayList<>();
    params.add(DataObject.newOctetStringData(this.imageIdentifier));
    params.add(DataObject.newUInteger32Data(this.getImageSize()));
    final DataObject parameter = DataObject.newStructureData(params);

    this.setDescriptionForMethodCall(ImageTransferMethod.IMAGE_TRANSFER_INITIATE, parameter);

    final MethodResultCode resultCode =
        this.imageTransferCosem.callMethod(ImageTransferMethod.IMAGE_TRANSFER_INITIATE, parameter);

    if (resultCode != MethodResultCode.SUCCESS) {
      log.warn("Method IMAGE_TRANSFER_INITIATE gave result {}", resultCode);
    }
  }

  /**
   * Transfers image blocks starting from the given first block until the last.
   *
   * <p>ImageBlocks are accepted only by those COSEM servers, in which the Image transfer process
   * has been successfully initiated. Other servers silently discard any ImageBlocks received.
   */
  public void transferImageBlocks(final int firstBlock) throws OsgpException {
    if (!this.shouldTransferImage()) {
      throw new ProtocolAdapterException(EXCEPTION_MSG_IMAGE_TRANSFER_NOT_INITIATED);
    }

    final int blocks = this.numberOfBlocks();
    for (int i = firstBlock; i < blocks; i++) {
      this.logUploadPercentage(i, blocks);
      this.imageBlockTransfer(i);
    }
  }

  /**
   * Checks the completeness of the Image transferred. If the Image is not complete, it transfers
   * the ImageBlocks not (yet) transferred. This is an iterative process, continued until the whole
   * Image is successfully transferred.
   */
  public void transferMissingImageBlocks() throws ProtocolAdapterException {
    int blockNumber;
    while ((blockNumber = this.getImageFirstNotTransferredBlockNumber()) < this.numberOfBlocks()) {
      log.info("Retransferring block {}.", blockNumber);
      this.imageBlockTransfer(blockNumber);
    }
  }

  /**
   * The Image is verified. This is done by invoking the image_verify method by the client and
   * testing the image transfer status.
   */
  public void verifyImage() throws OsgpException {
    final DataObject parameter = DataObject.newInteger8Data((byte) 0);
    this.setDescriptionForMethodCall(ImageTransferMethod.IMAGE_VERIFY, parameter);

    final MethodResultCode verified =
        this.imageTransferCosem.callMethod(ImageTransferMethod.IMAGE_VERIFY, parameter);
    if (verified == null) {
      throw new ProtocolAdapterException(EXCEPTION_MSG_IMAGE_VERIFY_NOT_CALLED);
    }

    if (verified == MethodResultCode.SUCCESS) {
      return;
    }

    if (verified == MethodResultCode.TEMPORARY_FAILURE) {
      this.waitForImageVerification();
    }

    // If activation was triggered the device will not verify again.
    if (this.imageIsVerified()) {
      return;
    }

    final int status = this.getImageTransferStatus();
    throw new ImageTransferException(
        String.format(
            EXCEPTION_MSG_IMAGE_VERIFICATION_ERROR,
            ImageTransferStatus.getByValue(status).name(),
            verified.name()));
  }

  /**
   * Checks the information on the Image transferred to see if it is OK to activate the image. This
   * information is held by the image_to_activate_info attribute of the Image transfer object and it
   * is generated as a result of the Image verification.
   *
   * @return OK to activate image
   */
  public boolean imageToActivateOk() throws ProtocolAdapterException {
    this.connector
        .getDlmsMessageListener()
        .setDescription(
            "ImageTransfer read image_to_activate_info, read attribute: "
                + JdlmsObjectToStringUtil.describeAttributes(
                    this.imageTransferCosem.createAttributeAddress(
                        ImageTransferAttribute.IMAGE_TO_ACTIVATE_INFO)));

    final DataObject imageToActivateInfoData =
        this.imageTransferCosem.readAttribute(ImageTransferAttribute.IMAGE_TO_ACTIVATE_INFO);

    if (imageToActivateInfoData.getType() != Type.ARRAY) {
      /*
       * This is an optional check, so only log an error about the
       * situation encountered, leaving the flow to continue, since image
       * verification should already have been successful.
       */
      log.error(EXCEPTION_MSG_IMAGE_TO_ACTIVATE_NOT_OK);
      return true;
    }

    final List<DataObject> imageToActivateInfo = imageToActivateInfoData.getValue();
    for (final DataObject imageToActivateInfoElement : imageToActivateInfo) {
      final List<DataObject> imageToActivateDetails = imageToActivateInfoElement.getValue();

      final long imageToActivateSize = imageToActivateDetails.get(0).getValue();
      final byte[] imageToActivateIdentificationBytes = imageToActivateDetails.get(1).getValue();
      final byte[] imageSignature = imageToActivateDetails.get(2).getValue();
      if (Arrays.equals(imageToActivateIdentificationBytes, this.imageIdentifier)
          && this.isSignature(imageSignature)
          && imageToActivateSize == this.imageData.length) {
        final String imageDescription =
            this.describeImageInfo(
                imageToActivateSize, imageToActivateIdentificationBytes, imageSignature);
        log.info("Found matching image to activate info element ({})", imageDescription);
        return true;
      } else {
        final String imageToActivateDescription =
            this.describeImageInfo(
                imageToActivateSize, imageToActivateIdentificationBytes, imageSignature);
        final String imageDescription =
            this.describeImageInfo(
                this.imageData.length,
                this.imageIdentifier,
                Arrays.copyOf(this.imageData, imageSignature.length));
        log.info(
            "Retrieved an image to activate info element ({}) with value not matching the image being "
                + "transferred ({}).",
            imageToActivateDescription,
            imageDescription);
      }
    }

    /*
     * This is an optional check, so just return true, leaving the flow to
     * continue, since image verification should already have been
     * successful.
     */
    log.warn("No image to activate info element matched the firmware image being transferred.");
    return true;
  }

  private String describeImageInfo(
      final long size, final byte[] identification, final byte[] signature) {
    return String.format(
        "size=%d, identification=%s, signature=%s",
        size, Arrays.toString(identification), Arrays.toString(signature));
  }

  /** The image is activated. */
  public void activateImage() throws OsgpException {
    final DataObject parameter = DataObject.newInteger8Data((byte) 0);
    this.setDescriptionForMethodCall(ImageTransferMethod.IMAGE_ACTIVATE, parameter);

    final MethodResultCode imageActivate =
        this.imageTransferCosem.callMethod(ImageTransferMethod.IMAGE_ACTIVATE, parameter);
    if (imageActivate == null) {
      throw new ProtocolAdapterException(EXCEPTION_MSG_IMAGE_ACTIVATE_NOT_CALLED);
    }
  }

  private void waitForImageInitiation() throws OsgpException {
    final ExecutorService executorService = Executors.newSingleThreadExecutor();
    final int status;
    try {
      final Future<Integer> newStatus =
          executorService.submit(
              new ImageTransferStatusChangeWatcher(
                  ImageTransferStatus.NOT_INITIATED,
                  this.properties.getInitiationStatusCheckInterval(),
                  this.properties.getInitiationStatusCheckTimeout()));

      status = newStatus.get();
    } catch (final InterruptedException | ExecutionException e) {
      throw new ProtocolAdapterException("", e);
    } finally {
      shutdownAndAwaitTermination(executorService, Duration.ofSeconds(AWAIT_TERMINATION_IN_SEC));
    }

    if (status != ImageTransferStatus.INITIATED.getValue()) {
      throw new ImageTransferException(EXCEPTION_MSG_IMAGE_TRANSFER_NOT_INITIATED);
    }
  }

  private void waitForImageVerification() throws OsgpException {
    final ExecutorService executorService = Executors.newSingleThreadExecutor();
    final int status;
    try {
      final Future<Integer> newStatus =
          executorService.submit(
              new ImageTransferStatusChangeWatcher(
                  ImageTransferStatus.VERIFICATION_INITIATED,
                  this.properties.getVerificationStatusCheckInterval(),
                  this.properties.getVerificationStatusCheckTimeout()));

      status = newStatus.get();
    } catch (final InterruptedException | ExecutionException e) {
      throw new ProtocolAdapterException("", e);
    } finally {
      shutdownAndAwaitTermination(executorService, Duration.ofSeconds(AWAIT_TERMINATION_IN_SEC));
    }

    if (status == ImageTransferStatus.VERIFICATION_FAILED.getValue()) {
      throw new ImageTransferException(EXCEPTION_MSG_IMAGE_NOT_VERIFIED);
    }
  }

  private int getImageSize() {
    return this.imageData.length;
  }

  private int numberOfBlocks() throws ProtocolAdapterException {
    final int blocks = (int) Math.ceil((double) this.getImageSize() / this.getImageBlockSize());
    log.info(
        "Calculated number of blocks: {} / {} = {}",
        this.getImageSize(),
        this.getImageBlockSize(),
        blocks);
    return blocks;
  }

  private int readImageBlockSize() throws ProtocolAdapterException {
    this.connector
        .getDlmsMessageListener()
        .setDescription(
            "ImageTransfer read image_block_size, read attribute: "
                + JdlmsObjectToStringUtil.describeAttributes(
                    this.imageTransferCosem.createAttributeAddress(
                        ImageTransferAttribute.IMAGE_BLOCK_SIZE)));

    final DataObject imageBlockSizeData =
        this.imageTransferCosem.readAttribute(ImageTransferAttribute.IMAGE_BLOCK_SIZE);
    if (imageBlockSizeData == null || !imageBlockSizeData.isNumber()) {
      throw new ProtocolAdapterException(EXCEPTION_MSG_IMAGE_BLOCK_SIZE_NOT_READ);
    }

    final Long imageBlockSizeValue = imageBlockSizeData.getValue();
    this.imageBlockSize = imageBlockSizeValue.intValue();
    this.imageBlockSizeReadFlag = true;
    return this.imageBlockSize;
  }

  private int getImageBlockSize() throws ProtocolAdapterException {
    if (!this.imageBlockSizeReadFlag) {
      return this.readImageBlockSize();
    }
    return this.imageBlockSize;
  }

  public int getImageFirstNotTransferredBlockNumber() throws ProtocolAdapterException {
    this.connector
        .getDlmsMessageListener()
        .setDescription(
            "ImageTransfer read image_first_not_transferred_block_number, read attribute: "
                + JdlmsObjectToStringUtil.describeAttributes(
                    this.imageTransferCosem.createAttributeAddress(
                        ImageTransferAttribute.IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER)));

    final DataObject imageFirstNotReadBlockNumberData =
        this.imageTransferCosem.readAttribute(
            ImageTransferAttribute.IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER);
    if (imageFirstNotReadBlockNumberData == null || !imageFirstNotReadBlockNumberData.isNumber()) {
      throw new ProtocolAdapterException(
          EXCEPTION_MSG_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER_NOT_READ);
    }

    final Long imageFirstNotReadBlockNumber = imageFirstNotReadBlockNumberData.getValue();
    return imageFirstNotReadBlockNumber.intValue();
  }

  private boolean isImageTransferStatusIn(final ImageTransferStatus... statuses)
      throws OsgpException {
    for (final ImageTransferStatus status : statuses) {
      if (status == ImageTransferStatus.INITIATED) {
        this.waitForImageInitiation();
      }
      if ((this.getImageTransferStatus()) == status.getValue()) {
        return true;
      }
    }

    return false;
  }

  private int getImageTransferStatus() throws ProtocolAdapterException {
    this.connector
        .getDlmsMessageListener()
        .setDescription(
            "ImageTransfer read image_transfer_status, read attribute: "
                + JdlmsObjectToStringUtil.describeAttributes(
                    this.imageTransferCosem.createAttributeAddress(
                        ImageTransferAttribute.IMAGE_TRANSFER_STATUS)));

    final DataObject imageTransferStatusData =
        this.imageTransferCosem.readAttribute(ImageTransferAttribute.IMAGE_TRANSFER_STATUS);
    if (imageTransferStatusData == null || !imageTransferStatusData.isNumber()) {
      throw new ProtocolAdapterException(EXCEPTION_MSG_IMAGE_TRANSFER_STATUS_NOT_READ);
    }

    return (Integer) imageTransferStatusData.getValue();
  }

  private void imageBlockTransfer(final int blockNumber) throws ProtocolAdapterException {
    final int startIndex = this.getImageBlockSize() * blockNumber;

    int endIndex = startIndex + this.getImageBlockSize();
    // Do not transfer data with padded 0 bytes.
    endIndex = (endIndex <= this.imageData.length) ? endIndex : this.imageData.length;
    final byte[] transferData = Arrays.copyOfRange(this.imageData, startIndex, endIndex);

    final List<DataObject> params = new ArrayList<>();
    params.add(DataObject.newUInteger32Data(blockNumber));
    params.add(DataObject.newOctetStringData(transferData));
    final DataObject parameter = DataObject.newStructureData(params);

    this.setDescriptionForMethodCall(ImageTransferMethod.IMAGE_BLOCK_TRANSFER, parameter);

    final MethodResultCode resultCode =
        this.imageTransferCosem.callMethod(ImageTransferMethod.IMAGE_BLOCK_TRANSFER, parameter);

    if (resultCode != MethodResultCode.SUCCESS) {
      log.info("Method IMAGE_BLOCK_TRANSFER gave result {} for block {}", resultCode, blockNumber);
    }
  }

  private void setDescriptionForMethodCall(
      final ImageTransferMethod method, final DataObject parameter) {
    this.connector
        .getDlmsMessageListener()
        .setDescription(
            "ImageTransfer call "
                + method.name().toLowerCase(Locale.UK)
                + " with parameter "
                + parameter
                + ", call method: "
                + JdlmsObjectToStringUtil.describeMethod(
                    this.imageTransferCosem.createMethodParameter(method, parameter)));
  }

  private void logUploadPercentage(final int block, final int totalBlocks) {
    final int step = (int) Math.round(totalBlocks / (100 / LOGGER_PERCENTAGE_STEP));
    if (totalBlocks != 0 && step != 0 && block % step == 0) {
      log.info(
          "Firmware upload progress {}%. ({} / {})",
          (block * 100) / totalBlocks, block, totalBlocks);
    }
  }

  /**
   * Compare the signature with the first bytes of the image data.
   *
   * @param signature Signature retrieved from device.
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

  public static class ImageTransferProperties {
    private int verificationStatusCheckInterval;
    private int verificationStatusCheckTimeout;
    private int initiationStatusCheckInterval;
    private int initiationStatusCheckTimeout;

    public int getVerificationStatusCheckInterval() {
      return this.verificationStatusCheckInterval;
    }

    public void setVerificationStatusCheckInterval(final int verificationStatusCheckInterval) {
      this.verificationStatusCheckInterval = verificationStatusCheckInterval;
    }

    public int getVerificationStatusCheckTimeout() {
      return this.verificationStatusCheckTimeout;
    }

    public void setVerificationStatusCheckTimeout(final int verificationStatusCheckTimeout) {
      this.verificationStatusCheckTimeout = verificationStatusCheckTimeout;
    }

    public int getInitiationStatusCheckInterval() {
      return this.initiationStatusCheckInterval;
    }

    public void setInitiationStatusCheckInterval(final int initiationStatusCheckInterval) {
      this.initiationStatusCheckInterval = initiationStatusCheckInterval;
    }

    public int getInitiationStatusCheckTimeout() {
      return this.initiationStatusCheckTimeout;
    }

    public void setInitiationStatusCheckTimeout(final int initiationStatusCheckTimeout) {
      this.initiationStatusCheckTimeout = initiationStatusCheckTimeout;
    }
  }

  private class ImageTransferStatusChangeWatcher implements Callable<Integer> {
    private final ImageTransferStatus imageTransferStatusWaitingToChange;
    private final int pollingInterval;
    private final int timeout;
    private int slept = 0;

    public ImageTransferStatusChangeWatcher(
        final ImageTransferStatus imageTransferStatusWaitingToChange,
        final int pollingInterval,
        final int timeout) {
      this.imageTransferStatusWaitingToChange = imageTransferStatusWaitingToChange;
      this.pollingInterval = pollingInterval;
      this.timeout = timeout;
    }

    @Override
    public Integer call() throws Exception {
      int status = 0;
      while (this.slept < this.timeout) {
        status = ImageTransfer.this.getImageTransferStatus();
        if (status != this.imageTransferStatusWaitingToChange.getValue()) {
          return status;
        }

        log.info("Waiting for status change.");
        final int doSleep =
            (this.slept + this.pollingInterval < this.timeout)
                ? this.pollingInterval
                : this.timeout - this.slept;
        Thread.sleep(doSleep);
        this.slept += doSleep;
      }

      return status;
    }
  }

  /** Possible values of Attribute IMAGE_TRANSFER_STATUS(6). */
  private enum ImageTransferStatus {
    NOT_INITIATED(0),
    INITIATED(1),
    VERIFICATION_INITIATED(2),
    VERIFICATION_SUCCESSFUL(3),
    VERIFICATION_FAILED(4),
    ACTIVATION_INITIATED(5),
    ACTIVATION_SUCCESSFUL(6),
    ACTIVATION_FAILED(7);

    private final int value;
    private static final Map<Integer, ImageTransferStatus> map = new HashMap<>();

    static {
      for (final ImageTransferStatus status : ImageTransferStatus.values()) {
        map.put(status.value, status);
      }
    }

    ImageTransferStatus(final int imageTransferStatus) {
      this.value = imageTransferStatus;
    }

    private int getValue() {
      return this.value;
    }

    public static ImageTransferStatus getByValue(final int value) {
      final ImageTransferStatus status = map.get(value);
      if (status == null) {
        throw new IllegalArgumentException(
            String.format("No ImageTransferStatus found with code %d (int)", value));
      }
      return status;
    }
  }
}
