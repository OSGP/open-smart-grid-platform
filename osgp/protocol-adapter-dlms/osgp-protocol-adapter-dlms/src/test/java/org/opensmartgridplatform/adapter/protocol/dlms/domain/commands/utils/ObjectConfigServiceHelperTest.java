// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.MBUS_CLIENT_SETUP;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.MBUS_DIAGNOSTIC;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AttributeAddress;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.objectconfig.Attribute;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;

@ExtendWith(MockitoExtension.class)
class ObjectConfigServiceHelperTest {

  private static final String OBIS = "0.1.24.1.0.255";
  private static final String OBIS2 = "0.1.24.2.0.255";

  @Mock private ObjectConfigService objectConfigService;
  @Mock private DlmsDevice dlmsDevice;

  @Mock private Attribute attribute;

  @Mock CosemObject cosemObject;
  @Mock CosemObject cosemObject2;

  @InjectMocks private ObjectConfigServiceHelper objectConfigServiceHelper;

  @Test
  void findDefaultAttributeAddressShouldReturnAnObject()
      throws ProtocolAdapterException, ObjectConfigException {

    when(this.attribute.getId()).thenReturn(2);
    when(this.cosemObject.getObis()).thenReturn(OBIS);
    when(this.cosemObject.getAttribute(2)).thenReturn(this.attribute);

    when(this.objectConfigService.getOptionalCosemObject(
            Protocol.SMR_5_1.getName(), Protocol.SMR_5_1.getVersion(), MBUS_CLIENT_SETUP))
        .thenReturn(Optional.of(this.cosemObject));

    final AttributeAddress attributeAddress =
        this.objectConfigServiceHelper.findDefaultAttributeAddress(
            this.dlmsDevice, Protocol.SMR_5_1, MBUS_CLIENT_SETUP, 1);

    assertAttributeAddress(attributeAddress, OBIS);
  }

  @Test
  void findAttributeAddressShouldReturnAnObject()
      throws ProtocolAdapterException, ObjectConfigException {

    when(this.attribute.getId()).thenReturn(2);
    when(this.cosemObject.getObis()).thenReturn(OBIS);
    when(this.cosemObject.getAttribute(2)).thenReturn(this.attribute);

    when(this.objectConfigService.getOptionalCosemObject(
            Protocol.SMR_5_1.getName(), Protocol.SMR_5_1.getVersion(), MBUS_CLIENT_SETUP))
        .thenReturn(Optional.of(this.cosemObject));

    final AttributeAddress attributeAddress =
        this.objectConfigServiceHelper.findAttributeAddress(
            this.dlmsDevice, Protocol.SMR_5_1, MBUS_CLIENT_SETUP, 1, 2);

    assertAttributeAddress(attributeAddress, OBIS);
  }

  @Test
  void findDefaultAttributeAddressShouldThrowException() throws ObjectConfigException {
    when(this.cosemObject.getObis()).thenReturn(OBIS);
    when(this.dlmsDevice.getDeviceId()).thenReturn(Long.valueOf("28001"));

    when(this.objectConfigService.getOptionalCosemObject(
            Protocol.SMR_5_1.getName(), Protocol.SMR_5_1.getVersion(), MBUS_CLIENT_SETUP))
        .thenReturn(Optional.of(this.cosemObject));

    final ProtocolAdapterException protocolAdapterException =
        assertThrows(
            ProtocolAdapterException.class,
            () ->
                this.objectConfigServiceHelper.findDefaultAttributeAddress(
                    this.dlmsDevice, Protocol.SMR_5_1, MBUS_CLIENT_SETUP, 1));

    assertThat(protocolAdapterException).isNotNull();
    assertThat(protocolAdapterException.getMessage())
        .isEqualTo("Did not find MBUS_CLIENT_SETUP object for device 28001 for channel 1");
  }

  @Test
  void findAttributeAddressShouldThrowException() throws ObjectConfigException {
    when(this.cosemObject.getObis()).thenReturn(OBIS);
    when(this.dlmsDevice.getDeviceId()).thenReturn(Long.valueOf("28001"));

    when(this.objectConfigService.getOptionalCosemObject(
            Protocol.SMR_5_1.getName(), Protocol.SMR_5_1.getVersion(), MBUS_CLIENT_SETUP))
        .thenReturn(Optional.of(this.cosemObject));

    final ProtocolAdapterException protocolAdapterException =
        assertThrows(
            ProtocolAdapterException.class,
            () ->
                this.objectConfigServiceHelper.findAttributeAddress(
                    this.dlmsDevice, Protocol.SMR_5_1, MBUS_CLIENT_SETUP, 1, 2));

    assertThat(protocolAdapterException).isNotNull();
    assertThat(protocolAdapterException.getMessage())
        .isEqualTo("Did not find MBUS_CLIENT_SETUP object for device 28001 for channel 1");
  }

