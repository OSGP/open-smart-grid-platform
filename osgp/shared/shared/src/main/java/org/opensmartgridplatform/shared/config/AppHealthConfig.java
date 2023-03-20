/*
 * Copyright 2023 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.shared.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
// Points Spring to the AppHealthServer.
@ComponentScan(basePackages = {"org.opensmartgridplatform.shared.health"})
public class AppHealthConfig {}
