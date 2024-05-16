// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.datadecoder;

import static org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeType.ADJACENT_CELLS;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeType.CAPTURE_OBJECT_DEFINITION;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeType.CAPTURE_OBJECT_DEFINITION_LIST;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeType.CELL_INFO;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeType.CIRCUIT_SWITCHED_STATUS;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeType.DATE_TIME;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeType.MODEM_REGISTRATION_STATUS;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeType.PACKET_SWITCHED_STATUS;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeType.SCALER_UNIT;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeType.SORT_METHOD;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeType.UNKNOWN;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CircuitSwitchedStatusDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemObjectDefinitionDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ModemRegistrationStatusDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PacketSwitchedStatusDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SignalQualityDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service(value = "basicDlmsClassDataDecoder")
public class BasicDlmsClassDataDecoder {

  @Autowired private DlmsHelper dlmsHelper;
  @Autowired private BasicDlmsDataDecoder basicDlmsDataDecoder;

  private final Map<AttributeType, Function<DataObject, String>> decoderMap =
      new EnumMap<>(AttributeType.class);

  public BasicDlmsClassDataDecoder() {
    this.decoderMap.put(ADJACENT_CELLS, this::decodeAdjacentCells);
    this.decoderMap.put(CAPTURE_OBJECT_DEFINITION_LIST, this::decodeCaptureObjects);
    this.decoderMap.put(CAPTURE_OBJECT_DEFINITION, this::decodeCaptureObject);
    this.decoderMap.put(CELL_INFO, this::decodeCellInfo);
    this.decoderMap.put(CIRCUIT_SWITCHED_STATUS, this::decodeCsStatus);
    this.decoderMap.put(DATE_TIME, this::decodeDateTime);
    this.decoderMap.put(MODEM_REGISTRATION_STATUS, this::decodeModemRegistrationStatus);
    this.decoderMap.put(PACKET_SWITCHED_STATUS, this::decodePsStatus);
    this.decoderMap.put(SCALER_UNIT, this::decodeAttributeScalerUnit);
    this.decoderMap.put(SORT_METHOD, this::decodeSortMethod);
    this.decoderMap.put(UNKNOWN, this::decodeUnknown);
  }

  public String decodeAttributeValue(
      final int classId, final int attributeId, final DataObject attributeData) {

    final InterfaceClass dlmsClass = InterfaceClass.interfaceClassFor(classId);
    final AttributeType attributeType = dlmsClass.getAttributeType(attributeId);

    final Function<DataObject, String> decoder = this.decoderMap.getOrDefault(attributeType, null);
    if (decoder != null) {
      return decoder.apply(attributeData);
    }

    return null;
  }

  private String decodeUnknown(final DataObject attributeData) {
    return null;
  }

  private String decodeDateTime(final DataObject attributeData) {
    try {
      return this.basicDlmsDataDecoder.decodeDateTime(attributeData);
    } catch (final Exception e) {
      return "decoding datetime failed: " + e.getMessage();
    }
  }

  // Class-id 3, 4, 5: (Extended / Demand) Register
  private String decodeAttributeScalerUnit(final DataObject attributeData) {
    try {
      return this.dlmsHelper.getScalerUnit(attributeData, "read scaler unit");
    } catch (final Exception e) {
      return "decoding scaler unit failed: " + e.getMessage();
    }
  }

  // Class-id 7: Profile Generic
  private String decodeCaptureObjects(final DataObject attributeData) {
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

  private String decodeCaptureObject(final DataObject attributeData) {
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

  private String decodeSortMethod(final DataObject attributeData) {
    try {
      final int value = this.dlmsHelper.readInteger(attributeData, "read sort method");

      return switch (value) {
        case 1 -> "fifo";
        case 2 -> "lifo";
        case 3 -> "largest";
        case 4 -> "smallest";
        case 5 -> "nearest_to_zero";
        case 6 -> "furthest_from_zero";
        default -> "unknown sort method " + value;
      };
    } catch (final Exception e) {
      return "decoding sort method failed: " + e.getMessage();
    }
  }

  // Class-id 47: GSM diagnostic
  private String decodeModemRegistrationStatus(final DataObject attributeData) {
    try {
      final int value = this.dlmsHelper.readInteger(attributeData, "read status");
      return ModemRegistrationStatusDto.fromIndexValue(value).name();
    } catch (final Exception e) {
      return "decoding modem registration status failed: " + e.getMessage();
    }
  }

  private String decodeCsStatus(final DataObject attributeData) {
    try {
      final int value = this.dlmsHelper.readInteger(attributeData, "read cs status");
      return CircuitSwitchedStatusDto.fromIndexValue(value).name();
    } catch (final Exception e) {
      return "decoding cs status failed: " + e.getMessage();
    }
  }

  private String decodePsStatus(final DataObject attributeData) {
    try {
      final int value = this.dlmsHelper.readInteger(attributeData, "read ps status");
      return PacketSwitchedStatusDto.fromIndexValue(value).name();
    } catch (final Exception e) {
      return "decoding ps status failed: " + e.getMessage();
    }
  }

  private String decodeCellInfo(final DataObject attributeData) {
    try {
      final List<DataObject> dataObjects = attributeData.getValue();
      return "cellId: "
          + dataObjects.get(0).getValue()
          + ", locationId: "
          + dataObjects.get(1).getValue()
          + ", signalQuality: "
          + SignalQualityDto.fromIndexValue((short) dataObjects.get(2).getValue()).name()
          + ", bitErrorRate: "
          + (short) dataObjects.get(3).getValue()
          + ", mobile country code: "
          + dataObjects.get(4).getValue()
          + ", mobile network code: "
          + dataObjects.get(5).getValue()
          + ", channel: "
          + dataObjects.get(6).getValue();
    } catch (final Exception e) {
      return "decoding cell info failed: " + e.getMessage();
    }
  }

  private String decodeAdjacentCells(final DataObject attributeData) {
    try {
      final StringBuilder decoded = new StringBuilder();
      final List<DataObject> adjacentCells = attributeData.getValue();
      for (final DataObject dataObject : adjacentCells) {
        final List<DataObject> structure = dataObject.getValue();

        decoded.append("cellId: ").append(structure.get(0).getValue().toString());
        decoded
            .append(", signalQuality: ")
            .append(SignalQualityDto.fromIndexValue((short) structure.get(1).getValue()).name());
      }
      return decoded.toString();
    } catch (final Exception e) {
      return "decoding cell info failed: " + e.getMessage();
    }
  }
}
