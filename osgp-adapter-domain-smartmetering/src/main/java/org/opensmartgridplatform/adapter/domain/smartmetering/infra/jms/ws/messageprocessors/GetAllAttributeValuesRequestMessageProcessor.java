/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.messageprocessors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.AdhocService;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.WebServiceRequestMessageProcessor;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;

@Component
public class GetAllAttributeValuesRequestMessageProcessor extends WebServiceRequestMessageProcessor {

    @Autowired
    @Qualifier("domainSmartMeteringAdhocService")
    private AdhocService adhocService;

    protected GetAllAttributeValuesRequestMessageProcessor() {
        super(DeviceFunction.GET_ALL_ATTRIBUTE_VALUES);
    }

    @Override
    protected void handleMessage(final DeviceMessageMetadata deviceMessageMetadata, final Object dataObject)
            throws FunctionalException {

        this.adhocService.getAllAttributeValues(deviceMessageMetadata);
    }

}
