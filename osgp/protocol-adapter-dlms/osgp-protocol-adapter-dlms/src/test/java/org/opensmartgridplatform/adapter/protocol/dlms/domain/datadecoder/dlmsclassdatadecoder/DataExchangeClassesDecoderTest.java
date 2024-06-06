// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.datadecoder.dlmsclassdatadecoder;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.datadecoder.BasicDlmsDataDecoder;

class DataExchangeClassesDecoderTest {
  private final DlmsHelper dlmsHelper = new DlmsHelper();
  private final BasicDlmsDataDecoder dlmsDataDecoder = new BasicDlmsDataDecoder(this.dlmsHelper);
  private final DataExchangeClassesDecoder decoder =
      new DataExchangeClassesDecoder(this.dlmsHelper);

  @Test
  void testDecodeModemRegistrationStatus() {
    final String result =
        this.decoder.decodeModemRegistrationStatus(DataObject.newEnumerateData(5));
    assertThat(result).isEqualTo("REGISTERED_ROAMING");
  }

  @Test
  void testDecodeCsStatus() {
    final String result = this.decoder.decodeCsStatus(DataObject.newEnumerateData(4));
    assertThat(result).isEqualTo("RESERVED");
  }

  @Test
  void testDecodePsStatus() {
    final String result = this.decoder.decodePsStatus(DataObject.newEnumerateData(3));
    assertThat(result).isEqualTo("UMTS");
  }

  @Test
  void testDecodeCellInfo() {
    final DataObject cellId = DataObject.newUInteger32Data(123);
    final DataObject locationId = DataObject.newUInteger16Data(456);
    final DataObject signalQuality = DataObject.newUInteger8Data((short) 5);
    final DataObject ber = DataObject.newUInteger8Data((short) 6);
    final DataObject mcc = DataObject.newUInteger16Data(789);
    final DataObject mnc = DataObject.newUInteger16Data(987);
    final DataObject channel_number = DataObject.newUInteger32Data(55);
    final DataObject cellInfo =
        DataObject.newStructureData(
            cellId, locationId, signalQuality, ber, mcc, mnc, channel_number);
    final String result = this.decoder.decodeCellInfo(cellInfo);
    assertThat(result)
        .isEqualTo(
            "cellId: 123, locationId: 456, signalQuality: MINUS_103_DBM, bitErrorRate: 6, mobile country code: 789, mobile network code: 987, channel: 55");
  }

  @Test
  void testDecodeAdjacentCells() {
    final DataObject cellId1 = DataObject.newUInteger32Data(111);
    final DataObject cellId2 = DataObject.newUInteger32Data(222);
    final DataObject signalQuality1 = DataObject.newUInteger8Data((short) 11);
    final DataObject signalQuality2 = DataObject.newUInteger8Data((short) 22);
    final DataObject cell1 = DataObject.newStructureData(cellId1, signalQuality1);
    final DataObject cell2 = DataObject.newStructureData(cellId2, signalQuality2);
    final DataObject adjacentCells = DataObject.newArrayData(List.of(cell1, cell2));
    final String result = this.decoder.decodeAdjacentCells(adjacentCells);
    assertThat(result)
        .isEqualTo(
            "cellId: 111, signalQuality: MINUS_91_DBM, cellId: 222, signalQuality: MINUS_69_DBM");
  }
}
