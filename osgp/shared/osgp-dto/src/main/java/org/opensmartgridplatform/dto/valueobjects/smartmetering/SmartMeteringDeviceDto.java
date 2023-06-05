// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class SmartMeteringDeviceDto implements Serializable {

  private static final long serialVersionUID = -6133164707489276802L;

  private String deviceIdentification;

  private String deviceType;

  private String communicationMethod;

  private String communicationProvider;

  private String iccId;

  private String protocolName;

  private String protocolVersion;

  private byte[] masterKey;

  private byte[] globalEncryptionUnicastKey;

  private byte[] authenticationKey;

  private String supplier;

  private boolean hls3Active;

  private boolean hls4Active;

  private boolean hls5Active;

  private Date deliveryDate;

  private String mbusIdentificationNumber;

  private String mbusManufacturerIdentification;

  private Short mbusVersion;

  private Short mbusDeviceTypeIdentification;

  private byte[] mbusDefaultKey;
  private byte[] mbusUserKey;
  private byte[] mbusFirmwareUpdateAuthenticationKey;
  private byte[] mbusP0Key;

  private String timezone;

  private boolean polyphase;
  private Long port;
  private Integer challengeLength;
  private boolean ipAddressIsStatic;
  private boolean withListSupported;
  private boolean selectiveAccessSupported;
}
