package com.alliander.osgp.platform.cucumber.steps.ws_admin;

import com.alliander.osgp.platform.cucumber.SoapUiRunner;

public abstract class AdminStepsBase extends SoapUiRunner {

    /**
     * Constructor.
     * The steps in this folder use the Admin soapui project.
     */
    protected AdminStepsBase() {
    	super("soap-ui-project/Admin-SoapUI-project.xml");
    }
}
