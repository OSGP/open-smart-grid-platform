//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.BitString;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.GetConfigurationObjectService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.GetConfigurationObjectServiceSmr5;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.SetConfigurationObjectService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.SetConfigurationObjectServiceSmr5;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationObjectDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GprsOperationModeTypeDto;

@ExtendWith(MockitoExtension.class)
public class SetConfigurationObjectCommandExecutorSmr5IT
    extends SetConfigurationObjectCommandExecutorITBase {

  @BeforeEach
  public void setUp() throws IOException {
    final DlmsHelper dlmsHelper = new DlmsHelper();
    final GetConfigurationObjectService getService =
        new GetConfigurationObjectServiceSmr5(dlmsHelper);
    final SetConfigurationObjectService setService =
        new SetConfigurationObjectServiceSmr5(dlmsHelper);
    super.setUp(getService, setService);
  }

  @Test
  public void execute() throws IOException, ProtocolAdapterException {

    // SETUP
    // configurationToSet: ------1---10101-
    // GPRS operation mode is not part of the ConfigurationObject in SMR5
    final GprsOperationModeTypeDto gprsMode = null;
    final ConfigurationObjectDto configurationToSet =
        this.createConfigurationObjectDto(
            gprsMode,
            this.createFlagDto(ConfigurationFlagTypeDto.HLS_5_ON_P_3_ENABLE, true),
            this.createFlagDto(ConfigurationFlagTypeDto.DIRECT_ATTACH_AT_POWER_ON, true),
            this.createFlagDto(ConfigurationFlagTypeDto.HLS_6_ON_P3_ENABLE, false),
            this.createFlagDto(ConfigurationFlagTypeDto.HLS_7_ON_P3_ENABLE, true),
            this.createFlagDto(ConfigurationFlagTypeDto.HLS_6_ON_P0_ENABLE, false),
            this.createFlagDto(ConfigurationFlagTypeDto.HLS_7_ON_P0_ENABLE, true));

    // flagsOnDevice: 0000000001110100
    final byte[] flagsOnDevice =
        this.createFlagBytes(
            ConfigurationFlagTypeDto.HLS_5_ON_PO_ENABLE,
            ConfigurationFlagTypeDto.DIRECT_ATTACH_AT_POWER_ON,
            ConfigurationFlagTypeDto.HLS_6_ON_P3_ENABLE,
            ConfigurationFlagTypeDto.HLS_6_ON_P0_ENABLE);

    // result of merging configurationToSet and flagsOnDevice
    final byte firstExpectedByte = this.asByte("00000010");
    final byte secondExpectedByte = this.asByte("01101010");

    final DataObject deviceData = this.createBitStringData(flagsOnDevice);
    when(this.getResult.getResultData()).thenReturn(deviceData);

    final DlmsDevice device = new DlmsDevice();
    device.setProtocol(Protocol.SMR_5_0_0);

    // CALL
    final AccessResultCode result =
        this.instance.execute(this.conn, device, configurationToSet, this.messageMetadata);

    // VERIFY
    assertThat(result).isEqualTo(AccessResultCode.SUCCESS);

    final BitString configurationFlags = this.captureSetParameterBitStringData();
    final byte[] flagsSentToDevice = configurationFlags.getBitString();

    assertThat(flagsSentToDevice[0]).isEqualTo(firstExpectedByte);
    assertThat(flagsSentToDevice[1]).isEqualTo(secondExpectedByte);
  }

  @Override
  Integer getBitPosition(final ConfigurationFlagTypeDto flag) {
    return flag.getBitPositionSmr5().orElseThrow(RuntimeException::new);
  }

  private DataObject createBitStringData(final byte[] flags) {
    final BitString bitString = new BitString(flags, 16);
    return DataObject.newBitStringData(bitString);
  }

  private BitString captureSetParameterBitStringData() throws IOException {
    verify(this.dlmsConnection).set(this.setParameterArgumentCaptor.capture());
    final SetParameter setParameter = this.setParameterArgumentCaptor.getValue();
    final DataObject setParameterData = setParameter.getData();
    return setParameterData.getValue();
  }
}
