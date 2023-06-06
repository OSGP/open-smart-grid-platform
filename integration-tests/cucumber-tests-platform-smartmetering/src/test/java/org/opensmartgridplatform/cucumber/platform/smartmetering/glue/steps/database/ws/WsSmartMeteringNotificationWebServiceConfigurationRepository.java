// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.database.ws;

import org.opensmartgridplatform.adapter.ws.domain.repositories.NotificationWebServiceConfigurationRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WsSmartMeteringNotificationWebServiceConfigurationRepository
    extends NotificationWebServiceConfigurationRepository {}
