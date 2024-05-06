// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.pushsetup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.GetResultImpl;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MessageTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.TransportServiceTypeDto;

public class PushSetupBuilder {

  private final AccessResultCode accessResultCode;
  private final ObisCode obisCode;
  private final TransportServiceTypeDto transportServiceTypeDto;

  public PushSetupBuilder(
      final AccessResultCode resultCode,
      final ObisCode obisCode,
      final TransportServiceTypeDto transportServiceTypeDto) {
    this.accessResultCode = resultCode;
    this.obisCode = obisCode;
    this.transportServiceTypeDto = transportServiceTypeDto;
  }

  public GetResultImpl buildPushObjectList() {

    final List<DataObject> list = new ArrayList<>();
    list.add(this.createCosemObjectDefinition(10));
    list.add(this.createCosemObjectDefinition(20));
    list.add(this.createCosemObjectDefinition(30));
    list.add(this.createCosemObjectDefinition(40));
    final DataObject arrayData = DataObject.newArrayData(list);
    return new GetResultImpl(arrayData, this.accessResultCode);
  }

  public GetResultImpl buildSendDestinationAndMethod() {
    return new GetResultImpl(this.createSendDestinationAndMethod(), this.accessResultCode);
  }

  public GetResultImpl buildCommunicationWindow() {
    final List<DataObject> list =
        List.of(
            DataObject.newDateTimeData(new CosemDateTime(1, 1, 1, 1, 1, 1, 1)),
            DataObject.newDateTimeData(new CosemDateTime(1, 1, 1, 1, 1, 1, 1)));

    final DataObject arrayData = DataObject.newArrayData(List.of(DataObject.newArrayData(list)));
    return new GetResultImpl(arrayData, this.accessResultCode);
  }

  private DataObject createCosemObjectDefinition(final int i) {
    final DataObject classIdElement = DataObject.newUInteger16Data(1 + i);
    final DataObject obisCodeElement = DataObject.newOctetStringData(this.obisCode.bytes());
    final DataObject attributeIdElement = DataObject.newInteger8Data((byte) (2 + i));
    final DataObject dataIndexElement = DataObject.newUInteger16Data(3 + i);

    return DataObject.newStructureData(
        Arrays.asList(classIdElement, obisCodeElement, attributeIdElement, dataIndexElement));
  }

  private DataObject createSendDestinationAndMethod() {
    final List<DataObject> sendDestinationAndMethodElements = new ArrayList<>();
    sendDestinationAndMethodElements.add(
        DataObject.newEnumerateData(this.transportServiceTypeDto.getDlmsEnumValue()));
    sendDestinationAndMethodElements.add(DataObject.newOctetStringData("destintion".getBytes()));
    sendDestinationAndMethodElements.add(
        DataObject.newEnumerateData(MessageTypeDto.A_XDR_ENCODED_X_DLMS_APDU.getDlmsEnumValue()));

    return DataObject.newStructureData(sendDestinationAndMethodElements);
  }
}
