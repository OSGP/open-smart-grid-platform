// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.cosem;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import org.openmuc.jdlms.AttributeAccessMode;
import org.openmuc.jdlms.CosemAttribute;
import org.openmuc.jdlms.CosemClass;
import org.openmuc.jdlms.CosemInterfaceObject;
import org.openmuc.jdlms.CosemMethod;
import org.openmuc.jdlms.IllegalMethodAccessException;
import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.datatypes.BitString;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;
import org.opensmartgridplatform.simulator.protocol.dlms.util.DynamicValues;
import org.springframework.beans.factory.annotation.Autowired;

@CosemClass(id = 18)
public class ImageTransfer extends CosemInterfaceObject {

  public static final int IMAGE_TRANSFER_CLASS_ID = 18;

  public static final int ATTRIBUTE_ID_IMAGE_BLOCK_SIZE = 2;
  public static final int ATTRIBUTE_ID_IMAGE_TRANSFERRED_BLOCK_STATUS = 3;
  public static final int ATTRIBUTE_ID_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER = 4;
  public static final int ATTRIBUTE_ID_IMAGE_TRANSFER_ENABLED = 5;
  public static final int ATTRIBUTE_ID_IMAGE_TRANSFER_STATUS = 6;
  public static final int ATTRIBUTE_ID_IMAGE_TO_ACTIVATE_INFO = 7;

  public static final int METHOD_ID_IMAGE_TRANSFER_INITIATE = 1;
  public static final int METHOD_ID_IMAGE_BLOCK_TRANSFER = 2;
  public static final int METHOD_ID_IMAGE_VERIFY = 3;
  public static final int METHOD_ID_IMAGE_ACTIVATE = 4;
  @Autowired private DynamicValues dynamicValues;

  @CosemAttribute(
      id = ATTRIBUTE_ID_IMAGE_BLOCK_SIZE,
      type = Type.DOUBLE_LONG_UNSIGNED,
      accessMode = AttributeAccessMode.READ_ONLY)
  private DataObject imageBlockSize;

  @CosemAttribute(
      id = ATTRIBUTE_ID_IMAGE_TRANSFERRED_BLOCK_STATUS,
      type = Type.BIT_STRING,
      accessMode = AttributeAccessMode.READ_ONLY)
  private DataObject imageTransferredBlockStatus;

  @CosemAttribute(
      id = ATTRIBUTE_ID_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER,
      type = Type.DOUBLE_LONG_UNSIGNED,
      accessMode = AttributeAccessMode.READ_ONLY)
  private DataObject imageFirstNotTransferredBlockNumber;

  @CosemAttribute(
      id = ATTRIBUTE_ID_IMAGE_TRANSFER_ENABLED,
      type = Type.BOOLEAN,
      accessMode = AttributeAccessMode.READ_AND_WRITE)
  private DataObject imageTransferEnabled;

  @CosemAttribute(
      id = ATTRIBUTE_ID_IMAGE_TRANSFER_STATUS,
      type = Type.ENUMERATE,
      accessMode = AttributeAccessMode.READ_ONLY)
  private DataObject imageTransferStatus;

  @CosemAttribute(
      id = ATTRIBUTE_ID_IMAGE_TO_ACTIVATE_INFO,
      type = Type.ARRAY,
      accessMode = AttributeAccessMode.READ_ONLY)
  private DataObject imageToActivateInfo;

  private final int activationStatusChangeDelay;

  private final double transferFailureChance;

  private long blocksExpected = 0L;

  private final Set<Long> blocksReceived = new TreeSet<>();

  private boolean verified = false;

  /*
  The expected image size is set in the constructor. If the ImageTransfer object is used to upload
  a firmware file from block 0 the upload procedure will be started by calling the Cosem method
  IMAGE_TRANSFER_INITIATE. This method will overwrite the expected image size with the appropriate
  value. In case of the 'resume on blocks' flow through the upload procedure the initiate method is not
  called. To be able to calculate number of expected blocks the expected image size used the value
  set by the DefaultDeviceProfile. Image used in tests with the Simulator have to correspond with
  this size.
  */
  private long expectedImageSize;

  private byte[] identification;

  private byte[] signature;

  public ImageTransfer(
      final String logicalName,
      final int activationStatusChangeDelay,
      final double transferFailureChance) {
    super(logicalName);

    this.activationStatusChangeDelay = activationStatusChangeDelay;
    this.transferFailureChance = transferFailureChance;
  }

