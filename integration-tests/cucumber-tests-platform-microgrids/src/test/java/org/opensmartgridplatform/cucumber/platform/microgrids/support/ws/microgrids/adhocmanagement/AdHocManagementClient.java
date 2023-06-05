// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.microgrids.support.ws.microgrids.adhocmanagement;

import java.util.concurrent.TimeUnit;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.GetDataAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.GetDataAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.GetDataRequest;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.GetDataResponse;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.SetDataAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.SetDataAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.SetDataRequest;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.SetDataResponse;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.notification.Notification;
import org.opensmartgridplatform.cucumber.platform.microgrids.support.ws.microgrids.NotificationService;
import org.opensmartgridplatform.cucumber.platform.support.ws.BaseClient;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.opensmartgridplatform.shared.infra.ws.DefaultWebServiceTemplateFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

@Component
public class AdHocManagementClient extends BaseClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(AdHocManagementClient.class);

  @Autowired
  @Qualifier("webServiceTemplateFactoryMicrogridsAdHocManagement")
  private DefaultWebServiceTemplateFactory webServiceTemplateFactoryMicrogridsAdHocManagement;

  @Autowired private NotificationService notificationService;

  @Value("${iec61850.rtu.response.wait.fail.duration:15000}")
  private int waitFailMillis;

  public GetDataAsyncResponse getDataAsync(final GetDataRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate webServiceTemplate =
        this.webServiceTemplateFactoryMicrogridsAdHocManagement.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (GetDataAsyncResponse) webServiceTemplate.marshalSendAndReceive(request);
  }

  public SetDataAsyncResponse setDataAsync(final SetDataRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate webServiceTemplate =
        this.webServiceTemplateFactoryMicrogridsAdHocManagement.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (SetDataAsyncResponse) webServiceTemplate.marshalSendAndReceive(request);
  }

  public GetDataResponse getData(final GetDataAsyncRequest request)
      throws WebServiceSecurityException {

    final String correlationUid = request.getAsyncRequest().getCorrelationUid();
    this.waitForNotification(correlationUid);

    final WebServiceTemplate webServiceTemplate =
        this.webServiceTemplateFactoryMicrogridsAdHocManagement.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (GetDataResponse) webServiceTemplate.marshalSendAndReceive(request);
  }

  public SetDataResponse setData(final SetDataAsyncRequest request)
      throws WebServiceSecurityException {

    final String correlationUid = request.getAsyncRequest().getCorrelationUid();
    this.waitForNotification(correlationUid);

    final WebServiceTemplate webServiceTemplate =
        this.webServiceTemplateFactoryMicrogridsAdHocManagement.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (SetDataResponse) webServiceTemplate.marshalSendAndReceive(request);
  }

  private void waitForNotification(final String correlationUid) {
    LOGGER.info(
        "Waiting for a notification for correlation UID {} for at most {} milliseconds.",
        correlationUid,
        this.waitFailMillis);

    final Notification notification =
        this.notificationService.getNotification(
            correlationUid, this.waitFailMillis, TimeUnit.MILLISECONDS);

    if (notification == null) {
      throw new AssertionError(
          "Did not receive a notification for correlation UID: "
              + correlationUid
              + " within "
              + this.waitFailMillis
              + " milliseconds");
    }
  }
}
