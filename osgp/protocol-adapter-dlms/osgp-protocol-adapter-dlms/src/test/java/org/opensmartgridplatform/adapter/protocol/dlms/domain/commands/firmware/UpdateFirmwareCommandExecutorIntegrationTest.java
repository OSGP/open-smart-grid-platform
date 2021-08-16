/*
 * Copyright 2021 Alliander N.V.
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.MethodResult;
import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.MacGenerationService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub.DlmsConnectionManagerStub;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub.DlmsConnectionStub;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.FirmwareFileCachingRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.FirmwareImageIdentifierCachingRepository;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ImageTransferAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.method.ImageTransferMethod;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.UpdateFirmwareRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class UpdateFirmwareCommandExecutorIntegrationTest {

  private UpdateFirmwareCommandExecutor commandExecutor;

  @Mock private DlmsDeviceRepository dlmsDeviceRepository;
  @Mock private FirmwareFileCachingRepository firmwareFileCachingRepository;
  @Mock private FirmwareImageIdentifierCachingRepository firmwareImageIdentifierCachingRepository;
  @Mock private MacGenerationService macGenerationService;

  private DlmsConnectionManagerStub connectionManagerStub;
  private DlmsConnectionStub connectionStub;
  private MessageMetadata messageMetadata;

  private final int verificationStatusCheckInterval = 1;
  private final int verificationStatusCheckTimeout = 2;
  private final int initiationStatusCheckInterval = 3;
  private final int initiationStatusCheckTimeout = 4;

  @BeforeEach
  void setUp() {
    this.commandExecutor =
        new UpdateFirmwareCommandExecutor(
            this.dlmsDeviceRepository,
            this.firmwareFileCachingRepository,
            this.firmwareImageIdentifierCachingRepository,
            this.macGenerationService,
            this.verificationStatusCheckInterval,
            this.verificationStatusCheckTimeout,
            this.initiationStatusCheckInterval,
            this.initiationStatusCheckTimeout);

    this.connectionStub = new DlmsConnectionStub();
    this.connectionManagerStub = new DlmsConnectionManagerStub(this.connectionStub);

    this.connectionStub.setDefaultReturnValue(DataObject.newArrayData(Collections.emptyList()));

    this.connectionStub.addReturnValue(
        this.createAttributeAddressForImageTransfer(ImageTransferAttribute.IMAGE_TRANSFER_ENABLED),
        DataObject.newBoolData(true));
    this.connectionStub.addReturnValue(
        this.createAttributeAddressForImageTransfer(ImageTransferAttribute.IMAGE_TRANSFER_STATUS),
        DataObject.newInteger32Data(1),
        5);
    this.connectionStub.addReturnValue(
        this.createAttributeAddressForImageTransfer(ImageTransferAttribute.IMAGE_TRANSFER_STATUS),
        DataObject.newInteger32Data(6));
    this.connectionStub.addReturnValue(
        this.createAttributeAddressForImageTransfer(ImageTransferAttribute.IMAGE_BLOCK_SIZE),
        DataObject.newUInteger32Data(100));
    this.connectionStub.addReturnValue(
        this.createAttributeAddressForImageTransfer(
            ImageTransferAttribute.IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER),
        DataObject.newUInteger32Data(100));

    this.messageMetadata =
        MessageMetadata.newMessageMetadataBuilder().withCorrelationUid("123456").build();

    final MethodResult methodResult = mock(MethodResult.class);
    when(methodResult.getResultCode()).thenReturn(MethodResultCode.SUCCESS);
    this.connectionStub.setDefaultMethodResult(methodResult);
  }

  @Test
  void testExecute() throws Exception {
    final DlmsDevice device = new DlmsDevice();
    final String firmwareIdentification = RandomStringUtils.randomAlphabetic(10);
    final String deviceIdentification = RandomStringUtils.randomAlphabetic(10);

    final byte[] firmwareFile =
        RandomStringUtils.randomAlphabetic(100).getBytes(StandardCharsets.UTF_8);

    final String firmwareImageIdentifier = "496d6167654964656e746966696572";

    when(this.firmwareFileCachingRepository.retrieve(firmwareIdentification))
        .thenReturn(firmwareFile);
    when(this.firmwareImageIdentifierCachingRepository.retrieve(firmwareIdentification))
        .thenReturn(firmwareImageIdentifier);

    final UpdateFirmwareRequestDto updateFirmwareRequestDto =
        new UpdateFirmwareRequestDto(firmwareIdentification, deviceIdentification);
    this.commandExecutor.execute(
        this.connectionManagerStub, device, updateFirmwareRequestDto, this.messageMetadata);

    verify(this.dlmsDeviceRepository, never()).findByDeviceIdentification(deviceIdentification);
    verify(this.macGenerationService, never()).calculateMac(any(), any(), any());
    verify(this.firmwareFileCachingRepository, times(1)).retrieve(firmwareIdentification);
    verify(this.firmwareImageIdentifierCachingRepository, times(1))
        .retrieve(firmwareIdentification);

    this.doAsserts();
  }

  @Test
  void testExecuteMbusFirmware() throws Exception {
    final DlmsDevice device = new DlmsDevice();
    device.setMbusIdentificationNumber(1L);
    final String firmwareIdentification = RandomStringUtils.randomAlphabetic(10);
    final String deviceIdentification = RandomStringUtils.randomAlphabetic(10);
    device.setDeviceIdentification(deviceIdentification);

    final byte[] firmwareFile =
        org.bouncycastle.util.encoders.Hex.decode(
            "534d523500230011004000310000001000020801e91effffffff500303000000000000831c9d5aa5b4f"
                + "fbfd057035a8a7896a4abe7afa36687fbc48944bcee0343eed3a75aab882ec1cf57820adfd4394e262d"
                + "5fa821c678e71c05c47e1c69c4bfffe1fd");

    when(this.dlmsDeviceRepository.findByDeviceIdentification(deviceIdentification))
        .thenReturn(device);
    when(this.firmwareFileCachingRepository.retrieve(firmwareIdentification))
        .thenReturn(firmwareFile);
    when(this.macGenerationService.calculateMac(any(), any(), any())).thenReturn(new byte[16]);

    final UpdateFirmwareRequestDto updateFirmwareRequestDto =
        new UpdateFirmwareRequestDto(firmwareIdentification, deviceIdentification);
    this.commandExecutor.execute(
        this.connectionManagerStub, device, updateFirmwareRequestDto, this.messageMetadata);

    verify(this.dlmsDeviceRepository, times(1)).findByDeviceIdentification(deviceIdentification);
    verify(this.macGenerationService, times(1)).calculateMac(any(), any(), any());
    verify(this.firmwareFileCachingRepository, times(1)).retrieve(firmwareIdentification);
    verify(this.firmwareImageIdentifierCachingRepository, never()).retrieve(firmwareIdentification);

    this.doAsserts();
  }

  private void doAsserts() {
    assertThat(
            this.connectionStub.hasMethodBeenInvoked(ImageTransferMethod.IMAGE_TRANSFER_INITIATE))
        .isTrue();
    assertThat(this.connectionStub.hasMethodBeenInvoked(ImageTransferMethod.IMAGE_BLOCK_TRANSFER))
        .isTrue();
    assertThat(this.connectionStub.hasMethodBeenInvoked(ImageTransferMethod.IMAGE_ACTIVATE))
        .isTrue();

    assertThat(this.connectionStub.getSetParameters(ImageTransferAttribute.IMAGE_TRANSFER_ENABLED))
        .isEmpty();
  }

  public AttributeAddress createAttributeAddressForImageTransfer(
      final AttributeClass attributeClass) {
    return new AttributeAddress(18, new ObisCode("0.0.44.0.0.255"), attributeClass.attributeId());
  }
}
