// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.JmsMessageSender;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.repositories.DeviceAuthorizationRepository;
import org.opensmartgridplatform.domain.core.repositories.DeviceModelRepository;
import org.opensmartgridplatform.domain.core.repositories.ManufacturerRepository;
import org.opensmartgridplatform.domain.core.repositories.OrganisationRepository;
import org.opensmartgridplatform.domain.core.repositories.ProtocolInfoRepository;
import org.opensmartgridplatform.domain.core.repositories.SmartMeterRepository;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceModel;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AddSmartMeterRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SmartMeteringDevice;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SmartMeteringDeviceDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class InstallationServiceTest {

  private static final String DEVICE_IDENTIFICATION = "test-device-identification";
  private static final String PROTOCOL_NAME = "test-protocol-name";
  private static final String PROTOCOL_VERSION = "test-protocol-version";
  private static final String PROTOCOL_VARIANT = "test-protocol-variant";

  @Spy @InjectMocks private SmartMeterService smartMeterService;
  @InjectMocks private InstallationService instance;

  @Mock private SmartMeterRepository smartMeteringDeviceRepository;
  @Mock private MapperFactory mapperFactory;
  @Mock private MapperFacade mapperFacade;
  @Mock private ProtocolInfoRepository protocolInfoRepository;
  @Mock private ManufacturerRepository manufacturerRepository;
  @Mock private DeviceModelRepository deviceModelRepository;
  @Mock private OrganisationRepository organisationRepository;
  @Mock private DeviceAuthorizationRepository deviceAuthorizationRepository;
  @Mock private JmsMessageSender osgpCoreRequestMessageSender;

  @Mock private MessageMetadata deviceMessageMetadata;
  @Mock private AddSmartMeterRequest addSmartMeterRequest;
  @Mock private SmartMeteringDevice smartMeteringDevice;
  @Mock private SmartMeter smartMeter;
  @Mock private ProtocolInfo protocolInfo;
  @Mock private DeviceModel deviceModel;

  @BeforeEach
  public void setUp() {
    when(this.addSmartMeterRequest.getDevice()).thenReturn(this.smartMeteringDevice);
    when(this.deviceMessageMetadata.getDeviceIdentification()).thenReturn(DEVICE_IDENTIFICATION);
    when(this.addSmartMeterRequest.getDeviceModel()).thenReturn(this.deviceModel);
    when(this.addSmartMeterRequest.getDevice().getProtocolName()).thenReturn(PROTOCOL_NAME);
    when(this.addSmartMeterRequest.getDevice().getProtocolVersion()).thenReturn(PROTOCOL_VERSION);
    when(this.addSmartMeterRequest.getDevice().getProtocolVariant()).thenReturn(PROTOCOL_VARIANT);
    when(this.mapperFactory.getMapperFacade()).thenReturn(this.mapperFacade);
    when(this.mapperFacade.map(this.smartMeteringDevice, SmartMeter.class))
        .thenReturn(this.smartMeter);
    when(this.smartMeteringDeviceRepository.save(this.smartMeter)).thenReturn(this.smartMeter);
    ReflectionTestUtils.setField(this.instance, "smartMeterService", this.smartMeterService);
  }

  @Test
  void addMeterProtocolInfoSaved() throws FunctionalException {
    when(this.smartMeteringDeviceRepository.findByDeviceIdentification(DEVICE_IDENTIFICATION))
        .thenReturn(null);
    when(this.smartMeteringDevice.getProtocolName()).thenReturn(PROTOCOL_NAME);
    when(this.smartMeteringDevice.getProtocolVersion()).thenReturn(PROTOCOL_VERSION);
    when(this.smartMeteringDevice.getProtocolVariant()).thenReturn(PROTOCOL_VARIANT);
    when(this.protocolInfoRepository.findByProtocolAndProtocolVersionAndProtocolVariant(
            PROTOCOL_NAME, PROTOCOL_VERSION, PROTOCOL_VARIANT))
        .thenReturn(this.protocolInfo);
    when(this.mapperFacade.map(this.addSmartMeterRequest.getDevice(), SmartMeteringDeviceDto.class))
        .thenReturn(new SmartMeteringDeviceDto());

    this.instance.addMeter(this.deviceMessageMetadata, this.addSmartMeterRequest);

    verify(this.smartMeter).updateProtocol(this.protocolInfo);
    verify(this.smartMeteringDeviceRepository).save(this.smartMeter);
  }

  @Test
  void addMeterProtocolInfoNotFound() {
    when(this.smartMeteringDeviceRepository.findByDeviceIdentification(DEVICE_IDENTIFICATION))
        .thenReturn(null);
    when(this.smartMeteringDevice.getProtocolName()).thenReturn(PROTOCOL_NAME);
    when(this.smartMeteringDevice.getProtocolVersion()).thenReturn(PROTOCOL_VERSION);
    when(this.protocolInfoRepository.findByProtocolAndProtocolVersion(
            PROTOCOL_NAME, PROTOCOL_VERSION))
        .thenReturn(null);

    try {
      this.instance.addMeter(this.deviceMessageMetadata, this.addSmartMeterRequest);
    } catch (final FunctionalException e) {
      assertThat(e.getExceptionType())
          .isEqualTo(FunctionalExceptionType.UNKNOWN_PROTOCOL_NAME_OR_VERSION_OR_VARIANT);
      assertThat(e.getComponentType()).isEqualTo(ComponentType.DOMAIN_SMART_METERING);
    }
  }

  @Test
  void addMeterDeviceExists() {
    when(this.smartMeteringDeviceRepository.findByDeviceIdentification(DEVICE_IDENTIFICATION))
        .thenReturn(this.smartMeter);

    try {
      this.instance.addMeter(this.deviceMessageMetadata, this.addSmartMeterRequest);
    } catch (final FunctionalException e) {
      assertThat(e.getExceptionType()).isEqualTo(FunctionalExceptionType.EXISTING_DEVICE);
      assertThat(e.getComponentType()).isEqualTo(ComponentType.DOMAIN_SMART_METERING);
    }
  }

  @Test
  void testUpdateSmartMeter() throws FunctionalException {
    final SmartMeter existingSmartMeter = new SmartMeter();
    existingSmartMeter.setId(1L);
    existingSmartMeter.setVersion(2L);

    when(this.smartMeteringDeviceRepository.findByDeviceIdentification(DEVICE_IDENTIFICATION))
        .thenReturn(existingSmartMeter);
    when(this.addSmartMeterRequest.getOverwrite()).thenReturn(true);

    doReturn(existingSmartMeter)
        .when(this.smartMeterService)
        .convertSmartMeter(this.smartMeteringDevice);

    when(this.protocolInfoRepository.findByProtocolAndProtocolVersionAndProtocolVariant(
            this.smartMeteringDevice.getProtocolName(),
            this.smartMeteringDevice.getProtocolVersion(),
            this.smartMeteringDevice.getProtocolVariant()))
        .thenReturn(this.protocolInfo);
    when(this.mapperFacade.map(this.addSmartMeterRequest.getDevice(), SmartMeteringDeviceDto.class))
        .thenReturn(new SmartMeteringDeviceDto());

    this.instance.addMeter(this.deviceMessageMetadata, this.addSmartMeterRequest);

    verify(this.smartMeteringDeviceRepository, times(1)).save(any(SmartMeter.class));
  }

  @Test
  void testNoUpdateOnExistingMeterWhenOverwriteIsFalse() {
    final SmartMeter existingSmartMeter = new SmartMeter();
    existingSmartMeter.setId(1L);
    existingSmartMeter.setVersion(2L);

    when(this.smartMeteringDeviceRepository.findByDeviceIdentification(DEVICE_IDENTIFICATION))
        .thenReturn(existingSmartMeter);
    when(this.addSmartMeterRequest.getOverwrite()).thenReturn(false);

    doReturn(existingSmartMeter)
        .when(this.smartMeterService)
        .convertSmartMeter(this.smartMeteringDevice);

    when(this.protocolInfoRepository.findByProtocolAndProtocolVersionAndProtocolVariant(
            this.smartMeteringDevice.getProtocolName(),
            this.smartMeteringDevice.getProtocolVersion(),
            this.smartMeteringDevice.getProtocolVariant()))
        .thenReturn(this.protocolInfo);

    assertThatExceptionOfType(FunctionalException.class)
        .isThrownBy(
            () -> {
              this.instance.addMeter(this.deviceMessageMetadata, this.addSmartMeterRequest);
            });
  }
}
