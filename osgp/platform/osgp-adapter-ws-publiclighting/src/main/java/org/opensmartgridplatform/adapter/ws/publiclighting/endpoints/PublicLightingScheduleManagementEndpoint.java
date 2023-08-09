// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.publiclighting.endpoints;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import javax.validation.ConstraintViolationException;
import org.opensmartgridplatform.adapter.ws.domain.entities.ResponseData;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.MessagePriority;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.OrganisationIdentification;
import org.opensmartgridplatform.adapter.ws.publiclighting.application.mapping.ScheduleManagementMapper;
import org.opensmartgridplatform.adapter.ws.publiclighting.application.services.ScheduleManagementService;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.common.AsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.schedulemanagement.SetScheduleAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.schedulemanagement.SetScheduleAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.schedulemanagement.SetScheduleRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.schedulemanagement.SetScheduleResponse;
import org.opensmartgridplatform.adapter.ws.shared.services.ResponseDataService;
import org.opensmartgridplatform.domain.core.exceptions.ValidationException;
import org.opensmartgridplatform.domain.core.valueobjects.Schedule;
import org.opensmartgridplatform.domain.core.valueobjects.ScheduleEntry;
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
public class PublicLightingScheduleManagementEndpoint {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(PublicLightingScheduleManagementEndpoint.class);
  private static final String NAMESPACE =
      "http://www.opensmartgridplatform.org/schemas/publiclighting/schedulemanagement/2014/10";
  private static final ComponentType COMPONENT_WS_PUBLIC_LIGHTING =
      ComponentType.WS_PUBLIC_LIGHTING;

  private final ScheduleManagementService scheduleManagementService;
  private final ScheduleManagementMapper scheduleManagementMapper;

  @Autowired private ResponseDataService responseDataService;

  /**
   * Constructor
   *
   * @param scheduleManagementService The service instance for this end point.
   * @param scheduleManagementMapper The mapper instance for this end point.
   */
  @Autowired
  public PublicLightingScheduleManagementEndpoint(
      @Qualifier(value = "wsPublicLightingScheduleManagementService")
          final ScheduleManagementService scheduleManagementService,
      @Qualifier(value = "publicLightingScheduleManagementMapper")
          final ScheduleManagementMapper scheduleManagementMapper) {
    this.scheduleManagementService = scheduleManagementService;
    this.scheduleManagementMapper = scheduleManagementMapper;
  }

  // === SET LIGHT SCHEDULE ===

  @PayloadRoot(localPart = "SetScheduleRequest", namespace = NAMESPACE)
  @ResponsePayload
  public SetScheduleAsyncResponse setLightSchedule(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetScheduleRequest request,
      @MessagePriority final String messagePriority)
      throws OsgpException {

    LOGGER.info(
        "Set Schedule Request received from organisation: {} for device: {} with message priority: {}.",
        organisationIdentification,
        request.getDeviceIdentification(),
        messagePriority);

    final SetScheduleAsyncResponse response = new SetScheduleAsyncResponse();

    try {
      // Get the request parameters, make sure that they are in UTC.
      // Maybe add an adapter to the service, so that all datetime are
      // converted to utc automatically.
      final ZonedDateTime scheduleTime =
          request.getScheduledTime() == null
              ? null
              : ZonedDateTime.ofInstant(
                  request.getScheduledTime().toGregorianCalendar().toInstant(), ZoneId.of("UTC"));

      final List<ScheduleEntry> scheduleEntries =
          this.scheduleManagementMapper.mapAsList(
              request.getSchedules(),
              org.opensmartgridplatform.domain.core.valueobjects.ScheduleEntry.class);

      final Schedule schedule =
          new Schedule(
              scheduleEntries,
              request.getAstronomicalSunriseOffset(),
              request.getAstronomicalSunsetOffset());

      final String correlationUid =
          this.scheduleManagementService.enqueueSetLightSchedule(
              organisationIdentification,
              request.getDeviceIdentification(),
              schedule,
              scheduleTime,
              MessagePriorityEnum.getMessagePriority(messagePriority));

      final AsyncResponse asyncResponse = new AsyncResponse();
      asyncResponse.setCorrelationUid(correlationUid);
      asyncResponse.setDeviceId(request.getDeviceIdentification());
      response.setAsyncResponse(asyncResponse);
    } catch (final ConstraintViolationException e) {
      LOGGER.error("Exception: {}, StackTrace: {}", e.getMessage(), e.getStackTrace(), e);
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.WS_PUBLIC_LIGHTING,
          new ValidationException(e.getConstraintViolations()));
    } catch (final Exception e) {
      this.handleException(e);
    }

    return response;
  }

  @PayloadRoot(localPart = "SetScheduleAsyncRequest", namespace = NAMESPACE)
  @ResponsePayload
  public SetScheduleResponse getSetLightScheduleResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetScheduleAsyncRequest request)
      throws OsgpException {

    LOGGER.info(
        "Get Set Schedule Response Request received from organisation: {} for correlationUid: {}.",
        organisationIdentification,
        request.getAsyncRequest().getCorrelationUid());

    final SetScheduleResponse response = new SetScheduleResponse();

    try {
      final ResponseData responseData =
          this.responseDataService.dequeue(
              request.getAsyncRequest().getCorrelationUid(), COMPONENT_WS_PUBLIC_LIGHTING);
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
      throw new TechnicalException(COMPONENT_WS_PUBLIC_LIGHTING, e);
    }
  }
}
