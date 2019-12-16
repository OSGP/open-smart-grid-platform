/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.endpoints;

import java.util.List;

import org.opensmartgridplatform.adapter.ws.domain.entities.ResponseData;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.BypassRetry;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.MessagePriority;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.OrganisationIdentification;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.ResponseUrl;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.Actions;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.BundleAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.BundleAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.BundleRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.BundleResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Action;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.services.ActionMapperResponseService;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.services.ActionMapperService;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.services.BundleService;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActionRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.BundleMessagesResponse;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
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
public class SmartMeteringBundleEndpoint extends SmartMeteringEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmartMeteringBundleEndpoint.class);
    private static final String NAMESPACE = "http://www.opensmartgridplatform"
            + ".org/schemas/smartmetering/sm-bundle/2014/10";

    private final BundleService bundleService;
    private final ActionMapperService actionMapperService;
    private final ActionMapperResponseService actionMapperResponseService;

    @Autowired
    public SmartMeteringBundleEndpoint(
            @Qualifier(value = "wsSmartMeteringBundleService") final BundleService bundleService,
            @Qualifier(value = "wsSmartMeteringActionMapperService") final ActionMapperService actionMapperService,
            @Qualifier(value = "wsSmartMeteringActionResponseMapperService") final ActionMapperResponseService actionMapperResponseService) {
        this.bundleService = bundleService;
        this.actionMapperService = actionMapperService;
        this.actionMapperResponseService = actionMapperResponseService;
    }

    @PayloadRoot(localPart = "BundleRequest", namespace = NAMESPACE)
    @ResponsePayload
    public BundleAsyncResponse bundleRequest(@OrganisationIdentification final String organisationIdentification,
            @MessagePriority final String messagePriority, @ResponseUrl final String responseUrl,
            @BypassRetry final String bypassRetry, @RequestPayload final BundleRequest request) throws OsgpException {

        LOGGER.info("Bundle request for organisation: {} and device: {}. and responseUrl: {}. Request contains {}.",
                organisationIdentification, request.getDeviceIdentification(), responseUrl,
                request.getActions().getActionList());

        BundleAsyncResponse response = null;
        try {
            // Create response.
            response = new BundleAsyncResponse();

            // Get the request parameters, make sure that date time are in UTC.
            final String deviceIdentification = request.getDeviceIdentification();

            final Actions actions = request.getActions();
            final List<? extends Action> actionList = actions.getActionList();

            final List<ActionRequest> actionRequestList = this.actionMapperService.mapAllActions(actionList);

            final String correlationUid = this.bundleService.enqueueBundleRequest(organisationIdentification,
                    deviceIdentification, actionRequestList, MessagePriorityEnum.getMessagePriority(messagePriority),
                    Boolean.parseBoolean(bypassRetry));

            response.setCorrelationUid(correlationUid);
            response.setDeviceIdentification(request.getDeviceIdentification());

            this.saveResponseUrlIfNeeded(correlationUid, responseUrl);
        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "BundleAsyncRequest", namespace = NAMESPACE)
    @ResponsePayload
    public BundleResponse getBundleResponse(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final BundleAsyncRequest request) throws OsgpException {

        LOGGER.info("Get bundle response for organisation: {} and device: {}.", organisationIdentification,
                request.getDeviceIdentification());

        final ResponseData responseData = this.responseDataService.dequeue(request.getCorrelationUid(),
                BundleMessagesResponse.class, ComponentType.WS_SMART_METERING);

        this.throwExceptionIfResultNotOk(responseData, "get bundle response");

        // Create response.
        return this.actionMapperResponseService.mapAllActions(responseData.getMessageData());
    }
}
