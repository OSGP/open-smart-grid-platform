/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.core.application.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DomainInfo;
import com.alliander.osgp.domain.core.entities.Event;
import com.alliander.osgp.domain.core.entities.Ssld;
import com.alliander.osgp.domain.core.exceptions.UnknownEntityException;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.DomainInfoRepository;
import com.alliander.osgp.domain.core.repositories.EventRepository;
import com.alliander.osgp.domain.core.repositories.SsldRepository;

/**
 * Service class which encapsulates data access and transaction management. The
 * main service class {#link EventNotificationMessageService} uses this helper
 * class.
 */
@Service
public class EventNotificationHelperService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private SsldRepository ssldRepository;

    @Autowired
    private DomainInfoRepository domainInfoRepository;

    public Device findDevice(final String deviceIdentification) throws UnknownEntityException {
        final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
        if (device == null) {
            throw new UnknownEntityException(Device.class, deviceIdentification);
        }
        return device;
    }

    @Transactional(value = "transactionManager")
    public Device saveDevice(final Device device) {
        return this.deviceRepository.save(device);
    }

    public Ssld findSsld(final Long id) throws UnknownEntityException {
        final Ssld ssld = this.ssldRepository.findOne(id);
        if (ssld == null) {
            throw new UnknownEntityException(Ssld.class, Long.toString(id));
        }
        return ssld;
    }

    @Transactional(value = "transactionManager")
    public Ssld saveSsld(final Ssld ssld) {
        return this.ssldRepository.save(ssld);
    }

    @Transactional(value = "transactionManager")
    public Event saveEvent(final Event event) {
        return this.eventRepository.save(event);
    }

    public DomainInfo findDomainInfo(final String domainName, final String domainVersion) {
        return this.domainInfoRepository.findByDomainAndDomainVersion(domainName, domainVersion);
    }

}
