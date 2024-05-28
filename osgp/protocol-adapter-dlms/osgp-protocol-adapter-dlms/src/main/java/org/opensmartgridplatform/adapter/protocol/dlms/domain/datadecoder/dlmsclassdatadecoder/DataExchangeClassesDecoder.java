// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.datadecoder.dlmsclassdatadecoder;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CircuitSwitchedStatusDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ModemRegistrationStatusDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PacketSwitchedStatusDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SignalQualityDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service(value = "dataExchangeClassesDecoder")
public class DataExchangeClassesDecoder {

  // This class contains decoding functions for the attributes of the DLMS classes in DLMS Blue
  // book chapter "Interface classes for setting up data exchange via local ports and modems".

  private final DlmsHelper dlmsHelper;

  @Autowired
  public DataExchangeClassesDecoder(final DlmsHelper dlmsHelper) {
    this.dlmsHelper = dlmsHelper;
  }

  // Class-id 47: GSM diagnostic
  public String decodeModemRegistrationStatus(final DataObject attributeData) {
    try {
      final int value = this.dlmsHelper.readInteger(attributeData, "read status");
      return ModemRegistrationStatusDto.fromIndexValue(value).name();
    } catch (final Exception e) {
      return "decoding modem registration status failed: " + e.getMessage();
    }
  }

  public String decodeCsStatus(final DataObject attributeData) {
    try {
      final int value = this.dlmsHelper.readInteger(attributeData, "read cs status");
      return CircuitSwitchedStatusDto.fromIndexValue(value).name();
    } catch (final Exception e) {
      return "decoding cs status failed: " + e.getMessage();
    }
  }

  public String decodePsStatus(final DataObject attributeData) {
    try {
      final int value = this.dlmsHelper.readInteger(attributeData, "read ps status");
      return PacketSwitchedStatusDto.fromIndexValue(value).name();
    } catch (final Exception e) {
      return "decoding ps status failed: " + e.getMessage();
    }
  }

  public String decodeCellInfo(final DataObject attributeData) {
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

  public String decodeAdjacentCells(final DataObject attributeData) {
    try {
      final StringBuilder decoded = new StringBuilder();
      final List<DataObject> adjacentCells = attributeData.getValue();
      for (final DataObject dataObject : adjacentCells) {
        final List<DataObject> structure = dataObject.getValue();
        if (!decoded.isEmpty()) {
          decoded.append(", ");
        }
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
