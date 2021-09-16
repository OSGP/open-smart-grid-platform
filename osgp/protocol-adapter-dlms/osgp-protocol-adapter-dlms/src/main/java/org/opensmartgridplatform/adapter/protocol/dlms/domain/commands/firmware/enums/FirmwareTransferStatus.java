package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.enums;

import java.util.HashMap;
import java.util.Map;

public enum FirmwareTransferStatus {
  IDLE(0),
  DATA_RECEIVE(1),
  VALIDATING(2),
  VALIDATED(3),
  VALIDATION_FAILED(4),
  ACTIVATING(5),
  ACTIVATED(6),
  ACTIVATION_FAILED(7);

  private final int code;
  private static final Map<Integer, FirmwareTransferStatus> map = new HashMap<>();

  static {
    for (final FirmwareTransferStatus status : FirmwareTransferStatus.values()) {
      map.put(status.code, status);
    }
  }

  private FirmwareTransferStatus(final int code) {
    this.code = code;
  }

  public static FirmwareTransferStatus getByCode(final int code) {
    final FirmwareTransferStatus status = map.get(code);
    if (status == null) {
      throw new IllegalArgumentException(
          String.format("No FirmwareTransferStatus found with code %d (int)", code));
    }
    return status;
  }

  public int getCode() {
    return this.code;
  }
}
