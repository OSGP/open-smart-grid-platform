/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.publiclighting.domain.platform;

import java.util.Map;
import java.util.function.BiFunction;
import org.opensmartgridplatform.cucumber.platform.helpers.Protocol;
import org.opensmartgridplatform.domain.core.entities.Device;

@FunctionalInterface
public interface PlatformDeviceCreator<T extends Device>
    extends BiFunction<Protocol, Map<String, String>, T> {}
