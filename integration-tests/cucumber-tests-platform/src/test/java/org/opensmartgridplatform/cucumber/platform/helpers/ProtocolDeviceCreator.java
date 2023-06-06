// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.helpers;

import java.util.Map;
import java.util.function.Function;
import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;

@FunctionalInterface
public interface ProtocolDeviceCreator<T extends AbstractEntity>
    extends Function<Map<String, String>, T> {}
