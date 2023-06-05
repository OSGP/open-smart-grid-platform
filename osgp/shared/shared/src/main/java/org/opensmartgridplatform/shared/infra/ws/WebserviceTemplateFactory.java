// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
