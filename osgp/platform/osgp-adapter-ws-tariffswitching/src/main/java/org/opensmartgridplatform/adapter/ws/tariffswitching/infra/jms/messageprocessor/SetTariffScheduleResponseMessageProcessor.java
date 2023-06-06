// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.tariffswitching.infra.jms.messageprocessor;

import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.stereotype.Component;

@Component
public class SetTariffScheduleResponseMessageProcessor extends DomainResponseMessageProcessor {

  protected SetTariffScheduleResponseMessageProcessor() {
    super(MessageType.SET_TARIFF_SCHEDULE);
  }
}
