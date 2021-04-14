/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.helpers;

import java.util.Map;
import java.util.function.Function;
import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;

@FunctionalInterface
public interface ProtocolDeviceCreator<T extends AbstractEntity>
    extends Function<Map<String, String>, T> {}
