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
import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
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
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetPowerQualityProfileRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodicMeterReadsQuery;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
@Slf4j
class MonitoringServiceTest {

  public static final String TEST_1024000000001 = "TEST1024000000001";

  public static final String NETWORK_ADDRESS = "127.0.0.1";

  private static final String DEVICE_MODEL_CODE = "BK-G4 ETB WR";

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
  void requestPeriodicMeterReads(final String methodName, final Class clazz)
      throws FunctionalException,
          InvocationTargetException,
          NoSuchMethodException,
          IllegalAccessException {

    final MessageMetadata messageMetadata = mock(MessageMetadata.class);
    final SmartMeter smartMeter = mockSmartMeter("base code", null);
    final SmartMeter meterOnCh1 = mockSmartMeter("channel 1 code", (short) 1);
    final SmartMeter meterOnCh2 = mockSmartMeter("channel 2 code", (short) 2);
    final SmartMeter meterOnCh3 = mockSmartMeter("channel 3 code", (short) 3);
    final SmartMeter meterOnCh4 = mockSmartMeter("channel 4 code", (short) 4);

    when(messageMetadata.builder()).thenReturn(MessageMetadata.newBuilder());
    when(messageMetadata.getDeviceIdentification()).thenReturn(TEST_1024000000001);
    when(messageMetadata.getOrganisationIdentification()).thenReturn("test-org");
    when(this.domainHelperService.findSmartMeter(TEST_1024000000001)).thenReturn(smartMeter);
    when(this.domainHelperService.searchMBusDevicesFor(smartMeter))
        .thenReturn(List.of(smartMeter, meterOnCh1, meterOnCh2, meterOnCh3, meterOnCh4));

    this.invokeMethodByName(methodName, clazz, messageMetadata);

    verify(this.osgpCoreRequestMessageSender)
        .send(Mockito.any(), this.messageMetadataCaptor.capture());

    final MessageMetadata metadata = this.messageMetadataCaptor.getValue();
    final String expectedDeviceModelCode =
        String.format(
            "%s,%s,%s,%s,%s",
            getModelCode(smartMeter),
            getModelCode(meterOnCh1),
            getModelCode(meterOnCh2),
            getModelCode(meterOnCh3),
            getModelCode(meterOnCh4));

    assertNotNull(metadata);
    assertEquals(NETWORK_ADDRESS, metadata.getNetworkAddress());
    assertEquals(BTS_580, metadata.getBaseTransceiverStationId());
    assertEquals(CELL_ID_1, metadata.getCellId());
    assertEquals(expectedDeviceModelCode, metadata.getDeviceModelCode());
  }

  private static String getModelCode(final SmartMeter smartMeter) {
    return smartMeter.getDeviceModel().getModelCode();
  }

  private static Stream<Arguments> methodNames() {
    return Stream.of(
        Arguments.of("requestPeriodicMeterReads", PeriodicMeterReadsQuery.class),
        Arguments.of("requestActualMeterReads", ActualMeterReadsQuery.class),
        Arguments.of("requestActualPowerQuality", ActualPowerQualityRequest.class),
        Arguments.of("requestPeriodicMeterReads", PeriodicMeterReadsQuery.class),
        Arguments.of("requestPowerQualityProfile", GetPowerQualityProfileRequest.class));
  }

  private <T> void invokeMethodByName(
      final String methodName, final Class<T> clazz, final MessageMetadata messageMetadata)
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    final Method method =
        MonitoringService.class.getMethod(methodName, MessageMetadata.class, clazz);
    method.invoke(this.monitoringService, messageMetadata, mock(clazz));
  }

  private void verifyMessageMetadata() {}

  private static SmartMeter mockSmartMeter(final String deviceModelCode, final Short channel) {
    final DeviceModel deviceModel = mock(DeviceModel.class);

    final SmartMeter smartMeter = mock(SmartMeter.class);

    if (channel == null) {
      when(smartMeter.getNetworkAddress()).thenReturn(NETWORK_ADDRESS);
      when(smartMeter.getBtsId()).thenReturn(BTS_580);
      when(smartMeter.getCellId()).thenReturn(CELL_ID_1);
    } else {
      when(smartMeter.getChannel()).thenReturn(channel);
    }

    when(smartMeter.getDeviceModel()).thenReturn(deviceModel);
    when(deviceModel.getModelCode()).thenReturn(deviceModelCode);

    return smartMeter;
  }
}
