// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.datadecoder;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service(value = "basicDlmsDataDecoder")
public class BasicDlmsDataDecoder {

  private static final List<DataObject.Type> simpleNumbers =
      List.of(
          DataObject.Type.DOUBLE_LONG,
          DataObject.Type.DOUBLE_LONG_UNSIGNED,
          DataObject.Type.INTEGER,
          DataObject.Type.UNSIGNED,
          DataObject.Type.LONG64,
          DataObject.Type.LONG64_UNSIGNED,
          DataObject.Type.LONG_INTEGER,
          DataObject.Type.LONG_UNSIGNED);

  private final DlmsHelper dlmsHelper;

  @Autowired
  public BasicDlmsDataDecoder(final DlmsHelper dlmsHelper) {
    this.dlmsHelper = dlmsHelper;
  }

  public String decodeAttributeValue(final DataObject attributeData)
      throws ProtocolAdapterException {

    if (simpleNumbers.contains(attributeData.getType())) {
      return this.decodeNumber(attributeData);
    }

    if (attributeData.getType() == Type.BOOLEAN) {
      return this.decodeBoolean(attributeData);
    }

    if (attributeData.getType() == Type.OCTET_STRING) {
      return this.decodeOctetString(attributeData);
    }

    if (attributeData.getType() == Type.VISIBLE_STRING) {
      return this.decodeVisibleString(attributeData);
    }

    return null;
  }

  private String decodeNumber(final DataObject attributeData) throws ProtocolAdapterException {
    final Long value = this.dlmsHelper.readLong(attributeData, "read number");

    if (value == null) {
      return "null";
    } else {
      return value.toString();
    }
  }

  private String decodeBoolean(final DataObject attributeData) {
    return (int) attributeData.getValue() == 1 ? "true" : "false";
  }

  private String decodeOctetString(final DataObject attributeData) {

    try {
      return this.decodeDateTime(attributeData);
    } catch (final Exception ignored) {
      // Decoding failed, so byte array is not a date time
    }

    final byte[] byteArray = attributeData.getValue();
    return new String(byteArray);
  }

  public String decodeDateTime(final DataObject attributeData) throws ProtocolAdapterException {
    return this.cosemDateTimeToString(
        this.dlmsHelper.readDateTime(attributeData, "Try to read time from bytes"));
  }

  private String cosemDateTimeToString(final CosemDateTimeDto dateTimeDto) {
    return this.getDayOfWeek(dateTimeDto)
        + dateTimeDto.getDate().asLocalDate()
        + ", "
        + dateTimeDto.getTime().asLocalTime().toString()
        + ", deviation="
        + dateTimeDto.getDeviation()
        + ", "
        + dateTimeDto.getClockStatus()
        + ']';
  }

  private String getDayOfWeek(final CosemDateTimeDto dateTimeDto) {
    return switch (dateTimeDto.getDate().getDayOfWeek()) {
      case 1 -> "Monday ";
      case 2 -> "Tuesday ";
      case 3 -> "Wednesday ";
      case 4 -> "Thursday ";
      case 5 -> "Friday ";
      case 6 -> "Saturday ";
      case 7 -> "Sunday ";
      case 0xFF -> "Day of week not specified, ";
      default -> "DayOfWeek value unknown: " + dateTimeDto.getDate().getDayOfWeek() + ", ";
    };
  }

  private String decodeVisibleString(final DataObject attributeData) {

    final byte[] byteArray = attributeData.getValue();
    return new String(byteArray);
  }
}
