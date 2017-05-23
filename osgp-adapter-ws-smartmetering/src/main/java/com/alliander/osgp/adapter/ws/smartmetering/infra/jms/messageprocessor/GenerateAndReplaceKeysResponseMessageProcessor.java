package com.alliander.osgp.adapter.ws.smartmetering.infra.jms.messageprocessor;

import org.springframework.stereotype.Component;

import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;

@Component
public class GenerateAndReplaceKeysResponseMessageProcessor extends DomainResponseMessageProcessor {

    protected GenerateAndReplaceKeysResponseMessageProcessor() {
        super(DeviceFunction.GENERATE_AND_REPLACE_KEYS);
    }
}
