/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.endpointinterceptors;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.opensmartgridplatform.adapter.ws.infra.jms.EndpointClassAndMethod;
import org.opensmartgridplatform.adapter.ws.infra.jms.LoggingMessageSender;
import org.opensmartgridplatform.adapter.ws.infra.jms.LoggingRequestMessage;
import org.opensmartgridplatform.adapter.ws.infra.jms.ResponseResultAndDataSize;
import org.opensmartgridplatform.domain.core.exceptions.WebServiceMonitorInterceptorException;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.server.endpoint.MethodEndpoint;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class WebServiceMonitorInterceptor implements EndpointInterceptor {

  private static final Logger LOGGER = LoggerFactory.getLogger(WebServiceMonitorInterceptor.class);

  @Autowired
  @Qualifier("loggingMessageSender")
  private LoggingMessageSender loggingMessageSender;

  private static final String CORRELATION_UID = "correlationUid";
  private static final String DEVICE_ID = "deviceId";
  private static final String RESPONSE_RESULT = "result";
  private static final String RESPONSE_DATA_SIZE = "dataSize";

  private static final String XML_ELEMENT_CORRELATION_UID = "CorrelationUid";
  private static final String XML_ELEMENT_DEVICE_ID = "DeviceId";
  private static final String XML_ELEMENT_OSGP_RESULT_TYPE = "Result";

  private static final String FAULT_RESPONSE_RESULT = "SOAP_FAULT";

  private final String organisationIdentificationHeader;
  private final String userNameHeader;
  private final String applicationNameHeader;

  private final boolean soapMessageLoggingEnabled;
  private final boolean soapMessagePrintingEnabled;

  public WebServiceMonitorInterceptor(
      final String organisationIdentificationHeader,
      final String userNameHeader,
      final String applicationNameHeader,
      final WebServiceMonitorInterceptorCapabilities capabilities) {
    LOGGER.info(
        "Initializing WebServiceMonitorInterceptor with SOAP headers [{}, {}, {}] and {}.",
        organisationIdentificationHeader,
        userNameHeader,
        applicationNameHeader,
        capabilities);

    this.organisationIdentificationHeader = organisationIdentificationHeader;
    this.userNameHeader = userNameHeader;
    this.applicationNameHeader = applicationNameHeader;
    this.soapMessageLoggingEnabled = capabilities.isSoapMessageLoggingEnabled();
    this.soapMessagePrintingEnabled = capabilities.isSoapMessagePrintingEnabled();
  }

  @Override
  public boolean handleRequest(final MessageContext messageContext, final Object endpoint)
      throws Exception {

    final SoapMessage request = this.getSoapRequest(messageContext);
    this.printSoapMessage(request);

    return true;
  }

  @Override
  public boolean handleResponse(final MessageContext messageContext, final Object endpoint)
      throws Exception {

    final SoapMessage response = this.getSoapResponse(messageContext);
    this.printSoapMessage(response);
    this.createAndSendLoggingRequestMessage(messageContext, endpoint, null);

    return true;
  }

  @Override
  public boolean handleFault(final MessageContext messageContext, final Object endpoint)
      throws Exception {

    final SoapMessage fault = this.getSoapResponse(messageContext);
    this.printSoapMessage(fault);
    this.createAndSendLoggingRequestMessage(messageContext, endpoint, FAULT_RESPONSE_RESULT);

    return true;
  }

  @Override
  public void afterCompletion(
      final MessageContext messageContext, final Object endpoint, final Exception ex)
      throws Exception {
    // This method is not used, but is part of the EndpointInterceptor
    // interface.
  }

  /**
   * Get the class name and method name from the end point parameter.
   *
   * @param endpoint Object representing the end point class.
   */
  private EndpointClassAndMethod getEndPointClassAndMethod(final Object endpoint) {
    final MethodEndpoint method = (MethodEndpoint) endpoint;
    return new EndpointClassAndMethod(this.classNameFrom(method), this.methodNameFrom(method));
  }

  private String classNameFrom(final MethodEndpoint method) {
    return method.getBean().toString().split("@")[0];
  }

  private String methodNameFrom(final MethodEndpoint method) {
    return method.getMethod().getName() + "()";
  }

  /**
   * Get a value for a valueName from the soap header.
   *
   * @param soapHeader The soap header.
   * @return The value, or an empty string if not found.
   */
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

  /**
   * Try to find the XML_ELEMENT_CORRELATION_UID, XML_ELEMENT_DEVICE_ID and the
   * XML_ELEMENT_OSGP_RESULT_TYPE from the soap message. Note that these elements are not always
   * present in the soap message. In that case, the values will be null. Also, determine the data
   * size of the response.
   *
   * @param soapMessage The soap message.
   * @return Map containing CORRELATION_UID, DEVICE_ID, RESPONSE_RESULT and RESPONSE_DATA_SIZE.
   */
  private Map<String, Object> parseSoapMessage(final SoapMessage soapMessage) {
    try {
      // Determine the data size of the message (stream).
      final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      soapMessage.writeTo(outputStream);
      final int dataSize = outputStream.size();

      // Try to find the desired XML elements in the document.
      final Document document = soapMessage.getDocument();
      final String correlationUid =
          this.evaluateXPathExpression(document, XML_ELEMENT_CORRELATION_UID);
      final String deviceId = this.evaluateXPathExpression(document, XML_ELEMENT_DEVICE_ID);
      final String result = this.evaluateXPathExpression(document, XML_ELEMENT_OSGP_RESULT_TYPE);

      // Create the Map containing the output.
      final Map<String, Object> map = new HashMap<>();
      map.put(CORRELATION_UID, correlationUid);
      map.put(DEVICE_ID, deviceId);
      map.put(RESPONSE_RESULT, result);
      map.put(RESPONSE_DATA_SIZE, dataSize);

      return map;
    } catch (final Exception e) {
      LOGGER.error("failed to parse soap message or to execute xPath expression", e);
      return null;
    }
  }

  /**
   * Search an XML element using an XPath expression.
   *
   * @param document The XML document.
   * @param element The name of the desired XML element.
   * @return The content of the XML element, or null if the element is not found.
   * @throws XPathExpressionException In case the expression fails to compile or evaluate, an
   *     exception will be thrown.
   */
  private String evaluateXPathExpression(final Document document, final String element)
      throws XPathExpressionException {
    final String expression = String.format("//*[contains(local-name(), '%s')]", element);

    final XPathFactory xFactory = XPathFactory.newInstance();
    final XPath xPath = xFactory.newXPath();

    final XPathExpression xPathExpression = xPath.compile(expression);
    final NodeList nodeList = (NodeList) xPathExpression.evaluate(document, XPathConstants.NODESET);

    if (nodeList != null && nodeList.getLength() > 0) {
      return nodeList.item(0).getTextContent();
    } else {
      return null;
    }
  }

  /**
   * Create the logging Request Message and send it using {@link
   * WebServiceMonitorInterceptor#loggingMessageSender}.
   *
   * @param messageContext The messageContext.
   * @param endpoint The endpoint.
   * @param overrideResult When set, use this value as result for the {@link LoggingRequestMessage}.
   * @throws WebServiceMonitorInterceptorException
   */
  private void createAndSendLoggingRequestMessage(
      final MessageContext messageContext, final Object endpoint, final String overrideResult)
      throws WebServiceMonitorInterceptorException {
    if (!this.soapMessageLoggingEnabled) {
      return;
    }

    // Get the current time.
    final Date now = new Date();

    // Get EndPointClass and EndPointMethod.
    final EndpointClassAndMethod classAndMethod = this.getEndPointClassAndMethod(endpoint);

    // Get the request.
    final SoapMessage request = this.getSoapRequest(messageContext);
    final SoapHeader soapHeader = request.getSoapHeader();

    // Read OrganisationIdentification, UserName and ApplicationName from
    // header from request.
    final String organisationIdentification =
        this.getHeaderValue(soapHeader, this.organisationIdentificationHeader);
    final String userName = this.getHeaderValue(soapHeader, this.userNameHeader);
    final String appName = this.getHeaderValue(soapHeader, this.applicationNameHeader);

    // Read correlationUid and deviceId from request.
    final Map<String, Object> requestData = this.parseSoapMessage(request);
    if (requestData == null) {
      throw new WebServiceMonitorInterceptorException(
          "unable to get correlationUid or deviceId from request");
    }

    // Get the response.
    final SoapMessage response = this.getSoapResponse(messageContext);

    // Read correlationUid and deviceId and result and data size from
    // response.
    final Map<String, Object> responseData = this.parseSoapMessage(response);
    if (responseData == null) {
      throw new WebServiceMonitorInterceptorException(
          "unable to get correlationUid or deviceId or result from response");
    }

    // Check response for correlationId, otherwise request.
    String correlationUid = (String) responseData.get(CORRELATION_UID);
    if (StringUtils.isEmpty(correlationUid)) {
      correlationUid = (String) requestData.get(CORRELATION_UID);
    }

    // Creating the logging request message.
    final String deviceIdentification = (String) requestData.get(DEVICE_ID);
    final CorrelationIds ids =
        new CorrelationIds(organisationIdentification, deviceIdentification, correlationUid);
    final ResponseResultAndDataSize responseResultAndDataSize =
        this.resultAndDataSizeFrom(responseData);

    final LoggingRequestMessage loggingRequestMessage =
        new LoggingRequestMessage(
            now, ids, userName, appName, classAndMethod, responseResultAndDataSize);
    if (!StringUtils.isEmpty(overrideResult)) {
      loggingRequestMessage.setResponseResult(overrideResult);
    }
    this.loggingMessageSender.send(loggingRequestMessage);
  }

  private SoapMessage getSoapRequest(final MessageContext messageContext) {
    final WebServiceMessage webServiceMessage = messageContext.getRequest();
    Assert.isInstanceOf(SoapMessage.class, webServiceMessage);
    return (SoapMessage) webServiceMessage;
  }

  private SoapMessage getSoapResponse(final MessageContext messageContext) {
    final WebServiceMessage webServiceMessage = messageContext.getResponse();
    Assert.isInstanceOf(SoapMessage.class, webServiceMessage);
    return (SoapMessage) webServiceMessage;
  }

  private ResponseResultAndDataSize resultAndDataSizeFrom(final Map<String, Object> responseData) {
    return new ResponseResultAndDataSize(
        (String) responseData.get(RESPONSE_RESULT), (int) responseData.get(RESPONSE_DATA_SIZE));
  }

  /**
   * Print a soap message.
   *
   * @param soapMessage The message to print.
   */
  private void printSoapMessage(final SoapMessage soapMessage) {
    if (!this.soapMessagePrintingEnabled) {
      return;
    }

    try {
      final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      soapMessage.writeTo(outputStream);
      final String message = new String(outputStream.toByteArray());
      LOGGER.info("soap message: {}", message);
    } catch (final IOException e) {
      LOGGER.error("Unexpected error while writing soap message", e);
    }
  }
}
