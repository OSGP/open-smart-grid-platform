// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils;

import java.util.Optional;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.objectconfig.Attribute;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.springframework.stereotype.Component;

@Component
public class ObjectConfigServiceHelper {

  private final ObjectConfigService objectConfigService;

  private ObjectConfigServiceHelper(final ObjectConfigService objectConfigService) {
    this.objectConfigService = objectConfigService;
  }

  public Optional<AttributeAddress> findAttributeAddress(
      final DlmsDevice device, final DlmsObjectType type, final int attributeId)
      throws ProtocolAdapterException {

    final Optional<CosemObject> optObject = this.getObject(device, type);
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

  private Optional<CosemObject> getObject(final DlmsDevice device, final DlmsObjectType objectType)
      throws ProtocolAdapterException {
    try {
      return Optional.ofNullable(
          this.objectConfigService.getCosemObject(
              device.getProtocolName(),
              device.getProtocolVersion(),
              org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.valueOf(
                  objectType.name())));
    } catch (final ObjectConfigException e) {
      final String message =
          String.format(
              "Cannot read CosemObject for protocol %s, version %s and DlsmObjectType %s",
              device.getProtocolName(), device.getProtocolVersion(), objectType.name());
      throw new ProtocolAdapterException(message, e);
    }
  }
}
