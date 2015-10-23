/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.domain.core.entities.SmartMeteringDevice;
import com.alliander.osgp.domain.core.exceptions.UnknownEntityException;
import com.alliander.osgp.domain.core.services.SmartMeteringDeviceDomainService;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;

@Service(value = "domainSmartMeteringHelperService")
public class DomainHelperService {

    private static final ComponentType COMPONENT_TYPE = ComponentType.DOMAIN_SMART_METERING;

    @Autowired
    private SmartMeteringDeviceDomainService smartMeteringDeviceDomainService;

    public SmartMeteringDevice findSmartMeteringDevice(final String deviceIdentification) throws FunctionalException {
        final SmartMeteringDevice smartMeteringDevice;
        try {
            smartMeteringDevice = this.smartMeteringDeviceDomainService.searchSmartMeteringDevice(deviceIdentification);
        } catch (final UnknownEntityException e) {
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICE, COMPONENT_TYPE, e);
        }
        return smartMeteringDevice;
    }

    /**
     * Makes sure a FunctionalException is thrown if there is no known device
     * for the provided deviceIdentification.
     * <p>
     * If you need access to the SmartMeteringDevice if it exists, call
     * {@link #findSmartMeteringDevice(String)} instead.
     *
     * @param deviceIdentification
     *            the identification for which a smart metering device is looked
     *            for.
     */
    public void ensureFunctionalExceptionForUnknownDevice(final String deviceIdentification) throws FunctionalException {

        /*
         * findSmartMeteringDevice throws a FunctionalException containing
         * information about the device identification for which no smart
         * metering device could be found.
         */
        this.findSmartMeteringDevice(deviceIdentification);
    }
}
