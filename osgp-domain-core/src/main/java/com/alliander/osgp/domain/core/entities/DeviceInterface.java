/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.entities;

import java.util.List;

import com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup;

/**
 * @author OSGP
 *
 */
public interface DeviceInterface {

    String getDeviceIdentification();

    String getDeviceType();

    ProtocolInfo getProtocolInfo();

    List<DeviceAuthorization> getAuthorizations();

    DeviceAuthorization addAuthorization(final Organisation organisation, final DeviceFunctionGroup functionGroup);

    Organisation getOwner();

    List<String> getOrganisations();
}
