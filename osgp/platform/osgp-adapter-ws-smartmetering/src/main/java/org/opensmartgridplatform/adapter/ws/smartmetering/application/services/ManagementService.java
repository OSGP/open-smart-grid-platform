/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.adapter.ws.domain.entities.ResponseData;
import org.opensmartgridplatform.adapter.ws.domain.repositories.ResponseDataRepository;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.AsyncResponse;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.syncrequest.FindMessageLogsSyncRequestExecutor;
import org.opensmartgridplatform.adapter.ws.smartmetering.endpoints.RequestMessageMetadata;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.Event;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.EventMessagesResponse;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderService;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.validation.Identification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Transactional(value = "transactionManager")
@Validated
public class ManagementService {

  private static final int PAGE_SIZE = 30;

  @Autowired private RequestService requestService;

  @Autowired private DomainHelperService domainHelperService;

  @Autowired private DeviceRepository deviceRepository;

  @Autowired private ResponseDataRepository responseDataRepository;

  @Autowired private CorrelationIdProviderService correlationIdProviderService;

  @Autowired private FindMessageLogsSyncRequestExecutor findMessageLogsSyncRequestExecutor;

  public ManagementService() {
    // Parameterless constructor required for transactions
  }

  public AsyncResponse enqueueAndSendFindLogsRequest(
      final RequestMessageMetadata requestMessageMetadata, final int pageNumber)
      throws FunctionalException {

    log.debug(
        "{} called with organisation {} and device {}",
        requestMessageMetadata.getMessageType(),
        requestMessageMetadata.getOrganisationIdentification(),
        requestMessageMetadata.getDeviceIdentification());

    this.requestService.checkAllowed(
        requestMessageMetadata.getOrganisationIdentification(),
        requestMessageMetadata.getDeviceIdentification(),
        requestMessageMetadata.getDeviceFunction());

    final String correlationUid =
        this.correlationIdProviderService.getCorrelationId(
            requestMessageMetadata.getOrganisationIdentification(),
            requestMessageMetadata.getDeviceIdentification());

    final MessageMetadata deviceMessageMetadata =
        requestMessageMetadata.newMessageMetadata(correlationUid);

    this.findMessageLogsSyncRequestExecutor.execute(
        requestMessageMetadata.getOrganisationIdentification(),
        requestMessageMetadata.getDeviceIdentification(),
        correlationUid,
        pageNumber);

    return this.requestService.createAsyncResponse(
        correlationUid, deviceMessageMetadata.getDeviceIdentification());
  }

  public Page<Device> findAllDevices(
      @Identification final String organisationIdentification, final int pageNumber)
      throws FunctionalException {

    log.debug(
        "findAllDevices called with organisation {} and pageNumber {}",
        organisationIdentification,
        pageNumber);

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);

    final PageRequest request =
        PageRequest.of(pageNumber, PAGE_SIZE, Sort.Direction.DESC, "deviceIdentification");
    return this.deviceRepository.findAllAuthorized(organisation, request);
  }

  public List<Event> findEventsByCorrelationUid(
      final String organisationIdentification, final String correlationUid) throws OsgpException {

    log.info("findEventsByCorrelationUid called with organisation {}}", organisationIdentification);

    this.domainHelperService.findOrganisation(organisationIdentification);

    final ResponseData responseData =
        this.responseDataRepository.findByCorrelationUid(correlationUid);
    final List<Event> events = new ArrayList<>();

    final Serializable messageData = responseData.getMessageData();

    if (messageData instanceof EventMessagesResponse) {
      events.addAll(((EventMessagesResponse) messageData).getEvents());

      log.info("deleting ResponseData for correlation uid {}.", correlationUid);
      this.responseDataRepository.delete(responseData);

    } else {
      /**
       * If the returned data is not an EventMessageContainer but a String, there has been an
       * exception. The exception message has been put in the messageData.
       *
       * <p>As there is no way of knowing what the type of the exception was (because it is passed
       * as a String) it is thrown as a TechnicalException because the user is most probably not to
       * blame for the exception.
       */
      if (messageData instanceof String) {
        throw new TechnicalException(ComponentType.UNKNOWN, (String) messageData);
      }
      log.info(
          "findEventsByCorrelationUid found other type of meter response data: {} for correlation UID: {}",
          messageData.getClass().getName(),
          correlationUid);
    }

    log.info("returning a list containing {} events", events.size());
    return events;
  }
}
