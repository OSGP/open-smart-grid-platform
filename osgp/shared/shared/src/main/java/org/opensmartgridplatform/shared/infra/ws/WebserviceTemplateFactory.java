/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.infra.ws;

import java.net.URL;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.springframework.ws.client.core.WebServiceTemplate;

public interface WebserviceTemplateFactory {
  WebServiceTemplate getTemplate(
      final String organisationIdentification, final String userName, final String notificationURL)
      throws WebServiceSecurityException;

  WebServiceTemplate getTemplate(
      final String organisationIdentification, final String userName, final URL targetUri)
      throws WebServiceSecurityException;
}
