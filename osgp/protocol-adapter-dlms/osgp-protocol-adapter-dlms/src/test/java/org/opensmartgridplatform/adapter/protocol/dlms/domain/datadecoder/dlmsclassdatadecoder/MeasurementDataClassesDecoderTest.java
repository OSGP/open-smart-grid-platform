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

class MeasurementDataClassesDecoderTest {
  private final DlmsHelper dlmsHelper = new DlmsHelper();
  private final BasicDlmsDataDecoder dlmsDataDecoder = new BasicDlmsDataDecoder(this.dlmsHelper);
  private final MeasurementDataClassesDecoder decoder =
      new MeasurementDataClassesDecoder(this.dlmsHelper);

  @Test
  void testDecodeScalerUnit() {
    final DataObject scaler = DataObject.newInteger8Data((byte) 1);
    final DataObject unit = DataObject.newEnumerateData(24);
    final DataObject scalerUnit = DataObject.newStructureData(scaler, unit);
    final String result = this.decoder.decodeScalerUnit(scalerUnit);
    assertThat(result).isEqualTo("1, BAR");
  }

  @Test
  void testDecodeCaptureObjects() {
    final DataObject classId1 = DataObject.newUInteger16Data(4);
    final DataObject obis1 = DataObject.newOctetStringData(new byte[] {1, 2, 3, 4, 5, (byte) 255});
    final DataObject attributeId1 = DataObject.newInteger8Data((byte) 1);
    final DataObject dataIndex1 = DataObject.newUInteger16Data(5);
    final DataObject object1 =
        DataObject.newStructureData(classId1, obis1, attributeId1, dataIndex1);

    final DataObject classId2 = DataObject.newUInteger16Data(8);
    final DataObject obis2 = DataObject.newOctetStringData(new byte[] {6, 7, 8, 9, 10, (byte) 255});
    final DataObject attributeId2 = DataObject.newInteger8Data((byte) 2);
    final DataObject dataIndex2 = DataObject.newUInteger16Data(8);
    final DataObject object2 =
        DataObject.newStructureData(classId2, obis2, attributeId2, dataIndex2);

    final DataObject captureObjects = DataObject.newArrayData(List.of(object1, object2));

    final String result = this.decoder.decodeCaptureObjects(captureObjects);
    assertThat(result).isEqualTo("{4,1-2:3.4.5.255,1,5}, {8,6-7:8.9.10.255,2,8}");
  }

  @Test
  void testDecodeCaptureObject() {
    final DataObject classId = DataObject.newUInteger16Data(18);
    final DataObject obis = DataObject.newOctetStringData(new byte[] {1, 0, 0, 0, 0, (byte) 255});
    final DataObject attributeId = DataObject.newInteger8Data((byte) 5);
    final DataObject dataIndex = DataObject.newUInteger16Data(0);
    final DataObject captureObject =
        DataObject.newStructureData(classId, obis, attributeId, dataIndex);

    final String result = this.decoder.decodeCaptureObject(captureObject);
    assertThat(result).isEqualTo("{18,1-0:0.0.0.255,5,0}");
  }

  @Test
  void testDecodeSortMethod() {
    final String result = this.decoder.decodeSortMethod(DataObject.newEnumerateData(3));
    assertThat(result).isEqualTo("LARGEST");
  }
}
