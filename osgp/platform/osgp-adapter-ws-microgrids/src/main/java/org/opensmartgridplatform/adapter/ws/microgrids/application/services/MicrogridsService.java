// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.microgrids.application.services;

import jakarta.validation.constraints.NotNull;
import org.opensmartgridplatform.adapter.ws.domain.entities.ResponseData;
import org.opensmartgridplatform.adapter.ws.microgrids.application.exceptionhandling.ResponseNotFoundException;
import org.opensmartgridplatform.adapter.ws.microgrids.infra.jms.MicrogridsRequestMessage;
import org.opensmartgridplatform.adapter.ws.microgrids.infra.jms.MicrogridsRequestMessageSender;
import org.opensmartgridplatform.adapter.ws.shared.services.ResponseDataService;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.entities.RtuDevice;
import org.opensmartgridplatform.domain.core.exceptions.ArgumentNullOrEmptyException;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.microgrids.valueobjects.EmptyResponse;
import org.opensmartgridplatform.domain.microgrids.valueobjects.GetDataRequest;
import org.opensmartgridplatform.domain.microgrids.valueobjects.GetDataResponse;
import org.opensmartgridplatform.domain.microgrids.valueobjects.SetDataRequest;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderService;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.validation.Identification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Transactional(value = "transactionManager")
@Validated
public class MicrogridsService {

  private static final Logger LOGGER = LoggerFactory.getLogger(MicrogridsService.class);

  @Autowired private DomainHelperService domainHelperService;

  @Autowired private CorrelationIdProviderService correlationIdProviderService;

  @Autowired private MicrogridsRequestMessageSender requestMessageSender;

  @Autowired private ResponseDataService responseDataService;

  public MicrogridsService() {
    // Parameterless constructor required for transactions
  }

  public String enqueueGetDataRequest(
      @Identification final String organisationIdentification,
      @Identification final String deviceIdentification,
      @NotNull final GetDataRequest dataRequest)
      throws OsgpException {

    LOGGER.debug(
        "enqueueGetDataRequest called with organisation {} and device {}",
        organisationIdentification,
        deviceIdentification);

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);

    final String correlationUid =
        this.correlationIdProviderService.getCorrelationId(
            organisationIdentification, deviceIdentification);

    final RtuDevice device = this.domainHelperService.findDevice(deviceIdentification);
    this.domainHelperService.isAllowed(organisation, device, DeviceFunction.GET_DATA);

    final MicrogridsRequestMessage message =
        new MicrogridsRequestMessage(
            MessageType.GET_DATA,
            correlationUid,
            organisationIdentification,
            deviceIdentification,
            dataRequest);

    try {
      this.requestMessageSender.send(message);
    } catch (final ArgumentNullOrEmptyException e) {
      throw new TechnicalException(ComponentType.WS_MICROGRIDS, e);
    }

    return correlationUid;
  }

  public GetDataResponse dequeueGetDataResponse(final String correlationUid) throws OsgpException {

    LOGGER.debug("dequeueGetDataRequest called with correlation uid {}", correlationUid);

    final ResponseData responseData =
        this.responseDataService.dequeue(
            correlationUid, ResponseMessage.class, ComponentType.WS_MICROGRIDS);
    final ResponseMessage response = (ResponseMessage) responseData.getMessageData();

    switch (response.getResult()) {
      case NOT_FOUND:
        throw new ResponseNotFoundException(
            ComponentType.WS_MICROGRIDS, "Response message not found.");
      case NOT_OK:
        if (response.getOsgpException() != null) {
          throw response.getOsgpException();
        }
        throw new TechnicalException(ComponentType.WS_MICROGRIDS, "Response message not ok.");
      case OK:
        return (GetDataResponse) response.getDataObject();
      default:
        // Should not get here
        throw new TechnicalException(
            ComponentType.WS_MICROGRIDS, "Response message contains invalid result.");
    }
  }

  public String enqueueSetDataRequest(
      final String organisationIdentification,
      final String deviceIdentification,
      final SetDataRequest setDataRequest)
      throws OsgpException {

    LOGGER.debug(
        "enqueueSetDataRequest called with organisation {} and device {}",
        organisationIdentification,
        deviceIdentification);

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);

    final String correlationUid =
        this.correlationIdProviderService.getCorrelationId(
            organisationIdentification, deviceIdentification);

    final RtuDevice device = this.domainHelperService.findDevice(deviceIdentification);
    this.domainHelperService.isAllowed(organisation, device, DeviceFunction.SET_DATA);

    final MicrogridsRequestMessage message =
        new MicrogridsRequestMessage(
            MessageType.SET_DATA,
            correlationUid,
            organisationIdentification,
            deviceIdentification,
            setDataRequest);

    try {
      this.requestMessageSender.send(message);
    } catch (final ArgumentNullOrEmptyException e) {
      throw new TechnicalException(ComponentType.WS_MICROGRIDS, e);
    }

    return correlationUid;
  }

  public EmptyResponse dequeueSetDataResponse(final String correlationUid) throws OsgpException {

    LOGGER.debug("dequeueSetDataRequest called with correlation uid {}", correlationUid);

    final ResponseData responseData =
        this.responseDataService.dequeue(
            correlationUid, ResponseMessage.class, ComponentType.WS_MICROGRIDS);
    final ResponseMessage response = (ResponseMessage) responseData.getMessageData();

    switch (response.getResult()) {
      case NOT_FOUND:
        throw new ResponseNotFoundException(
            ComponentType.WS_MICROGRIDS, "Response message not found.");
      case NOT_OK:
        if (response.getOsgpException() != null) {
          throw response.getOsgpException();
        }
        throw new TechnicalException(ComponentType.WS_MICROGRIDS, "Response message not ok.");
      case OK:
        return new EmptyResponse();
      default:
        // Should not get here
        throw new TechnicalException(
            ComponentType.WS_MICROGRIDS, "Response message contains invalid result.");
    }
  }
}
