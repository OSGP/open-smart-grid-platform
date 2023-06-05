// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.core.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.domain.core.application.mapping.DomainCoreMapper;
import org.opensmartgridplatform.adapter.domain.core.infra.jms.core.OsgpCoreRequestMessageSender;
import org.opensmartgridplatform.adapter.domain.core.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.LightMeasurementDevice;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.exceptions.ValidationException;
import org.opensmartgridplatform.domain.core.repositories.SsldRepository;
import org.opensmartgridplatform.domain.core.services.DeviceDomainService;
import org.opensmartgridplatform.domain.core.services.OrganisationDomainService;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceStatus;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceStatusMapped;
import org.opensmartgridplatform.domain.core.valueobjects.LightType;
import org.opensmartgridplatform.domain.core.valueobjects.LightValue;
import org.opensmartgridplatform.domain.core.valueobjects.LinkType;
import org.opensmartgridplatform.domain.core.valueobjects.TariffValue;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.NoDeviceResponseException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;

@ExtendWith(MockitoExtension.class)
public class DeviceInstallationServiceTest {

  private static final String TEST_IP = "testIp";
  private static final String TEST_DEVICE = "testDevice";
  private static final String TEST_UID = "testUid";
  private static final String TEST_ORGANISATION = "testOrganisation";
  private static final String TEST_MESSAGE_TYPE = MessageType.GET_STATUS.name();

  private static final int MESSAGE_PRIORITY = 1;

  public static final RequestMessage REQUEST_MESSAGE =
      new RequestMessage(TEST_UID, TEST_ORGANISATION, TEST_DEVICE, null);
  private static final CorrelationIds CORRELATION_IDS =
      new CorrelationIds(TEST_ORGANISATION, TEST_DEVICE, TEST_UID);

  @Mock private OrganisationDomainService organisationDomainService;

  @Mock private DeviceDomainService deviceDomainService;

  @Mock private OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;

  @Mock private WebServiceResponseMessageSender webServiceResponseMessageSender;

  @Mock private DomainCoreMapper domainCoreMapper;

  @Mock private SsldRepository ssldRepository;

  @InjectMocks private DeviceInstallationService deviceInstallationService;

  @Captor private ArgumentCaptor<ResponseMessage> argumentResponseMessage;

  @Captor private ArgumentCaptor<RequestMessage> argumentRequestMessage;

  @Captor private ArgumentCaptor<String> argumentMessageType;

  @Captor private ArgumentCaptor<String> argumentIpAddress;

  @Captor private ArgumentCaptor<Integer> argumentPriority;

  @Test
  public void testGetStatusTestDeviceTypeIsNotLmdType() throws FunctionalException {
    final Device device = Mockito.mock(Device.class);
    when(device.getIpAddress()).thenReturn(TEST_IP);
    when(this.deviceDomainService.searchActiveDevice(TEST_DEVICE, ComponentType.DOMAIN_CORE))
        .thenReturn(device);

    this.deviceInstallationService.getStatus(
        TEST_ORGANISATION, TEST_DEVICE, TEST_UID, TEST_MESSAGE_TYPE, MESSAGE_PRIORITY);

    verify(this.osgpCoreRequestMessageSender)
        .send(
            this.argumentRequestMessage.capture(),
            this.argumentMessageType.capture(),
            this.argumentPriority.capture(),
            this.argumentIpAddress.capture());

    assertThat(this.argumentRequestMessage.getValue())
        .usingRecursiveComparison()
        .isEqualTo(REQUEST_MESSAGE);
    assertThat(this.argumentMessageType.getValue()).isEqualTo(TEST_MESSAGE_TYPE);
    assertThat(this.argumentPriority.getValue()).isEqualTo(MESSAGE_PRIORITY);
    assertThat(this.argumentIpAddress.getValue()).isEqualTo(TEST_IP);
  }

  @Test
  public void testGetStatusTestDeviceTypeIsLmdType() throws FunctionalException {
    final Device device = Mockito.mock(Device.class);
    when(device.getDeviceType()).thenReturn("LMD");
    when(device.getIpAddress()).thenReturn(TEST_IP);
    when(this.deviceDomainService.searchActiveDevice(TEST_DEVICE, ComponentType.DOMAIN_CORE))
        .thenReturn(device);

    this.deviceInstallationService.getStatus(
        TEST_ORGANISATION, TEST_DEVICE, TEST_UID, TEST_MESSAGE_TYPE, MESSAGE_PRIORITY);

    verify(this.osgpCoreRequestMessageSender)
        .send(
            this.argumentRequestMessage.capture(),
            this.argumentMessageType.capture(),
            this.argumentPriority.capture(),
            this.argumentIpAddress.capture());

    assertThat(this.argumentRequestMessage.getValue())
        .usingRecursiveComparison()
        .isEqualTo(REQUEST_MESSAGE);
    assertThat(this.argumentMessageType.getValue()).isEqualTo("GET_LIGHT_SENSOR_STATUS");
    assertThat(this.argumentPriority.getValue()).isEqualTo(MESSAGE_PRIORITY);
    assertThat(this.argumentIpAddress.getValue()).isEqualTo(TEST_IP);
  }

