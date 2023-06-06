// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.domain.model.protocol;

import javax.jms.Destination;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;

public interface ProtocolResponseService {
  public void send(
      final ResponseMessage responseMessage,
      final String messageType,
      final ProtocolInfo protocolInfo,
      MessageMetadata messageMetadata);

  public void sendWithDestination(
      final ResponseMessage responseMessage,
      final String messageType,
      final ProtocolInfo protocolInfo,
      MessageMetadata messageMetadata,
      final Destination destination);
}
