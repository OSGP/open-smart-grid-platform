// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig;

import java.util.ArrayList;
import java.util.List;
import org.openmuc.jdlms.AttributeAddress;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsExtendedRegister;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsRegister;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

public class AttributeAddressForProfile {
  private final AttributeAddress attributeAddress;
  private final List<DlmsCaptureObject> selectedObjects;

  public AttributeAddressForProfile(
      final AttributeAddress attributeAddress, final List<DlmsCaptureObject> selectedObjects) {
    this.attributeAddress = attributeAddress;
    this.selectedObjects = selectedObjects;
  }

  public AttributeAddress getAttributeAddress() {
    return this.attributeAddress;
  }

  List<DlmsCaptureObject> getSelectedObjects() {
    return this.selectedObjects;
  }

  public Integer getIndex(final DlmsObjectType type, final Integer attributeId) {
    int index = 0;

    for (final DlmsCaptureObject object : this.selectedObjects) {
      if (object.getRelatedObject().getType().equals(type)
          && (attributeId == null || object.getAttributeId() == attributeId)) {
        return index;
      }
      index++;
    }

    return null;
  }

  public Integer getIndex(final DlmsObjectType type, final Integer attributeId, final int channel) {
    int index = 0;

    for (final DlmsCaptureObject object : this.selectedObjects) {
      if (object.getRelatedObject().getType().equals(type)
          && (attributeId == null || object.getAttributeId() == attributeId)
          && ((object.getChannel() == null) || (object.getChannel() == channel))) {
        return index;
      }
      index++;
    }

    return null;
  }

  public DlmsCaptureObject getCaptureObject(final DlmsObjectType dlmsObjectType)
      throws ProtocolAdapterException {
    return this.selectedObjects.stream()
        .filter(c -> c.getRelatedObject().getType() == dlmsObjectType)
        .findFirst()
        .orElseThrow(
            () ->
                new ProtocolAdapterException(
                    String.format(
                        "No capture object found for dlms object type %s.",
                        dlmsObjectType.toString())));
  }

  public List<DlmsRegister> getCaptureObjects(
      final Class<? extends DlmsRegister> dlmsObjectClass, final boolean defaultAttributeId) {

    final List<DlmsCaptureObject> captureObjects =
        this.selectedObjects.stream()
            .filter(
                c ->
                    !defaultAttributeId
                        || c.getAttributeId() == c.getRelatedObject().getDefaultAttributeId())
            .toList();

    final List<DlmsRegister> registers = new ArrayList<>();

    for (final DlmsCaptureObject captureObject : captureObjects) {
      final DlmsObject relatedObject = captureObject.getRelatedObject();
      if (dlmsObjectClass.isInstance(relatedObject)) {
        final DlmsRegister originalRegister = (DlmsRegister) relatedObject;
        final String obisCode = originalRegister.getObisCodeAsString();
        if (originalRegister instanceof DlmsExtendedRegister) {
          final Integer channel = captureObject.getChannel();
          final String obisCodeWithChannel =
              channel == null
                  ? obisCode
                  : obisCode.replace("<c>", String.valueOf(captureObject.getChannel()));
          final DlmsExtendedRegister register =
              new DlmsExtendedRegister(
                  originalRegister.getType(),
                  obisCodeWithChannel,
                  originalRegister.getScaler(),
                  originalRegister.getUnit(),
                  originalRegister.getMedium());

          registers.add(register);
        } else {
          final DlmsRegister register =
              new DlmsRegister(
                  originalRegister.getType(),
                  obisCode,
                  originalRegister.getScaler(),
                  originalRegister.getUnit(),
                  originalRegister.getMedium());

          registers.add(register);
        }
      }
    }

    return registers;
  }
}
