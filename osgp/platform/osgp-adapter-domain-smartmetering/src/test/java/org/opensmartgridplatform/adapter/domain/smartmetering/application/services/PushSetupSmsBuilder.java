// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import java.util.ArrayList;
import java.util.List;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemObisCode;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemObjectDefinition;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.MessageType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushSetupSms;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SendDestinationAndMethod;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.TransportServiceType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.WindowElement;

public class PushSetupSmsBuilder {

  private CosemObisCode logicalName = new CosemObisCode(new int[] {1, 2, 3, 4, 5, 6});
  private final TransportServiceType transportServiceType = TransportServiceType.TCP;
  private final MessageType messageType = MessageType.A_XDR_ENCODED_X_DLMS_APDU;
  private SendDestinationAndMethod sendDestinationAndMethod =
      new SendDestinationAndMethod(this.transportServiceType, "destination", this.messageType);
  private Integer randomisationStartInterval = Integer.valueOf(1);
  private Integer numberOfRetries = Integer.valueOf(10);
  private Integer repetitionDelay = Integer.valueOf(2);

  private List<CosemObjectDefinition> pushObjectList;
  private List<WindowElement> communicationWindow;

  public PushSetupSms build() {

    final PushSetupSms.Builder pushSetupSmsBuilder = new PushSetupSms.Builder();
    pushSetupSmsBuilder
        .withLogicalName(this.logicalName)
        .withPushObjectList(this.pushObjectList)
        .withSendDestinationAndMethod(this.sendDestinationAndMethod)
        .withCommunicationWindow(this.communicationWindow)
        .withRandomisationStartInterval(this.randomisationStartInterval)
        .withNumberOfRetries(this.numberOfRetries)
        .withRepetitionDelay(this.repetitionDelay);
    return pushSetupSmsBuilder.build();
  }

  public PushSetupSmsBuilder withNullValues() {
    this.logicalName = null;
    this.pushObjectList = null;
    this.sendDestinationAndMethod = null;
    this.communicationWindow = null;
    this.randomisationStartInterval = null;
    this.numberOfRetries = null;
    this.repetitionDelay = null;
    return this;
  }

  public PushSetupSmsBuilder withEmptyLists(
      final ArrayList<CosemObjectDefinition> pushObjectList,
      final ArrayList<WindowElement> communicationWindow) {
    this.pushObjectList = pushObjectList;
    this.communicationWindow = communicationWindow;
    return this;
  }

  public PushSetupSmsBuilder withFilledLists(
      final CosemObjectDefinition cosemObjectDefinition, final WindowElement windowElement) {
    this.pushObjectList = new ArrayList<>();
    this.pushObjectList.add(cosemObjectDefinition);
    this.communicationWindow = new ArrayList<>();
    this.communicationWindow.add(windowElement);
    return this;
  }
}
