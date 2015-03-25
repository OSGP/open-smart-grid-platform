package com.alliander.osgp.adapter.ws.endpointinterceptors;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.server.endpoint.MethodEndpoint;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.alliander.osgp.adapter.ws.infra.jms.LoggingMessageSender;
import com.alliander.osgp.adapter.ws.infra.jms.LoggingRequestMessage;
import com.alliander.osgp.domain.core.exceptions.WebServiceMonitorInterceptorException;

@Transactional(value = "transactionManager")
public class WebServiceMonitorInterceptor implements EndpointInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebServiceMonitorInterceptor.class);

    @Autowired
    @Qualifier("loggingMessageSender")
    private LoggingMessageSender loggingMessageSender;

    private static final String CLASS_NAME = "className";
    private static final String METHOD_NAME = "methodName";
    private static final String CORRELATION_UID = "correlationUid";
    private static final String DEVICE_ID = "deviceId";
    private static final String RESPONSE_RESULT = "result";
    private static final String RESPONSE_DATA_SIZE = "dataSize";

    private static final String XML_ELEMENT_CORRELATION_UID = "CorrelationUid";
    private static final String XML_ELEMENT_DEVICE_ID = "DeviceId";
    private static final String XML_ELEMENT_OSP_RESULT_TYPE = "Result";

    private static final String UTF_8 = "UTF-8";

    private final String organisationIdentification;
    private final String userName;
    private final String applicationName;

    public WebServiceMonitorInterceptor(final String organisationIdentification, final String userName,
            final String applicationName) {
        this.organisationIdentification = organisationIdentification;
        this.userName = userName;
        this.applicationName = applicationName;
    }

    @Override
    public boolean handleRequest(final MessageContext messageContext, final Object endpoint) throws Exception {
        // This method is not used, but is part of the EndpointInterceptor
        // interface.
        return true;
    }

    @Override
    public boolean handleResponse(final MessageContext messageContext, final Object endpoint) throws Exception {
        // Get the current time.
        final Date now = new Date();

        // Get EndPointClass and EndPointMethod.
        final Map<String, String> classAndMethod = this.getEndPointClassAndMethod(endpoint);

        // Get the request.
        Assert.isInstanceOf(SoapMessage.class, messageContext.getRequest());
        final SoapMessage request = (SoapMessage) messageContext.getRequest();
        final SoapHeader soapHeader = request.getSoapHeader();

        // Read OrganisationIdentification from header from request.
        final String organisationIdentification = this.getHeaderValue(soapHeader, this.organisationIdentification);

        // Read UserName from header from request.
        final String userName = this.getHeaderValue(soapHeader, this.userName);

        // Read ApplicationName from header from request.
        final String applicationName = this.getHeaderValue(soapHeader, this.applicationName);

        // Read correlationUid and deviceId from request.
        final Map<String, Object> requestData = this.parseSoapMessage(request);

        if (requestData == null) {
            throw new WebServiceMonitorInterceptorException("unable to get correlationUid or deviceId from request");
        }

        // Get the response.
        Assert.isInstanceOf(SoapMessage.class, messageContext.getResponse());
        final SoapMessage response = (SoapMessage) messageContext.getResponse();

        // Read correlationUid and deviceId and result and data size from
        // response.
        final Map<String, Object> responseData = this.parseSoapMessage(response);

        if (responseData == null) {
            throw new WebServiceMonitorInterceptorException(
                    "unable to get correlationUid or deviceId or result from response");
        }

        // Check response for correlationId, otherwise request
        String correlationId = (String) responseData.get(CORRELATION_UID);
        if (StringUtils.isEmpty(correlationId)) {
            correlationId = (String) requestData.get(CORRELATION_UID);
        }

        // Creating the logging request message
        final LoggingRequestMessage loggingRequestMessage = new LoggingRequestMessage(now, organisationIdentification,
                userName, applicationName, classAndMethod.get(CLASS_NAME), classAndMethod.get(METHOD_NAME),
                (String) requestData.get(DEVICE_ID), correlationId, (String) responseData.get(RESPONSE_RESULT),
                (int) responseData.get(RESPONSE_DATA_SIZE));

        this.loggingMessageSender.send(loggingRequestMessage);

        return true;
    }

    @Override
    public boolean handleFault(final MessageContext messageContext, final Object endpoint) throws Exception {
        // This method is not used, but is part of the EndpointInterceptor
        // interface.
        return false;
    }

    @Override
    public void afterCompletion(final MessageContext messageContext, final Object endpoint, final Exception ex)
            throws Exception {
        // This method is not used, but is part of the EndpointInterceptor
        // interface.
    }

    /**
     * Get the class name and method name from the end point parameter.
     * 
     * @param endpoint
     *            Object representing the end point class.
     * 
     * @return Map containing CLASS_NAME and METHOD_NAME.
     */
    private Map<String, String> getEndPointClassAndMethod(final Object endpoint) {
        final MethodEndpoint method = (MethodEndpoint) endpoint;
        final String className = method.getBean().toString();
        final String methodName = method.getMethod().getName();

        final Map<String, String> classAndMethod = new HashMap<String, String>(2);
        classAndMethod.put(CLASS_NAME, className.split("@")[0]);
        classAndMethod.put(METHOD_NAME, methodName + "()");

        return classAndMethod;
    }

    /**
     * Get a value for a valueName from the soap header.
     * 
     * @param soapHeader
     *            The soap header.
     * 
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
     * Try to find the XML_ELEMENT_CORRELATION_UID, XML_ELEMENT_DEVICE_ID and
     * the XML_ELEMENT_OSP_RESULT_TYPE from the soap message. Note that these
     * elements are not always present in the soap message. In that case, the
     * values will be null. Also, determine the data size of the response.
     * 
     * @param soapMessage
     *            The soap message.
     * 
     * @return Map containing CORRELATION_UID, DEVICE_ID, RESPONSE_RESULT and
     *         RESPONSE_DATA_SIZE.
     */
    private Map<String, Object> parseSoapMessage(final SoapMessage soapMessage) {
        try {
            // Create a stream and write the message to the stream.
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            soapMessage.writeTo(outputStream);
            // Determine the data size of the message (stream).
            final int dataSize = outputStream.size();
            // Get the XML from the message (stream).
            final String xml = new String(outputStream.toByteArray(), UTF_8);

            // Create a document of the XML.
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final Document document = factory.newDocumentBuilder().parse(
                    new InputSource(new ByteArrayInputStream(xml.getBytes(UTF_8))));

            // Try to find the desired XML elements in the document.
            final String correlationUid = this.evaluateXPathExpression(document, XML_ELEMENT_CORRELATION_UID);
            final String deviceId = this.evaluateXPathExpression(document, XML_ELEMENT_DEVICE_ID);
            final String result = this.evaluateXPathExpression(document, XML_ELEMENT_OSP_RESULT_TYPE);

            // Create the Map containing the output.
            final Map<String, Object> map = new HashMap<String, Object>(4);
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
     * @param document
     *            The XML document.
     * @param element
     *            The name of the desired XML element.
     * 
     * @return The content of the XML element, or null if the element is not
     *         found.
     * 
     * @throws XPathExpressionException
     *             In case the expression fails to compile or evaluate, an
     *             exception will be thrown.
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
}
