package org.opensmartgridplatform.adapter.protocol.dlms.exceptions;

public class DeviceKeyProcessAlreadyRunningException extends Exception {

  private static final long serialVersionUID = 1998438538193678335L;

  public DeviceKeyProcessAlreadyRunningException() {
    super();
  }

  public DeviceKeyProcessAlreadyRunningException(final String message) {
    super(message);
  }
}
