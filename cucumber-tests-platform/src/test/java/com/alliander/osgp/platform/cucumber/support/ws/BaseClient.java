/**
 * Copyright 2012-2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.support.ws;

import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;

/**
 * Base client.
 */
public abstract class BaseClient {

    protected String getOrganizationIdentification() {
    	return (String) ScenarioContext.Current().get(Keys.KEY_ORGANIZATION_IDENTIFICATION, Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION);
    }
    
    protected String getUserName() {
        return (String) ScenarioContext.Current().get(Keys.KEY_USER_NAME, Defaults.DEFAULT_USER_NAME);
    }
}
