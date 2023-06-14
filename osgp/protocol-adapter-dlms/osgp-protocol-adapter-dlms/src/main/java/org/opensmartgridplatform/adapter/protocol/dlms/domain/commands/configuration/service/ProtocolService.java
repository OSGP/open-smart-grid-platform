// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;

public interface ProtocolService {

  /** Indicates whether this service can handle the protocol */
  boolean handles(Protocol protocol);
}
