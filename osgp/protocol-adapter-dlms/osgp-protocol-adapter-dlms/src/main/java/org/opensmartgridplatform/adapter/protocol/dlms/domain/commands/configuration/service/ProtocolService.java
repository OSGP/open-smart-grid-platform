/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;

public interface ProtocolService {

  /** Indicates whether this service can handle the protocol */
  boolean handles(Protocol protocol);
}
