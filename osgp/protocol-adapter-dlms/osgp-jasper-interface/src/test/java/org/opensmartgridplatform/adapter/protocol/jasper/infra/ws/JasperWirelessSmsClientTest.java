/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.jasper.infra.ws;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.ws.test.client.RequestMatchers.payload;
import static org.springframework.ws.test.client.ResponseCreators.withPayload;

import com.jasperwireless.api.ws.service.GetSMSDetailsResponse;
import com.jasperwireless.api.ws.service.SendSMSResponse;
import com.jasperwireless.api.ws.service.SmsMessageType;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.Source;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.test.client.MockWebServiceServer;
import org.springframework.xml.transform.StringSource;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = JasperWirelessTestConfig.class,
    initializers = JasperWirelessSmsClientTest.PropertyMockingApplicationContextInitializer.class)
class JasperWirelessSmsClientTest {

  private static final String WKAEWUPSMS_CORRID = "wkaewupsms123";
  private static final String LICENSEKEY = "a-combination-of-characters";
  private static final String ICC_ID = "8931086113127163687";
  private static final String SMS_MSG_ID = "4302867004";
  private static final String JWCC_STATUS = "Delivered";
  private static final String MODEM_STATUS = "DeliverAckReceivedStatusSuccessful";
  private static final String API_VERSION = "1234";
  private static final String VALIDITY_PERIOD = "6";

  static class PropertyMockingApplicationContextInitializer
      implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(final ConfigurableApplicationContext applicationContext) {
      final MockEnvironment mockEnvironment = new MockEnvironment();
      mockEnvironment.setProperty("jwcc.uri.sms", "https://acme.com/ws/service/Sms");
      mockEnvironment.setProperty("jwcc.licensekey", LICENSEKEY);
      mockEnvironment.setProperty("jwcc.api_version", API_VERSION);
      mockEnvironment.setProperty("jwcc.username", "JohnDoe");
      mockEnvironment.setProperty("jwcc.password", "Whatever");
      mockEnvironment.setProperty("jwcc.validity_period", VALIDITY_PERIOD);

