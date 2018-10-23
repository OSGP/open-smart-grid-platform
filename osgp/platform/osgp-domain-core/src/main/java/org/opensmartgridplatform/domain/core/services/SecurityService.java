/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.services;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceAuthorization;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.exceptions.NotAuthorizedException;
import org.opensmartgridplatform.domain.core.repositories.DeviceAuthorizationRepository;
import org.opensmartgridplatform.domain.core.repositories.DeviceFunctionMappingRepository;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunctionGroup;
import org.opensmartgridplatform.domain.core.valueobjects.PlatformFunction;
import org.opensmartgridplatform.domain.core.valueobjects.PlatformFunctionGroup;

@Service
public class SecurityService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityService.class);

    private static Map<PlatformFunctionGroup, PlatformFunction[]> platformMapping;
    static {
        platformMapping = new HashMap<>();
        platformMapping.put(PlatformFunctionGroup.ADMIN,
                new PlatformFunction[] { PlatformFunction.CREATE_ORGANISATION, PlatformFunction.GET_ORGANISATIONS,
                        PlatformFunction.REMOVE_ORGANISATION, PlatformFunction.CHANGE_ORGANISATION,
                        PlatformFunction.GET_DEVICE_NO_OWNER, PlatformFunction.GET_MESSAGES,
                        PlatformFunction.FIND_DEVICES, PlatformFunction.SET_OWNER, PlatformFunction.UPDATE_KEY,
                        PlatformFunction.REVOKE_KEY, PlatformFunction.FIND_SCHEDULED_TASKS,
                        PlatformFunction.CREATE_MANUFACTURER, PlatformFunction.GET_MANUFACTURERS,
                        PlatformFunction.CHANGE_MANUFACTURER, PlatformFunction.REMOVE_MANUFACTURER,
                        PlatformFunction.GET_PROTOCOL_INFOS, PlatformFunction.UPDATE_DEVICE_PROTOCOL,
                        PlatformFunction.GET_DEVICE_MODELS, PlatformFunction.CREATE_DEVICE_MODEL,
                        PlatformFunction.REMOVE_DEVICE_MODEL, PlatformFunction.CHANGE_DEVICE_MODEL,
                        PlatformFunction.GET_FIRMWARE, PlatformFunction.CREATE_FIRMWARE,
                        PlatformFunction.REMOVE_FIRMWARE, PlatformFunction.CHANGE_FIRMWARE });

        platformMapping.put(PlatformFunctionGroup.USER,
                new PlatformFunction[] { PlatformFunction.GET_ORGANISATIONS, PlatformFunction.FIND_DEVICES });
    }

    @Autowired
    private DeviceAuthorizationRepository deviceAuthorizationRepository;

    @Autowired
    private DeviceFunctionMappingRepository deviceFunctionMappingRepository;

    /**
     * Checks whether organization has the correct authority on the platform for
     * requested function
     *
     * @param organisation
     * @param function
     * @throws NotAuthorizedException
     *             when not authorized for function
     */
    public void checkAuthorization(final Organisation organisation, final PlatformFunction function)
            throws NotAuthorizedException {
        if (ArrayUtils.contains(platformMapping.get(organisation.getFunctionGroup()), function)) {
            LOGGER.info("Organisation {} is allowed function {}", organisation.getOrganisationIdentification(),
                    function);
            return;
        }

        LOGGER.warn("Organisation {} is not allowed function {}", organisation.getOrganisationIdentification(),
                function);
        throw new NotAuthorizedException(organisation.getOrganisationIdentification());
    }

    /**
     * Checks whether organization has correct authority on requested device for
     * requested function
     *
     * @param organisation
     * @param device
     * @param function
     * @throws NotAuthorizedException
     *             when not authorized for function
     */
    public void checkAuthorization(final Organisation organisation, final Device device, final DeviceFunction function)
            throws NotAuthorizedException {
        // Fetch all authorizations for device
        final List<DeviceAuthorization> authorizations = this.deviceAuthorizationRepository
                .findByOrganisationAndDevice(organisation, device);

        final List<DeviceFunction> authorizedDeviceFunctions = this.getDeviceFunctionsForAuthorizations(authorizations);
        if (authorizedDeviceFunctions != null && authorizedDeviceFunctions.contains(function)) {
            LOGGER.info("Organisation {} is allowed {} for device {}", new Object[] {
                    organisation.getOrganisationIdentification(), function, device.getDeviceIdentification() });
            return;
        }

        // Not allowed to access requested function
        LOGGER.warn("Organisation {} is not allowed {} for device {}", new Object[] {
                organisation.getOrganisationIdentification(), function, device.getDeviceIdentification() });
        throw new NotAuthorizedException(organisation.getOrganisationIdentification());
    }

    private List<DeviceFunction> getDeviceFunctionsForAuthorizations(final List<DeviceAuthorization> authorizations) {
        if (authorizations == null || authorizations.isEmpty()) {
            return Collections.emptyList();
        }

        final Set<DeviceFunctionGroup> authorizedFunctionGroups = EnumSet.noneOf(DeviceFunctionGroup.class);
        for (final DeviceAuthorization authorization : authorizations) {
            if (authorization.getFunctionGroup() != null) {
                authorizedFunctionGroups.add(authorization.getFunctionGroup());
            }
        }

        return this.deviceFunctionMappingRepository.findByDeviceFunctionGroups(authorizedFunctionGroups);
    }
}
