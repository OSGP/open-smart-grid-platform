/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.endpointinterceptors;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import org.apache.commons.logging.Log;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.util.Assert;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.interceptor.EndpointInterceptorAdapter;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapMessage;

/**
 * Intercepts a SOAP endpoint invocation, examines the SOAP header, and extracts MessageMetadata
 * information from the SOAP header elements.
 *
 * <p>This interceptor puts the collected MessageMetadata as a property in the MessageContext using
 * the configure {@link #contextPropertyName} as the name for this property.
 *
 * <p>See the {@link AnnotationMethodArgumentResolver} for an example how the MessageMetadata can be
 * tied to an annotated endpoint method parameter.
 */
public class SoapHeaderMessageMetadataInterceptor extends EndpointInterceptorAdapter {

  private static final boolean CONTINUE_PROCESSING_INTERCEPTOR_CHAIN = true;

  private static final BiFunction<String, Log, BiConsumer<String, MessageMetadata.Builder>>
      ORGANISATION_IDENTIFICATION_PROCESSOR =
          (localPartOfName, logger) ->
              (value, builder) -> builder.withOrganisationIdentification(value);

  private static final BiFunction<String, Log, BiConsumer<String, MessageMetadata.Builder>>
      MESSAGE_PRIORITY_PROCESSOR =
          (localPartOfName, logger) ->
              (value, builder) -> {
                try {
                  builder.withMessagePriority(Integer.parseInt(value));
                } catch (final NumberFormatException nfe) {
                  logNumberFormatException(localPartOfName, value, logger, nfe);
                }
              };

  private static final BiFunction<String, Log, BiConsumer<String, MessageMetadata.Builder>>
      BYPASS_RETRY_PROCESSOR =
          (localPartOfName, logger) ->
              (value, builder) -> builder.withBypassRetry(Boolean.parseBoolean(value));

  private static final BiFunction<String, Log, BiConsumer<String, MessageMetadata.Builder>>
      SCHEDULE_TIME_PROCESSOR =
          (localPartOfName, logger) ->
              (value, builder) -> {
                try {
                  builder.withScheduleTime(Long.parseLong(value));
                } catch (final NumberFormatException nfe) {
                  logNumberFormatException(localPartOfName, value, logger, nfe);
                }
              };

  private static final BiFunction<String, Log, BiConsumer<String, MessageMetadata.Builder>>
      MAX_SCHEDULE_TIME_PROCESSOR =
          (localPartOfName, logger) ->
              (value, builder) -> {
                try {
                  builder.withMaxScheduleTime(Long.parseLong(value));
                } catch (final NumberFormatException nfe) {
                  logNumberFormatException(localPartOfName, value, logger, nfe);
                }
              };

  private static final BiFunction<String, Log, BiConsumer<String, MessageMetadata.Builder>>
      UNAPPLICABLE_HEADER_ELEMENT_PROCESSOR =
          (localPartOfName, logger) ->
              (value, builder) -> {
                if (logger.isDebugEnabled()) {
                  logger.debug(
                      "Header element not applicable to MessageMetadata: " + localPartOfName);
                }
              };

  private static void logNumberFormatException(
      final String localPartOfName,
      final String value,
      final Log logger,
      final NumberFormatException nfe) {

    logger.error(
        "Value of header element " + localPartOfName + " could not be parsed numerically: " + value,
        nfe);
  }

  private static final Map<
          String, BiFunction<String, Log, BiConsumer<String, MessageMetadata.Builder>>>
      HEADER_ELEMENT_PROCESSORS;

  static {
    final Map<String, BiFunction<String, Log, BiConsumer<String, MessageMetadata.Builder>>>
        processorMap = new HashMap<>();
    processorMap.put("OrganisationIdentification", ORGANISATION_IDENTIFICATION_PROCESSOR);
    processorMap.put("MessagePriority", MESSAGE_PRIORITY_PROCESSOR);
    processorMap.put("BypassRetry", BYPASS_RETRY_PROCESSOR);
    processorMap.put("ScheduleTime", SCHEDULE_TIME_PROCESSOR);
    processorMap.put("MaxScheduleTime", MAX_SCHEDULE_TIME_PROCESSOR);
    HEADER_ELEMENT_PROCESSORS = Collections.unmodifiableMap(processorMap);
  }

  private final String contextPropertyName;

  public SoapHeaderMessageMetadataInterceptor(final String contextPropertyName) {
    this.contextPropertyName = contextPropertyName;
  }

  @Override
  public boolean handleRequest(final MessageContext messageContext, final Object endpoint)
      throws Exception {

    Assert.isInstanceOf(SoapMessage.class, messageContext.getRequest());
    final SoapMessage request = (SoapMessage) messageContext.getRequest();
    final SoapHeader soapHeader = request.getSoapHeader();

    final MessageMetadata.Builder messageMetadataBuilder = MessageMetadata.newBuilder();

    soapHeader
        .examineAllHeaderElements()
        .forEachRemaining(
            soapHeaderElement -> {
              final String localPartOfName = soapHeaderElement.getName().getLocalPart();
              final String text = soapHeaderElement.getText();
              HEADER_ELEMENT_PROCESSORS
                  .getOrDefault(localPartOfName, UNAPPLICABLE_HEADER_ELEMENT_PROCESSOR)
                  .apply(localPartOfName, this.logger)
                  .accept(text, messageMetadataBuilder);
            });

    messageContext.setProperty(this.contextPropertyName, messageMetadataBuilder.build());

    return CONTINUE_PROCESSING_INTERCEPTOR_CHAIN;
  }
}
