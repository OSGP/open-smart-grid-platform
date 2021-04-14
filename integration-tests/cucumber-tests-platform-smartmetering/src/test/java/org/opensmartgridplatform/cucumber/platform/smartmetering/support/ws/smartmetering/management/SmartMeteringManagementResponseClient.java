/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.AsyncRequest;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.SmartMeteringBaseClient;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.opensmartgridplatform.shared.infra.ws.DefaultWebServiceTemplateFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

@Component
public class SmartMeteringManagementResponseClient<T, V extends AsyncRequest>
    extends SmartMeteringBaseClient {

  @Autowired
  private DefaultWebServiceTemplateFactory smartMeteringManagementWebServiceTemplateFactory;

  private WebServiceTemplate getTemplate() throws WebServiceSecurityException {
    return this.smartMeteringManagementWebServiceTemplateFactory.getTemplate(
        this.getOrganizationIdentification(), this.getUserName());
  }

  @SuppressWarnings("unchecked")
  public T getResponse(final V request) throws WebServiceSecurityException {

    this.waitForNotification(request.getCorrelationUid());
    return (T) this.getTemplate().marshalSendAndReceive(request);
  }
}
