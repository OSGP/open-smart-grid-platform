//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.core.infra.jms.protocol.inbound.messageprocessors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.stubbing.Answer;
import org.opensmartgridplatform.core.application.services.EventNotificationMessageService;
import org.opensmartgridplatform.core.domain.model.domain.DomainRequestService;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceAuthorization;
import org.opensmartgridplatform.domain.core.entities.DomainInfo;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.repositories.DeviceAuthorizationRepository;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.DomainInfoRepository;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunctionGroup;
import org.opensmartgridplatform.domain.core.valueobjects.EventType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushNotificationAlarmDto;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ObjectMessageBuilder;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PushNotificationAlarmMessageProcessorTest {

  private static final String DEVICE_IDENTIFICATION = "dvc-1";

  @Mock private PushNotificationAlarmDto pushNotificationAlarm;

  @Mock private DeviceRepository deviceRepository;

  @Mock private EventNotificationMessageService eventNotificationMessageService;

  @Mock private DeviceAuthorizationRepository deviceAuthorizationRepository;

  @Mock private DeviceAuthorization deviceAuthorization;

  @Mock private Organisation organisation;

  @Mock private DomainInfoRepository domainInfoRepository;

  @Mock private DomainInfo domainInfo;

  @Mock private DomainRequestService domainRequestService;

  @InjectMocks private PushNotificationAlarmMessageProcessor pushNotificationAlarmMessageProcessor;
  private ObjectMessage message;
  private Device device;

  @BeforeEach
  public void init() throws JMSException, UnknownEntityException {

    final String correlationUid = "corr-uid-1";
    final String organisationIdentification = "test-org";
    final String ipAddress = "127.0.0.1";

    final RequestMessage requestMessage =
        new RequestMessage(
            correlationUid,
            organisationIdentification,
            DEVICE_IDENTIFICATION,
            ipAddress,
            null,
            null,
            this.pushNotificationAlarm);

    this.message =
        new ObjectMessageBuilder()
            .withCorrelationUid(correlationUid)
            .withMessageType(MessageType.PUSH_NOTIFICATION_ALARM.name())
            .withDeviceIdentification(DEVICE_IDENTIFICATION)
            .withObject(requestMessage)
            .build();

    this.device = new Device(DEVICE_IDENTIFICATION);

    when(this.deviceRepository.findByDeviceIdentification(DEVICE_IDENTIFICATION))
        .thenReturn(this.device);
    when(this.deviceRepository.save(this.device))
        .thenAnswer((Answer<Void>) invocationOnMock -> null);
    doNothing()
        .when(this.eventNotificationMessageService)
        .handleEvent(
            any(String.class),
            any(Date.class),
            any(EventType.class),
            any(String.class),
            any(Integer.class));
    when(this.deviceAuthorizationRepository.findByDeviceAndFunctionGroup(
            this.device, DeviceFunctionGroup.OWNER))
        .thenReturn(Collections.singletonList(this.deviceAuthorization));
    when(this.deviceAuthorization.getOrganisation()).thenReturn(this.organisation);
    when(this.organisation.getOrganisationIdentification())
        .thenReturn(requestMessage.getOrganisationIdentification());
    when(this.domainInfoRepository.findAll())
        .thenReturn(Collections.singletonList(this.domainInfo));
    when(this.domainInfo.getDomain()).thenReturn("SMART_METERING");
    when(this.domainInfo.getDomainVersion()).thenReturn("1.0");
    doNothing()
        .when(this.domainRequestService)
        .send(any(RequestMessage.class), any(String.class), any(DomainInfo.class));
  }

  @Test
  public void testProcessMessageSuccess() throws JMSException {

    assertThat(this.device.getLastSuccessfulConnectionTimestamp()).isNull();

    this.pushNotificationAlarmMessageProcessor.processMessage(this.message);

    assertThat(this.device.getLastSuccessfulConnectionTimestamp()).isNotNull();

    verify(this.deviceRepository).save(this.device);
  }

  @Test
  public void testUnknownDevice() {
    when(this.deviceRepository.findByDeviceIdentification(DEVICE_IDENTIFICATION)).thenReturn(null);
    assertThatExceptionOfType(JMSException.class)
        .isThrownBy(
            () -> {
              this.pushNotificationAlarmMessageProcessor.processMessage(this.message);
            });
  }

  @Test
  public void testUnknownDeviceAuthorization() {
    when(this.deviceAuthorizationRepository.findByDeviceAndFunctionGroup(
            this.device, DeviceFunctionGroup.OWNER))
        .thenReturn(null);
    assertThatExceptionOfType(JMSException.class)
        .isThrownBy(
            () -> {
              this.pushNotificationAlarmMessageProcessor.processMessage(this.message);
            });
  }
}
