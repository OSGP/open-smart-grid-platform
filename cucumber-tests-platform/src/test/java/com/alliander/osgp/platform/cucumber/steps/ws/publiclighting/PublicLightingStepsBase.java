/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.ws.publiclighting;

import com.alliander.osgp.platform.cucumber.SoapUiRunner;

public abstract class PublicLightingStepsBase extends SoapUiRunner {

    /**
     * Constructor.
     * The steps in this folder use the PublicLighting SoapUI project.
     */
    protected PublicLightingStepsBase() {
        super("soap-ui-project/PublicLighting-SoapUI-project.xml");
    }
}
