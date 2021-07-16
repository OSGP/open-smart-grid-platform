/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.common;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.smcommon.DeleteResponseDataRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.smcommon.DeleteResponseDataResponse;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.SmartMeteringBaseClient;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.opensmartgridplatform.shared.infra.ws.DefaultWebServiceTemplateFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

@Component
public class SmartMeteringCommonClient extends SmartMeteringBaseClient {

  @Autowired private DefaultWebServiceTemplateFactory smartMeteringCommonWebServiceTemplateFactory;

  public DeleteResponseDataResponse sendDeleteResponseDataRequest(
      final DeleteResponseDataRequest request) throws WebServiceSecurityException {
    return (DeleteResponseDataResponse) this.getTemplate().marshalSendAndReceive(request);
  }

  private WebServiceTemplate getTemplate() throws WebServiceSecurityException {
    return this.smartMeteringCommonWebServiceTemplateFactory.getTemplate(
        this.getOrganizationIdentification(), this.getUserName());
  }
}
