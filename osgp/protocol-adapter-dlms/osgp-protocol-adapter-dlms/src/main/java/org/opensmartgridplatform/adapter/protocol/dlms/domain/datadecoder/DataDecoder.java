// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.datadecoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.datadecoder.dlmsclassdatadecoder.DlmsClassDataDecoder;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeClass;
import org.opensmartgridplatform.dlms.objectconfig.Attribute;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.DlmsProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service(value = "dataDecoder")
public class DataDecoder {

  private final DlmsHelper dlmsHelper;
  private final BasicDlmsDataDecoder basicDlmsDataDecoder;
  private final DlmsClassDataDecoder dlmsClassDataDecoder;
  private final ProfileDataDecoder profileDataDecoder;

  @Autowired
  public DataDecoder(
      final DlmsHelper dlmsHelper,
      final BasicDlmsDataDecoder basicDlmsDataDecoder,
      final DlmsClassDataDecoder dlmsClassDataDecoder,
      final ProfileDataDecoder profileDataDecoder) {
    this.dlmsHelper = dlmsHelper;
    this.basicDlmsDataDecoder = basicDlmsDataDecoder;
    this.dlmsClassDataDecoder = dlmsClassDataDecoder;
    this.profileDataDecoder = profileDataDecoder;
  }

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

      int attributeId = 1;
      for (final DataObject dataObject : attributeData) {
        attributes.add(
            this.decodeAttributeData(
                objectFromProfile,
                attributeId,
                dataObject,
                objectListElement.getAttributes().get(attributeId - 1)));
        attributeId++;
      }

      return this.createCosemObject(
          objectFromProfile.getDescription(),
          classId,
          objectListElement.getVersion(),
          obisCode,
          this.getObjectNote(objectFromProfile, objectListElement),
          attributes);
    } catch (final Exception e) {
      final String error =
          "Decoding failed, raw data: "
              + this.minimizeRawData(
                  this.dlmsHelper.getDebugInfo(DataObject.newStructureData(attributeData)));
      log.warn(error, e);

      return this.createCosemObject(null, classId, -1, obisCode, error, null);
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
      final String rawData =
          "raw data: " + this.minimizeRawData(this.dlmsHelper.getDebugInfo(attributeData));
      final String error = "Decoding failed";
      log.warn(error + ", raw data: " + rawData, e);

      return new Attribute(attributeId, null, error, null, null, null, null, rawData, null, null);
    }
  }

  private CosemObject getCosemObjectByObis(
      final DlmsProfile dlmsProfile, final String obis, final int classId) {
    final Optional<CosemObject> optionalCosemObject =
        dlmsProfile.getObjects().stream().filter(o -> o.getObis().equals(obis)).findFirst();

    return optionalCosemObject.orElseGet(
        () -> this.createCosemObject(null, classId, -1, obis, null, null));
  }

  private String getObjectNote(
      final CosemObject objectFromProfile, final ObjectListElement objectListElement) {
    String note = objectFromProfile.getNote() != null ? objectFromProfile.getNote() : "";
    note = note + this.compareReadValuesWithProfile(objectFromProfile, objectListElement);

    return note.isEmpty() ? null : note;
  }

  private String compareReadValuesWithProfile(
      final CosemObject objectFromProfile, final ObjectListElement objectListElement) {
    String note = "";

    if (objectFromProfile.getTag() != null) {
      if (objectFromProfile.getAttributes().size() + 1
          != objectListElement.getAttributes().size()) {
        note =
            note
                + "\nRead number of attributes "
                + objectListElement.getAttributes().size()
                + " differs from number of attributes in profile "
                + (objectFromProfile.getAttributes().size() + 1);
      }

      if (objectListElement.getClassId() != objectFromProfile.getClassId()) {
        note =
            note
                + "\nRead classId "
                + objectListElement.getClassId()
                + " differs from classId in profile "
                + objectFromProfile.getClassId();
      }
    }

    return note;
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

    final String value =
        this.decodeAttributeValue(cosemObject, attributeFromProfile, attributeData);

    return new Attribute(
        attributeId,
        attributeFromProfile.getDescription(),
        attributeFromProfile.getNote(),
        attributeFromProfile.getDatatype(),
        null,
        null,
        value,
        rawData,
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
          .replace("[[B]:", "[B]:")
          .replace("ResultData is", "");
    }
  }

  private String decodeAttributeValue(
      final CosemObject objectFromProfile,
      final Attribute attributeFromProfile,
      final DataObject attributeData)
      throws ProtocolAdapterException {

    if (attributeFromProfile.getId() == 1) {
      return this.decodeAttributeLogicalName(attributeData);
    }

    String decodedValue =
        this.profileDataDecoder.decodeAttributeValue(attributeFromProfile, attributeData);

    if (decodedValue == null) {
      decodedValue =
          this.dlmsClassDataDecoder.decodeAttributeValue(
              objectFromProfile.getClassId(), attributeFromProfile.getId(), attributeData);
    }

    if (decodedValue == null) {
      decodedValue = this.basicDlmsDataDecoder.decodeAttributeValue(attributeData);
    }

    return Objects.requireNonNullElse(decodedValue, "No decoder found for value");
  }

  private String getAttributeDescription(final InterfaceClass interfaceClass, final int attrId) {
    final AttributeClass[] attributesForClasses = interfaceClass.getAttributeEnumValues();

    for (final AttributeClass attributeClass : attributesForClasses) {
      if (attributeClass.attributeId() == attrId) {
        return attributeClass.attributeName().toLowerCase();
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
}
