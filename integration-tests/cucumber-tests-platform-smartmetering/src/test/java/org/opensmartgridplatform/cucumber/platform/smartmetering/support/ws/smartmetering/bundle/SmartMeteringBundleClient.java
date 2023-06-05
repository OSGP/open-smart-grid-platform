// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.BundleAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.BundleAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.BundleRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.BundleResponse;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.SmartMeteringBaseClient;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.opensmartgridplatform.shared.infra.ws.DefaultWebServiceTemplateFactory;
import org.opensmartgridplatform.shared.infra.ws.WebServiceMessageCallbackHeaderFieldsAdder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

@Component
public class SmartMeteringBundleClient extends SmartMeteringBaseClient {

  @Autowired private DefaultWebServiceTemplateFactory smartMeteringBundleWebServiceTemplateFactory;

  public BundleAsyncResponse sendBundleRequest(final BundleRequest request)
      throws WebServiceSecurityException {
    return (BundleAsyncResponse) this.getTemplate().marshalSendAndReceive(request);
  }

  public BundleAsyncResponse sendBundleRequest(
      final BundleRequest request, final Map<String, String> extraHeaders)
      throws WebServiceSecurityException {
    final WebServiceMessageCallbackHeaderFieldsAdder messageCallback =
        new WebServiceMessageCallbackHeaderFieldsAdder(extraHeaders);
    return (BundleAsyncResponse) this.getTemplate().marshalSendAndReceive(request, messageCallback);
  }

  public BundleResponse retrieveBundleResponse(final BundleAsyncRequest asyncRequest)
      throws WebServiceSecurityException {

    final String correlationUid = asyncRequest.getCorrelationUid();
    this.waitForNotification(correlationUid);

    return (BundleResponse) this.getTemplate().marshalSendAndReceive(asyncRequest);
  }

  private WebServiceTemplate getTemplate() throws WebServiceSecurityException {
    return this.smartMeteringBundleWebServiceTemplateFactory.getTemplate(
        this.getOrganizationIdentification(), this.getUserName());
  }
}
