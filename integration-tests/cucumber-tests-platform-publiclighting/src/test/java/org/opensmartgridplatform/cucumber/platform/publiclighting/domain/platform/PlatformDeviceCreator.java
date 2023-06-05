// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.publiclighting.domain.platform;

import java.util.Map;
import java.util.function.BiFunction;
import org.opensmartgridplatform.cucumber.platform.helpers.Protocol;
import org.opensmartgridplatform.domain.core.entities.Device;

@FunctionalInterface
public interface PlatformDeviceCreator<T extends Device>
    extends BiFunction<Protocol, Map<String, String>, T> {}
