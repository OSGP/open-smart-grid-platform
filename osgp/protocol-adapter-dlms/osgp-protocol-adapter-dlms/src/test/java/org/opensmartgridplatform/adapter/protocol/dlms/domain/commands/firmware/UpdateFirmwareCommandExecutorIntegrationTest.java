/*
 * Copyright 2021 Alliander N.V.
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
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
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub.DlmsConnectionManagerStub;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub.DlmsConnectionStub;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.FirmwareFileCachingRepository;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ImageTransferAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.method.ImageTransferMethod;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class UpdateFirmwareCommandExecutorIntegrationTest {

  private UpdateFirmwareCommandExecutor commandExecutor;
  @Mock private FirmwareFileCachingRepository firmwareFileCachingRepository;
  private final int verificationStatusCheckInterval = 1;
  private final int verificationStatusCheckTimeout = 2;
  private final int initiationStatusCheckInterval = 3;
  private final int initiationStatusCheckTimeout = 4;

  private DlmsConnectionManagerStub connectionManagerStub;
  private DlmsConnectionStub connectionStub;
  private MessageMetadata messageMetadata;

  @BeforeEach
  void setUp() {
    this.commandExecutor =
        new UpdateFirmwareCommandExecutor(
            this.firmwareFileCachingRepository,
            this.verificationStatusCheckInterval,
            this.verificationStatusCheckTimeout,
            this.initiationStatusCheckInterval,
            this.initiationStatusCheckTimeout);

    this.connectionStub = new DlmsConnectionStub();
    this.connectionManagerStub = new DlmsConnectionManagerStub(this.connectionStub);

    this.connectionStub.setDefaultReturnValue(DataObject.newArrayData(Collections.emptyList()));

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
    final byte[] firmwareFile =
        RandomStringUtils.randomAlphabetic(10).getBytes(StandardCharsets.UTF_8);

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

    when(this.firmwareFileCachingRepository.retrieve(firmwareIdentification))
        .thenReturn(firmwareFile);

    this.commandExecutor.execute(
        this.connectionManagerStub, device, firmwareIdentification, this.messageMetadata);

    assertThat(
            this.connectionStub.hasMethodBeenInvoked(ImageTransferMethod.IMAGE_TRANSFER_INITIATE))
        .isTrue();
    assertThat(this.connectionStub.hasMethodBeenInvoked(ImageTransferMethod.IMAGE_BLOCK_TRANSFER))
        .isTrue();
    assertThat(this.connectionStub.hasMethodBeenInvoked(ImageTransferMethod.IMAGE_ACTIVATE))
        .isTrue();

    this.assertSetParameter(
        ImageTransferAttribute.IMAGE_TRANSFER_ENABLED,
        Collections.singletonList(DataObject.newBoolData(false)));
  }

  private void assertSetParameter(
      final ImageTransferAttribute imageTransferEnabled, final List<DataObject> expectedValues) {
    final List<SetParameter> setParameters =
        this.connectionStub.getSetParameters(imageTransferEnabled);
    assertThat(setParameters.size()).isEqualTo(expectedValues.size());

    assertThat(setParameters.stream().map(SetParameter::getData).collect(Collectors.toList()))
        .usingRecursiveComparison()
        .isEqualTo(expectedValues);
  }

  public AttributeAddress createAttributeAddressForImageTransfer(
      final AttributeClass attributeClass) {
    return new AttributeAddress(18, new ObisCode("0.0.44.0.0.255"), attributeClass.attributeId());
  }
}
