// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.common.glue.steps.database.ws;

import org.opensmartgridplatform.adapter.ws.domain.repositories.ApplicationKeyConfigurationRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WsCoreApplicationKeyConfigurationRepository
    extends ApplicationKeyConfigurationRepository {}