      applicationContext.setEnvironment(mockEnvironment);
    }
  }

  @Autowired private WebServiceTemplate webServiceTemplate;

  @Mock private CorrelationIdProviderService correlationIdProviderService;

  private MockWebServiceServer mockServer;

  @InjectMocks @Autowired private JasperWirelessSmsClient wsClientService;

  @BeforeEach
  void createServer() {
    MockitoAnnotations.openMocks(this);
    this.mockServer = MockWebServiceServer.createServer(this.webServiceTemplate);
  }

  @Test
  void testSendWakeUpSms() {
    // given
    final Source requestPayload =
        new StringSource(
            "<ns2:SendSMSRequest "
                + "xmlns:ns2=\"http://api.jasperwireless.com/ws/schema\" messageTextEncoding=\"\">"
                + "<ns2:messageId>"
                + WKAEWUPSMS_CORRID
                + "</ns2:messageId>"
                + "<ns2:version>"
                + API_VERSION
                + "</ns2:version>"
                + "<ns2:licenseKey>"
                + LICENSEKEY
                + "</ns2:licenseKey>"
                + "<ns2:sentToIccid>"
                + ICC_ID
                + "</ns2:sentToIccid>"
                + "<ns2:tpvp>"
                + VALIDITY_PERIOD
                + "</ns2:tpvp>"
                + "<ns2:messageText/>"
                + "</ns2:SendSMSRequest>");

    final Source responsePayload =
        new StringSource(
            "<ns2:SendSMSResponse "
                + "ns2:requestId=\"IfBlIDGkzgTkWqa3\" xmlns:ns2=\"http://api.jasperwireless.com/ws/schema\">"
                + "<ns2:correlationId>"
                + WKAEWUPSMS_CORRID
                + "</ns2:correlationId>"
                + "<ns2:version>"
                + API_VERSION
                + "</ns2:version>"
                + "<ns2:build>jasper_release_6.29-160108-154179</ns2:build>"
                + "<ns2:timestamp>2016-01-18T12:22:05.082Z</ns2:timestamp>"
                + "<ns2:smsMsgId>"
                + SMS_MSG_ID
                + "</ns2:smsMsgId>"
                + "</ns2:SendSMSResponse>");

    // when
    when(this.correlationIdProviderService.getCorrelationId("wakeupsms", ICC_ID))
        .thenReturn(WKAEWUPSMS_CORRID);

    // then
    this.mockServer.expect(payload(requestPayload)).andRespond(withPayload(responsePayload));

    final SendSMSResponse response = this.wsClientService.sendWakeUpSMS(ICC_ID);

    this.mockServer.verify();
    assertThat(String.valueOf(response.getSmsMsgId())).isEqualTo(SMS_MSG_ID);
  }

  @Test
  void testSendWakeUpSmsResult() {
    // given
    final Source requestPayload =
        new StringSource(
            "<ns2:GetSMSDetailsRequest "
                + "xmlns:ns2=\"http://api.jasperwireless.com/ws/schema\" messageTextEncoding=\"\">"
                + "<ns2:messageId>"
                + WKAEWUPSMS_CORRID
                + "</ns2:messageId>"
                + "<ns2:version>"
                + API_VERSION
                + "</ns2:version>"
                + "<ns2:licenseKey>"
                + LICENSEKEY
                + "</ns2:licenseKey>"
                + "<ns2:smsMsgIds>"
                + "<ns2:smsMsgId>"
                + SMS_MSG_ID
                + "</ns2:smsMsgId>"
                + "</ns2:smsMsgIds>"
                + "</ns2:GetSMSDetailsRequest>");

    final Source responsePayload =
        new StringSource(
            "<ns2:GetSMSDetailsResponse "
                + "ns2:requestId=\"c16KNt8BksvZDLex\" xmlns:ns2=\"http://api.jasperwireless.com/ws/schema\">"
                + "<ns2:correlationId>"
                + WKAEWUPSMS_CORRID
                + "</ns2:correlationId>"
                + "<ns2:version>"
                + API_VERSION
                + "</ns2:version>"
                + "<ns2:build>jasper_release_6.29-160108-154179</ns2:build>"
                + "<ns2:timestamp>2016-01-18T12:31:51.760Z</ns2:timestamp>"
                + "<ns2:smsMessages>"
                + "<ns2:smsMessage>"
                + "<ns2:smsMsgId>"
                + SMS_MSG_ID
                + "</ns2:smsMsgId>"
                + "<ns2:status>"
                + JWCC_STATUS
                + "</ns2:status>"
                + "<ns2:senderLogin>MaartenvanHaasteren</ns2:senderLogin>"
                + "<ns2:sentToIccid>3197002475559</ns2:sentToIccid>"
                + "<ns2:sentFrom>Server</ns2:sentFrom>"
                + "<ns2:smsMsgAttemptStatus>"
                + MODEM_STATUS
                + "</ns2:smsMsgAttemptStatus>"
                + "<ns2:msgType>MT</ns2:msgType>"
                + "<ns2:dateSent>2016-01-18T12:22:04.853Z</ns2:dateSent>"
                + "<ns2:dateReceived>2016-01-18T12:22:09.878Z</ns2:dateReceived>"
                + "<ns2:dateAdded>2016-01-18T12:22:04.854Z</ns2:dateAdded>"
                + "<ns2:dateModified>2016-01-18T12:22:09.889Z</ns2:dateModified>"
                + "</ns2:smsMessage>"
                + "</ns2:smsMessages>"
                + "</ns2:GetSMSDetailsResponse>");

    // when
    when(this.correlationIdProviderService.getCorrelationId("wakeupsms", ICC_ID))
        .thenReturn(WKAEWUPSMS_CORRID);

    // then
    this.mockServer.expect(payload(requestPayload)).andRespond(withPayload(responsePayload));

    final GetSMSDetailsResponse response =
        this.wsClientService.getSMSDetails(new Long(SMS_MSG_ID), ICC_ID);

    this.mockServer.verify();
    final List<SmsMessageType> smsMessageTypes = response.getSmsMessages().getSmsMessage();
    assertThat(smsMessageTypes).isNotNull();
    final SmsMessageType smsMessageType = smsMessageTypes.get(0);
    assertThat(smsMessageType.getStatus()).isEqualTo(JWCC_STATUS);
    assertThat(smsMessageType.getSmsMsgAttemptStatus()).isEqualTo(MODEM_STATUS);
  }

  public XMLGregorianCalendar getXmlGregorianCalendarNow() throws DatatypeConfigurationException {
    final GregorianCalendar gregorianCalendar = new GregorianCalendar();
    final DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
    return datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
  }
}
