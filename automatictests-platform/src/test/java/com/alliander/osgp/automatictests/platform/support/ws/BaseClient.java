/**
 * Copyright 2012-2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.automatictests.platform.support.ws;

import com.alliander.osgp.automatictests.platform.Defaults;
import com.alliander.osgp.automatictests.platform.Keys;
import com.alliander.osgp.automatictests.platform.core.ScenarioContext;

/**
 * Base client.
 */
public abstract class BaseClient {

    protected String getOrganizationIdentification() {
    	return (String) ScenarioContext.Current().get(Keys.ORGANIZATION_IDENTIFICATION, Defaults.ORGANIZATION_IDENTIFICATION);
    }
    
    protected String getUserName() {
        return (String) ScenarioContext.Current().get(Keys.USER_NAME, Defaults.USER_NAME);
    }
}
