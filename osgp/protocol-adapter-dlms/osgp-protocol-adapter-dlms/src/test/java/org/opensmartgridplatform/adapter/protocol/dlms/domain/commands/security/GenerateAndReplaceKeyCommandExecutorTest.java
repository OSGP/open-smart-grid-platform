/*
 * SPDX-FileCopyrightText: Copyright Contributors to the GXF project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.security;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.MethodParameter;
import org.openmuc.jdlms.MethodResult;
import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.SetParameter;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.DeviceKeyProcessingService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.SecretManagementService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ClearMBusStatusOnAllChannelsRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GenerateAndReplaceKeysRequestDataDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class GenerateAndReplaceKeyCommandExecutorTest {

  @Mock private SecretManagementService secretManagementService;

  @Mock private DeviceKeyProcessingService deviceKeyProcessingService;
  @Mock private DlmsDeviceRepository dlmsDeviceRepository;
  @Mock private DlmsConnectionManager connectionManager;

  @Mock private DlmsConnection dlmsConnection;

  private DlmsMessageListener dlmsMessageListener;

  @Mock private ClearMBusStatusOnAllChannelsRequestDto dto;

  @Mock private MessageMetadata messageMetadata;

  @Mock private GetResult getResult;

  @Mock private MethodResult methodResult;

  @Captor private ArgumentCaptor<AttributeAddress> attributeAddressArgumentCaptor;

  @Captor private ArgumentCaptor<SetParameter> setParameterArgumentCaptor;

  @Captor private ArgumentCaptor<MethodParameter> methodParameterArgumentCaptor;
  @InjectMocks private GenerateAndReplaceKeyCommandExecutor commandExecutor;

  @BeforeEach
  public void setup() {
    this.dlmsMessageListener = Mockito.mock(DlmsMessageListener.class);
  }

  @Test
  void testExecute() throws Exception {
    final DlmsDevice device = Mockito.mock(DlmsDevice.class);
    final GenerateAndReplaceKeysRequestDataDto actionRequestDto =
        Mockito.mock(GenerateAndReplaceKeysRequestDataDto.class);
    final MessageMetadata messageMetadata = Mockito.mock(MessageMetadata.class);

    when(this.connectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.connectionManager.getConnection()).thenReturn(this.dlmsConnection);
    when(this.methodResult.getResultCode()).thenReturn(MethodResultCode.SUCCESS);
    when(this.dlmsConnection.action(this.methodParameterArgumentCaptor.capture()))
        .thenReturn(this.methodResult);
    when(device.getDeviceIdentification()).thenReturn("device1");
    when(messageMetadata.getDeviceIdentification()).thenReturn("device1");
    when(device.getDeviceIdentification()).thenReturn("device1");
    when(messageMetadata.getDeviceIdentification()).thenReturn("device1");

    final byte[] decryptedMasterKey = new byte[16];
    when(this.secretManagementService.getKey(any(), any(), any())).thenReturn(decryptedMasterKey);
    final Map<SecurityKeyType, byte[]> generatedKeys =
        Map.of(
            SecurityKeyType.E_METER_AUTHENTICATION,
            new byte[16],
            SecurityKeyType.E_METER_ENCRYPTION,
            new byte[16]);
    when(this.secretManagementService.generate128BitsKeysAndStoreAsNewKeys(any(), any(), any()))
        .thenReturn(generatedKeys);

    final ActionResponseDto result =
        this.commandExecutor.execute(
            this.connectionManager, device, actionRequestDto, messageMetadata);

    assertThat("Replace keys for device device1 was successful")
        .isEqualTo(result.getResultString());

    verify(this.secretManagementService, times(1))
        .generate128BitsKeysAndStoreAsNewKeys(Mockito.any(), Mockito.any(), Mockito.any());
    verify(this.secretManagementService, times(2))
        .getKey(Mockito.any(), Mockito.any(), Mockito.any());
    verify(this.secretManagementService, times(2))
        .activateNewKey(Mockito.any(), Mockito.any(), Mockito.any());

    verify(this.dlmsDeviceRepository, times(1))
        .updateInvocationCounter(Mockito.any(), Mockito.any());
  }
}
