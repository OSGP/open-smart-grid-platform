// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.datadecoder.dlmsclassdatadecoder;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.datadecoder.BasicDlmsDataDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service(value = "timeAndEventsClassesDecoder")
public class TimeAndEventsClassesDecoder {

  private final DlmsHelper dlmsHelper;
  private final BasicDlmsDataDecoder basicDlmsDataDecoder;

  @Autowired
  public TimeAndEventsClassesDecoder(
      final DlmsHelper dlmsHelper, final BasicDlmsDataDecoder basicDlmsDataDecoder) {
    this.dlmsHelper = dlmsHelper;
    this.basicDlmsDataDecoder = basicDlmsDataDecoder;
  }

  // Class-id 11: Special days table
  public String decodeSpecialDays(final DataObject attributeData) {
    final List<String> specialDaysDecoded = new ArrayList<>();

    try {
      if (attributeData.getType() == Type.ARRAY) {
        final List<DataObject> specialDays = attributeData.getValue();
        for (final DataObject specialDay : specialDays) {
          specialDaysDecoded.add(this.decodeSpecialDay(specialDay));
        }

        return String.join("\n", specialDaysDecoded);
      } else {
        return "ARRAY expected, but found " + attributeData.getType().name();
      }
    } catch (final Exception e) {
      return "decoding special days failed: " + e.getMessage();
    }
  }

  private String decodeSpecialDay(final DataObject specialDay) {
    try {
      if (specialDay.getType() == Type.STRUCTURE) {
        final List<DataObject> fields = specialDay.getValue();
        final int index = this.dlmsHelper.readInteger(fields.get(0), "read special day index");
        final String date = this.basicDlmsDataDecoder.decodeDate(fields.get(1));
        final int day_id = this.dlmsHelper.readInteger(fields.get(2), "read special day id");

        return index + ". (id " + day_id + "): " + date;
      } else {
        return "STRUCTURE expected, but found " + specialDay.getType().name();
      }
    } catch (final Exception e) {
      return "decoding special day failed: " + e.getMessage();
    }
  }
}
