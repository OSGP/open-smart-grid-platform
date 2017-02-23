/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.dlms.builders;

import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;

public abstract class AbstractAsyncRequestBuilder {

    protected String deviceIdentification;
    protected String correlationUid;

    protected Class<?> contextClass;
    protected Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

    public AbstractAsyncRequestBuilder(Class<?> contextClass) {
        super();
        this.contextClass = contextClass;
    }

    public AbstractAsyncRequestBuilder withDeviceidentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
        return this;
    }

    public AbstractAsyncRequestBuilder withCorrelationUid(final String correlationUid) {
        this.correlationUid = correlationUid;
        return this;
    }

    public AbstractAsyncRequestBuilder fromContext() {
        this.correlationUid = (String) ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID);
        this.deviceIdentification = (String) ScenarioContext.Current().get(Keys.KEY_DEVICE_IDENTIFICATION);
        return this;
    }

    public abstract Object build();

}
