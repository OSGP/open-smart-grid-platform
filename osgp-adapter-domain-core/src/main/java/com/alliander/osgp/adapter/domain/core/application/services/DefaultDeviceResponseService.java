package com.alliander.osgp.adapter.domain.core.application.services;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.adapter.domain.core.infra.jms.ws.WebServiceResponseMessageSender;
import com.alliander.osgp.domain.core.exceptions.PlatformException;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service(value = "domainCoreDefaultDeviceResponseService")
public class DefaultDeviceResponseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDeviceResponseService.class);

    @Autowired
    private WebServiceResponseMessageSender webServiceResponseMessageSender;

    public void handleDefaultDeviceResponse(final String deviceIdentification, final String organisationIdentification,
            final String correlationUid, final String messageType, final ResponseMessageResultType deviceResult,
            final String errorDescription) {

        ResponseMessageResultType result = ResponseMessageResultType.OK;
        String description = "";

        try {
            if (deviceResult == ResponseMessageResultType.NOT_OK || StringUtils.isNotEmpty(errorDescription)) {
                throw new PlatformException("Device Response not ok.");
            }
        } catch (final Exception e) {
            LOGGER.error("Unexpected Exception", e);
            result = ResponseMessageResultType.NOT_OK;
            description = e.getMessage();
        }

        this.webServiceResponseMessageSender.send(new ResponseMessage(correlationUid, organisationIdentification,
                deviceIdentification, result, description, null));
    }
}
