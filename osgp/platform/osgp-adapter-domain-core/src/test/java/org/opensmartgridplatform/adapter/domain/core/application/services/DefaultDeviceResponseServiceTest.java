// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.core.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opensmartgridplatform.adapter.domain.core.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;

public class DefaultDeviceResponseServiceTest {

  @Mock private WebServiceResponseMessageSender webServiceResponseMessageSender;

  @InjectMocks private DefaultDeviceResponseService defaultDeviceResponseService;

  private static final CorrelationIds IDS =
      new CorrelationIds("orginazationTestId", "deviceIdTest", "correlationUid");
  private static final String MESSAGE_TYPE = "Warning";
  private static final int MESSAGE_PRIORITY = 3;

  @BeforeEach
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testDefaultDeviceResponseWithNotOkTypeAndException() {

    // Arrange
    final ResponseMessageResultType deviceResult = ResponseMessageResultType.NOT_OK;
    final OsgpException exception =
        new OsgpException(ComponentType.DOMAIN_CORE, "There was an exception");

    final ResponseMessage expectedResponseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withIds(IDS)
            .withResult(ResponseMessageResultType.NOT_OK)
            .withOsgpException(exception)
            .withMessagePriority(MESSAGE_PRIORITY)
            .withMessageType(MESSAGE_TYPE)
            .build();

    // Act
    this.defaultDeviceResponseService.handleDefaultDeviceResponse(
        IDS, MESSAGE_TYPE, MESSAGE_PRIORITY, deviceResult, exception);

    final ArgumentCaptor<ResponseMessage> argument = ArgumentCaptor.forClass(ResponseMessage.class);
    verify(this.webServiceResponseMessageSender).send(argument.capture());

    // Assert
    assertThat(argument.getValue()).usingRecursiveComparison().isEqualTo(expectedResponseMessage);
  }

  @Test
  public void testDefaultDeviceResponseWithNotOkTypeAndNoException() {

    // Arrange
    final ResponseMessageResultType deviceResult = ResponseMessageResultType.NOT_OK;
    final OsgpException exception = null;
    final OsgpException osgpException =
        new TechnicalException(ComponentType.DOMAIN_CORE, "An unknown error occurred");

    final ResponseMessage expectedResponseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withIds(IDS)
            .withResult(ResponseMessageResultType.NOT_OK)
            .withOsgpException(osgpException)
            .withMessagePriority(MESSAGE_PRIORITY)
            .withMessageType(MESSAGE_TYPE)
            .build();

    this.defaultDeviceResponseService.handleDefaultDeviceResponse(
        IDS, MESSAGE_TYPE, MESSAGE_PRIORITY, deviceResult, exception);

    // Act
    final ArgumentCaptor<ResponseMessage> argument = ArgumentCaptor.forClass(ResponseMessage.class);
    verify(this.webServiceResponseMessageSender).send(argument.capture());

    // Assert
    assertThat(argument.getValue()).usingRecursiveComparison().isEqualTo(expectedResponseMessage);
  }

  @Test
  public void testDefaultDeviceResponseWithOkTypeAndException() {

    // Arrange
    final ResponseMessageResultType deviceResult = ResponseMessageResultType.OK;
    final OsgpException exception =
        new OsgpException(ComponentType.DOMAIN_CORE, "There was an exception");

    final ResponseMessage expectedResponseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withIds(IDS)
            .withResult(ResponseMessageResultType.NOT_OK)
            .withOsgpException(exception)
            .withMessagePriority(MESSAGE_PRIORITY)
            .withMessageType(MESSAGE_TYPE)
            .build();

    // Act
    this.defaultDeviceResponseService.handleDefaultDeviceResponse(
        IDS, MESSAGE_TYPE, MESSAGE_PRIORITY, deviceResult, exception);

    final ArgumentCaptor<ResponseMessage> argument = ArgumentCaptor.forClass(ResponseMessage.class);
    verify(this.webServiceResponseMessageSender).send(argument.capture());

    // Assert
    assertThat(argument.getValue()).usingRecursiveComparison().isEqualTo(expectedResponseMessage);
  }

  @Test
  public void testDefaultDeviceResponseWithOkTypeAndNoException() {

    // Arrange
    final ResponseMessageResultType deviceResult = ResponseMessageResultType.OK;
    final OsgpException exception = null;

    final ResponseMessage expectedResponseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withIds(IDS)
            .withResult(ResponseMessageResultType.OK)
            .withOsgpException(exception)
            .withMessagePriority(MESSAGE_PRIORITY)
            .withMessageType(MESSAGE_TYPE)
            .build();

    // Act
    this.defaultDeviceResponseService.handleDefaultDeviceResponse(
        IDS, MESSAGE_TYPE, MESSAGE_PRIORITY, deviceResult, exception);

    final ArgumentCaptor<ResponseMessage> argument = ArgumentCaptor.forClass(ResponseMessage.class);
    verify(this.webServiceResponseMessageSender).send(argument.capture());

    // Assert
    assertThat(argument.getValue()).usingRecursiveComparison().isEqualTo(expectedResponseMessage);
  }
}
