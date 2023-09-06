// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AttributeAddress;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.objectconfig.Attribute;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;

@ExtendWith(MockitoExtension.class)
class ObjectConfigServiceHelperTest {

  @Mock private ObjectConfigService objectConfigService;
  @Mock private DlmsDevice dlmsDevice;

  @Mock private Attribute attribute;

  @Mock CosemObject cosemObject;

  @InjectMocks private ObjectConfigServiceHelper objectConfigServiceHelper;

  @Test
  void findAttributeAddressShouldReturnAnObject()
      throws ProtocolAdapterException, ObjectConfigException {

    when(this.attribute.getId()).thenReturn(2);
    when(this.cosemObject.getObis()).thenReturn("0.1.24.1.0.255");
    when(this.cosemObject.getAttribute(2)).thenReturn(this.attribute);

    when(this.objectConfigService.getOptionalCosemObject(
            Protocol.SMR_5_1.getName(),
            Protocol.SMR_5_1.getVersion(),
            org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.MBUS_CLIENT_SETUP))
        .thenReturn(Optional.of(this.cosemObject));

    final AttributeAddress attributeAddress =
        this.objectConfigServiceHelper.findAttributeAddress(
            this.dlmsDevice, Protocol.SMR_5_1, DlmsObjectType.MBUS_CLIENT_SETUP, 1);

    assertAttributeAddress(attributeAddress);
  }

  @Test
  void findAttributeAddressShouldThrowException() throws ObjectConfigException {
    when(this.cosemObject.getObis()).thenReturn("0.1.24.1.0.255");
    when(this.dlmsDevice.getDeviceId()).thenReturn(Long.valueOf("28001"));

    when(this.objectConfigService.getOptionalCosemObject(
            Protocol.SMR_5_1.getName(),
            Protocol.SMR_5_1.getVersion(),
            org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.MBUS_CLIENT_SETUP))
        .thenReturn(Optional.of(this.cosemObject));

    final ProtocolAdapterException protocolAdapterException =
        assertThrows(
            ProtocolAdapterException.class,
            () -> {
              this.objectConfigServiceHelper.findAttributeAddress(
                  this.dlmsDevice, Protocol.SMR_5_1, DlmsObjectType.MBUS_CLIENT_SETUP, 1);
            });

    assertThat(protocolAdapterException).isNotNull();
    assertThat(protocolAdapterException.getMessage())
        .isEqualTo("Did not find MBUS_CLIENT_SETUP object for device 28001 for channel 1");
  }

  @Test
  void findOptionalAttributeAddressShouldReturnAnAttributeAddress()
      throws ObjectConfigException, ProtocolAdapterException {

    when(this.attribute.getId()).thenReturn(2);
    when(this.cosemObject.getObis()).thenReturn("0.1.24.1.0.255");
    when(this.cosemObject.getAttribute(2)).thenReturn(this.attribute);

    when(this.objectConfigService.getOptionalCosemObject(
            Protocol.SMR_5_1.getName(),
            Protocol.SMR_5_1.getVersion(),
            org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.MBUS_CLIENT_SETUP))
        .thenReturn(Optional.of(this.cosemObject));

    final Optional<AttributeAddress> attributeAddressOpt =
        this.objectConfigServiceHelper.findOptionalAttributeAddress(
            Protocol.SMR_5_1, DlmsObjectType.MBUS_CLIENT_SETUP, Integer.valueOf(1));

    assertThat(attributeAddressOpt).isPresent();
    assertAttributeAddress(attributeAddressOpt.get());
  }

  @Test
  void findOptionalAttributeAddressShouldReturnAEmptyOptional()
      throws ObjectConfigException, ProtocolAdapterException {

    when(this.objectConfigService.getOptionalCosemObject(
            Protocol.SMR_5_1.getName(),
            Protocol.SMR_5_1.getVersion(),
            org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.MBUS_CLIENT_SETUP))
        .thenReturn(Optional.empty());

    final Optional<AttributeAddress> attributeAddressOpt =
        this.objectConfigServiceHelper.findOptionalAttributeAddress(
            Protocol.SMR_5_1, DlmsObjectType.MBUS_CLIENT_SETUP, Integer.valueOf(1));

    assertThat(attributeAddressOpt).isEmpty();
  }

  private static void assertAttributeAddress(final AttributeAddress attributeAddress) {
    assertThat(attributeAddress).isNotNull();
    assertThat(attributeAddress.getClassId()).as("classId").isZero();
    assertThat(attributeAddress.getId()).as("id").isEqualTo(2);
    assertThat(attributeAddress.getInstanceId()).as("instanceId").hasToString("0.1.24.1.0.255");
  }
}
