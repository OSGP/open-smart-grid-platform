// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.microgrids.infra.jms.messageprocessors;

import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.stereotype.Component;

@Component
public class GetDataResponseMessageProcessor extends AbstractDomainResponseMessageProcessor {

  protected GetDataResponseMessageProcessor() {
    super(MessageType.GET_DATA);
  }
}
