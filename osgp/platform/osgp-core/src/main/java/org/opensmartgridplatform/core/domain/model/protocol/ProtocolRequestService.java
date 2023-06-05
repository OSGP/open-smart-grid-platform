// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.domain.model.protocol;

import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;
import org.opensmartgridplatform.shared.infra.jms.ProtocolRequestMessage;

public interface ProtocolRequestService {
  public void send(ProtocolRequestMessage message, ProtocolInfo protocolInfo);

  public boolean isSupported(ProtocolInfo protocolInfo);
}
