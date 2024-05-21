// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.datadecoder.dlmsclassdatadecoder;

import static org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeType.ADJACENT_CELLS;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeType.CAPTURE_OBJECT_DEFINITION;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeType.CAPTURE_OBJECT_LIST;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeType.CELL_INFO;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeType.CIRCUIT_SWITCHED_STATUS;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeType.DATE_TIME;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeType.MODEM_REGISTRATION_STATUS;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeType.PACKET_SWITCHED_STATUS;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeType.SCALER_UNIT;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeType.SORT_METHOD;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeType.UNKNOWN;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.datadecoder.BasicDlmsDataDecoder;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service(value = "dlmsClassDataDecoder")
public class DlmsClassDataDecoder {

  private final DlmsHelper dlmsHelper;
  private final BasicDlmsDataDecoder basicDlmsDataDecoder;

  private final Map<AttributeType, Function<DataObject, String>> map =
      new EnumMap<>(AttributeType.class);

  @Autowired
  public DlmsClassDataDecoder(
      final DlmsHelper dlmsHelper,
      final BasicDlmsDataDecoder basicDlmsDataDecoder,
      final DataExchangeClassesDecoder dataExchangeDecoder,
      final MeasurementDataClassesDecoder measurementDataDecoder) {
    this.dlmsHelper = dlmsHelper;
    this.basicDlmsDataDecoder = basicDlmsDataDecoder;

    this.map.put(ADJACENT_CELLS, dataExchangeDecoder::decodeAdjacentCells);
    this.map.put(CAPTURE_OBJECT_LIST, measurementDataDecoder::decodeCaptureObjects);
    this.map.put(CAPTURE_OBJECT_DEFINITION, measurementDataDecoder::decodeCaptureObject);
    this.map.put(CELL_INFO, dataExchangeDecoder::decodeCellInfo);
    this.map.put(CIRCUIT_SWITCHED_STATUS, dataExchangeDecoder::decodeCsStatus);
    this.map.put(DATE_TIME, this::decodeDateTime);
    this.map.put(MODEM_REGISTRATION_STATUS, dataExchangeDecoder::decodeModemRegistrationStatus);
    this.map.put(PACKET_SWITCHED_STATUS, dataExchangeDecoder::decodePsStatus);
    this.map.put(SCALER_UNIT, measurementDataDecoder::decodeAttributeScalerUnit);
    this.map.put(SORT_METHOD, measurementDataDecoder::decodeSortMethod);
    this.map.put(UNKNOWN, this::decodeUnknown);
  }

  public String decodeAttributeValue(
      final int classId, final int attributeId, final DataObject attributeData) {

    final InterfaceClass dlmsClass = InterfaceClass.interfaceClassFor(classId);
    final AttributeType attributeType = dlmsClass.getAttributeType(attributeId);

    final Function<DataObject, String> decoder = this.map.getOrDefault(attributeType, null);
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
}
