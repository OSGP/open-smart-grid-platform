package com.alliander.osgp.platform.cucumber.steps.ws_admin;

import com.alliander.osgp.platform.cucumber.SoapUiRunner;

public abstract class AdminStepsBase extends SoapUiRunner {

	/**
	 * Labels used in the soap ui requests/responses.
	 */
	protected static final String DEVICE_IDENTIFICATION_LABEL = "DeviceIdentification";
    protected static final String ORGANISATION_IDENTIFICATION_LABEL = "OrganisationIdentification";
    protected static final String ENDPOINT_LABEL = "ServiceEndpoint";
    
    /**
     * Constructor.
     * The steps in this folder use the Admin soapui project.
     */
    protected AdminStepsBase() {
    	super("soap-ui-project/Admin-SoapUI-project.xml");
    }
}
