// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.datadecoder;

import static org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeType.DSMR_EQUIPMENT_IDENTIFIER;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeType.SIGNATURE;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeType;
import org.opensmartgridplatform.dlms.objectconfig.Attribute;
import org.springframework.stereotype.Service;

@Slf4j
@Service(value = "profileDataDecoder")
public class ProfileDataDecoder {

  // This service decodes attributes that have a special type in some DLMS classes as defined in
  // an additional specification, e.g. (D)SMR.

  private final Map<AttributeType, Function<DataObject, String>> decoderMap =
      new EnumMap<>(AttributeType.class);

  public ProfileDataDecoder() {
    this.decoderMap.put(SIGNATURE, this::decodeSignature);
    this.decoderMap.put(DSMR_EQUIPMENT_IDENTIFIER, this::decodeDsmrEquipmentIdentifier);
  }

  public String decodeAttributeValue(
      final Attribute attributeFromProfile, final DataObject attributeData) {

    final Function<DataObject, String> decoder =
        this.decoderMap.getOrDefault(attributeFromProfile.getAttributetype(), null);

    if (decoder != null) {
      return decoder.apply(attributeData);
    }

    return null;
  }

  private String decodeSignature(final DataObject attributeData) {
    try {
      final byte[] byteArray = attributeData.getValue();
      return Hex.toHexString(byteArray);
    } catch (final Exception e) {
      return "decoding signature failed: " + e.getMessage();
    }
  }

  private String decodeDsmrEquipmentIdentifier(final DataObject attributeData) {
    try {
      final byte[] byteArray = attributeData.getValue();
      final String equipmentIdentifier = new String(byteArray);

      if (equipmentIdentifier.length() == 17) {
        return equipmentIdentifier
            + ", Meter code: "
            + equipmentIdentifier.substring(0, 5)
            + ", serial number: "
            + equipmentIdentifier.substring(5, 15)
            + ", year: 20"
            + equipmentIdentifier.substring(15, 17);
      } else {
        return equipmentIdentifier;
      }
    } catch (final Exception e) {
      return "decoding equipment identifier failed: " + e.getMessage();
    }
  }
}