  @Test
  public void testHandleGetStatusResponseNotOk() {
    this.deviceInstallationService.handleGetStatusResponse(
        null,
        CORRELATION_IDS,
        TEST_MESSAGE_TYPE,
        MESSAGE_PRIORITY,
        ResponseMessageResultType.NOT_OK,
        null);

    verify(this.webServiceResponseMessageSender).send(this.argumentResponseMessage.capture());
    assertThat(this.argumentResponseMessage.getValue())
        .usingRecursiveComparison()
        .isEqualTo(this.createNewResponseMessage(ResponseMessageResultType.NOT_OK, null, null));
  }

  @Test
  public void testHandleGetStatusResponseOkLmdStatusNotNull()
      throws FunctionalException, ValidationException {
    final TariffValue editedTariffValue = new TariffValue();
    editedTariffValue.setHigh(true);
    editedTariffValue.setIndex(10);
    final DeviceStatusMapped deviceStatus =
        new DeviceStatusMapped(
            null,
            Arrays.asList(
                new LightValue(0, true, 50),
                new LightValue(MESSAGE_PRIORITY, true, 75),
                new LightValue(2, false, 0)),
            LinkType.ETHERNET,
            LinkType.GPRS,
            LightType.ONE_TO_TEN_VOLT,
            0);
    when(this.domainCoreMapper.map(null, DeviceStatus.class)).thenReturn(deviceStatus);

    final Device mockedDevice = Mockito.mock(Device.class);
    when(mockedDevice.getDeviceType()).thenReturn(LightMeasurementDevice.LMD_TYPE);
    when(this.deviceDomainService.searchDevice(TEST_DEVICE)).thenReturn(mockedDevice);

    this.deviceInstallationService.handleGetStatusResponse(
        null,
        CORRELATION_IDS,
        TEST_MESSAGE_TYPE,
        MESSAGE_PRIORITY,
        ResponseMessageResultType.OK,
        null);

    verify(this.webServiceResponseMessageSender).send(this.argumentResponseMessage.capture());
    assertThat(this.argumentResponseMessage.getValue())
        .usingRecursiveComparison()
        .isEqualTo(this.createNewResponseMessage(ResponseMessageResultType.OK, null, deviceStatus));
  }

  @Test
  public void testHandleGetStatusResponseOkLmdStatusNull() throws FunctionalException {
    when(this.domainCoreMapper.map(null, DeviceStatus.class)).thenReturn(null);

    final Device mockedDevice = Mockito.mock(Device.class);
    when(mockedDevice.getDeviceType()).thenReturn(LightMeasurementDevice.LMD_TYPE);
    when(this.deviceDomainService.searchDevice(TEST_DEVICE)).thenReturn(mockedDevice);

    this.deviceInstallationService.handleGetStatusResponse(
        null,
        CORRELATION_IDS,
        TEST_MESSAGE_TYPE,
        MESSAGE_PRIORITY,
        ResponseMessageResultType.OK,
        null);

    verify(this.webServiceResponseMessageSender).send(this.argumentResponseMessage.capture());
    assertThat(this.argumentResponseMessage.getValue())
        .usingRecursiveComparison()
        .isEqualTo(
            this.createNewResponseMessage(
                ResponseMessageResultType.NOT_OK,
                new TechnicalException(
                    ComponentType.DOMAIN_CORE,
                    "Light measurement device was not able to report light sensor status",
                    new NoDeviceResponseException()),
                null));
  }

  @Test
  public void testHandleGetStatusResponseOkNotLmdStatusNotNull() throws FunctionalException {
    final DeviceStatus mockedStatus = Mockito.mock(DeviceStatus.class);
    when(mockedStatus.getLightValues()).thenReturn(Collections.emptyList());
    when(this.domainCoreMapper.map(null, DeviceStatus.class)).thenReturn(mockedStatus);

    final Device mockedDevice = Mockito.mock(Device.class);
    when(mockedDevice.getDeviceType()).thenReturn(null);
    when(this.deviceDomainService.searchDevice(TEST_DEVICE)).thenReturn(mockedDevice);

    when(this.ssldRepository.findByDeviceIdentification(TEST_DEVICE)).thenReturn(new Ssld());

    this.deviceInstallationService.handleGetStatusResponse(
        null,
        CORRELATION_IDS,
        TEST_MESSAGE_TYPE,
        MESSAGE_PRIORITY,
        ResponseMessageResultType.OK,
        null);

    verify(this.webServiceResponseMessageSender).send(this.argumentResponseMessage.capture());
    assertThat(this.argumentResponseMessage.getValue())
        .usingRecursiveComparison()
        .ignoringFields("dataObject")
        .isEqualTo(this.createNewResponseMessage(ResponseMessageResultType.OK, null, null));
  }

