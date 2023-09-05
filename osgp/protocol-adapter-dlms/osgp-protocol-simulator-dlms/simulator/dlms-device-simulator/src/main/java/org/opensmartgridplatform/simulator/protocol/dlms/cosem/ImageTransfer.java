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

@CosemClass(id = 18)
public class ImageTransfer extends CosemInterfaceObject {

  @CosemAttribute(
      id = 2,
      type = Type.DOUBLE_LONG_UNSIGNED,
      accessMode = AttributeAccessMode.READ_ONLY)
  private final DataObject imageBlockSize;

  @CosemAttribute(id = 3, type = Type.BIT_STRING, accessMode = AttributeAccessMode.READ_ONLY)
  private final DataObject imageTransferredBlockStatus;

  @CosemAttribute(
      id = 4,
      type = Type.DOUBLE_LONG_UNSIGNED,
      accessMode = AttributeAccessMode.READ_ONLY)
  private DataObject imageFirstNotTransferredBlockNumber;

  @CosemAttribute(id = 5, type = Type.BOOLEAN, accessMode = AttributeAccessMode.READ_AND_WRITE)
  private final DataObject imageTransferEnabled;

  @CosemAttribute(id = 6, type = Type.ENUMERATE, accessMode = AttributeAccessMode.READ_ONLY)
  private DataObject imageTransferStatus;

  @CosemAttribute(id = 7, type = Type.ARRAY, accessMode = AttributeAccessMode.READ_ONLY)
  private DataObject imageToActivateInfo;

  private final int imageBlockSizeValue;

  private final int activationStatusChangeDelay;

  private final double transferFailureChance;

  private long blocksExpected = 0L;

  private final Set<Long> blocksReceived = new TreeSet<>();

  private boolean verified = false;

  private long expectedImageSize;

  private byte[] identification;

  private byte[] signature;

  public ImageTransfer(
      final int imageBlockSizeValue,
      final int activationStatusChangeDelay,
      final double transferFailureChance) {
    super("0.0.44.0.0.255");

    this.imageBlockSizeValue = imageBlockSizeValue;
    this.activationStatusChangeDelay = activationStatusChangeDelay;
    this.transferFailureChance = transferFailureChance;
    this.imageBlockSize = DataObject.newUInteger32Data(this.imageBlockSizeValue);
    this.imageTransferredBlockStatus = DataObject.newNullData();
    this.imageFirstNotTransferredBlockNumber = DataObject.newUInteger32Data(0);
    this.imageTransferEnabled = DataObject.newBoolData(false);

    this.imageTransferStatus =
        DataObject.newEnumerateData(ImageTransferStatusType.IMAGE_TRANSFER_NOT_INITIATED.value());
  }

  private void checkImageTransferEnabled() throws IllegalMethodAccessException {
    if (!(boolean) this.imageTransferEnabled.getValue()) {
      throw new IllegalMethodAccessException(MethodResultCode.OTHER_REASON);
    }
  }

  private void setImageFirstNotTransferredBlockNumber() {
    for (int i = 0; i < this.blocksExpected; i++) {
      if (!this.blocksReceived.contains((long) i)) {
        this.imageFirstNotTransferredBlockNumber = DataObject.newUInteger32Data(i);
        return;
      }
    }

    this.imageFirstNotTransferredBlockNumber = DataObject.newUInteger32Data(this.blocksExpected);
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
    this.imageToActivateInfo = DataObject.newArrayData(Arrays.asList(imageInfo));
  }

  private void verify() {
    final boolean corrupt =
        new String(this.identification, StandardCharsets.UTF_8).endsWith(".corrupt");

    this.verified = !corrupt && this.blocksExpected == this.blocksReceived.size();
    if (this.verified) {
      this.imageTransferStatus =
          DataObject.newEnumerateData(
              ImageTransferStatusType.IMAGE_VERIFICATION_SUCCESSFUL.value());
    } else {
      this.imageTransferStatus =
          DataObject.newEnumerateData(ImageTransferStatusType.IMAGE_VERIFICATION_FAILED.value());
    }
  }

  public DataObject getImageTransferredBlockStatus() {
    if (this.blocksExpected == 0L) {
      return DataObject.newBitStringData(new BitString(new byte[0], 0));
    }

    final BitSet bitSet = new BitSet();

    final long bitsExpected = this.blocksExpected * (this.imageBlockSizeValue * 8);
    for (int i = 0; i < bitsExpected; i++) {
      bitSet.set(i, this.blocksReceived.contains((long) i));
    }

    final byte[] byteData = bitSet.toByteArray();
    return DataObject.newBitStringData(new BitString(byteData, byteData.length * 8));
  }

  @CosemMethod(id = 1, consumes = Type.STRUCTURE)
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

    this.blocksExpected = this.expectedImageSize / this.imageBlockSizeValue;
    if ((Long) paramList.get(1).getValue() % this.imageBlockSizeValue != 0) {
      this.blocksExpected++;
    }

    this.blocksReceived.clear();
    this.setImageFirstNotTransferredBlockNumber();

    this.imageTransferStatus =
        DataObject.newEnumerateData(ImageTransferStatusType.IMAGE_TRANSFER_INITIATED.value());
  }

  @CosemMethod(id = 2, consumes = Type.STRUCTURE)
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

  @CosemMethod(id = 3, consumes = Type.INTEGER)
  public void imageVerify(final DataObject param) throws IllegalMethodAccessException {
    this.setImageToActivateInfo();

    this.imageTransferStatus =
        DataObject.newEnumerateData(ImageTransferStatusType.IMAGE_VERIFICATION_INITIATED.value());

    this.verify();

    if (this.verified) {
      return;
    }

    throw new IllegalMethodAccessException(MethodResultCode.OTHER_REASON);
  }

  @CosemMethod(id = 4, consumes = Type.INTEGER)
  public void imageActivate(final DataObject param) throws IllegalMethodAccessException {
    this.verify();
    if (!this.verified) {
      throw new IllegalMethodAccessException(MethodResultCode.OTHER_REASON);
    }

    this.imageTransferStatus =
        DataObject.newEnumerateData(ImageTransferStatusType.IMAGE_ACTIVATION_INITIATED.value());

    // Delayed change of image transfer status.
    new Timer()
        .schedule(
            new TimerTask() {
              @Override
              public void run() {
                ImageTransfer.this.imageTransferStatus =
                    DataObject.newEnumerateData(
                        ImageTransferStatusType.IMAGE_ACTIVATION_SUCCESSFUL.value());
              }
            },
            this.activationStatusChangeDelay);

    throw new IllegalMethodAccessException(MethodResultCode.TEMPORARY_FAILURE);
  }
}
