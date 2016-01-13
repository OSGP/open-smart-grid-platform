package org.osgp.adapter.protocol.dlms.infra.ws;

import static org.springframework.ws.test.client.RequestMatchers.payload;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.util.JAXBSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.test.client.MockWebServiceServer;

import com.jasperwireless.api.ws.service.sms.SendSMSRequest;
import com.jasperwireless.api.ws.service.sms.SendSMSResponse;

@RunWith(SpringJUnit4ClassRunner.class)
// @ContextConfiguration({ "classpath*:applicationContext.xml" })
@ContextConfiguration(classes = JasperWirelessConfig.class, initializers = JasperWirelessSMSClientTest.PropertyMockingApplicationContextInitializer.class)
public class JasperWirelessSMSClientTest {

    private static final String ICC_ID = "8931086214024039846";

    public static class PropertyMockingApplicationContextInitializer implements
            ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(final ConfigurableApplicationContext applicationContext) {
            final MockEnvironment mockEnvironment = new MockEnvironment();
            mockEnvironment.setProperty("jwcc.uri.sms", "https://api.jasperwireless.com/ws/service/Sms");
            mockEnvironment.setProperty("jwcc.licensekey", "39994af2-ff93-4400-a683-37eb26754122");
            mockEnvironment.setProperty("jwcc.api_version", "5.90");
            mockEnvironment.setProperty("jwcc.username", "jwcc-account-name");
            mockEnvironment.setProperty("jwcc.password", "jwcc-account-password");

            applicationContext.setEnvironment(mockEnvironment);
        }
    }

    @Autowired
    WebServiceTemplate webServiceTemplate;

    @Autowired
    private JasperWirelessSMSClientImpl wsClientService;

    private MockWebServiceServer mockServer;

    @Before
    public void createServer() throws Exception {
        this.mockServer = MockWebServiceServer.createServer(this.webServiceTemplate);
    }

    @Test
    public void testSendWakeUpSMS() throws Exception {

        final SendSMSRequest sendSMSRequest = new SendSMSRequest();
        sendSMSRequest.setLicenseKey("39994af2-ff93-4400-a683-37eb26754122");
        sendSMSRequest.setMessageId("");
        sendSMSRequest.setMessageText("");
        sendSMSRequest.setMessageTextEncoding("");
        sendSMSRequest.setSentToIccid(ICC_ID);
        sendSMSRequest.setVersion("5.90");
        final JAXBContext jc = JAXBContext.newInstance(SendSMSRequest.class);
        final JAXBSource requestPayload = new JAXBSource(jc, sendSMSRequest);

        // final Source requestPayload = new StringSource(
        // "<SendSMSRequest messageTextEncoding=\"\" xmlns=\"http://api.jasperwireless.com/ws/schema\">"
        // + "<messageId></messageId>" + "<version>5.90</version>"
        // + "<licenseKey>39994af2-ff93-4400-a683-37eb26754122</licenseKey>"
        // + "<sentToIccid>8931086214024039846</sentToIccid>" +
        // "<messageText></messageText>"
        // + "</SendSMSRequest>");

        // final Source responsePayload = new StringSource(
        // "<Fault xmlns=\"http://api.jasperwireless.com/ws/schema\">"
        // + "<faultcode>SOAP-ENV:Client</faultcode>"
        // + "<faultstring>400200</faultstring>"
        // + "<detail>"
        // +
        // "<jws:error xmlns:jws=\"http://api.jasperwireless.com/ws/schema\">Security validation error. Your username or password is incorrect.</jws:error>"
        // +
        // "<jws:exception xmlns:jws=\"http://api.jasperwireless.com/ws/schema\">com.jasperwireless.ws.ApiSecurityValidationException</jws:exception>"
        // +
        // "<jws:message xmlns:jws=\"http://api.jasperwireless.com/ws/schema\">org.springframework.ws.soap.security.xwss.XwsSecurityValidationException: com.sun.xml.wss.XWSSecurityException: Message does not conform to configured policy [ AuthenticationTokenPolicy(S) ]:  No Security Header found; nested exception is com.sun.xml.wss.XWSSecurityException: com.sun.xml.wss.XWSSecurityException: Message does not conform to configured policy [ AuthenticationTokenPolicy(S) ]:  No Security Header found Cause: com.sun.xml.wss.XWSSecurityException: Message does not conform to configured policy [ AuthenticationTokenPolicy(S) ]:  No Security Header found; nested exception is com.sun.xml.wss.XWSSecurityException: com.sun.xml.wss.XWSSecurityException: Message does not conform to configured policy [ AuthenticationTokenPolicy(S) ]:  No Security Header found</jws:message>"
        // + "</detail>" + "</Fault>");

        this.mockServer.expect(payload(requestPayload)); // .andRespond(withPayload(responsePayload));

        final SendSMSResponse response = this.wsClientService.sendWakeUpSMS(ICC_ID);

        this.mockServer.verify();
    }
}
