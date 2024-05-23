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

class DlmsClassDataDecoderTest {
  private static final int CLASS_ID_REGISTER = 3;
  private static final int ATTRIBUTE_ID_SCALER_UNIT = 3;

  private static final int CLASS_ID_PROFILE_GENERIC = 7;
  private static final int ATTRIBUTE_ID_CAPTURE_OBJECTS = 3;
  private static final int ATTRIBUTE_ID_SORT_METHOD = 5;
  private static final int ATTRIBUTE_ID_SORT_OBJECT = 6;

  private static final int CLASS_ID_GSM_DIAGNOSTIC = 47;
  private static final int ATTRIBUTE_ID_STATUS = 3;
  private static final int ATTRIBUTE_ID_CS_STATUS = 4;
  private static final int ATTRIBUTE_ID_PS_STATUS = 5;
  private static final int ATTRIBUTE_ID_CELL_INFO = 6;
  private static final int ATTRIBUTE_ID_ADJACENT_CELLS = 7;

  private final DlmsHelper dlmsHelper = new DlmsHelper();
  private final BasicDlmsDataDecoder dlmsDataDecoder = new BasicDlmsDataDecoder(this.dlmsHelper);
  private final DataExchangeClassesDecoder dataExchangeClassesDecoder =
      new DataExchangeClassesDecoder(this.dlmsHelper, this.dlmsDataDecoder);
  private final MeasurementDataClassesDecoder measurementDataClassesDecoder =
      new MeasurementDataClassesDecoder(this.dlmsHelper, this.dlmsDataDecoder);
  private final TimeAndEventsClassesDecoder timeAndEventsClassesDecoder =
      new TimeAndEventsClassesDecoder(this.dlmsHelper, this.dlmsDataDecoder);
  private final DlmsClassDataDecoder decoder =
      new DlmsClassDataDecoder(
          this.dlmsHelper,
          this.dlmsDataDecoder,
          this.dataExchangeClassesDecoder,
          this.measurementDataClassesDecoder,
          this.timeAndEventsClassesDecoder);

  @Test
  void testDecodeScalerUnit() {
    final String result =
        this.decoder.decodeAttributeValue(
            CLASS_ID_REGISTER,
            ATTRIBUTE_ID_SCALER_UNIT,
            DataObject.newStructureData(
                DataObject.newInteger8Data((byte) 2), DataObject.newEnumerateData(30)));

    assertThat(result).isEqualTo("2, WH");
  }

  @Test
  void testDecodeCaptureObjects() {
    final String result =
        this.decoder.decodeAttributeValue(
            CLASS_ID_PROFILE_GENERIC,
            ATTRIBUTE_ID_CAPTURE_OBJECTS,
            DataObject.newArrayData(
                List.of(
                    DataObject.newStructureData(
                        DataObject.newUInteger16Data(8),
                        DataObject.newOctetStringData(
                            new byte[] {0x00, 0x01, 0x02, 0x03, 0x04, 0x05}),
                        DataObject.newInteger8Data((byte) 2),
                        DataObject.newUInteger16Data(0)),
                    DataObject.newStructureData(
                        DataObject.newUInteger16Data(7),
                        DataObject.newOctetStringData(
                            new byte[] {0x06, 0x07, 0x08, 0x09, 0x0A, (byte) 0xFF}),
                        DataObject.newInteger8Data((byte) 3),
                        DataObject.newUInteger16Data(10)))));

    assertThat(result).isEqualTo("{8,0-1:2.3.4.5,2,0}, {7,6-7:8.9.10.255,3,10}");
  }

  @Test
  void testDecodeSortObject() {
    final String result =
        this.decoder.decodeAttributeValue(
            CLASS_ID_PROFILE_GENERIC,
            ATTRIBUTE_ID_SORT_OBJECT,
            DataObject.newStructureData(
                DataObject.newUInteger16Data(3),
                DataObject.newOctetStringData(new byte[] {0x01, 0x02, 0x03, 0x04, 0x05, 0x06}),
                DataObject.newInteger8Data((byte) 3),
                DataObject.newUInteger16Data(0)));

    assertThat(result).isEqualTo("{3,1-2:3.4.5.6,3,0}");
  }

  @Test
  void testDecodeSortMethod() {
    final String result =
        this.decoder.decodeAttributeValue(
            CLASS_ID_PROFILE_GENERIC, ATTRIBUTE_ID_SORT_METHOD, DataObject.newEnumerateData(1));

    assertThat(result).isEqualTo("FIFO");
  }

  @Test
  void testDecodeGsmDiagnosticStatus() {
    final String result =
        this.decoder.decodeAttributeValue(
            CLASS_ID_GSM_DIAGNOSTIC, ATTRIBUTE_ID_STATUS, DataObject.newEnumerateData(5));

    assertThat(result).isEqualTo("REGISTERED_ROAMING");
  }

  @Test
  void testDecodeGsmDiagnosticCsStatus() {
    final String result =
        this.decoder.decodeAttributeValue(
            CLASS_ID_GSM_DIAGNOSTIC, ATTRIBUTE_ID_CS_STATUS, DataObject.newEnumerateData(0));

    assertThat(result).isEqualTo("INACTIVE");
  }

  @Test
  void testDecodeGsmDiagnosticPsStatus() {
    final String result =
        this.decoder.decodeAttributeValue(
            CLASS_ID_GSM_DIAGNOSTIC, ATTRIBUTE_ID_PS_STATUS, DataObject.newEnumerateData(6));

    assertThat(result).isEqualTo("CDMA");
  }

  @Test
  void testDecodeGsmDiagnosticCellInfo() {
    final String result =
        this.decoder.decodeAttributeValue(
            CLASS_ID_GSM_DIAGNOSTIC,
            ATTRIBUTE_ID_CELL_INFO,
            DataObject.newStructureData(
                List.of(
                    DataObject.newUInteger32Data(77),
                    DataObject.newUInteger16Data(2230),
                    DataObject.newUInteger8Data((short) 13),
                    DataObject.newUInteger8Data((short) 6),
                    DataObject.newUInteger16Data(66),
                    DataObject.newUInteger16Data(204),
                    DataObject.newUInteger32Data(107))));

    assertThat(result)
        .isEqualTo(
            "cellId: 77, locationId: 2230, signalQuality: MINUS_87_DBM, bitErrorRate: 6, mobile country code: 66, mobile network code: 204, channel: 107");
  }

  @Test
  void testDecodeGsmDiagnosticAdjacentCells() {
    final String result =
        this.decoder.decodeAttributeValue(
            CLASS_ID_GSM_DIAGNOSTIC,
            ATTRIBUTE_ID_ADJACENT_CELLS,
            DataObject.newArrayData(
                List.of(
                    DataObject.newStructureData(
                        DataObject.newUInteger32Data(93), DataObject.newUInteger8Data((short) 11)),
                    DataObject.newStructureData(
                        DataObject.newUInteger32Data(94),
                        DataObject.newUInteger8Data((short) 12)))));

    assertThat(result)
        .isEqualTo(
            "cellId: 93, signalQuality: MINUS_91_DBM, cellId: 94, signalQuality: MINUS_89_DBM");
  }
}
