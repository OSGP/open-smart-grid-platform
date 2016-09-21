package com.alliander.osgp.adapter.ws.microgrids.application.exceptionhandling;

import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;

public class ResponseNotFoundException extends OsgpException {

    private static final long serialVersionUID = 1706342594144271262L;

    public ResponseNotFoundException(final ComponentType componentType, final String message) {
        super(componentType, message);
    }

}
