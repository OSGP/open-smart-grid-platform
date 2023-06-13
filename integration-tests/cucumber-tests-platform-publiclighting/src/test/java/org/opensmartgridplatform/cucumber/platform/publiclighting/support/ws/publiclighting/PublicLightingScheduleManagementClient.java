// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.publiclighting.support.ws.publiclighting;

import org.opensmartgridplatform.adapter.ws.schema.publiclighting.schedulemanagement.SetScheduleAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.schedulemanagement.SetScheduleAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.schedulemanagement.SetScheduleRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.schedulemanagement.SetScheduleResponse;
import org.opensmartgridplatform.cucumber.platform.support.ws.BaseClient;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.opensmartgridplatform.shared.infra.ws.DefaultWebServiceTemplateFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

@Component
public class PublicLightingScheduleManagementClient extends BaseClient {

  @Autowired private DefaultWebServiceTemplateFactory publicLightingScheduleManagementWstf;

  public SetScheduleResponse getSetSchedule(final SetScheduleAsyncRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate webServiceTemplate =
        this.publicLightingScheduleManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());

    return (SetScheduleResponse) webServiceTemplate.marshalSendAndReceive(request);
  }

  public SetScheduleAsyncResponse setSchedule(final SetScheduleRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate webServiceTemplate =
        this.publicLightingScheduleManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());

    return (SetScheduleAsyncResponse) webServiceTemplate.marshalSendAndReceive(request);
  }
}
