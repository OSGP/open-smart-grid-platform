package com.alliander.osgp.platform.cucumber.support.ws;

import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;

public abstract class BaseClient {

    protected String getOrganizationIdentification() {
    	return (String) ScenarioContext.Current().get(Keys.KEY_ORGANIZATION_IDENTIFICATION, Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION);
    }
    
    protected String getUserName() {
        return (String) ScenarioContext.Current().get(Keys.KEY_USER_NAME, Defaults.DEFAULT_USER_NAME);
    }
}