  @Test
  public void testHandleGetStatusResponseOkNotLMDStatusNull() throws FunctionalException {
    when(this.domainCoreMapper.map(null, DeviceStatus.class)).thenReturn(null);

    final Device mockedDevice = Mockito.mock(Device.class);
    when(mockedDevice.getDeviceType()).thenReturn(null);
    when(this.deviceDomainService.searchDevice(TEST_DEVICE)).thenReturn(mockedDevice);

    when(this.ssldRepository.findByDeviceIdentification(TEST_DEVICE)).thenReturn(new Ssld());

    this.deviceInstallationService.handleGetStatusResponse(
        null,
        CORRELATION_IDS,
        TEST_MESSAGE_TYPE,
        MESSAGE_PRIORITY,
        ResponseMessageResultType.OK,
        null);

    verify(this.webServiceResponseMessageSender).send(this.argumentResponseMessage.capture());
    assertThat(this.argumentResponseMessage.getValue())
        .usingRecursiveComparison()
        .ignoringFields("dataObject")
        .isEqualTo(
            this.createNewResponseMessage(
                ResponseMessageResultType.NOT_OK,
                new TechnicalException(
                    ComponentType.DOMAIN_CORE,
                    "SSLD was not able to report relay status",
                    new NoDeviceResponseException()),
                null));
  }

  @Test
  public void testStartSelfTest() throws FunctionalException {
    final Device device = Mockito.mock(Device.class);
    when(device.getIpAddress()).thenReturn(TEST_IP);
    when(this.deviceDomainService.searchActiveDevice(TEST_DEVICE, ComponentType.DOMAIN_CORE))
        .thenReturn(device);

    this.deviceInstallationService.startSelfTest(
        TEST_DEVICE, TEST_ORGANISATION, TEST_UID, TEST_MESSAGE_TYPE, MESSAGE_PRIORITY);

    verify(this.osgpCoreRequestMessageSender)
        .send(
            this.argumentRequestMessage.capture(),
            this.argumentMessageType.capture(),
            this.argumentPriority.capture(),
            this.argumentIpAddress.capture());

    assertThat(this.argumentRequestMessage.getValue())
        .usingRecursiveComparison()
        .isEqualTo(REQUEST_MESSAGE);
    assertThat(this.argumentMessageType.getValue()).isEqualTo(TEST_MESSAGE_TYPE);
    assertThat(this.argumentPriority.getValue()).isEqualTo(MESSAGE_PRIORITY);
    assertThat(this.argumentIpAddress.getValue()).isEqualTo(TEST_IP);
  }

  @Test
  public void testStopSelfTest() throws FunctionalException {
    final Device device = Mockito.mock(Device.class);
    when(device.getIpAddress()).thenReturn(TEST_IP);
    when(this.deviceDomainService.searchActiveDevice(TEST_DEVICE, ComponentType.DOMAIN_CORE))
        .thenReturn(device);

    this.deviceInstallationService.stopSelfTest(
        TEST_DEVICE, TEST_ORGANISATION, TEST_UID, TEST_MESSAGE_TYPE, MESSAGE_PRIORITY);

    verify(this.osgpCoreRequestMessageSender)
        .send(
            this.argumentRequestMessage.capture(),
            this.argumentMessageType.capture(),
            this.argumentPriority.capture(),
            this.argumentIpAddress.capture());

    assertThat(this.argumentRequestMessage.getValue())
        .usingRecursiveComparison()
        .isEqualTo(REQUEST_MESSAGE);
    assertThat(this.argumentMessageType.getValue()).isEqualTo(TEST_MESSAGE_TYPE);
    assertThat(this.argumentPriority.getValue()).isEqualTo(MESSAGE_PRIORITY);
    assertThat(this.argumentIpAddress.getValue()).isEqualTo(TEST_IP);
  }

  private ResponseMessage createNewResponseMessage(
      final ResponseMessageResultType resultType,
      final OsgpException exception,
      final Serializable dataObject) {
    return ResponseMessage.newResponseMessageBuilder()
        .withIds(CORRELATION_IDS)
        .withResult(resultType)
        .withOsgpException(exception)
        .withDataObject(dataObject)
        .withMessagePriority(MESSAGE_PRIORITY)
        .withMessageType(TEST_MESSAGE_TYPE)
        .build();
  }
}
