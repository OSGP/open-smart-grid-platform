//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.distributionautomation.support.ws.distributionautomation;

import java.util.concurrent.TimeUnit;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.GetHealthStatusAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.GetHealthStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.GetMeasurementReportRequest;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.GetMeasurementReportResponse;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.notification.Notification;
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
public class DistributionAutomationDeviceManagementClient extends BaseClient {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DistributionAutomationDeviceManagementClient.class);

  @Autowired
  @Qualifier("webServiceTemplateFactoryDistributionAutomationDeviceManagement")
  private DefaultWebServiceTemplateFactory
      webServiceTemplateFactoryDistributionAutomationDeviceManagement;

  @Autowired private NotificationService notificationService;

  @Value("${iec60870.rtu.response.wait.fail.duration:15000}")
  private int waitFailMillis;

  public GetHealthStatusAsyncResponse getHealthStatus(
      final String organisationIdentification, final GetHealthStatusRequest request)
      throws WebServiceSecurityException {
    final WebServiceTemplate webServiceTemplate =
        this.webServiceTemplateFactoryDistributionAutomationDeviceManagement.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());
    return (GetHealthStatusAsyncResponse) webServiceTemplate.marshalSendAndReceive(request);
  }

  public GetMeasurementReportResponse getMeasurementReportResponse(final Notification notification)
      throws WebServiceSecurityException {

    LOGGER.info(
        "Get the measurement report for correlationUid {}", notification.getCorrelationUid());

    final WebServiceTemplate webServiceTemplate =
        this.webServiceTemplateFactoryDistributionAutomationDeviceManagement.getTemplate(
            this.getOrganizationIdentification(), this.getUserName());

    final GetMeasurementReportRequest request = new GetMeasurementReportRequest();
    request.setCorrelationUid(notification.getCorrelationUid());

    return (GetMeasurementReportResponse) webServiceTemplate.marshalSendAndReceive(request);
  }

  /**
   * Waits for a notification. The kind of notification or its correlationUid does not matter.
   * Throws an assertion error, when no notification is received within the configured timeout.
   *
   * @return The first notification received.
   */
  public Notification waitForNotification() {
    LOGGER.info("Waiting for a notification for at most {} milliseconds.", this.waitFailMillis);

    final Notification notification =
        this.notificationService.getNotification(this.waitFailMillis, TimeUnit.MILLISECONDS);

    if (notification == null) {
      throw new AssertionError(
          "Did not receive a notification within " + this.waitFailMillis + " milliseconds");
    }

    return notification;
  }
}
