// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.tariffswitching.endpoints;

import javax.validation.ConstraintViolationException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.opensmartgridplatform.adapter.ws.domain.entities.ResponseData;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.MessagePriority;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.OrganisationIdentification;
import org.opensmartgridplatform.adapter.ws.schema.tariffswitching.common.AsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.tariffswitching.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.tariffswitching.schedulemanagement.SetScheduleAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.tariffswitching.schedulemanagement.SetScheduleAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.tariffswitching.schedulemanagement.SetScheduleRequest;
import org.opensmartgridplatform.adapter.ws.schema.tariffswitching.schedulemanagement.SetScheduleResponse;
import org.opensmartgridplatform.adapter.ws.shared.services.ResponseDataService;
import org.opensmartgridplatform.adapter.ws.tariffswitching.application.mapping.ScheduleManagementMapper;
import org.opensmartgridplatform.adapter.ws.tariffswitching.application.services.ScheduleManagementService;
import org.opensmartgridplatform.domain.core.exceptions.ValidationException;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.wsheaderattribute.priority.MessagePriorityEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class TariffSwitchingScheduleManagementEndpoint {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(TariffSwitchingScheduleManagementEndpoint.class);
  private static final String NAMESPACE =
      "http://www.opensmartgridplatform.org/schemas/tariffswitching/schedulemanagement/2014/10";
  private static final ComponentType COMPONENT_WS_TARIFF_SWITCHING =
      ComponentType.WS_TARIFF_SWITCHING;

  private final ScheduleManagementService scheduleManagementService;
  private final ScheduleManagementMapper scheduleManagementMapper;

  @Autowired private ResponseDataService responseDataService;

  /**
   * Constructor.
   *
   * @param scheduleManagementService The service instance.
   * @param scheduleManagementMapper The mapper instance.
   */
  @Autowired
  public TariffSwitchingScheduleManagementEndpoint(
      @Qualifier(value = "wsTariffSwitchingScheduleManagementService")
          final ScheduleManagementService scheduleManagementService,
      @Qualifier(value = "tariffSwitchingScheduleManagementMapper")
          final ScheduleManagementMapper scheduleManagementMapper) {
    this.scheduleManagementService = scheduleManagementService;
    this.scheduleManagementMapper = scheduleManagementMapper;
  }

  @PayloadRoot(localPart = "SetScheduleRequest", namespace = NAMESPACE)
  @ResponsePayload
  public SetScheduleAsyncResponse setSchedule(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetScheduleRequest request,
      @MessagePriority final String messagePriority)
      throws OsgpException {

    LOGGER.info(
        "Set Tariff Schedule Request received from organisation: {} for device: {} with message priority: {}.",
        organisationIdentification,
        request.getDeviceIdentification(),
        messagePriority);

    // Get the request parameters, make sure that they are in UTC.
    // Maybe add an adapter to the service, so that all datetime are
    // converted to utc automatically.
    final DateTime scheduleTime =
        request.getScheduledTime() == null
            ? null
            : new DateTime(request.getScheduledTime().toGregorianCalendar())
                .toDateTime(DateTimeZone.UTC);

    final SetScheduleAsyncResponse response = new SetScheduleAsyncResponse();

    try {
      final String correlationUid =
          this.scheduleManagementService.enqueueSetTariffSchedule(
              organisationIdentification,
              request.getDeviceIdentification(),
              this.scheduleManagementMapper.mapAsList(
                  request.getSchedules(),
                  org.opensmartgridplatform.domain.core.valueobjects.ScheduleEntry.class),
              scheduleTime,
              MessagePriorityEnum.getMessagePriority(messagePriority));

      final AsyncResponse asyncResponse = new AsyncResponse();
      asyncResponse.setCorrelationUid(correlationUid);
      asyncResponse.setDeviceId(request.getDeviceIdentification());
      response.setAsyncResponse(asyncResponse);
    } catch (final ConstraintViolationException e) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.WS_TARIFF_SWITCHING,
          new ValidationException(e.getConstraintViolations()));
    } catch (final Exception e) {
      this.handleException(e);
    }

    return response;
  }

  @PayloadRoot(localPart = "SetScheduleAsyncRequest", namespace = NAMESPACE)
  @ResponsePayload
  public SetScheduleResponse getSetScheduleResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetScheduleAsyncRequest request)
      throws OsgpException {

    LOGGER.info(
        "Get Tariff Schedule Response Request received from organisation: {} for correlationUID: {}.",
        organisationIdentification,
        request.getAsyncRequest().getCorrelationUid());

    final SetScheduleResponse response = new SetScheduleResponse();

    try {
      final ResponseData responseData =
          this.responseDataService.dequeue(
              request.getAsyncRequest().getCorrelationUid(), COMPONENT_WS_TARIFF_SWITCHING);
      response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
      if (responseData.getMessageData() instanceof String) {
        response.setDescription((String) responseData.getMessageData());
      }
    } catch (final Exception e) {
      this.handleException(e);
    }

    return response;
  }

  private void handleException(final Exception e) throws OsgpException {
    // Rethrow exception if it already is a functional or technical
    // exception,
    // otherwise throw new technical exception.
    LOGGER.error("Exception occurred: ", e);
    if (e instanceof OsgpException) {
      throw (OsgpException) e;
    } else {
      throw new TechnicalException(COMPONENT_WS_TARIFF_SWITCHING, e);
    }
  }
}
