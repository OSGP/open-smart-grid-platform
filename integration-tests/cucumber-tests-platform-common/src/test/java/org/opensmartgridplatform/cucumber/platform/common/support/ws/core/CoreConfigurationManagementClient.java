// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.common.support.ws.core;

import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.GetConfigurationAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.GetConfigurationAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.GetConfigurationRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.GetConfigurationResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.SetConfigurationAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.SetConfigurationAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.SetConfigurationRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.SetConfigurationResponse;
import org.opensmartgridplatform.cucumber.platform.support.ws.BaseClient;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.opensmartgridplatform.shared.infra.ws.DefaultWebServiceTemplateFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

@Component
public class CoreConfigurationManagementClient extends BaseClient {

  @Autowired private DefaultWebServiceTemplateFactory coreConfigurationManagementWstf;

  public GetConfigurationAsyncResponse getConfiguration(final GetConfigurationRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.coreConfigurationManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (GetConfigurationAsyncResponse) wst.marshalSendAndReceive(request);
  }

  public GetConfigurationResponse getGetConfiguration(final GetConfigurationAsyncRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.coreConfigurationManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (GetConfigurationResponse) wst.marshalSendAndReceive(request);
  }

  public SetConfigurationAsyncResponse setConfiguration(final SetConfigurationRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.coreConfigurationManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (SetConfigurationAsyncResponse) wst.marshalSendAndReceive(request);
  }

  public SetConfigurationResponse getSetConfiguration(final SetConfigurationAsyncRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate wst =
        this.coreConfigurationManagementWstf.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (SetConfigurationResponse) wst.marshalSendAndReceive(request);
  }
}
