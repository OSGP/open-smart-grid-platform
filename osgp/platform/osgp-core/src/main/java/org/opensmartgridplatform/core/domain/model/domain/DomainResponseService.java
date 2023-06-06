// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.domain.model.domain;

import org.opensmartgridplatform.shared.infra.jms.ProtocolRequestMessage;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;

public interface DomainResponseService {
  public void send(ProtocolResponseMessage message);

  public void send(ProtocolRequestMessage message, Exception e);
}