  private void checkImageTransferEnabled() throws IllegalMethodAccessException {
    if (!(boolean) this.getImageTransferEnabled().getValue()) {
      throw new IllegalMethodAccessException(MethodResultCode.OTHER_REASON);
    }
  }

  private void setImageFirstNotTransferredBlockNumber() {
    for (int i = 0; i < this.blocksExpected; i++) {
      if (!this.blocksReceived.contains((long) i)) {
        this.setImageFirstNotTransferredBlockNumber(DataObject.newUInteger32Data(i));
        return;
      }
    }

    this.setImageFirstNotTransferredBlockNumber(DataObject.newUInteger32Data(this.blocksExpected));
  }

  private void setImageToActivateInfo() {
    /**
     * Expected image size is returned instead of the real received number of bytes. Real received
     * number of bytes is different because of 0 padded transfer blocks.
     */
    final DataObject imageInfo =
        DataObject.newStructureData(
            Arrays.asList(
                DataObject.newUInteger32Data(this.expectedImageSize),
                DataObject.newOctetStringData(this.identification),
                DataObject.newOctetStringData(this.signature)));
    this.setImageToActivateInfo(DataObject.newArrayData(Arrays.asList(imageInfo)));
  }

  private void verify() {
    final boolean corrupt =
        new String(this.identification, StandardCharsets.UTF_8).endsWith(".corrupt");

    this.verified = !corrupt && this.blocksExpected == this.blocksReceived.size();
    if (this.verified) {
      this.setImageTransferStatus(
          DataObject.newEnumerateData(
              ImageTransferStatusType.IMAGE_VERIFICATION_SUCCESSFUL.value()));
    } else {
      this.setImageTransferStatus(
          DataObject.newEnumerateData(ImageTransferStatusType.IMAGE_VERIFICATION_FAILED.value()));
    }
  }

  public DataObject getImageTransferredBlockStatus() {
    if (this.blocksExpected == 0L) {
      return DataObject.newBitStringData(new BitString(new byte[0], 0));
    }

    final BitSet bitSet = new BitSet();

    final Long imageBlockSizeValue = this.getImageBlockSize().getValue();
    final long bitsExpected = this.blocksExpected * (imageBlockSizeValue * 8);
    for (int i = 0; i < bitsExpected; i++) {
      bitSet.set(i, this.blocksReceived.contains((long) i));
    }

    final byte[] byteData = bitSet.toByteArray();
    return DataObject.newBitStringData(new BitString(byteData, byteData.length * 8));
  }

  @CosemMethod(id = METHOD_ID_IMAGE_TRANSFER_INITIATE, consumes = Type.STRUCTURE)
  public void imageTransferInitiate(final DataObject params) throws IllegalMethodAccessException {
    this.checkImageTransferEnabled();

    final List<DataObject> paramList = params.getValue();

    if (paramList.get(0).getType() != Type.OCTET_STRING) {
      throw new IllegalMethodAccessException(MethodResultCode.TYPE_UNMATCHED);
    }
    if (paramList.get(1).getType() != Type.DOUBLE_LONG_UNSIGNED) {
      throw new IllegalMethodAccessException(MethodResultCode.TYPE_UNMATCHED);
    }

    this.identification = paramList.get(0).getValue();

    this.expectedImageSize = paramList.get(1).getValue();

    final Long imageBlockSizeValue = this.getImageBlockSize().getValue();
    this.blocksExpected = this.expectedImageSize / imageBlockSizeValue;
    if (this.expectedImageSize % imageBlockSizeValue != 0) {
      this.blocksExpected++;
    }

    this.blocksReceived.clear();
    this.setImageFirstNotTransferredBlockNumber();

    this.setImageTransferStatus(
        DataObject.newEnumerateData(ImageTransferStatusType.IMAGE_TRANSFER_INITIATED.value()));
  }

  @CosemMethod(id = METHOD_ID_IMAGE_BLOCK_TRANSFER, consumes = Type.STRUCTURE)
  public void imageBlockTransfer(final DataObject params) throws IllegalMethodAccessException {
    this.checkImageTransferEnabled();

    final List<DataObject> paramList = params.getValue();

    if (paramList.get(0).getType() != Type.DOUBLE_LONG_UNSIGNED) {
      throw new IllegalMethodAccessException(MethodResultCode.TYPE_UNMATCHED);
    }
    if (paramList.get(1).getType() != Type.OCTET_STRING) {
      throw new IllegalMethodAccessException(MethodResultCode.TYPE_UNMATCHED);
    }

    final Long blockNumber = paramList.get(0).getValue();
    if (blockNumber == 0L) {
      final byte[] blockData = paramList.get(1).getValue();
      this.signature = Arrays.copyOf(blockData, 4);
    }

    final double d = Math.random();
    if (d < (1 - this.transferFailureChance)) {
      this.blocksReceived.add(blockNumber);
    }

    this.setImageFirstNotTransferredBlockNumber();
  }

