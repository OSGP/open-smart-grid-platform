/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.dlms.cucumber.steps.ws.smartmetering.smartmeteringinstallation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.CoupleMbusDeviceAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.CoupleMbusDeviceAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.CoupleMbusDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.CoupleMbusDeviceResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.DeCoupleMbusDeviceAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.DeCoupleMbusDeviceAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.DeCoupleMbusDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.DeCoupleMbusDeviceResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.ObjectFactory;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.support.ws.WebServiceSecurityException;
import com.alliander.osgp.platform.cucumber.support.ws.WebServiceTemplateFactory;
import com.alliander.osgp.platform.dlms.cucumber.steps.ws.smartmetering.AbstractSmartMeteringSteps;
import com.alliander.osgp.platform.dlms.cucumber.steps.ws.smartmetering.PollingReader;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class CoupleAndDecoupleDevice extends AbstractSmartMeteringSteps {

    @Value("${maxTimeResponseAvailability}")
    private int maxTimeResponseAvailability;

    @Autowired
    @Qualifier("webServiceTemplateFactoryInstallationManagement")
    private WebServiceTemplateFactory webServiceTemplateFactory;

    @When("^the Couple G-meter \"([^\"]*)\" request on channel (\\d+) is received$")
    public void theCoupleGMeterRequestIsReceived(final String gasMeter, final Short channel)
            throws WebServiceSecurityException {

        final CoupleMbusDeviceRequest request = new ObjectFactory().createCoupleMbusDeviceRequest();
        request.setDeviceIdentification((String) ScenarioContext.Current().get(Keys.KEY_DEVICE_IDENTIFICATION));
        request.setMbusDeviceIdentification(gasMeter);
        request.setChannel(channel);

        final CoupleMbusDeviceAsyncResponse response = (CoupleMbusDeviceAsyncResponse) this.webServiceTemplateFactory
                .getTemplate(Defaults.DEFAULT_ORGANISATION_IDENTIFICATION, Defaults.DEFAULT_USER_NAME)
                .marshalSendAndReceive(request);

        this.checkAndSaveCorrelationId(response.getCorrelationUid());
    }

    @When("^the Couple G-meter \"([^\"]*)\" to E-meter \"([^\"]*)\" request on channel (\\d+) is received$")
    public void theCoupleGMeterToEMeterRequestOnChannelIsReceived(final String gasMeter, final String eMeter,
            final Short channel) throws WebServiceSecurityException {

        final CoupleMbusDeviceRequest request = new ObjectFactory().createCoupleMbusDeviceRequest();
        request.setDeviceIdentification(eMeter);
        request.setMbusDeviceIdentification(gasMeter);
        request.setChannel(channel);

        final CoupleMbusDeviceResponse response = (CoupleMbusDeviceResponse) this.webServiceTemplateFactory
                .getTemplate(Defaults.DEFAULT_ORGANISATION_IDENTIFICATION, Defaults.DEFAULT_USER_NAME)
                .marshalSendAndReceive(request);

        final OsgpResultType result = response.getResult();
        assertTrue(OsgpResultType.OK.equals(result));
    }

    /**
     * @param gasMeter
     *            the device identification of the gas meter
     * @param eMeter
     *            the device identification of the e meter
     * @param channel
     *            the channel the gas meter on the e meter
     * @throws WebServiceSecurityException
     *             should not be thrown in this test
     */
    @Then("^the Couple G-meter \"([^\"]*)\" to E-meter \"([^\"]*)\" request on channel (\\d+) throws an SoapException with message \"([^\"]*)\"$")
    public void theCoupleGMeterToEMeterRequestOnChannelThrowsAnSoapException(final String gasMeter,
            final String eMeter, final Short channel, final String soapExceptionMessage)
            throws WebServiceSecurityException {
        final CoupleMbusDeviceRequest request = new ObjectFactory().createCoupleMbusDeviceRequest();
        request.setDeviceIdentification(eMeter);
        request.setMbusDeviceIdentification(gasMeter);
        request.setChannel(channel);

        try {
            this.webServiceTemplateFactory.getTemplate(Defaults.DEFAULT_ORGANISATION_IDENTIFICATION,
                    Defaults.DEFAULT_USER_NAME).marshalSendAndReceive(request);
            fail("A SoapFaultClientException should be thrown");
        } catch (final SoapFaultClientException e) {
            assertEquals(soapExceptionMessage, e.getMessage());
        }

    }

    @Then("^the DeCouple G-meter \"([^\"]*)\" from E-meter \"([^\"]*)\" request throws an SoapException with message \"([^\"]*)\"$")
    public void theDeCoupleGMeterToEMeterRequestThrowsAnSoapException(final String gasMeter, final String eMeter,
            final String soapExceptionMessage) throws WebServiceSecurityException {
        final DeCoupleMbusDeviceRequest request = new ObjectFactory().createDeCoupleMbusDeviceRequest();
        request.setDeviceIdentification(eMeter);
        request.setMbusDeviceIdentification(gasMeter);

        try {
            final DeCoupleMbusDeviceAsyncResponse r = (DeCoupleMbusDeviceAsyncResponse) this.webServiceTemplateFactory
                    .getTemplate(Defaults.DEFAULT_ORGANISATION_IDENTIFICATION, Defaults.DEFAULT_USER_NAME)
                    .marshalSendAndReceive(request);
            fail("A SoapFaultClientException should be thrown");
        } catch (final SoapFaultClientException e) {
            assertEquals(soapExceptionMessage, e.getMessage());
        }

    }

    @When("^the Couple G-meter \"([^\"]*)\" request on channel (\\d+) is throws an SoapException with message \"([^\"]*)\"$")
    public void theCoupleGMeterRequestOnChannelIsThrowsAnSoapExceptionWithMessage(final String gasMeter,
            final Short channel, final String soapExceptionMessage) throws WebServiceSecurityException {
        final CoupleMbusDeviceRequest request = new ObjectFactory().createCoupleMbusDeviceRequest();
        request.setDeviceIdentification((String) ScenarioContext.Current().get(Keys.KEY_DEVICE_IDENTIFICATION));
        request.setMbusDeviceIdentification(gasMeter);
        request.setChannel(channel);

        try {
            final CoupleMbusDeviceResponse response = (CoupleMbusDeviceResponse) this.webServiceTemplateFactory
                    .getTemplate(Defaults.DEFAULT_ORGANISATION_IDENTIFICATION, Defaults.DEFAULT_USER_NAME)
                    .marshalSendAndReceive(request);
            fail("A SoapFaultClientException should be thrown");
        } catch (final SoapFaultClientException e) {
            assertEquals(soapExceptionMessage, e.getMessage());
        }

    }

    @When("^the Decouple G-meter \"([^\"]*)\" request is received$")
    public void theDecoupleGMeterRequestIsReceived(final String gasMeter) throws WebServiceSecurityException {

        final DeCoupleMbusDeviceRequest request = new ObjectFactory().createDeCoupleMbusDeviceRequest();
        request.setDeviceIdentification((String) ScenarioContext.Current().get(Keys.KEY_DEVICE_IDENTIFICATION));
        request.setMbusDeviceIdentification(gasMeter);
        final DeCoupleMbusDeviceAsyncResponse response = (DeCoupleMbusDeviceAsyncResponse) this.webServiceTemplateFactory
                .getTemplate(Defaults.DEFAULT_ORGANISATION_IDENTIFICATION, Defaults.DEFAULT_USER_NAME)
                .marshalSendAndReceive(request);

        this.checkAndSaveCorrelationId(response.getCorrelationUid());
    }

    @When("^the Decouple G-meter \"([^\"]*)\" from E-meter \"([^\"]*)\" request is received$")
    public void theDecoupleGMeterFromEMeterRequestIsReceived(final String gasMeter, final String eMeter)
            throws WebServiceSecurityException {

        final DeCoupleMbusDeviceRequest request = new ObjectFactory().createDeCoupleMbusDeviceRequest();
        request.setDeviceIdentification(eMeter);
        request.setMbusDeviceIdentification(gasMeter);
        final DeCoupleMbusDeviceAsyncResponse response = (DeCoupleMbusDeviceAsyncResponse) this.webServiceTemplateFactory
                .getTemplate(Defaults.DEFAULT_ORGANISATION_IDENTIFICATION, Defaults.DEFAULT_USER_NAME)
                .marshalSendAndReceive(request);

        this.checkAndSaveCorrelationId(response.getCorrelationUid());
    }

    @Then("^the couple response is \"([^\"]*)\" and contains$")
    public void theCoupleResponseContains(final String status, final List<String> resultList)
            throws WebServiceSecurityException, InterruptedException {

        // Thread.sleep(SLEEP);

        final CoupleMbusDeviceAsyncRequest request = new ObjectFactory().createCoupleMbusDeviceAsyncRequest();
        request.setCorrelationUid((String) ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID));
        request.setDeviceIdentification((String) ScenarioContext.Current().get(Keys.KEY_DEVICE_IDENTIFICATION));

        final PollingReader pr = new PollingReader(this.maxTimeResponseAvailability) {

            @Override
            public Object getResponsePolling() throws WebServiceSecurityException {

                final CoupleMbusDeviceResponse response = (CoupleMbusDeviceResponse) CoupleAndDecoupleDevice.this.webServiceTemplateFactory
                        .getTemplate(Defaults.DEFAULT_ORGANISATION_IDENTIFICATION, Defaults.DEFAULT_USER_NAME)
                        .marshalSendAndReceive(request);
                return response;
            }
        };
        final OsgpResultType resultType = OsgpResultType.fromValue(status);

        final CoupleMbusDeviceResponse response = (CoupleMbusDeviceResponse) pr.run();

        assertTrue(resultType.equals(response.getResult()));
        assertTrue(this.checkDescription(response.getDescription(), resultList));

    }

    @Then("^the decouple response is \"([^\"]*)\" and contains$")
    public void theDecoupleResponseIsAndContains(final String status, final List<String> resultList)
            throws WebServiceSecurityException, InterruptedException {

        final DeCoupleMbusDeviceAsyncRequest request = new ObjectFactory().createDeCoupleMbusDeviceAsyncRequest();
        request.setCorrelationUid((String) ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID));
        request.setDeviceIdentification((String) ScenarioContext.Current().get(Keys.KEY_DEVICE_IDENTIFICATION));

        final PollingReader pr = new PollingReader(this.maxTimeResponseAvailability) {

            @Override
            public Object getResponsePolling() throws WebServiceSecurityException {
                final DeCoupleMbusDeviceResponse response = (DeCoupleMbusDeviceResponse) CoupleAndDecoupleDevice.this.webServiceTemplateFactory
                        .getTemplate(Defaults.DEFAULT_ORGANISATION_IDENTIFICATION, Defaults.DEFAULT_USER_NAME)
                        .marshalSendAndReceive(request);

                response.getDescription();

                return response;
            }
        };

        final DeCoupleMbusDeviceResponse response = (DeCoupleMbusDeviceResponse) pr.run();

        final OsgpResultType resultType = OsgpResultType.fromValue(status);

        assertTrue(resultType.equals(response.getResult()));
        assertTrue(this.checkDescription(response.getDescription(), resultList));

    }
}
