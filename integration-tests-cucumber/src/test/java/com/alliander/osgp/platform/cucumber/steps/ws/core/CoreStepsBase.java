package com.alliander.osgp.platform.cucumber.steps.ws.core;

import com.alliander.osgp.platform.cucumber.SoapUiRunner;

public abstract class CoreStepsBase extends SoapUiRunner {

    /**
     * Constructor.
     * The steps in this folder use the Core SoapUI project.
     */
    protected CoreStepsBase() {
        super("soap-ui-project/Core-SoapUI-project.xml");
    }
}
