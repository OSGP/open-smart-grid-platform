// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.da.application.routing;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.domain.da.infra.jms.kafka.KafkaResponseMessageSender;
import org.opensmartgridplatform.adapter.domain.da.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.services.DeviceDomainService;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.IntegrationType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;

@ExtendWith(MockitoExtension.class)
class ResponseMessageRouterTest {

  @Mock private DeviceDomainService deviceDomainService;

  @Mock private WebServiceResponseMessageSender webServiceResponseMessageSender;

  @Mock private KafkaResponseMessageSender kafkaResponseMessageSender;

  @InjectMocks private ResponseMessageRouter responseMessageRouter;

  private static final String MESSAGE_TYPE = DeviceFunction.GET_DATA.toString();
  private static final String DEVICE_ID = "TST-01";
  private static final ResponseMessage MESSAGE =
      ResponseMessage.newResponseMessageBuilder().withDeviceIdentification(DEVICE_ID).build();

  @Test
  void testSendDefault() {

    // Act
    this.responseMessageRouter.send(MESSAGE, MESSAGE_TYPE);

    // Assert
    verify(this.webServiceResponseMessageSender).send(any(ResponseMessage.class), anyString());
  }

  @Test
  void testSendWebservice() throws FunctionalException {

    // Arrange
    final Device device = this.createDevice(IntegrationType.WEB_SERVICE);
    when(this.deviceDomainService.searchDevice(anyString())).thenReturn(device);

    // Act
    this.responseMessageRouter.send(MESSAGE, MESSAGE_TYPE);

    // Assert
    verify(this.webServiceResponseMessageSender).send(any(ResponseMessage.class), anyString());
  }

  @Test
  void testSendKafka() throws FunctionalException {

    // Arrange
    final Device device = this.createDevice(IntegrationType.KAFKA);
    when(this.deviceDomainService.searchDevice(anyString())).thenReturn(device);

    // Act
    this.responseMessageRouter.send(MESSAGE, MESSAGE_TYPE);

    // Assert
    verify(this.kafkaResponseMessageSender).send(any(ResponseMessage.class), anyString());
  }

  @Test
  void testSendBoth() throws FunctionalException {

    // Arrange
    final Device device = this.createDevice(IntegrationType.BOTH);
    when(this.deviceDomainService.searchDevice(anyString())).thenReturn(device);

    // Act
    this.responseMessageRouter.send(MESSAGE, MESSAGE_TYPE);

    // Assert
    verify(this.webServiceResponseMessageSender).send(any(ResponseMessage.class), anyString());
    verify(this.kafkaResponseMessageSender).send(any(ResponseMessage.class), anyString());
  }

  private Device createDevice(final IntegrationType integrationType) {
    final Device device = new Device(DEVICE_ID);
    device.setIntegrationType(integrationType);
    return device;
  }
}
