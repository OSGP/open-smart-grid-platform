// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils;

import java.util.List;
import java.util.Optional;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SelectiveAccessDescription;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.objectconfig.Attribute;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
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

  public ObjectConfigServiceHelper(final ObjectConfigService objectConfigService) {
    this.objectConfigService = objectConfigService;
  }

  /**
   * Find an optional attribute from the ObjectConfigService based on the protocol and
   * protocolVersion, DlmsObjectType name and channel. When not found the Optional empty is
   * returned.
   *
   * @param protocol protocol like DSMR or SMR 5.5
   * @param dlmsObjectType the DlmsObjectType to find
   * @param channel the channel of the device to access
   * @return Optional<AttributeAddress> when found it returns a newly created AttributeAddress or
   *     else Optional.empty()
   */
  public Optional<AttributeAddress> findOptionalDefaultAttributeAddress(
      final Protocol protocol, final DlmsObjectType dlmsObjectType, final Integer channel) {

    return this.findOptionalAttributeAddress(
        protocol, dlmsObjectType, channel, DEFAULT_ATTRIBUTE_ID);
  }

  /**
   * Find an optional attribute from the ObjectConfigService based on the protocol and
   * protocolVersion, DlmsObjectType name and channel. When not found the Optional empty is
   * returned.
   *
   * @param protocol protocol like DSMR or SMR 5.5
   * @param dlmsObjectType the DlmsObjectType to find
   * @param selectiveAccessDescription selectiveAccessDescription used to find
   * @return Optional<AttributeAddress> when found it returns a newly created AttributeAddress or
   *     else Optional.empty()
   */
  public Optional<AttributeAddress> findOptionalDefaultAttributeAddress(
      final Protocol protocol,
      final DlmsObjectType dlmsObjectType,
      final SelectiveAccessDescription selectiveAccessDescription) {

    return this.findOptionalAttributeAddress(
        protocol, dlmsObjectType, (Integer) null, DEFAULT_ATTRIBUTE_ID, selectiveAccessDescription);
  }

  /**
   * Find an optional default attribute from the ObjectConfigService based on the protocol and
   * protocolVersion and a DlmsObjectType name. When not found the Optional empty is returned.
   *
   * @param protocol protocol like DSMR or SMR 5.5
   * @param dlmsObjectType the DlmsObjectType to find
   * @return Optional<AttributeAddress> when found it returns a newly created AttributeAddress or
   *     else Optional.empty()
   */
  public Optional<AttributeAddress> findOptionalDefaultAttributeAddress(
      final Protocol protocol, final DlmsObjectType dlmsObjectType) {

    return this.findOptionalAttributeAddress(protocol, dlmsObjectType, null, DEFAULT_ATTRIBUTE_ID);
  }

  /**
   * Find an optional attribute from the ObjectConfigService based on the protocol and
   * protocolVersion , DlmsObjectType name, channel and attributeId. When not found the Optional
   * empty is returned.
   *
   * @param protocol protocol like SMR 5.5
   * @param dlmsObjectType the DlmsObjectType to find
   * @param channel the channel of the device to access
   * @param attributeId the attributeId to find
   * @return Optional<AttributeAddress> when found it returns a newly created AttributeAddress or
   *     else Optional.empty()
   */
  public Optional<AttributeAddress> findOptionalAttributeAddress(
      final Protocol protocol,
      final DlmsObjectType dlmsObjectType,
      final Integer channel,
      final int attributeId) {
    return this.findOptionalAttributeAddress(
        protocol, dlmsObjectType, channel, attributeId, (SelectiveAccessDescription) null);
  }

  /**
   * Find an optional attribute from the ObjectConfigService based on the protocol and
   * protocolVersion , DlmsObjectType name, channel and attributeId. When not found the Optional
   * empty is returned.
   *
   * @param protocol protocol like SMR 5.5
   * @param dlmsObjectType the DlmsObjectType to find
   * @param channel the channel of the device to access
   * @param attributeId the attributeId to find
   * @param selectiveAccessDescription selectiveAccessDescription used to find
   * @return Optional<AttributeAddress> when found it returns a newly created AttributeAddress or
   *     else Optional.empty()
   */
  public Optional<AttributeAddress> findOptionalAttributeAddress(
      final Protocol protocol,
      final DlmsObjectType dlmsObjectType,
      final Integer channel,
      final int attributeId,
      final SelectiveAccessDescription selectiveAccessDescription) {

    final Optional<CosemObject> optObject =
        this.getOptionalCosemObject(protocol.getName(), protocol.getVersion(), dlmsObjectType);
    if (optObject.isEmpty()) {
      return Optional.empty();
    }

    final CosemObject cosemObject = optObject.get();
    final int classId = cosemObject.getClassId();

    final Optional<Attribute> attributeOpt =
        Optional.ofNullable(cosemObject.getAttribute(attributeId));
    if (selectiveAccessDescription == null) {
      final ObisCode obisCode = this.createObisCode(cosemObject.getObis(), channel);
      return attributeOpt.map(value -> new AttributeAddress(classId, obisCode, value.getId()));
    } else {
      final String obisCode = this.replaceChannel(cosemObject.getObis(), channel);
      return attributeOpt.map(
          value ->
              new AttributeAddress(classId, obisCode, value.getId(), selectiveAccessDescription));
    }
  }

  public List<AttributeAddress> findDefaultAttributeAddressesIgnoringMissingTypes(
      final Protocol protocol, final List<DlmsObjectType> dlmsObjectTypes) {
    try {
      final List<CosemObject> cosemObjects =
          this.objectConfigService.getCosemObjectsIgnoringMissingTypes(
              protocol.getName(), protocol.getVersion(), dlmsObjectTypes);

      return cosemObjects.stream()
          .map(
              object ->
                  new AttributeAddress(object.getClassId(), object.getObis(), DEFAULT_ATTRIBUTE_ID))
          .toList();
    } catch (final ObjectConfigException e) {
      return List.of();
    }
  }

  private Optional<CosemObject> getOptionalCosemObject(
      final String protocol, final String protocolVersion, final DlmsObjectType objectType) {

    try {
      return this.objectConfigService.getOptionalCosemObject(protocol, protocolVersion, objectType);
    } catch (final ObjectConfigException e) {
      return Optional.empty();
    }
  }

  /**
   * Find a required default attribute from the ObjectConfigService based on the protocol and
   * protocolVersion and a DlmsObjectType name. When not found a ProtocolAdapterException is thrown.
   *
   * @param dlmsDevice The device to find the object for
   * @param protocol protocol like SMR 5.5
   * @param dlmsObjectType the DlmsObjectType to find
   * @param channel the channel of the device to access
   * @return AttributeAddress when found it returns a newly created AttributeAddress or else a
   *     ProtocolAdapterException is thrown
   */
  public AttributeAddress findDefaultAttributeAddress(
      final DlmsDevice dlmsDevice,
      final Protocol protocol,
      final DlmsObjectType dlmsObjectType,
      final Integer channel)
      throws ProtocolAdapterException {

    return this.findAttributeAddress(
        dlmsDevice, protocol, dlmsObjectType, channel, DEFAULT_ATTRIBUTE_ID);
  }

  /**
   * Find a required attribute from the ObjectConfigService based on the protocol and
   * protocolVersion and a DlmsObjectType name. When not found a ProtocolAdapterException is thrown.
   *
   * @param dlmsDevice The device to find the object for
   * @param protocol protocol like SMR 5.5
   * @param dlmsObjectType the DlmsObjectType to find
   * @param channel the channel of the device to access
   * @param attributeId the attributeId to find
   * @return AttributeAddress when found it returns a newly created AttributeAddress or else a
   *     ProtocolAdapterException is thrown
   */
  public AttributeAddress findAttributeAddress(
      final DlmsDevice dlmsDevice,
      final Protocol protocol,
      final DlmsObjectType dlmsObjectType,
      final Integer channel,
      final int attributeId)
      throws ProtocolAdapterException {

    final Optional<AttributeAddress> attributeAddressOpt =
        this.findOptionalAttributeAddress(protocol, dlmsObjectType, channel, attributeId);

    return attributeAddressOpt.orElseThrow(
        () -> {
          final String message =
              String.format(
                  "Did not find %s object for device %s for channel %s",
                  dlmsObjectType, dlmsDevice.getDeviceId(), channel);
          return new ProtocolAdapterException(message);
        });
  }

  private ObisCode createObisCode(final String obisCode, final Integer channel) {
    return new ObisCode(this.replaceChannel(obisCode, channel));
  }

  private String replaceChannel(String obisCode, final Integer channel) {
    if (channel != null) {
      obisCode = obisCode.replace("x", channel.toString());
    }
    return obisCode;
  }
}
