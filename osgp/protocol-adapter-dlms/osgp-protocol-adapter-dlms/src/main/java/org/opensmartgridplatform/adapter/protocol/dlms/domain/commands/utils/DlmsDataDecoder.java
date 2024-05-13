// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils;

import static org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass.interfaceClassFor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeClass;
import org.opensmartgridplatform.dlms.objectconfig.Attribute;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.DlmsDataType;
import org.opensmartgridplatform.dlms.objectconfig.DlmsProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service(value = "dlmsDataDecoder")
public class DlmsDataDecoder {

  private static final List<DataObject.Type> simpleNumbers =
      List.of(
          DataObject.Type.DOUBLE_LONG,
          DataObject.Type.DOUBLE_LONG_UNSIGNED,
          DataObject.Type.INTEGER,
          DataObject.Type.UNSIGNED,
          DataObject.Type.LONG64,
          DataObject.Type.LONG64_UNSIGNED,
          DataObject.Type.LONG_INTEGER,
          DataObject.Type.LONG_UNSIGNED);

  @Autowired private DlmsHelper dlmsHelper;

  public CosemObject decodeObjectData(
      final ObjectListElement objectListElement,
      final List<DataObject> attributeData,
      final DlmsProfile dlmsProfile) {
    final int classId = objectListElement.getClassId();
    final String obisCode = objectListElement.getLogicalName();

    try {
      final CosemObject objectFromProfile =
          this.getCosemObjectByObis(dlmsProfile, obisCode, classId);

      final List<Attribute> attributes = new ArrayList<>();

      int index = 1;
      for (final DataObject dataObject : attributeData) {
        attributes.add(
            this.decodeAttributeData(
                objectFromProfile,
                index,
                dataObject,
                objectListElement.getAttributes().get(index)));
        index++;
      }

      String note =
          (objectFromProfile.getNote() != null && !objectFromProfile.getNote().isBlank())
              ? objectFromProfile.getNote()
              : "";

      if (objectFromProfile.getTag() != null
          && objectFromProfile.getAttributes().size() + 1
              != objectListElement.getAttributes().size()) {
        note =
            note
                + "\nRead number of attributes "
                + objectListElement.getAttributes().size()
                + " differs from number of attributes in profile "
                + objectFromProfile.getAttributes().size()
                + 1;
      }

      if (objectFromProfile.getTag() != null && classId != objectFromProfile.getClassId()) {
        note =
            note
                + "\nRead classId "
                + classId
                + " differs from classId in profile "
                + objectFromProfile.getClassId();
      }

      return this.createCosemObject(
          objectFromProfile.getDescription(),
          classId,
          objectListElement.getVersion(),
          obisCode,
          note,
          attributes);
    } catch (final Exception e) {
      return this.createCosemObject(
          InterfaceClass.interfaceClassFor(classId).name() + ", decoding failed: " + e.getMessage(),
          classId,
          -1,
          obisCode,
          "Raw data: "
              + this.minimizeRawData(
                  this.dlmsHelper.getDebugInfo(DataObject.newStructureData(attributeData))),
          null);
    }
  }

  public Attribute decodeAttributeData(
      final int classId,
      final String obisCode,
      final int attributeId,
      final DataObject attributeData,
      final DlmsProfile dlmsProfile) {

    try {
      final CosemObject cosemObject = this.getCosemObjectByObis(dlmsProfile, obisCode, classId);

      return this.decodeAttributeData(cosemObject, attributeId, attributeData, null);
    } catch (final Exception e) {
      return new Attribute(
          attributeId,
          null,
          "Decoding failed, raw data: "
              + this.minimizeRawData(this.dlmsHelper.getDebugInfo(attributeData)),
          null,
          null,
          null,
          null,
          null);
    }
  }

  private CosemObject getCosemObjectByObis(
      final DlmsProfile dlmsProfile, final String obis, final int classId) {
    final Optional<CosemObject> optionalCosemObject =
        dlmsProfile.getObjects().stream().filter(o -> o.getObis().equals(obis)).findFirst();

    final String className = interfaceClassFor(classId).name();

    return optionalCosemObject.orElseGet(
        () ->
            this.createCosemObject(
                className
                    + ", object not found in profile "
                    + dlmsProfile.getProfile()
                    + " "
                    + dlmsProfile.getVersion(),
                classId,
                -1,
                obis,
                null,
                null));
  }

