//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.AsyncResponse;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.SmartMeteringBaseClient;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.opensmartgridplatform.shared.infra.ws.DefaultWebServiceTemplateFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

@Component
public class SmartMeteringAdHocRequestClient<T extends AsyncResponse, V>
    extends SmartMeteringBaseClient {

  @Autowired private DefaultWebServiceTemplateFactory smartMeteringAdHocWebServiceTemplateFactory;

  private WebServiceTemplate getTemplate() throws WebServiceSecurityException {
    return this.smartMeteringAdHocWebServiceTemplateFactory.getTemplate(
        this.getOrganizationIdentification(), this.getUserName());
  }

  @SuppressWarnings("unchecked")
  public T doRequest(final V request) throws WebServiceSecurityException {
    return (T) this.getTemplate().marshalSendAndReceive(request);
  }
}
