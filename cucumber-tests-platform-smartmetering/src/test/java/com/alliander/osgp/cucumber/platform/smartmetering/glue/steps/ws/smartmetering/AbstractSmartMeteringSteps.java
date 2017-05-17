/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering;

import java.util.List;

import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.support.ws.BaseClient;

/**
 * Super class for Smartmetering general methods.
 */
public abstract class AbstractSmartMeteringSteps extends BaseClient {

    protected void checkAndSaveCorrelationId(final String correlationUid) {

        if (correlationUid == null) {
            throw new AssertionError("Correlation Uid should be given");
        }
        ScenarioContext.current().put(PlatformKeys.KEY_CORRELATION_UID, correlationUid);
    }

    protected boolean checkDescription(final String description, final List<String> resultList) {

        if (description == null) {
            return true;
        }

        for (final String item : resultList) {
            if (!description.contains(item)) {
                return false;
            }
        }
        return true;
    }
}
