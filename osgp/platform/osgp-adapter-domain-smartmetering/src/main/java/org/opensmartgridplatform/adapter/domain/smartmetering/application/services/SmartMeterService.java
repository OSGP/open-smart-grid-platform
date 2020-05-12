/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import ma.glasnost.orika.MapperFactory;
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

@Service(value = "domainSmartMeteringSmartMeterService")
@Transactional(value = "transactionManager")
public class SmartMeterService {

    @Autowired
    private SmartMeterRepository smartMeteringDeviceRepository;

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
        smartMeter = this.smartMeteringDeviceRepository.save(smartMeter);
        this.storeAuthorization(organisationIdentification, smartMeter);
    }

    public void removeMeter(final DeviceMessageMetadata deviceMessageMetadata) {

        final SmartMeter device = this.smartMeteringDeviceRepository
                .findByDeviceIdentification(deviceMessageMetadata.getDeviceIdentification());

        this.deviceAuthorizationRepository.deleteAll(device.getAuthorizations());
        this.smartMeteringDeviceRepository.delete(device);
    }

    public SmartMeter getSmartMeter(final String deviceId, final SmartMeteringDevice smartMeteringDevice)
            throws FunctionalException {
        if (this.smartMeteringDeviceRepository.findByDeviceIdentification(deviceId) != null) {
            throw new FunctionalException(FunctionalExceptionType.EXISTING_DEVICE, ComponentType.DOMAIN_SMART_METERING);
        }
        return this.mapperFactory.getMapperFacade().map(smartMeteringDevice, SmartMeter.class);
    }

    private ProtocolInfo getProtocolInfo(final SmartMeteringDevice smartMeteringDevice) throws FunctionalException {
        
        final ProtocolInfo protocolInfo = this.protocolInfoRepository
                .findByProtocolAndProtocolVersion(smartMeteringDevice.getProtocolInfoLookupName(),
                        smartMeteringDevice.getProtocolVersion());
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
        final Organisation organisation = this.organisationRepository
                .findByOrganisationIdentification(organisationIdentification);
        final DeviceAuthorization authorization = smartMeter.addAuthorization(organisation, DeviceFunctionGroup.OWNER);
        this.deviceAuthorizationRepository.save(authorization);
    }

}
