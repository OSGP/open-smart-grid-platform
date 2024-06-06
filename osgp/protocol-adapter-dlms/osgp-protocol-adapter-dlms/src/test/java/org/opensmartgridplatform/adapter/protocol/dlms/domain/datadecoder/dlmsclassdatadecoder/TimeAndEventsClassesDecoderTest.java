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

class TimeAndEventsClassesDecoderTest {
  private final DlmsHelper dlmsHelper = new DlmsHelper();
  private final BasicDlmsDataDecoder dlmsDataDecoder = new BasicDlmsDataDecoder(this.dlmsHelper);
  private final TimeAndEventsClassesDecoder decoder =
      new TimeAndEventsClassesDecoder(this.dlmsHelper, this.dlmsDataDecoder);

  @Test
  void testClockStatus() {
    final DataObject clockStatus = DataObject.newUInteger8Data((short) 3);
    final String result = this.decoder.decodeClockStatus(clockStatus);
    assertThat(result).isEqualToIgnoringNewLines("status: invalid value, doubtful value");
  }

  @Test
  void testClockBase() {
    final DataObject clockBase = DataObject.newEnumerateData(4);
    final String result = this.decoder.decodeClockBase(clockBase);
    assertThat(result).isEqualToIgnoringNewLines("GPS");
  }

  @Test
  void testSpecialDays() {

    final DataObject index1 = DataObject.newUInteger16Data(1);
    final DataObject date1 = DataObject.newOctetStringData(new byte[] {7, (byte) 232, 5, 23, 1});
    final DataObject id1 = DataObject.newUInteger8Data((short) 1);
    final DataObject specialDay1 = DataObject.newStructureData(index1, date1, id1);
    final DataObject index2 = DataObject.newUInteger16Data(2222);
    final DataObject date2 =
        DataObject.newOctetStringData(new byte[] {(byte) 0xFF, (byte) 0xFF, 12, 31, (byte) 0xFF});
    final DataObject id2 = DataObject.newUInteger8Data((short) 22);
    final DataObject specialDay2 = DataObject.newStructureData(index2, date2, id2);
    final DataObject specialDays = DataObject.newArrayData(List.of(specialDay1, specialDay2));

    final String result = this.decoder.decodeSpecialDays(specialDays);

    assertThat(result)
        .isEqualToIgnoringNewLines(
            "1. (id 1): Monday, 2024-5-23\n"
                + "2222. (id 22): Day of week not specified, Year not specified-12-31");
  }

  @Test
  void testDayProfile() {

    final DataObject start1 = DataObject.newOctetStringData(new byte[] {11, 0, 0, 0});
    final DataObject obis1 = DataObject.newOctetStringData(new byte[] {1, 1, 1, 1, 1, (byte) 255});
    final DataObject selector1 = DataObject.newUInteger16Data(1);
    final DataObject action1 = DataObject.newStructureData(start1, obis1, selector1);

    final DataObject start2 = DataObject.newOctetStringData(new byte[] {12, 0, 0, 0});
    final DataObject obis2 = DataObject.newOctetStringData(new byte[] {2, 2, 2, 2, 2, (byte) 255});
    final DataObject selector2 = DataObject.newUInteger16Data(2);
    final DataObject action2 = DataObject.newStructureData(start2, obis2, selector2);

    final DataObject start3 = DataObject.newOctetStringData(new byte[] {13, 0, 0, 0});
    final DataObject obis3 = DataObject.newOctetStringData(new byte[] {3, 3, 3, 3, 3, (byte) 255});
    final DataObject selector3 = DataObject.newUInteger16Data(3);
    final DataObject action3 = DataObject.newStructureData(start3, obis3, selector3);

    final DataObject idA = DataObject.newUInteger8Data((short) 1);
    final DataObject scheduleA = DataObject.newArrayData(List.of(action1, action2));
    final DataObject profileA = DataObject.newStructureData(idA, scheduleA);

    final DataObject idB = DataObject.newUInteger8Data((short) 2);
    final DataObject scheduleB = DataObject.newArrayData(List.of(action3));
    final DataObject profileB = DataObject.newStructureData(idB, scheduleB);

    final DataObject dayProfiles = DataObject.newArrayData(List.of(profileA, profileB));

    final String result = this.decoder.decodeDayProfiles(dayProfiles);

    assertThat(result)
        .isEqualToIgnoringNewLines(
            "1: 11:00:00.000 1-1:1.1.1.255 selector 1, 12:00:00.000 2-2:2.2.2.255 selector 2\n"
                + "2: 13:00:00.000 3-3:3.3.3.255 selector 3");
  }

  @Test
  void testWeekProfile() {

    final DataObject nameA = DataObject.newOctetStringData("NameA".getBytes());
    final DataObject nameB = DataObject.newOctetStringData("NameB".getBytes());
    final DataObject day1 = DataObject.newUInteger8Data((short) 1);
    final DataObject day2 = DataObject.newUInteger8Data((short) 2);
    final DataObject day3 = DataObject.newUInteger8Data((short) 3);
    final DataObject day4 = DataObject.newUInteger8Data((short) 4);
    final DataObject day5 = DataObject.newUInteger8Data((short) 5);
    final DataObject day6 = DataObject.newUInteger8Data((short) 6);
    final DataObject day7 = DataObject.newUInteger8Data((short) 7);

    final DataObject profileA =
        DataObject.newStructureData(nameA, day1, day2, day3, day4, day5, day6, day7);
    final DataObject profileB =
        DataObject.newStructureData(nameB, day7, day6, day5, day4, day3, day2, day1);

    final DataObject weekProfiles = DataObject.newArrayData(List.of(profileA, profileB));

    final String result = this.decoder.decodeWeekProfiles(weekProfiles);

    assertThat(result)
        .isEqualToIgnoringNewLines(
            "NameA: Monday 1, Tuesday 2, Wednesday 3, Thursday 4, Friday 5, Saturday 6, Sunday 7\n"
                + "NameB: Monday 7, Tuesday 6, Wednesday 5, Thursday 4, Friday 3, Saturday 2, Sunday 1");
  }

  @Test
  void testSeasonProfile() {

    final DataObject name1 = DataObject.newOctetStringData("Name1".getBytes());
    final DataObject start1 =
        DataObject.newOctetStringData(new byte[] {7, (byte) 232, 5, 24, 1, 8, 51, 1, 0, 0, 60, 0});
    final DataObject weekName1 = DataObject.newOctetStringData("WeekName1".getBytes());
    final DataObject name2 = DataObject.newOctetStringData("Name2".getBytes());
    final DataObject start2 =
        DataObject.newOctetStringData(new byte[] {7, (byte) 232, 5, 24, 2, 8, 52, 2, 0, 0, 60, 1});
    final DataObject weekName2 = DataObject.newOctetStringData("WeekName2".getBytes());

    final DataObject profile1 = DataObject.newStructureData(name1, start1, weekName1);
    final DataObject profile2 = DataObject.newStructureData(name2, start2, weekName2);

    final DataObject seasonProfiles = DataObject.newArrayData(List.of(profile1, profile2));

    final String result = this.decoder.decodeSeasonProfiles(seasonProfiles);

    assertThat(result)
        .isEqualToIgnoringNewLines(
            "Name1: 2024-05-24T08:51:01.000, weekName: WeekName1\n"
                + "Name2: 2024-05-24T08:52:02.000, weekName: WeekName2");
  }
}
