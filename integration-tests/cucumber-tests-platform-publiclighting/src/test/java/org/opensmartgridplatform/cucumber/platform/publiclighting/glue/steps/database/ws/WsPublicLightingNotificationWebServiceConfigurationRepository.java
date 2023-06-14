// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.publiclighting.glue.steps.database.ws;

import org.opensmartgridplatform.adapter.ws.domain.repositories.NotificationWebServiceConfigurationRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WsPublicLightingNotificationWebServiceConfigurationRepository
    extends NotificationWebServiceConfigurationRepository {}
