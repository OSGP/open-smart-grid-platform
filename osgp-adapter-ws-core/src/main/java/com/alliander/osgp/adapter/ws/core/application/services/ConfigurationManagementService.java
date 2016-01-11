/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.core.application.services;

import javax.validation.Valid;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.alliander.osgp.adapter.ws.core.infra.jms.CommonRequestMessage;
import com.alliander.osgp.adapter.ws.core.infra.jms.CommonRequestMessageSender;
import com.alliander.osgp.adapter.ws.core.infra.jms.CommonRequestMessageType;
import com.alliander.osgp.adapter.ws.core.infra.jms.CommonResponseMessageFinder;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.services.CorrelationIdProviderService;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.domain.core.valueobjects.Configuration;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;

@Service(value = "wsCoreConfigurationManagementService")
@Transactional("transactionManager")
@Validated
public class ConfigurationManagementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationManagementService.class);

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private CorrelationIdProviderService correlationIdProviderService;

    @Autowired
    private CommonRequestMessageSender commonRequestMessageSender;

    @Autowired
    private CommonResponseMessageFinder commonResponseMessageFinder;

    public ConfigurationManagementService() {
        // Parameterless constructor required for transactions
    }

    public String enqueueSetConfigurationRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, @Valid final Configuration configuration,
            final DateTime scheduledTime) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.SET_CONFIGURATION);
        this.domainHelperService.isInMaintenance(device);

        LOGGER.debug("enqueueSetConfigurationRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final CommonRequestMessage message = new CommonRequestMessage(CommonRequestMessageType.SET_CONFIGURATION,
                correlationUid, organisationIdentification, deviceIdentification, configuration, scheduledTime);

        this.commonRequestMessageSender.send(message);

        return correlationUid;
    }

    public ResponseMessage dequeueSetConfigurationResponse(final String correlationUid) throws OsgpException {
        return this.commonResponseMessageFinder.findMessage(correlationUid);
    }

    public String enqueueGetConfigurationRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.GET_CONFIGURATION);
        this.domainHelperService.isInMaintenance(device);

        LOGGER.debug("enqueueGetConfigurationRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final CommonRequestMessage message = new CommonRequestMessage(CommonRequestMessageType.GET_CONFIGURATION,
                correlationUid, organisationIdentification, deviceIdentification, null, null);

        this.commonRequestMessageSender.send(message);

        return correlationUid;
    }

    public ResponseMessage dequeueGetConfigurationResponse(final String correlationUid) throws OsgpException {
        return this.commonResponseMessageFinder.findMessage(correlationUid);
    }

}
