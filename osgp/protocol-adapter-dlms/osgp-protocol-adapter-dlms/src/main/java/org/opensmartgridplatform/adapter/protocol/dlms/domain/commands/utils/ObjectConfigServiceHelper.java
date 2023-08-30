// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils;

import java.util.Optional;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.objectconfig.Attribute;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.springframework.stereotype.Component;

@Component
/*
 * Is an intermediate class to get acces from the dlms-protocol-adapter to the ObjectConfigService.
 * It contains helper functions to lookup CosemObjects and Attributes.
 */
public class ObjectConfigServiceHelper {

  private final ObjectConfigService objectConfigService;

  @SuppressWarnings("java:S1144")
  /* Suppress warning java:S1144 because it's injected with spring autowired. */
  private ObjectConfigServiceHelper(final ObjectConfigService objectConfigService) {
    this.objectConfigService = objectConfigService;
  }

  /*
   * Find an Attribute from the ObjectConfigService based on the protocol and protocolVersion and a
   * DlmsObjectType name. When not found the Optional.empty is returned.
   *
   * @param protocol protocol like DSMR or SMR
   * @param protocolVersion like 4.2.2 or 5.5
   * @param dlmsObjectType the DlmsObjectType to find
   * @param attributeId the attributeId to find
   * @return Optional<AttributeAddress> when found it returns a newly created AttributeAddress of
   *     else Optional.empty()
   */
  public Optional<AttributeAddress> findOptionalAttributeAddress(
      final String protocol,
      final String protocolVersion,
      final DlmsObjectType dlmsObjectType,
      final int attributeId)
      throws ProtocolAdapterException {

    final Optional<CosemObject> optObject =
        this.getOptionalCosemObject(protocol, protocolVersion, dlmsObjectType);
    if (optObject.isEmpty()) {
      return Optional.empty();
    }

    final CosemObject cosemObject = optObject.get();
    final int classId = cosemObject.getClassId();
    final ObisCode obisCode = new ObisCode(cosemObject.getObis());

    final Optional<Attribute> attributeOpt =
        Optional.ofNullable(cosemObject.getAttribute(attributeId));

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
}
