// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils;

import java.util.Optional;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.objectconfig.Attribute;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.springframework.stereotype.Component;

@Component
/**
 * Is an intermediate class to get acces from the dlms-protocol-adapter to the ObjectConfigService.
 * It contains helper functions to lookup CosemObjects and Attributes.
 */
public class ObjectConfigServiceHelper {

  private static final int DEFAULT_ATTRIBUTE_ID = 2;

  private final ObjectConfigService objectConfigService;

  @SuppressWarnings("java:S1144")
  /* Suppress warning java:S1144 because it's injected with spring autowired. */
  private ObjectConfigServiceHelper(final ObjectConfigService objectConfigService) {
    this.objectConfigService = objectConfigService;
  }

  /**
   * Find an Attribute from the ObjectConfigService based on the protocol and protocolVersion and a
   * DlmsObjectType name. When not found the Optional.empty is returned.
   *
   * @param protocol protocol like DSMR or SMR 5.5
   * @param dlmsObjectType the DlmsObjectType to find
   * @return Optional<AttributeAddress> when found it returns a newly created AttributeAddress or
   *     else Optional.empty()
   */
  public Optional<AttributeAddress> findOptionalAttributeAddress(
      final Protocol protocol, final DlmsObjectType dlmsObjectType)
      throws ProtocolAdapterException {

    return this.findOptionalAttributeAddress(protocol, dlmsObjectType, null);
  }

  /**
   * Find an Attribute from the ObjectConfigService based on the protocol and protocolVersion and a
   * DlmsObjectType name. When not found the Optional.empty is returned.
   *
   * @param protocol protocol like SMR 5.5
   * @param dlmsObjectType the DlmsObjectType to find
   * @param channel the channel of the device
   * @return Optional<AttributeAddress> when found it returns a newly created AttributeAddress or
   *     else Optional.empty()
   */
  public Optional<AttributeAddress> findOptionalAttributeAddress(
      final Protocol protocol, final DlmsObjectType dlmsObjectType, final Integer channel)
      throws ProtocolAdapterException {

    final Optional<CosemObject> optObject =
        this.getOptionalCosemObject(protocol.getName(), protocol.getVersion(), dlmsObjectType);
    if (optObject.isEmpty()) {
      return Optional.empty();
    }

    final CosemObject cosemObject = optObject.get();
    final int classId = cosemObject.getClassId();
    final ObisCode obisCode = this.replaceChannel(cosemObject.getObis(), channel);

    final Optional<Attribute> attributeOpt =
        Optional.ofNullable(cosemObject.getAttribute(DEFAULT_ATTRIBUTE_ID));

    return attributeOpt.map(value -> new AttributeAddress(classId, obisCode, value.getId()));
  }

  private Optional<CosemObject> getOptionalCosemObject(
      final String protocol, final String protocolVersion, final DlmsObjectType objectType)
      throws ProtocolAdapterException {

    try {
      return this.objectConfigService.getOptionalCosemObject(
          protocol, protocolVersion, translateDlmsObjectType(objectType));
    } catch (final ObjectConfigException e) {
      return Optional.empty();
    }
  }

  /**
   * Find an Attribute from the ObjectConfigService based on the protocol and protocolVersion and a
   * DlmsObjectType name. When not found a ProtocolAdapterException is thrown.
   *
   * @param dlmsDevice The device to find the object for
   * @param protocol protocol like SMR 5.5
   * @param dlmsObjectType the DlmsObjectType to find
   * @param channel the channel of the device
   * @return AttributeAddress when found it returns a newly created AttributeAddress or else a
   *     ProtocolAdapterException is thrown
   */
  public AttributeAddress findAttributeAddress(
      final DlmsDevice dlmsDevice,
      final Protocol protocol,
      final DlmsObjectType dlmsObjectType,
      final Integer channel)
      throws ProtocolAdapterException {

    final Optional<AttributeAddress> attributeAddressOpt =
        this.findOptionalAttributeAddress(protocol, dlmsObjectType, channel);

    return attributeAddressOpt.orElseThrow(
        () -> {
          final String message =
              String.format(
                  "Did not find %s object for device %s for channel %s",
                  dlmsObjectType, dlmsDevice.getDeviceId(), channel);
          return new ProtocolAdapterException(message);
        });
  }

  private static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType translateDlmsObjectType(
      final DlmsObjectType objectType) throws ProtocolAdapterException {
    try {
      return org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.valueOf(objectType.name());
    } catch (final IllegalArgumentException e) {
      final String message =
          String.format("Cannot translate the DlmsObjectType with name %s", objectType);
      throw new ProtocolAdapterException(message, e);
    }
  }

  private ObisCode replaceChannel(String obisCode, final Integer channel) {
    if (channel != null) {
      obisCode = obisCode.replace("x", channel.toString());
    }

    return new ObisCode(obisCode);
  }
}
