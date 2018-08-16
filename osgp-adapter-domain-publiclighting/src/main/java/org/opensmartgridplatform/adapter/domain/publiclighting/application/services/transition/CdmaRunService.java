/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.publiclighting.application.services.transition;

import java.util.List;

import org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects.CdmaDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CdmaRunService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CdmaRunService.class);

    public CdmaRunService() {
        // Empty constructor to enable creation of bean
    }

    public void runCdmaBatch(final List<CdmaDevice> devices) {
        LOGGER.info("Run CDMA batch for " + devices.size() + " devices.");
        final CdmaBatch cdmaBatch = new CdmaBatch(devices);
    }

    private void runBatch() {

    }
}
