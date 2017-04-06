package com.smartsocietyservices.osgp.adapter.ws.da.presentation.ws;

import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.smartsocietyservices.osgp.adapter.ws.da.application.mapping.DistributionAutomationMapper;
import com.smartsocietyservices.osgp.adapter.ws.da.application.services.DistributionAutomationService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class GenericDistributionAutomationEndPoint {
    protected static final String NAMESPACE = "http://www.smartsocietyservices.com/schemas/osgp/distributionautomation/defs/2017/04";

    @Autowired
    protected DistributionAutomationService service;

    @Autowired
    protected DistributionAutomationMapper mapper;

    public GenericDistributionAutomationEndPoint() {
    }

    protected void handleException( final Logger logger, final Exception e ) throws OsgpException {
        // Rethrow exception if it already is a functional or technical
        // exception, otherwise throw new technical exception.
        logger.error( "Exception occurred: ", e );
        if ( e instanceof OsgpException ) {
            throw (OsgpException) e;
        } else {
            throw new TechnicalException( ComponentType.WS_DISTRIBUTION_AUTOMATION, e );
        }
    }
}
