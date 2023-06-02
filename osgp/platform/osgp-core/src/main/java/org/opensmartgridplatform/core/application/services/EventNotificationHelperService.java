//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.core.application.services;

import java.util.Optional;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DomainInfo;
import org.opensmartgridplatform.domain.core.entities.Event;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.DomainInfoRepository;
import org.opensmartgridplatform.domain.core.repositories.EventRepository;
import org.opensmartgridplatform.domain.core.repositories.SsldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class which encapsulates data access and transaction management. The main service class
 * {#link EventNotificationMessageService} uses this helper class.
 */
@Service
public class EventNotificationHelperService {

  @Autowired private DeviceRepository deviceRepository;

  @Autowired private EventRepository eventRepository;

  @Autowired private SsldRepository ssldRepository;

  @Autowired private DomainInfoRepository domainInfoRepository;

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
    final Optional<Ssld> ssld = this.ssldRepository.findById(id);
    if (!ssld.isPresent()) {
      throw new UnknownEntityException(Ssld.class, Long.toString(id));
    }
    return ssld.get();
  }

  public Ssld findSsld(final String deviceIdentification) throws UnknownEntityException {
    final Ssld ssld = this.ssldRepository.findByDeviceIdentification(deviceIdentification);
    if (ssld == null) {
      throw new UnknownEntityException(Ssld.class, deviceIdentification);
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
