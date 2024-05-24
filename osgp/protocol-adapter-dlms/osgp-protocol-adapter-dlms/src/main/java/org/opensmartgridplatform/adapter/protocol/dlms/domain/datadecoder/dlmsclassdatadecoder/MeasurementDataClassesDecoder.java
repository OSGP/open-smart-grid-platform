// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.datadecoder.dlmsclassdatadecoder;

import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.datadecoder.BasicDlmsDataDecoder;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemObjectDefinitionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service(value = "measurementDataClassesDecoder")
public class MeasurementDataClassesDecoder {

  // This class contains decoding functions for the attributes of the DLMS classes in DLMS Blue
  // book chapter "Interface classes for parameters and measurement data".

  private final DlmsHelper dlmsHelper;
  private final BasicDlmsDataDecoder basicDlmsDataDecoder;

  @Autowired
  public MeasurementDataClassesDecoder(
      final DlmsHelper dlmsHelper, final BasicDlmsDataDecoder basicDlmsDataDecoder) {
    this.dlmsHelper = dlmsHelper;
    this.basicDlmsDataDecoder = basicDlmsDataDecoder;
  }

  // Class-id 3, 4, 5: (Extended / Demand) Register
  public String decodeAttributeScalerUnit(final DataObject attributeData) {
    try {
      return this.dlmsHelper.getScalerUnit(attributeData, "read scaler unit");
    } catch (final Exception e) {
      return "decoding scaler unit failed: " + e.getMessage();
    }
  }

  // Class-id 7: Profile Generic
  public String decodeCaptureObjects(final DataObject attributeData) {
    try {
      return this.dlmsHelper
          .readListOfObjectDefinition(attributeData, "read capture objects")
          .stream()
          .map(CosemObjectDefinitionDto::toDsmrString)
          .collect(Collectors.joining(", "));
    } catch (final Exception e) {
      return "decoding capture objects failed: " + e.getMessage();
    }
  }

  public String decodeCaptureObject(final DataObject attributeData) {
    try {
      if (attributeData.getType() == Type.NULL_DATA) {
        return "no capture object";
      } else {
        return this.dlmsHelper
            .readObjectDefinition(attributeData, "read capture object")
            .toDsmrString();
      }
    } catch (final Exception e) {
      return "decoding capture object failed: " + e.getMessage();
    }
  }

  public String decodeSortMethod(final DataObject attributeData) {
    try {
      final int value = this.dlmsHelper.readInteger(attributeData, "read sort method");
      return SortMethod.getByValue(value).name();
    } catch (final Exception e) {
      return "decoding sort method failed: " + e.getMessage();
    }
  }

  private enum SortMethod {
    FIFO(1),
    LIFO(2),
    LARGEST(3),
    SMALLEST(4),
    NEAREST_TO_ZERO(5),
    FURTHEST_FROM_ZERO(6),
    UNKNOWN_SORT_METHOD(255);

    private final int value;

    SortMethod(final int value) {
      this.value = value;
    }

    public static SortMethod getByValue(final int value) {
      for (final SortMethod method : SortMethod.values()) {
        if (method.value == value) {
          return method;
        }
      }

      return UNKNOWN_SORT_METHOD;
    }
  }
}