  @Test
  void findOptionalDefaultAttributeAddressShouldReturnAnAttributeAddress()
      throws ObjectConfigException {

    when(this.attribute.getId()).thenReturn(2);
    when(this.cosemObject.getObis()).thenReturn(OBIS);
    when(this.cosemObject.getAttribute(2)).thenReturn(this.attribute);

    when(this.objectConfigService.getOptionalCosemObject(
            Protocol.SMR_5_1.getName(), Protocol.SMR_5_1.getVersion(), MBUS_CLIENT_SETUP))
        .thenReturn(Optional.of(this.cosemObject));

    final Optional<AttributeAddress> attributeAddressOpt =
        this.objectConfigServiceHelper.findOptionalDefaultAttributeAddress(
            Protocol.SMR_5_1, MBUS_CLIENT_SETUP, 1);

    assertThat(attributeAddressOpt).isPresent();
    assertAttributeAddress(attributeAddressOpt.get(), OBIS);
  }

  @Test
  void findOptionalAttributeAddressShouldReturnAnAttributeAddress() throws ObjectConfigException {

    when(this.attribute.getId()).thenReturn(2);
    when(this.cosemObject.getObis()).thenReturn(OBIS);
    when(this.cosemObject.getAttribute(2)).thenReturn(this.attribute);

    when(this.objectConfigService.getOptionalCosemObject(
            Protocol.SMR_5_1.getName(), Protocol.SMR_5_1.getVersion(), MBUS_CLIENT_SETUP))
        .thenReturn(Optional.of(this.cosemObject));

    final Optional<AttributeAddress> attributeAddressOpt =
        this.objectConfigServiceHelper.findOptionalAttributeAddress(
            Protocol.SMR_5_1, MBUS_CLIENT_SETUP, 1, 2);

    assertThat(attributeAddressOpt).isPresent();
    assertAttributeAddress(attributeAddressOpt.get(), OBIS);
  }

  @Test
  void findOptionalDefaultAttributeAddressShouldReturnAEmptyOptional()
      throws ObjectConfigException {

    when(this.objectConfigService.getOptionalCosemObject(
            Protocol.SMR_5_1.getName(), Protocol.SMR_5_1.getVersion(), MBUS_CLIENT_SETUP))
        .thenReturn(Optional.empty());

    final Optional<AttributeAddress> attributeAddressOpt =
        this.objectConfigServiceHelper.findOptionalDefaultAttributeAddress(
            Protocol.SMR_5_1, MBUS_CLIENT_SETUP, 1);

    assertThat(attributeAddressOpt).isEmpty();
  }

  @Test
  void findOptionalAttributeAddressShouldReturnAEmptyOptional() throws ObjectConfigException {

    when(this.objectConfigService.getOptionalCosemObject(
            Protocol.SMR_5_1.getName(), Protocol.SMR_5_1.getVersion(), MBUS_CLIENT_SETUP))
        .thenReturn(Optional.empty());

    final Optional<AttributeAddress> attributeAddressOpt =
        this.objectConfigServiceHelper.findOptionalAttributeAddress(
            Protocol.SMR_5_1, MBUS_CLIENT_SETUP, 1, 2);

    assertThat(attributeAddressOpt).isEmpty();
  }

  @Test
  void findDefaultAttributeAddressesIgnoringMissingTypesShouldReturnObjects()
      throws ObjectConfigException {

    when(this.cosemObject.getObis()).thenReturn(OBIS);
    when(this.cosemObject2.getObis()).thenReturn(OBIS2);

    when(this.objectConfigService.getCosemObjectsIgnoringMissingTypes(
            Protocol.SMR_5_1.getName(),
            Protocol.SMR_5_1.getVersion(),
            List.of(MBUS_CLIENT_SETUP, MBUS_DIAGNOSTIC)))
        .thenReturn(List.of(this.cosemObject, this.cosemObject2));

    final List<AttributeAddress> attributeAddresses =
        this.objectConfigServiceHelper.findDefaultAttributeAddressesIgnoringMissingTypes(
            Protocol.SMR_5_1, List.of(MBUS_CLIENT_SETUP, MBUS_DIAGNOSTIC));

    assertThat(attributeAddresses).hasSize(2);
    assertAttributeAddress(attributeAddresses.get(0), OBIS);
    assertAttributeAddress(attributeAddresses.get(1), OBIS2);
  }

  private static void assertAttributeAddress(
      final AttributeAddress attributeAddress, final String expectedObis) {
    assertThat(attributeAddress).isNotNull();
    assertThat(attributeAddress.getClassId()).as("classId").isZero();
    assertThat(attributeAddress.getId()).as("id").isEqualTo(2);
    assertThat(attributeAddress.getInstanceId()).as("instanceId").hasToString(expectedObis);
  }
}
