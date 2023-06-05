// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.publiclighting.support.ws.tariffswitching;

import org.opensmartgridplatform.adapter.ws.schema.tariffswitching.adhocmanagement.GetStatusAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.tariffswitching.adhocmanagement.GetStatusAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.tariffswitching.adhocmanagement.GetStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.tariffswitching.adhocmanagement.GetStatusResponse;
import org.opensmartgridplatform.cucumber.platform.support.ws.BaseClient;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.opensmartgridplatform.shared.infra.ws.DefaultWebServiceTemplateFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

@Component
public class TariffSwitchingAdHocManagementClient extends BaseClient {

  @Autowired private DefaultWebServiceTemplateFactory tariffSwitchingAdHocManagementWstf;

  public GetStatusAsyncResponse getStatus(final GetStatusRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate webServiceTemplate =
        this.tariffSwitchingAdHocManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (GetStatusAsyncResponse) webServiceTemplate.marshalSendAndReceive(request);
  }

  public GetStatusResponse getGetStatusResponse(final GetStatusAsyncRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate webServiceTemplate =
        this.tariffSwitchingAdHocManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (GetStatusResponse) webServiceTemplate.marshalSendAndReceive(request);
  }
}
