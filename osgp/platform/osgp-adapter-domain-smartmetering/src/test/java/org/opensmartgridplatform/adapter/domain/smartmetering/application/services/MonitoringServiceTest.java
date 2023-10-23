// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.MonitoringMapper;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.JmsMessageSender;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualMeterReadsQuery;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualPowerQualityRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ClearAlarmRegisterRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetPowerQualityProfileRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodicMeterReadsQuery;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ReadAlarmRegisterRequest;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
@Slf4j
class MonitoringServiceTest {

  public static final String TEST_1024000000001 = "TEST1024000000001";

  public static final String NETWORK_ADDRESS = "127.0.0.1";

  public static final int BTS_580 = 580;

  public static final int CELL_ID_1 = 1;

  @Mock private DomainHelperService domainHelperService;

  @Mock private MonitoringMapper monitoringMapper;

  @Mock private JmsMessageSender osgpCoreRequestMessageSender;

  @InjectMocks private MonitoringService monitoringService;

  private final ArgumentCaptor<MessageMetadata> messageMetadataCaptor =
      ArgumentCaptor.forClass(MessageMetadata.class);

  @ParameterizedTest
  @MethodSource("methodNames")
  void requestPeriodicMeterReads(final String name, final Class clazz) throws FunctionalException {

    this.executeMethodAndValidate(name, clazz, Boolean.TRUE);
  }

  private static Stream<Arguments> methodNames() {
    return Stream.of(
        Arguments.of("requestPeriodicMeterReads", PeriodicMeterReadsQuery.class),
        Arguments.of("requestActualMeterReads", ActualMeterReadsQuery.class),
        Arguments.of("requestActualPowerQuality", ActualPowerQualityRequest.class),
        Arguments.of("requestClearAlarmRegister", ClearAlarmRegisterRequest.class),
        Arguments.of("requestPeriodicMeterReads", PeriodicMeterReadsQuery.class),
        Arguments.of("requestPowerQualityProfile", GetPowerQualityProfileRequest.class),
        Arguments.of("requestReadAlarmRegister", ReadAlarmRegisterRequest.class));
  }

  private <T> void executeMethodAndValidate(
      final String methodName, final Class<T> clazz, final boolean withDeviceModel)
      throws FunctionalException {

    try {
      final MessageMetadata messageMetadata = mock(MessageMetadata.class);
      final SmartMeter smartMeter = mockSmartMeter(messageMetadata, withDeviceModel);

      when(this.domainHelperService.findSmartMeter(TEST_1024000000001)).thenReturn(smartMeter);

      final Method method =
          MonitoringService.class.getMethod(methodName, MessageMetadata.class, clazz);

      method.invoke(this.monitoringService, messageMetadata, mock(clazz));

      this.verifyMessageMetadata(withDeviceModel);
    } catch (final InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
      final String message = "No Exception expected";
      log.error(message, e);
      Assertions.fail(message);
    }
  }

  private void verifyMessageMetadata(final boolean withDeviceModelCode) {
    verify(this.osgpCoreRequestMessageSender)
        .send(Mockito.any(), this.messageMetadataCaptor.capture());

    final MessageMetadata metadata = this.messageMetadataCaptor.getValue();
    assertNotNull(metadata);
    assertEquals(NETWORK_ADDRESS, metadata.getNetworkAddress());
    assertEquals(BTS_580, metadata.getBaseTransceiverStationId());
    assertEquals(CELL_ID_1, metadata.getCellId());
    if (withDeviceModelCode) {
      assertEquals(metadata.getDeviceModelCode(), DEVICE_MODEL_CODE());
    }
  }

  private static SmartMeter mockSmartMeter(
      final MessageMetadata messageMetadata, final boolean withDeviceModel) {
    final DeviceModel deviceModel = mock(DeviceModel.class);

    final SmartMeter smartMeter = mock(SmartMeter.class);
    when(smartMeter.getNetworkAddress()).thenReturn(NETWORK_ADDRESS);
    when(smartMeter.getBtsId()).thenReturn(BTS_580);
    when(smartMeter.getCellId()).thenReturn(CELL_ID_1);
    if (withDeviceModel) {
      when(smartMeter.getDeviceModel()).thenReturn(deviceModel);
      when(deviceModel.getModelCode()).thenReturn(DEVICE_MODEL_CODE());
    }

    when(messageMetadata.builder()).thenReturn(MessageMetadata.newBuilder());
    when(messageMetadata.getDeviceIdentification()).thenReturn(TEST_1024000000001);
    when(messageMetadata.getOrganisationIdentification()).thenReturn("test-org");
    return smartMeter;
  }

  private static String DEVICE_MODEL_CODE() {
    return "BK-G4 ETB WR";
  }
}
