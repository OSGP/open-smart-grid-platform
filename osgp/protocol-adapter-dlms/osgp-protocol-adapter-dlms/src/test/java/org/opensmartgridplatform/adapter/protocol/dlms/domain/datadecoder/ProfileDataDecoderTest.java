// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.datadecoder;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeType;
import org.opensmartgridplatform.dlms.objectconfig.Attribute;

class ProfileDataDecoderTest {
  private final DlmsHelper dlmsHelper = new DlmsHelper();
  private final ProfileDataDecoder decoder = new ProfileDataDecoder();

  @Test
  void testDecodeSignature() {
    final String result =
        this.decoder.decodeAttributeValue(
            this.createAttribute(AttributeType.SIGNATURE),
            DataObject.newOctetStringData(new byte[] {0x01, 0x02, (byte) 0xFF}));

    assertThat(result).isEqualTo("0102ff");
  }

  @Test
  void testDecodeEquipmentIdentifier() {
    final String result =
        this.decoder.decodeAttributeValue(
            this.createAttribute(AttributeType.DSMR_EQUIPMENT_IDENTIFIER),
            DataObject.newOctetStringData(
                new byte[] {
                  0x45, 0x31, 0x32, 0x33, 0x34, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37,
                  0x38, 0x39, 0x32, 0x34
                }));

    assertThat(result)
        .isEqualTo("E1234012345678924, Meter code: E1234, serial number: 0123456789, year: 2024");
  }

  private Attribute createAttribute(final AttributeType type) {
    return new Attribute(1, null, null, null, type, null, null, null, null, null);
  }
}