  private Attribute decodeAttributeData(
      final CosemObject cosemObject,
      final int attributeId,
      final DataObject attributeData,
      final AttributeAccessItem accessItem)
      throws ProtocolAdapterException {
    Attribute attributeFromProfile;

    try {
      attributeFromProfile = cosemObject.getAttribute(attributeId);
    } catch (final Exception e) {
      final String description;
      if (attributeId == 1) {
        description = "logical_name";
      } else {
        description =
            this.getAttributeDescription(
                InterfaceClass.interfaceClassFor(cosemObject.getClassId()), attributeId);
      }
      attributeFromProfile =
          new Attribute(attributeId, description, null, null, null, null, null, null);
    }

    final String rawData = this.minimizeRawData(this.dlmsHelper.getDebugInfo(attributeData));
    final String note =
        (attributeFromProfile.getNote() == null || attributeFromProfile.getNote().isBlank())
            ? rawData
            : attributeFromProfile.getNote() + "\n" + rawData;

    final String value =
        this.decodeAttributeValue(cosemObject.getClassId(), attributeFromProfile, attributeData);

    return new Attribute(
        attributeId,
        attributeFromProfile.getDescription(),
        note,
        attributeFromProfile.getDatatype(),
        null,
        value,
        null,
        accessItem != null ? accessItem.getAccessMode() : null);
  }

  private String minimizeRawData(final String rawData) {
    if (rawData == null) {
      return "no data";
    } else {
      return rawData
          .replace("DataObject: Choice=", "")
          .replace("java.lang.", "")
          .replace("java.util.", "")
          .replace("ResultData is", "");
    }
  }

  private String decodeAttributeValue(
      final int classId, final Attribute attributeFromProfile, final DataObject attributeData)
      throws ProtocolAdapterException {

    if (attributeFromProfile.getId() == 1) {
      return this.decodeAttributeLogicalName(attributeData);
    }

    if (attributeFromProfile.getDatatype() == DlmsDataType.SCAL_UNIT_TYPE
        || attributeFromProfile.getDatatype() == null
            && this.attributeIsScalerUnit(classId, attributeFromProfile.getId())) {
      return this.decodeAttributeScalerUnit(attributeData);
    }

    if (simpleNumbers.contains(attributeData.getType())) {
      return this.decodeNumber(attributeData);
    }

    return "No decoder found for value";
  }

  private boolean attributeIsScalerUnit(final int classId, final int attributeId) {
    return (((classId == 3 || classId == 4) && attributeId == 3)
        || (classId == 5 && attributeId == 4));
  }

  private String getAttributeDescription(final InterfaceClass interfaceClass, final int attrId) {
    for (final Enum<?> attributeClass : interfaceClass.getAttributeEnumValues()) {
      if (((AttributeClass) attributeClass).attributeId() == attrId) {
        return ((AttributeClass) attributeClass).attributeName();
      }
    }

    return "unknown attribute";
  }

  private CosemObject createCosemObject(
      final String description,
      final int classId,
      final int version,
      final String obis,
      final String note,
      final List<Attribute> attributes) {
    return new CosemObject(
        null, description, classId, version, obis, null, note, null, null, attributes);
  }

  private String decodeAttributeLogicalName(final DataObject attributeData) {
    final byte[] obisCodeByteArray = attributeData.getValue();
    return (new ObisCode(obisCodeByteArray)).toString();
  }

  private String decodeAttributeScalerUnit(final DataObject attributeData)
      throws ProtocolAdapterException {
    return this.dlmsHelper.getScalerUnit(attributeData, "readValue");
  }

  private String decodeNumber(final DataObject result) throws ProtocolAdapterException {
    final Long value = this.dlmsHelper.readLong(result, "readValue");

    if (value == null) {
      return "null";
    } else {
      return value.toString();
    }
  }
}
