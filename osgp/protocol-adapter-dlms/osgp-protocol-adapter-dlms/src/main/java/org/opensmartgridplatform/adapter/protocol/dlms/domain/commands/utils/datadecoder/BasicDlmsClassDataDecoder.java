// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.datadecoder;

import static org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass.DEMAND_REGISTER;
import static org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass.EXTENDED_REGISTER;
import static org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass.GSM_DIAGNOSTIC;
import static org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass.PROFILE_GENERIC;
import static org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass.REGISTER;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.GsmDiagnosticAttribute.CIRCUIT_SWITCHED_STATUS;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.GsmDiagnosticAttribute.MODEM_REGISTRATION_STATUS;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.GsmDiagnosticAttribute.PACKET_SWITCHED_STATUS;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.ProfileGenericAttribute.CAPTURE_OBJECTS;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.ProfileGenericAttribute.SORT_METHOD;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.ProfileGenericAttribute.SORT_OBJECT;

import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.DemandRegisterAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.RegisterAttribute;
import org.opensmartgridplatform.dlms.objectconfig.Attribute;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CircuitSwitchedStatusDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemObjectDefinitionDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ModemRegistrationStatusDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PacketSwitchedStatusDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service(value = "basicDlmsClassDataDecoder")
public class BasicDlmsClassDataDecoder {

  @Autowired private DlmsHelper dlmsHelper;

  public String decodeAttributeValue(
      final CosemObject objectFromProfile,
      final Attribute attributeFromProfile,
      final DataObject attributeData)
      throws ProtocolAdapterException {

    final int classId = objectFromProfile.getClassId();

    if (this.attributeIsScalerUnit(classId, attributeFromProfile.getId())) {
      return this.decodeAttributeScalerUnit(attributeData);
    }

    if (classId == PROFILE_GENERIC.id()) {
      if (attributeFromProfile.getId() == CAPTURE_OBJECTS.attributeId()) {
        return this.decodeCaptureObjects(attributeData);
      } else if (attributeFromProfile.getId() == SORT_METHOD.attributeId()) {
        return this.decodeSortMethod(attributeData);
      } else if (attributeFromProfile.getId() == SORT_OBJECT.attributeId()) {
        return this.decodeSortObject(attributeData);
      }
    }

    if (classId == GSM_DIAGNOSTIC.id()) {
      if (attributeFromProfile.getId() == MODEM_REGISTRATION_STATUS.attributeId()) {
        return this.decodeStatus(attributeData);
      } else if (attributeFromProfile.getId() == CIRCUIT_SWITCHED_STATUS.attributeId()) {
        return this.decodeCsStatus(attributeData);
      } else if (attributeFromProfile.getId() == PACKET_SWITCHED_STATUS.attributeId()) {
        return this.decodePsStatus(attributeData);
      }
    }

    return null;
  }

  private boolean attributeIsScalerUnit(final int classId, final int attributeId) {
    return (((classId == REGISTER.id() || classId == EXTENDED_REGISTER.id())
            && attributeId == RegisterAttribute.SCALER_UNIT.attributeId())
        || (classId == DEMAND_REGISTER.id()
            && attributeId == DemandRegisterAttribute.SCALER_UNIT.attributeId()));
  }

  // Class-id 3: Register
  private String decodeAttributeScalerUnit(final DataObject attributeData)
      throws ProtocolAdapterException {
    return this.dlmsHelper.getScalerUnit(attributeData, "read scaler unit");
  }

  // Class-id 7: Profile Generic
  private String decodeCaptureObjects(final DataObject attributeData)
      throws ProtocolAdapterException {
    return this.dlmsHelper
        .readListOfObjectDefinition(attributeData, "read capture objects")
        .stream()
        .map(CosemObjectDefinitionDto::toDsmrString)
        .collect(Collectors.joining(", "));
  }

  private String decodeSortObject(final DataObject attributeData) throws ProtocolAdapterException {
    if (attributeData.getType() == Type.NULL_DATA) {
      return "no sort object";
    } else {
      return this.dlmsHelper.readObjectDefinition(attributeData, "read sort object").toDsmrString();
    }
  }

  private String decodeSortMethod(final DataObject attributeData) throws ProtocolAdapterException {
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
  }

  // Class-id 47: GSM diagnostic
  private String decodeStatus(final DataObject attributeData) throws ProtocolAdapterException {
    final int value = this.dlmsHelper.readInteger(attributeData, "read status");
    return ModemRegistrationStatusDto.fromIndexValue(value).name();
  }

  private String decodeCsStatus(final DataObject attributeData) throws ProtocolAdapterException {
    final int value = this.dlmsHelper.readInteger(attributeData, "read cs status");
    return CircuitSwitchedStatusDto.fromIndexValue(value).name();
  }

  private String decodePsStatus(final DataObject attributeData) throws ProtocolAdapterException {
    final int value = this.dlmsHelper.readInteger(attributeData, "read ps status");
    return PacketSwitchedStatusDto.fromIndexValue(value).name();
  }
}
