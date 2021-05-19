package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.imagedata;

import java.nio.ByteBuffer;
import lombok.Getter;
import lombok.Setter;
import org.bouncycastle.util.Arrays;

@Getter
@Setter
public class FirmwareImageDataHeader {

  private byte[] firmwareImageMagicNumber;
  private byte[] headerVersion;
  private byte[] headerLength;
  private byte[] firmwareImageVersion;
  private byte[] firmwareImageLength;
  private byte[] securityLength;
  private byte[] securityType;
  private byte[] addressType;
  private byte[] addressLength;
  private FirmwareImageDataHeaderAddressField firmwareImageDataHeaderAddressField;
  private byte[] activationType;
  private byte[] activationTime;

  Integer getTypeInt() {
    return this.toInteger(this.getFirmwareImageDataHeaderAddressField().getType());
  }

  Integer getVersionInt() {
    return this.toInteger(this.getFirmwareImageDataHeaderAddressField().getVersion());
  }

  String getMftHex() {
    return this.toHex(this.getFirmwareImageDataHeaderAddressField().getMft());
  }

  String getActivationTimeHex() {
    return this.toHex(this.activationTime);
  }

  Integer getActivationTypeInt() {
    return this.toInteger(this.activationType);
  }

  public Integer getAddressLengthInt() {
    return this.toInteger(Arrays.reverse(this.addressLength));
  }

  public Integer getAddressTypeInt() {
    return this.toInteger(this.addressType);
  }

  Integer getSecurityTypeInt() {
    return this.toInteger(this.securityType);
  }

  public Integer getSecurityLengthInt() {
    return this.toInteger(Arrays.reverse(this.securityLength));
  }

  String getFirmwareImageLengthHex() {
    return this.toHex(this.firmwareImageLength);
  }

  Integer getFirmwareImageLengthInt() {
    return this.toInteger2(Arrays.reverse(this.firmwareImageLength));
  }

  String getFirmwareImageVersionHex() {
    return this.toHex(this.firmwareImageVersion);
  }

  String getHeaderLengthHex() {
    return this.toHex(this.headerLength);
  }

  public Integer getHeaderLengthInt() {
    return this.toInteger(Arrays.reverse(this.headerLength));
  }

  Integer getHeaderVersionInt() {
    return this.toInteger(this.headerVersion);
  }

  public String getFirmwareImageMagicNumberHex() {
    return this.toHex(this.firmwareImageMagicNumber);
  }

  String getIdentificationNumber() {
    return this.toHex(this.getFirmwareImageDataHeaderAddressField().getId());
  }

  @Override
  public String toString() {
    final StringBuilder result = new StringBuilder();
    result.append("FirmwareImageDataHeader");
    result.append(
        "\n\tfirmwareImageMagicNumber: " + this.getFirmwareImageMagicNumberHex() + " (hex)");
    result.append("\n\theaderVersion: " + this.getHeaderVersionInt() + " (int)");
    result.append(
        "\n\theaderLength : "
            + this.getHeaderLengthHex()
            + " (hex) --> "
            + this.getHeaderLengthInt()
            + " (int)");
    result.append("\n\tfirmwareImageVersion : " + this.getFirmwareImageVersionHex() + " (hex)");
    result.append(
        "\n\tfirmwareImageLength : "
            + this.getFirmwareImageLengthHex()
            + " (hex) --> "
            + this.getFirmwareImageLengthInt()
            + " (int)");
    result.append("\n\tsecurityLength : " + this.getSecurityLengthInt() + " (int)");
    result.append("\n\tsecurityType: " + this.getSecurityTypeInt() + " (int)");
    result.append("\n\taddressType: " + this.getAddressTypeInt() + " (int)");
    result.append("\n\taddressLength: " + this.getAddressLengthInt() + " (int)");
    result.append("\n\tFirmwareHeaderAddressField");
    result.append("\n\t\tmft: " + this.getMftHex() + " (hex)");
    result.append("\n\t\tid: " + this.getIdentificationNumber() + " (hex)");
    result.append("\n\t\tversion: " + this.getVersionInt() + " (int)");
    result.append("\n\t\ttype: " + this.getTypeInt() + " (int)");
    result.append("\n\tactivationType: " + this.getActivationTypeInt() + " (int)");
    result.append("\n\tactivationTime: " + this.getActivationTimeHex() + " (hex)");
    return result.toString();
  }

  private String toHex(final byte[] bytes) {
    final StringBuilder result = new StringBuilder();
    for (int i = 0; i < bytes.length; i++) {
      String hex = Integer.toHexString(bytes[i] & 0xff);
      if (hex.length() % 2 == 1) {
        hex = "0" + hex;
      }
      result.append(hex);
    }
    return result.toString();
  }

  private Integer toInteger(final byte[] bytes) {
    return Integer.parseInt(this.toHex(bytes), 16);
  }

  private Integer toInteger2(final byte[] bytes) {
    return ByteBuffer.wrap(bytes).getInt();
  }
}
