package com.alliander.osgp.adapter.ws.endpointinterceptors;

import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;

import com.alliander.osgp.domain.core.exceptions.EmptyApplicationNameSoapHeaderException;
import com.alliander.osgp.domain.core.exceptions.EmptyOrganisationIdentificationSoapHeaderException;
import com.alliander.osgp.domain.core.exceptions.EmptyUserNameSoapHeaderException;

/**
 * Intercept a SOAP Header and put the contents in the MessageContext.
 */
public class SoapHeaderEndpointInterceptor implements EndpointInterceptor {

    private final String organisationIdentificationHeaderName;
    private final String contextPropertyName;

    private final String userNameHeaderName = "UserName";
    private final String applicationNameHeaderName = "ApplicationName";

    public SoapHeaderEndpointInterceptor(final String headerName, final String contextPropertyName) {
        this.organisationIdentificationHeaderName = headerName;
        this.contextPropertyName = contextPropertyName;
    }

    @Override
    public boolean handleRequest(final MessageContext messageContext, final Object endpoint) throws Exception {

        Assert.isInstanceOf(SoapMessage.class, messageContext.getRequest());
        final SoapMessage request = (SoapMessage) messageContext.getRequest();
        final SoapHeader soapHeader = request.getSoapHeader();

        // Try to get the values from the Soap Header.
        final String organisationIdentification = this.getHeaderValue(soapHeader,
                this.organisationIdentificationHeaderName);
        final String userName = this.getHeaderValue(soapHeader, this.userNameHeaderName);
        final String applicationName = this.getHeaderValue(soapHeader, this.applicationNameHeaderName);

        // Check if the values are empty, if so, throw exception.
        if (StringUtils.isEmpty(organisationIdentification)) {
            throw new EmptyOrganisationIdentificationSoapHeaderException();
        }

        if (StringUtils.isEmpty(userName)) {
            throw new EmptyUserNameSoapHeaderException();
        }

        if (StringUtils.isEmpty(applicationName)) {
            throw new EmptyApplicationNameSoapHeaderException();
        }

        // Finally, set the organisation identification into the message
        // context, so it can be used in the end point later.
        messageContext.setProperty(this.contextPropertyName, organisationIdentification);

        // Return true so the interceptor chain will continue.
        return true;
    }

    private String getHeaderValue(final SoapHeader soapHeader, final String valueName) {
        String value = "";
        final Iterator<SoapHeaderElement> iterator = soapHeader.examineAllHeaderElements();

        while (iterator.hasNext()) {
            final SoapHeaderElement element = iterator.next();

            if (element.getName().getLocalPart().equals(valueName)) {
                value = element.getText();
                break;
            }
        }

        return value;
    }

    @Override
    public boolean handleResponse(final MessageContext messageContext, final Object endpoint) throws Exception {
        return true;
    }

    @Override
    public boolean handleFault(final MessageContext messageContext, final Object endpoint) throws Exception {
        return true;
    }

    @Override
    public void afterCompletion(final MessageContext messageContext, final Object endpoint, final Exception ex)
            throws Exception {
        // Empty Method
    }
}
