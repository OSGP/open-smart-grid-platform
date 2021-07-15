/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.domain.model.domain;

import org.opensmartgridplatform.domain.core.entities.DomainInfo;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;

public interface DomainRequestService {
  public void send(RequestMessage message, String messageType, DomainInfo domainInfo);
}
