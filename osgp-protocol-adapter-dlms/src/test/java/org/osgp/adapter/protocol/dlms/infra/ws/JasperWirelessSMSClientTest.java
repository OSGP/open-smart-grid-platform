package org.osgp.adapter.protocol.dlms.infra.ws;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.ws.test.client.RequestMatchers.payload;
import static org.springframework.ws.test.client.ResponseCreators.withPayload;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.Source;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.osgp.adapter.protocol.dlms.application.config.JwccWSConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.test.client.MockWebServiceServer;
import org.springframework.xml.transform.StringSource;

import com.jasperwireless.api.ws.service.sms.SendSMSResponse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JasperWirelessConfig.class, initializers = JasperWirelessSMSClientTest.PropertyMockingApplicationContextInitializer.class)
public class JasperWirelessSMSClientTest {

    private static final String WKAEWUPSMS_CORRID = "wkaewupsms123";
    private static final String LICENSEKEY = "7f206979-4fdf-4cbe-8d65-0e984dac6a9e";
    private static final String ICC_ID = "8931086113127163687";
    private static final String SMS_MSG_ID = "4302867004";

    public static class PropertyMockingApplicationContextInitializer implements
            ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(final ConfigurableApplicationContext applicationContext) {
            final MockEnvironment mockEnvironment = new MockEnvironment();
            mockEnvironment.setProperty("jwcc.uri.sms", "https://kpnapi.jasperwireless.com/ws/service/Sms");
            mockEnvironment.setProperty("jwcc.licensekey", LICENSEKEY);
            mockEnvironment.setProperty("jwcc.api_version", "5.90");
            mockEnvironment.setProperty("jwcc.username", "MaartenvanHaasteren");
            mockEnvironment.setProperty("jwcc.password", "Jwcc@KPN123");

            applicationContext.setEnvironment(mockEnvironment);
        }
    }

    @Autowired
    WebServiceTemplate webServiceTemplate;

    @Autowired
    JwccWSConfig jwccWSConfig;

    @Mock
    CorrelationIdProviderService correlationIdProviderService;

    private MockWebServiceServer mockServer;

    @InjectMocks
    @Autowired
    private JasperWirelessSMSClientImpl wsClientService;

    @Before
    public void createServer() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockServer = MockWebServiceServer.createServer(this.webServiceTemplate);
    }

    @Test
    public void testSendWakeUpSMS() throws Exception {
        // given
        final Source requestPayload = new StringSource("<ns2:SendSMSRequest "
                + "xmlns:ns2=\"http://api.jasperwireless.com/ws/schema\" messageTextEncoding=\"\">" + "<ns2:messageId>"
                + WKAEWUPSMS_CORRID + "</ns2:messageId>" + "<ns2:version>5.90</ns2:version>" + "<ns2:licenseKey>"
                + LICENSEKEY + "</ns2:licenseKey>" + "<ns2:sentToIccid>" + ICC_ID + "</ns2:sentToIccid>"
                + "<ns2:messageText/>" + "</ns2:SendSMSRequest>");

        final Source responsePayload = new StringSource("<ns2:SendSMSResponse "
                + "ns2:requestId=\"IfBlIDGkzgTkWqa3\" xmlns:ns2=\"http://api.jasperwireless.com/ws/schema\">"
                + "<ns2:correlationId>" + WKAEWUPSMS_CORRID + "</ns2:correlationId>"
                + "<ns2:version>5.90</ns2:version>" + "<ns2:build>jasper_release_6.29-160108-154179</ns2:build>"
                + "<ns2:timestamp>2016-01-18T12:22:05.082Z</ns2:timestamp>" + "<ns2:smsMsgId>" + SMS_MSG_ID
                + "</ns2:smsMsgId>" + "</ns2:SendSMSResponse>");

        // when
        when(this.correlationIdProviderService.getCorrelationId("wakeupsms", ICC_ID)).thenReturn(WKAEWUPSMS_CORRID);

        // then
        this.mockServer.expect(payload(requestPayload)).andRespond(withPayload(responsePayload));

        final SendSMSResponse response = this.wsClientService.sendWakeUpSMS(ICC_ID);

        this.mockServer.verify();
        assertEquals(SMS_MSG_ID, String.valueOf(response.getSmsMsgId()));
    }

    public XMLGregorianCalendar getXMLGregorianCalendarNow() throws DatatypeConfigurationException {
        final GregorianCalendar gregorianCalendar = new GregorianCalendar();
        final DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
        final XMLGregorianCalendar now = datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
        return now;
    }
}
