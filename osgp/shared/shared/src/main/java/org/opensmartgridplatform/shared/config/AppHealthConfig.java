//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
// Points Spring to the AppHealthServer.
@ComponentScan(basePackages = {"org.opensmartgridplatform.shared.health"})
public class AppHealthConfig {}
