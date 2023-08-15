// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import org.apache.commons.lang3.RandomStringUtils;
import org.bouncycastle.util.encoders.Hex;
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
import org.opensmartgridplatform.adapter.protocol.dlms.application.config.UpdateFirmwareConfig;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.MacGenerationService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.ImageTransfer.ImageTransferProperties;
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
  @Mock private UpdateFirmwareConfig updateFirmwareConfig;

  private DlmsConnectionManagerStub connectionManagerStub;
  private DlmsConnectionStub connectionStub;
  private MessageMetadata messageMetadata;

  private final int verificationStatusCheckInterval = 1;
  private final int verificationStatusCheckTimeout = 2;
  private final int initiationStatusCheckInterval = 3;
  private final int initiationStatusCheckTimeout = 4;

  final byte[] firmwareFileNotMbus =
      org.bouncycastle.util.encoders.Hex.decode(
          "0000000000230011004000310000001000020801e91effffffff500303000000000000831c9d5aa5b4f"
              + "fbfd057035a8a7896a4abe7afa36687fbc48944bcee0343eed3a75aab882ec1cf57820adfd4394e262d"
              + "5fa821c678e71c05c47e1c69c4bfffe1fd");
  final String fwFileHash = "951948459d2b7b59883cfc75c2bc2b7e4a0232ae7973a0d99526afd9458b0c86";
  final byte[] firmwareFileMbus =
      org.bouncycastle.util.encoders.Hex.decode(
          "534d523500230011004000310000001000020801e91effffffff500303000000000000831c9d5aa5b4f"
              + "fbfd057035a8a7896a4abe7afa36687fbc48944bcee0343eed3a75aab882ec1cf57820adfd4394e262d"
              + "5fa821c678e71c05c47e1c69c4bfffe1fd");

  @BeforeEach
  void setUp() {

    this.messageMetadata = MessageMetadata.newBuilder().withCorrelationUid("123456").build();

    final ImageTransferProperties imageTransferProperties =
        new ImageTransfer.ImageTransferProperties();
    imageTransferProperties.setVerificationStatusCheckInterval(
        this.verificationStatusCheckInterval);
    imageTransferProperties.setVerificationStatusCheckTimeout(this.verificationStatusCheckTimeout);
    imageTransferProperties.setInitiationStatusCheckInterval(this.initiationStatusCheckInterval);
    imageTransferProperties.setInitiationStatusCheckTimeout(this.initiationStatusCheckTimeout);

    this.commandExecutor =
        new UpdateFirmwareCommandExecutor(
            this.dlmsDeviceRepository,
            this.firmwareFileCachingRepository,
            this.firmwareImageIdentifierCachingRepository,
            this.macGenerationService,
            imageTransferProperties);
  }

  private void initConnectionStubForTransferFromFirstBlock() {

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

    // initConnectionStubForTransferFromFirstBlock
    this.connectionStub.addReturnValue(
        this.createAttributeAddressForImageTransfer(ImageTransferAttribute.IMAGE_TRANSFER_STATUS),
        DataObject.newInteger32Data(6));
    this.connectionStub.addReturnValue(
        this.createAttributeAddressForImageTransfer(ImageTransferAttribute.IMAGE_BLOCK_SIZE),
        DataObject.newUInteger32Data(10));
    this.connectionStub.addReturnValue(
        this.createAttributeAddressForImageTransfer(
            ImageTransferAttribute.IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER),
        DataObject.newUInteger32Data(10));

    // Transfer state after all blocks transferred: VERIFICATION_SUCCESSFUL(3)
    this.connectionStub.addReturnValue(
        this.createAttributeAddressForImageTransfer(ImageTransferAttribute.IMAGE_TRANSFER_STATUS),
        DataObject.newInteger32Data(3));

    final MethodResult methodResult = mock(MethodResult.class);
    when(methodResult.getResultCode()).thenReturn(MethodResultCode.SUCCESS);

    this.connectionStub.setDefaultMethodResult(methodResult);
  }

  private void initConnectionStubForTransferResumeOnBlocks() {

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

    // configure meter simulator to follow ResumeOnBlocks flow
    // INITIATED(1)
    this.connectionStub.addReturnValue(
        this.createAttributeAddressForImageTransfer(ImageTransferAttribute.IMAGE_TRANSFER_STATUS),
        DataObject.newInteger32Data(1));
    this.connectionStub.addReturnValue(
        this.createAttributeAddressForImageTransfer(ImageTransferAttribute.IMAGE_BLOCK_SIZE),
        DataObject.newUInteger32Data(10));
    this.connectionStub.addReturnValue(
        this.createAttributeAddressForImageTransfer(
            ImageTransferAttribute.IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER),
        DataObject.newUInteger32Data(3));
    // configure meter simulator to have NO MissingImageBlocks
    this.connectionStub.addReturnValue(
        this.createAttributeAddressForImageTransfer(
            ImageTransferAttribute.IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER),
        DataObject.newUInteger32Data(10));
    // Transfer state after all blocks transferred: VERIFICATION_SUCCESSFUL(3)
    this.connectionStub.addReturnValue(
        this.createAttributeAddressForImageTransfer(ImageTransferAttribute.IMAGE_TRANSFER_STATUS),
        DataObject.newInteger32Data(3));

    final MethodResult methodResult = mock(MethodResult.class);
    when(methodResult.getResultCode()).thenReturn(MethodResultCode.SUCCESS);

    this.connectionStub.setDefaultMethodResult(methodResult);
  }

  @Test
  void testExecute() throws Exception {

    final DlmsDevice device = new DlmsDevice();
    final String firmwareIdentification = RandomStringUtils.randomAlphabetic(10);
    final String deviceIdentification = RandomStringUtils.randomAlphabetic(10);
    device.setDeviceIdentification(deviceIdentification);

    final byte[] firmwareImageIdentifier = Hex.decode("496d6167654964656e746966696572");

    when(this.firmwareFileCachingRepository.retrieve(firmwareIdentification))
        .thenReturn(this.firmwareFileNotMbus);
    when(this.firmwareImageIdentifierCachingRepository.retrieve(firmwareIdentification))
        .thenReturn(firmwareImageIdentifier);

    this.initConnectionStubForTransferFromFirstBlock();

    final UpdateFirmwareRequestDto updateFirmwareRequestDto =
        new UpdateFirmwareRequestDto(firmwareIdentification, deviceIdentification);
    this.commandExecutor.execute(
        this.connectionManagerStub, device, updateFirmwareRequestDto, this.messageMetadata);

    verify(this.dlmsDeviceRepository, never()).findByDeviceIdentification(deviceIdentification);
    verify(this.dlmsDeviceRepository, times(1)).storeFirmwareHash(deviceIdentification, null);
    verify(this.dlmsDeviceRepository, times(1))
        .storeFirmwareHash(deviceIdentification, this.fwFileHash);
    verify(this.macGenerationService, never()).calculateMac(any(), any(), any());
    verify(this.firmwareFileCachingRepository, times(1)).retrieve(firmwareIdentification);
    verify(this.firmwareImageIdentifierCachingRepository, times(1))
        .retrieve(firmwareIdentification);

    this.assertImageTransferRelatedInteractionWithConnection(1, 10, 1, 1);
  }

  @Test
  void testExecuteResumeOnBlocks() throws Exception {
    final DlmsDevice device = new DlmsDevice();
    final String firmwareIdentification = RandomStringUtils.randomAlphabetic(10);
    final String deviceIdentification = RandomStringUtils.randomAlphabetic(10);

    device.setDeviceIdentification(deviceIdentification);
    device.setFirmwareHash(this.fwFileHash);

    final byte[] firmwareImageIdentifier = Hex.decode("496d6167654964656e746966696572");

    when(this.firmwareFileCachingRepository.retrieve(firmwareIdentification))
        .thenReturn(this.firmwareFileNotMbus);
    when(this.firmwareImageIdentifierCachingRepository.retrieve(firmwareIdentification))
        .thenReturn(firmwareImageIdentifier);

    this.initConnectionStubForTransferResumeOnBlocks();

    final UpdateFirmwareRequestDto updateFirmwareRequestDto =
        new UpdateFirmwareRequestDto(firmwareIdentification, deviceIdentification);
    this.commandExecutor.execute(
        this.connectionManagerStub, device, updateFirmwareRequestDto, this.messageMetadata);

    verify(this.dlmsDeviceRepository, never()).findByDeviceIdentification(deviceIdentification);
    verify(this.dlmsDeviceRepository, times(1)).storeFirmwareHash(deviceIdentification, null);
    verify(this.macGenerationService, never()).calculateMac(any(), any(), any());
    verify(this.firmwareFileCachingRepository, times(1)).retrieve(firmwareIdentification);
    verify(this.firmwareImageIdentifierCachingRepository, times(1))
        .retrieve(firmwareIdentification);

    this.assertImageTransferRelatedInteractionWithConnection(0, 7, 1, 1);
  }

  @Test
  void testExecuteMbusFirmware() throws Exception {
    final DlmsDevice device = new DlmsDevice();
    device.setMbusIdentificationNumber("00000001");
    final String firmwareIdentification = RandomStringUtils.randomAlphabetic(10);
    final String deviceIdentification = RandomStringUtils.randomAlphabetic(10);
    device.setDeviceIdentification(deviceIdentification);

    when(this.dlmsDeviceRepository.findByDeviceIdentification(deviceIdentification))
        .thenReturn(device);
    when(this.firmwareFileCachingRepository.retrieve(firmwareIdentification))
        .thenReturn(this.firmwareFileMbus);
    when(this.macGenerationService.calculateMac(any(), any(), any())).thenReturn(new byte[16]);

    this.initConnectionStubForTransferFromFirstBlock();

    final UpdateFirmwareRequestDto updateFirmwareRequestDto =
        new UpdateFirmwareRequestDto(firmwareIdentification, deviceIdentification);
    this.commandExecutor.execute(
        this.connectionManagerStub, device, updateFirmwareRequestDto, this.messageMetadata);

    verify(this.dlmsDeviceRepository, times(1)).findByDeviceIdentification(deviceIdentification);
    verify(this.dlmsDeviceRepository, times(1)).storeFirmwareHash(deviceIdentification, null);
    verify(this.dlmsDeviceRepository, times(1))
        .storeFirmwareHash(deviceIdentification, this.fwFileHash);
    verify(this.macGenerationService, times(1)).calculateMac(any(), any(), any());
    verify(this.firmwareFileCachingRepository, times(1)).retrieve(firmwareIdentification);
    verify(this.firmwareImageIdentifierCachingRepository, never()).retrieve(firmwareIdentification);

    //    this.assertImageTransferRelatedInteractionWithConnection();
    this.assertImageTransferRelatedInteractionWithConnection(1, 10, 0, 1);
  }

  @Test
  void testExecuteMbusFirmwareResumeOnBlocks() throws Exception {
    final DlmsDevice device = new DlmsDevice();
    device.setMbusIdentificationNumber("00000001");
    final String firmwareIdentification = RandomStringUtils.randomAlphabetic(10);
    final String deviceIdentification = RandomStringUtils.randomAlphabetic(10);
    device.setDeviceIdentification(deviceIdentification);
    device.setFirmwareHash(this.fwFileHash);

    when(this.dlmsDeviceRepository.findByDeviceIdentification(deviceIdentification))
        .thenReturn(device);
    when(this.firmwareFileCachingRepository.retrieve(firmwareIdentification))
        .thenReturn(this.firmwareFileMbus);
    when(this.macGenerationService.calculateMac(any(), any(), any())).thenReturn(new byte[16]);

    this.initConnectionStubForTransferResumeOnBlocks();

    final UpdateFirmwareRequestDto updateFirmwareRequestDto =
        new UpdateFirmwareRequestDto(firmwareIdentification, deviceIdentification);
    this.commandExecutor.execute(
        this.connectionManagerStub, device, updateFirmwareRequestDto, this.messageMetadata);

    verify(this.dlmsDeviceRepository, times(1)).findByDeviceIdentification(deviceIdentification);
    verify(this.dlmsDeviceRepository, times(1)).storeFirmwareHash(deviceIdentification, null);
    verify(this.macGenerationService, times(1)).calculateMac(any(), any(), any());
    verify(this.firmwareFileCachingRepository, times(1)).retrieve(firmwareIdentification);
    verify(this.firmwareImageIdentifierCachingRepository, never()).retrieve(firmwareIdentification);

    this.assertImageTransferRelatedInteractionWithConnection(0, 7, 0, 1);
  }

  private void assertImageTransferRelatedInteractionWithConnection() {
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

  private void assertImageTransferRelatedInteractionWithConnection(
      final long initiate, final long transfer, final long verify, final long activate) {
    assertThat(
            this.connectionStub.hasMethodBeenInvokedTimes(
                ImageTransferMethod.IMAGE_TRANSFER_INITIATE))
        .isEqualTo(initiate);
    assertThat(
            this.connectionStub.hasMethodBeenInvokedTimes(ImageTransferMethod.IMAGE_BLOCK_TRANSFER))
        .isEqualTo(transfer);

    assertThat(this.connectionStub.hasMethodBeenInvokedTimes(ImageTransferMethod.IMAGE_VERIFY))
        .isEqualTo(verify);
    assertThat(this.connectionStub.hasMethodBeenInvokedTimes(ImageTransferMethod.IMAGE_ACTIVATE))
        .isEqualTo(activate);

    assertThat(this.connectionStub.getSetParameters(ImageTransferAttribute.IMAGE_TRANSFER_ENABLED))
        .isEmpty();
  }

  public AttributeAddress createAttributeAddressForImageTransfer(
      final AttributeClass attributeClass) {
    return new AttributeAddress(18, new ObisCode("0.0.44.0.0.255"), attributeClass.attributeId());
  }
}
