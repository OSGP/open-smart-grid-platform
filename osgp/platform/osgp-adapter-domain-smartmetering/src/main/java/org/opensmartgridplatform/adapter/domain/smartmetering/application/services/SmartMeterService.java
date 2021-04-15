/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.opensmartgridplatform.domain.core.entities.DeviceAuthorization;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.entities.Manufacturer;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.repositories.DeviceAuthorizationRepository;
import org.opensmartgridplatform.domain.core.repositories.DeviceModelRepository;
import org.opensmartgridplatform.domain.core.repositories.ManufacturerRepository;
import org.opensmartgridplatform.domain.core.repositories.OrganisationRepository;
import org.opensmartgridplatform.domain.core.repositories.ProtocolInfoRepository;
import org.opensmartgridplatform.domain.core.repositories.SmartMeterRepository;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunctionGroup;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AddSmartMeterRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SmartMeteringDevice;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFactory;

@Slf4j
@Service(value = "domainSmartMeteringSmartMeterService")
@Transactional(value = "transactionManager")
public class SmartMeterService {

    @Autowired
    private SmartMeterRepository smartMeterRepository;

    @Autowired
    private ManufacturerRepository manufacturerRepository;

    @Autowired
    private DeviceModelRepository deviceModelRepository;

    @Autowired
    private ProtocolInfoRepository protocolInfoRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private DeviceAuthorizationRepository deviceAuthorizationRepository;

    @Autowired
    private MapperFactory mapperFactory;

    public void storeMeter(final String organisationIdentification, final AddSmartMeterRequest addSmartMeterRequest,
            SmartMeter smartMeter) throws FunctionalException {
        final SmartMeteringDevice smartMeteringDevice = addSmartMeterRequest.getDevice();
        smartMeter.updateProtocol(this.getProtocolInfo(smartMeteringDevice));
        smartMeter.setDeviceModel(this.getDeviceModel(addSmartMeterRequest.getDeviceModel()));
        smartMeter = this.smartMeterRepository.save(smartMeter);
        this.storeAuthorization(organisationIdentification, smartMeter);
    }

    public void removeMeter(final DeviceMessageMetadata deviceMessageMetadata) {

        final SmartMeter device = this.smartMeterRepository.findByDeviceIdentification(
                deviceMessageMetadata.getDeviceIdentification());

        this.deviceAuthorizationRepository.deleteAll(device.getAuthorizations());
        this.smartMeterRepository.delete(device);
    }

    public void validateSmartMeterDoesNotExist(final String deviceIdentification) throws FunctionalException {
        if (this.smartMeterRepository.findByDeviceIdentification(deviceIdentification) != null) {
            throw new FunctionalException(FunctionalExceptionType.EXISTING_DEVICE, ComponentType.DOMAIN_SMART_METERING);
        }
    }

    private SmartMeter validateSmartMeterExists(final String deviceIdentification) throws FunctionalException {
        final SmartMeter smartMeter = this.smartMeterRepository.findByDeviceIdentification(deviceIdentification);

        if (smartMeter == null) {
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICE, ComponentType.DOMAIN_SMART_METERING);
        }
        return smartMeter;
    }

    public SmartMeter convertSmartMeter(final SmartMeteringDevice smartMeteringDevice) {
        return this.mapperFactory.getMapperFacade().map(smartMeteringDevice, SmartMeter.class);
    }

    private ProtocolInfo getProtocolInfo(final SmartMeteringDevice smartMeteringDevice) throws FunctionalException {

        final ProtocolInfo protocolInfo = this.protocolInfoRepository.findByProtocolAndProtocolVersion(
                smartMeteringDevice.getProtocolInfoLookupName(), smartMeteringDevice.getProtocolVersion());
        if (protocolInfo == null) {
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_PROTOCOL_NAME_OR_VERSION,
                    ComponentType.DOMAIN_SMART_METERING);
        }
        return protocolInfo;
    }

    private DeviceModel getDeviceModel(
            final org.opensmartgridplatform.domain.core.valueobjects.DeviceModel deviceModel) {
        final Manufacturer manufacturer = this.manufacturerRepository.findByCode(deviceModel.getManufacturer());
        return this.deviceModelRepository.findByManufacturerAndModelCode(manufacturer, deviceModel.getModelCode());
    }

    private void storeAuthorization(final String organisationIdentification, final SmartMeter smartMeter) {
        final Organisation organisation = this.organisationRepository.findByOrganisationIdentification(
                organisationIdentification);
        final DeviceAuthorization authorization = smartMeter.addAuthorization(organisation, DeviceFunctionGroup.OWNER);
        this.deviceAuthorizationRepository.save(authorization);
    }

}
