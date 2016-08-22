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

import com.alliander.osgp.domain.core.entities.SmartMeter;
import com.alliander.osgp.domain.core.exceptions.InactiveDeviceException;
import com.alliander.osgp.domain.core.exceptions.UnknownEntityException;
import com.alliander.osgp.domain.core.services.SmartMeterDomainService;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;

@Service(value = "domainSmartMeteringHelperService")
public class DomainHelperService {

    private static final ComponentType COMPONENT_TYPE = ComponentType.DOMAIN_SMART_METERING;

    @Autowired
    private SmartMeterDomainService smartMeteringDeviceDomainService;

    /**
     *
     * @param deviceIdentification
     * @return
     * @throws FunctionalException
     *             when there is no device
     */
    public SmartMeter findSmartMeter(final String deviceIdentification) throws FunctionalException {
        final SmartMeter smartMeter;
        try {
            smartMeter = this.smartMeteringDeviceDomainService.searchSmartMeter(deviceIdentification);
        } catch (final UnknownEntityException e) {
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICE, COMPONENT_TYPE, e);
        }
        return smartMeter;
    }

    /**
     * @param deviceIdentification
     *            the identification of the active device we're looking for
     * @return the active device for the given identification
     * @throws FunctionalException
     *             the device is either not in the database or not active
     */
    public SmartMeter findActiveSmartMeter(final String deviceIdentification) throws FunctionalException {
        SmartMeter smartMeter;
        try {
            smartMeter = this.smartMeteringDeviceDomainService.searchActiveSmartMeter(deviceIdentification);
        } catch (final InactiveDeviceException e) {
            throw new FunctionalException(FunctionalExceptionType.INACTIVE_DEVICE, COMPONENT_TYPE, e);
        } catch (final UnknownEntityException e) {
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICE, COMPONENT_TYPE, e);
        }
        return smartMeter;
    }

    public void ensureFunctionalExceptionForUnknownDevice(final String deviceIdentification)
            throws FunctionalException {

        /*
         * findSmartMeteringDevice throws a FunctionalException containing
         * information about the device identification for which no smart
         * metering device could be found.
         */
        this.findSmartMeter(deviceIdentification);
    }
}