  @CosemMethod(id = METHOD_ID_IMAGE_VERIFY, consumes = Type.INTEGER)
  public void imageVerify(final DataObject param) throws IllegalMethodAccessException {
    this.setImageToActivateInfo();

    this.setImageTransferStatus(
        DataObject.newEnumerateData(ImageTransferStatusType.IMAGE_VERIFICATION_INITIATED.value()));

    this.verify();

    if (this.verified) {
      return;
    }

    throw new IllegalMethodAccessException(MethodResultCode.OTHER_REASON);
  }

  @CosemMethod(id = METHOD_ID_IMAGE_ACTIVATE, consumes = Type.INTEGER)
  public void imageActivate(final DataObject param) throws IllegalMethodAccessException {
    this.verify();
    if (!this.verified) {
      throw new IllegalMethodAccessException(MethodResultCode.OTHER_REASON);
    }

    this.setImageTransferStatus(
        DataObject.newEnumerateData(ImageTransferStatusType.IMAGE_ACTIVATION_INITIATED.value()));

    // Delayed change of image transfer status.
    new Timer()
        .schedule(
            new TimerTask() {
              @Override
              public void run() {
                ImageTransfer.this.setImageTransferStatus(
                    DataObject.newEnumerateData(
                        ImageTransferStatusType.IMAGE_ACTIVATION_SUCCESSFUL.value()));
              }
            },
            this.activationStatusChangeDelay);

    throw new IllegalMethodAccessException(MethodResultCode.TEMPORARY_FAILURE);
  }

  public DataObject getImageBlockSize() {
    return this.dynamicValues.getDlmsAttributeValue(this, ATTRIBUTE_ID_IMAGE_BLOCK_SIZE);
  }

  public void setImageBlockSize(final DataObject attributeValue) {
    this.dynamicValues.setDlmsAttributeValue(this, ATTRIBUTE_ID_IMAGE_BLOCK_SIZE, attributeValue);
  }

  public void setImageTransferredBlockStatus(final DataObject attributeValue) {
    this.dynamicValues.setDlmsAttributeValue(this, ATTRIBUTE_ID_IMAGE_BLOCK_SIZE, attributeValue);
  }

  public DataObject getImageFirstNotTransferredBlockNumber() {
    return this.dynamicValues.getDlmsAttributeValue(
        this, ATTRIBUTE_ID_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER);
  }

  public void setImageFirstNotTransferredBlockNumber(final DataObject attributeValue) {
    this.dynamicValues.setDlmsAttributeValue(
        this, ATTRIBUTE_ID_IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER, attributeValue);
  }

  public DataObject getImageTransferEnabled() {
    return this.dynamicValues.getDlmsAttributeValue(this, ATTRIBUTE_ID_IMAGE_TRANSFER_ENABLED);
  }

  public void setImageTransferEnabled(final DataObject attributeValue) {
    this.dynamicValues.setDlmsAttributeValue(
        this, ATTRIBUTE_ID_IMAGE_TRANSFER_ENABLED, attributeValue);
  }

  public DataObject getImageTransferStatus() {
    return this.dynamicValues.getDlmsAttributeValue(this, ATTRIBUTE_ID_IMAGE_TRANSFER_STATUS);
  }

  public void setImageTransferStatus(final DataObject attributeValue) {
    this.dynamicValues.setDlmsAttributeValue(
        this, ATTRIBUTE_ID_IMAGE_TRANSFER_STATUS, attributeValue);
  }

  public DataObject getImageToActivateInfo() {
    return this.dynamicValues.getDlmsAttributeValue(this, ATTRIBUTE_ID_IMAGE_TO_ACTIVATE_INFO);
  }

  public void setImageToActivateInfo(final DataObject attributeValue) {
    this.dynamicValues.setDlmsAttributeValue(
        this, ATTRIBUTE_ID_IMAGE_TO_ACTIVATE_INFO, attributeValue);
  }
}
