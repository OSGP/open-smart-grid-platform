// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.datadecoder;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openmuc.jdlms.datatypes.CosemDate;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.CosemDateTime.ClockStatus;
import org.openmuc.jdlms.datatypes.CosemTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

class BasicDlmsDataDecoderTest {
  private final DlmsHelper dlmsHelper = new DlmsHelper();
  private final BasicDlmsDataDecoder decoder = new BasicDlmsDataDecoder(this.dlmsHelper);

  @Test
  void testDecodeInteger() throws ProtocolAdapterException {
    this.testNumber(DataObject.newInteger8Data((byte) 255), "-1");
  }

  @Test
  void testDecodeLongInteger() throws ProtocolAdapterException {
    this.testNumber(DataObject.newInteger16Data((short) -2500), "-2500");
  }

  @Test
  void testDecodeDoubleLong() throws ProtocolAdapterException {
    this.testNumber(DataObject.newInteger32Data(-10000), "-10000");
  }

  @Test
  void testDecodeLong64() throws ProtocolAdapterException {
    this.testNumber(DataObject.newInteger64Data(-75000), "-75000");
  }

  @Test
  void testDecodeUnsigned() throws ProtocolAdapterException {
    this.testNumber(DataObject.newUInteger8Data((short) 255), "255");
  }

  @Test
  void testDecodeLongUnsigned() throws ProtocolAdapterException {
    this.testNumber(DataObject.newUInteger16Data(3000), "3000");
  }

  @Test
  void testDecodeDoubleLongUnsigned() throws ProtocolAdapterException {
    this.testNumber(DataObject.newUInteger32Data(80000), "80000");
  }

  @Test
  void testDecodeLong64Unsigned() throws ProtocolAdapterException {
    this.testNumber(DataObject.newUInteger64Data(10_000_000_000L), "10000000000");
  }

  void testNumber(final DataObject dataObject, final String expectedResult)
      throws ProtocolAdapterException {
    assertThat(this.decoder.decodeAttributeValue(dataObject)).isEqualTo(expectedResult);
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void testDecodeBoolean(final boolean bool) throws ProtocolAdapterException {
    final DataObject dataObject = DataObject.newBoolData(bool);
    assertThat(this.decoder.decodeAttributeValue(dataObject)).isEqualTo(String.valueOf(bool));
  }

  @Test
  void testDecodeOctetStringWithDateTime() throws ProtocolAdapterException {
    final DataObject dataObject =
        DataObject.newOctetStringData(
            new CosemDateTime(2024, 5, 24, 13, 15, 0, 60, ClockStatus.INVALID_VALUE).encode());
    assertThat(this.decoder.decodeAttributeValue(dataObject))
        .isEqualTo(
            "Day of week not specified2024-05-24, 13:15:00.000, deviation=60, ClockStatus[value=0x01, bits=[invalid value]]]");
  }

  @Test
  void testDecodeOctetStringWithReadableText() throws ProtocolAdapterException {
    final DataObject dataObject = DataObject.newOctetStringData("Hello".getBytes());
    assertThat(this.decoder.decodeAttributeValue(dataObject)).isEqualTo("Hello");
  }

  @Test
  void testDecodeVisibleString() throws ProtocolAdapterException {
    final DataObject dataObject = DataObject.newVisibleStringData("Hello".getBytes());
    assertThat(this.decoder.decodeAttributeValue(dataObject)).isEqualTo("Hello");
  }

  @Test
  void testDecodeDate() {
    final DataObject dataObject =
        DataObject.newOctetStringData(new CosemDate(2024, 5, 24).encode());
    assertThat(this.decoder.decodeDate(dataObject))
        .isEqualTo("Day of week not specified, 2024-5-24");
  }

  @Test
  void testDecodeTime() {
    final DataObject dataObject =
        DataObject.newOctetStringData(new CosemTime(13, 27, 1, 25).encode());
    assertThat(this.decoder.decodeTime(dataObject)).isEqualTo("13:27:01.025");
  }
}
