// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.datadecoder;

import static org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeType.SIGNATURE;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeType;
import org.opensmartgridplatform.dlms.objectconfig.Attribute;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service(value = "profileDataDecoder")
public class ProfileDataDecoder {

  @Autowired private DlmsHelper dlmsHelper;

  private final Map<AttributeType, Function<DataObject, String>> decoderMap =
      new EnumMap<>(AttributeType.class);

  public ProfileDataDecoder() {
    this.decoderMap.put(SIGNATURE, this::decodeSignature);
  }

  public String decodeAttributeValue(
      final CosemObject objectFromProfile,
      final Attribute attributeFromProfile,
      final DataObject attributeData) {

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
}
