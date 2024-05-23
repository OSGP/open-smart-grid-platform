// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.datadecoder.dlmsclassdatadecoder;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.openmuc.jdlms.datatypes.CosemDate;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.datadecoder.BasicDlmsDataDecoder;

class TimeAndEventsClassesDecoderTest {
  private final DlmsHelper dlmsHelper = new DlmsHelper();
  private final BasicDlmsDataDecoder dlmsDataDecoder = new BasicDlmsDataDecoder(this.dlmsHelper);
  private final TimeAndEventsClassesDecoder decoder =
      new TimeAndEventsClassesDecoder(this.dlmsHelper, this.dlmsDataDecoder);

  @Test
  void testSpecialDays() {

    final DataObject index1 = DataObject.newUInteger16Data(1);
    final DataObject date1 = DataObject.newDateData(new CosemDate(2024, 5, 23, 1));
    final DataObject id1 = DataObject.newUInteger8Data((short) 1);
    final DataObject specialDay1 = DataObject.newStructureData(index1, date1, id1);
    final DataObject index2 = DataObject.newUInteger16Data(2222);
    final DataObject date2 = DataObject.newDateData(new CosemDate(0xFFFF, 12, 31, 0xFF));
    final DataObject id2 = DataObject.newUInteger8Data((short) 22);
    final DataObject specialDay2 = DataObject.newStructureData(index2, date2, id2);
    final DataObject specialDays = DataObject.newArrayData(List.of(specialDay1, specialDay2));

    final String result = this.decoder.decodeSpecialDays(specialDays);

    assertThat(result)
        .isEqualTo(
            "1. (id 1): Monday, 2024-5-23\n2222. (id 22): Day of week not specified, Year not specified-12-31");
  }
}
