// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import org.opensmartgridplatform.dto.testutil.DateBuilder;

/** Creates instances, for testing purposes only. */
public class SmartMeteringDeviceDtoBuilder {
  private static int counter = 0;

  public SmartMeteringDeviceDto build() {
    counter += 1;
    final SmartMeteringDeviceDto dto = new SmartMeteringDeviceDto();
    dto.setAuthenticationKey(("authenticationKey" + counter).getBytes());
    dto.setCommunicationMethod("communicationMethod" + counter);
    dto.setCommunicationProvider("communicationProvider" + counter);
    dto.setDeliveryDate(new DateBuilder().build());
    dto.setDeviceIdentification("deviceIdentification" + counter);
    dto.setDeviceType("deviceType" + counter);
    dto.setProtocolName("protocolName" + counter);
    dto.setProtocolVersion("protocolVersion" + counter);
    dto.setGlobalEncryptionUnicastKey(("globalEncryptionUnicastKey" + counter).getBytes());
    dto.setLls1Active(true);
    dto.setHls3Active(true);
    dto.setHls4Active(true);
    dto.setHls5Active(true);
    dto.setIccId("ICCId" + counter);
    dto.setMasterKey(("masterKey" + counter).getBytes());
    dto.setMbusIdentificationNumber(String.valueOf(1000L + counter));
    dto.setMbusManufacturerIdentification("mbusManufacturerIdentification" + counter);
    dto.setMbusVersion((short) counter);
    dto.setMbusDeviceTypeIdentification((short) (100 + counter));
    dto.setMbusDefaultKey(("defaultKey" + counter).getBytes());
    dto.setSupplier("supplier" + counter);
    dto.setPolyphase(true);
    dto.setPort(4000L);
    dto.setChallengeLength(15);
    dto.setIpAddressIsStatic(false);
    dto.setWithListMax(32);
    dto.setSelectiveAccessSupported(false);
    return dto;
  }
}
