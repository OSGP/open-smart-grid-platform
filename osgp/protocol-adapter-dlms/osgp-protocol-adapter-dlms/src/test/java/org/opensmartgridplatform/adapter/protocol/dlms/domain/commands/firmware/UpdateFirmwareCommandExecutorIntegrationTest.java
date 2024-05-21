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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.MethodResult;
import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
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
import org.opensmartgridplatform.dto.valueobjects.smartmetering.UpdateFirmwareRequestDataDto;
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

  final byte[] firmwareFileNotMbus =
      org.bouncycastle.util.encoders.Hex.decode(
          "0000000000230011004000310000001000020801e91effffffff500303000000000000831c9d5aa5b4f"
              + "fbfd057035a8a7896a4abe7afa36687fbc48944bcee0343eed3a75aab882ec1cf57820adfd4394e262d"
              + "5fa821c678e71c05c47e1c69c4bfffe1fd");

  final String fwFileHashNotMBus =
      "6e55dcd3ec19ea23b1ac2512dc9df827a4f8788cba4a76f49a91f158c4a5c4b3";
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
    imageTransferProperties.setVerificationStatusCheckInterval(1);
    imageTransferProperties.setVerificationStatusCheckTimeout(2);
    imageTransferProperties.setInitiationStatusCheckInterval(3);
    imageTransferProperties.setInitiationStatusCheckTimeout(4);

    this.commandExecutor =
        new UpdateFirmwareCommandExecutor(
            this.dlmsDeviceRepository,
            this.firmwareFileCachingRepository,
            this.firmwareImageIdentifierCachingRepository,
            this.macGenerationService,
            imageTransferProperties);
  }

  private void initConnectionStubForTransfer(
      final int firstTransferState, final int firstNotTransferredBlockNumber) {

    this.connectionStub = new DlmsConnectionStub();
    this.connectionManagerStub = new DlmsConnectionManagerStub(this.connectionStub);

    this.connectionStub.setDefaultReturnValue(DataObject.newArrayData(Collections.emptyList()));

    if (firstTransferState > -1) {
      this.connectionStub.addReturnValue(
          this.createAttributeAddressForImageTransfer(ImageTransferAttribute.IMAGE_TRANSFER_STATUS),
          DataObject.newInteger32Data(firstTransferState));
    }

    this.connectionStub.addReturnValue(
        this.createAttributeAddressForImageTransfer(ImageTransferAttribute.IMAGE_TRANSFER_ENABLED),
        DataObject.newBoolData(true));
    this.connectionStub.addReturnValue(
        this.createAttributeAddressForImageTransfer(ImageTransferAttribute.IMAGE_TRANSFER_STATUS),
        DataObject.newInteger32Data(1),
        4);

    // configure the connection stub to follow either FromFirstBlock or ResumeOnBlocks flow
    // If IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER is not the total number of blocks (10)
    // then the flow goes ResumeOnBlocks
    this.connectionStub.addReturnValue(
        this.createAttributeAddressForImageTransfer(ImageTransferAttribute.IMAGE_BLOCK_SIZE),
        DataObject.newUInteger32Data(10));
    this.connectionStub.addReturnValue(
        this.createAttributeAddressForImageTransfer(
            ImageTransferAttribute.IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER),
        DataObject.newUInteger32Data(firstNotTransferredBlockNumber));

    // NO MissingImageBlocks after all blocks transferred
    this.connectionStub.addReturnValue(
        this.createAttributeAddressForImageTransfer(
            ImageTransferAttribute.IMAGE_FIRST_NOT_TRANSFERRED_BLOCK_NUMBER),
        DataObject.newUInteger32Data(10));

    // Transfer state after all blocks transferred: VERIFICATION_INITIATED(2)
    this.connectionStub.addReturnValue(
        this.createAttributeAddressForImageTransfer(ImageTransferAttribute.IMAGE_TRANSFER_STATUS),
        DataObject.newInteger32Data(2),
        4);

    // Transfer state after image verification: VERIFICATION_SUCCESSFUL(3)
    this.connectionStub.addReturnValue(
        this.createAttributeAddressForImageTransfer(ImageTransferAttribute.IMAGE_TRANSFER_STATUS),
        DataObject.newInteger32Data(3));

    final MethodResult methodResult = mock(MethodResult.class);
    when(methodResult.getResultCode()).thenReturn(MethodResultCode.SUCCESS);

    this.connectionStub.setDefaultMethodResult(methodResult);
  }

  @ParameterizedTest
  @CsvSource({"true, false", "false, false"})
  void testExecuteFirmwareFileNotMBus(final boolean hasFwHash, final boolean fwHashIsEqual)
      throws Exception {

    final DlmsDevice device = new DlmsDevice();
    final String firmwareIdentification = RandomStringUtils.randomAlphabetic(10);
    final String deviceIdentification = RandomStringUtils.randomAlphabetic(10);

    device.setDeviceIdentification(deviceIdentification);
    device.setFirmwareHash("hash_of_different_firmware");

    final byte[] firmwareImageIdentifier = Hex.decode("496d6167654964656e746966696572");

    when(this.firmwareFileCachingRepository.retrieve(firmwareIdentification))
        .thenReturn(this.firmwareFileNotMbus);
    when(this.firmwareImageIdentifierCachingRepository.retrieve(firmwareIdentification))
        .thenReturn(firmwareImageIdentifier);

    // For some reason the first call to ImageTransfer.isInitiated() does not result in a
    // call to the connectionStub to get value for attribute IMAGE_TRANSFER_STATUS
    // with value -1 for firstTransState no returnValue for this call is defined
    this.initConnectionStubForTransfer(-1, 10);

    final UpdateFirmwareRequestDto updateFirmwareRequestDto =
        new UpdateFirmwareRequestDto(
            deviceIdentification,
            new UpdateFirmwareRequestDataDto(firmwareIdentification, null, null));
    this.commandExecutor.execute(
        this.connectionManagerStub, device, updateFirmwareRequestDto, this.messageMetadata);

    verify(this.dlmsDeviceRepository, never()).findByDeviceIdentification(deviceIdentification);
    verify(this.dlmsDeviceRepository, times(1)).storeFirmwareHash(deviceIdentification, null);
    verify(this.dlmsDeviceRepository, times(1))
        .storeFirmwareHash(deviceIdentification, this.fwFileHashNotMBus);
    verify(this.macGenerationService, never()).calculateMac(any(), any(), any());
    verify(this.firmwareFileCachingRepository, times(1)).retrieve(firmwareIdentification);
    verify(this.firmwareImageIdentifierCachingRepository, times(1))
        .retrieve(firmwareIdentification);

    this.assertImageTransferRelatedInteractionWithConnection(1, 10, 1);
  }

  @Test
  void testExecuteResumeOnBlocks() throws Exception {
    final DlmsDevice device = new DlmsDevice();
    final String firmwareIdentification = RandomStringUtils.randomAlphabetic(10);
    final String deviceIdentification = RandomStringUtils.randomAlphabetic(10);

    device.setDeviceIdentification(deviceIdentification);
    device.setFirmwareHash(this.fwFileHashNotMBus);

    final byte[] firmwareImageIdentifier = Hex.decode("496d6167654964656e746966696572");

    when(this.firmwareFileCachingRepository.retrieve(firmwareIdentification))
        .thenReturn(this.firmwareFileNotMbus);
    when(this.firmwareImageIdentifierCachingRepository.retrieve(firmwareIdentification))
        .thenReturn(firmwareImageIdentifier);

    this.initConnectionStubForTransfer(1, 3);

    final UpdateFirmwareRequestDto updateFirmwareRequestDto =
        new UpdateFirmwareRequestDto(
            deviceIdentification,
            new UpdateFirmwareRequestDataDto(firmwareIdentification, null, null));
    this.commandExecutor.execute(
        this.connectionManagerStub, device, updateFirmwareRequestDto, this.messageMetadata);

    verify(this.dlmsDeviceRepository, never()).findByDeviceIdentification(deviceIdentification);
    verify(this.dlmsDeviceRepository, times(1)).storeFirmwareHash(deviceIdentification, null);
    verify(this.macGenerationService, never()).calculateMac(any(), any(), any());
    verify(this.firmwareFileCachingRepository, times(1)).retrieve(firmwareIdentification);
    verify(this.firmwareImageIdentifierCachingRepository, times(1))
        .retrieve(firmwareIdentification);

    this.assertImageTransferRelatedInteractionWithConnection(0, 7, 1);
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

    this.initConnectionStubForTransfer(-1, 10);

    final UpdateFirmwareRequestDto updateFirmwareRequestDto =
        new UpdateFirmwareRequestDto(
            deviceIdentification,
            new UpdateFirmwareRequestDataDto(firmwareIdentification, null, null));
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
    this.assertImageTransferRelatedInteractionWithConnection(1, 10, 0);
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

    this.initConnectionStubForTransfer(1, 3);

    final UpdateFirmwareRequestDto updateFirmwareRequestDto =
        new UpdateFirmwareRequestDto(
            deviceIdentification,
            new UpdateFirmwareRequestDataDto(firmwareIdentification, null, null));
    this.commandExecutor.execute(
        this.connectionManagerStub, device, updateFirmwareRequestDto, this.messageMetadata);

    verify(this.dlmsDeviceRepository, times(1)).findByDeviceIdentification(deviceIdentification);
    verify(this.dlmsDeviceRepository, times(1)).storeFirmwareHash(deviceIdentification, null);
    verify(this.macGenerationService, times(1)).calculateMac(any(), any(), any());
    verify(this.firmwareFileCachingRepository, times(1)).retrieve(firmwareIdentification);
    verify(this.firmwareImageIdentifierCachingRepository, never()).retrieve(firmwareIdentification);

    this.assertImageTransferRelatedInteractionWithConnection(0, 7, 0);
  }

  private void assertImageTransferRelatedInteractionWithConnection(
      final long initiate, final long transfer, final long verify) {
    final long activate = 1;
    assertThat(
            this.connectionStub.getMethodInvocationCount(
                ImageTransferMethod.IMAGE_TRANSFER_INITIATE))
        .isEqualTo(initiate);
    assertThat(
            this.connectionStub.getMethodInvocationCount(ImageTransferMethod.IMAGE_BLOCK_TRANSFER))
        .isEqualTo(transfer);

    assertThat(this.connectionStub.getMethodInvocationCount(ImageTransferMethod.IMAGE_VERIFY))
        .isEqualTo(verify);
    assertThat(this.connectionStub.getMethodInvocationCount(ImageTransferMethod.IMAGE_ACTIVATE))
        .isEqualTo(activate);

    assertThat(this.connectionStub.getSetParameters(ImageTransferAttribute.IMAGE_TRANSFER_ENABLED))
        .isEmpty();
  }

  public AttributeAddress createAttributeAddressForImageTransfer(
      final AttributeClass attributeClass) {
    return new AttributeAddress(18, new ObisCode("0.0.44.0.0.255"), attributeClass.attributeId());
  }
}
