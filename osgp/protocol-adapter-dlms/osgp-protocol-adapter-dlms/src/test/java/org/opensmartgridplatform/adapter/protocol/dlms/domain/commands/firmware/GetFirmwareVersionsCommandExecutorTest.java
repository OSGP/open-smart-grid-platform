// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.when;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.GetResultBuilder;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dto.valueobjects.FirmwareModuleType;
import org.opensmartgridplatform.dto.valueobjects.FirmwareVersionDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
public class GetFirmwareVersionsCommandExecutorTest {
  private static final int CLASS_ID = 1;
  private static final int ATTRIBUTE_ID = 2;

  private static final ObisCode OBIS_CODE_ACTIVE_FIRMWARE_VERSION = new ObisCode("1.0.0.2.0.255");
  private static final ObisCode OBIS_CODE_MODULE_ACTIVE_FIRMWARE_VERSION =
      new ObisCode("1.1.0.2.0.255");
  private static final ObisCode OBIS_CODE_COMMUNICATION_MODULE_ACTIVE_FIRMWARE_VERSION =
      new ObisCode("1.2.0.2.0.255");
  private static final ObisCode OBIS_CODE_MBUS_DRIVER_ACTIVE_FIRMWARE_VERSION =
      new ObisCode("1.4.0.2.0.255");

  private GetFirmwareVersionsCommandExecutor executor;

  @Mock private DlmsMessageListener listener;

  @Mock private DlmsHelper helperService;

  private DlmsConnectionManager connectionHolder;

  private MessageMetadata messageMetadata;

  @BeforeEach
  public void setUp() {
    this.executor = new GetFirmwareVersionsCommandExecutor(this.helperService);
    this.connectionHolder = new DlmsConnectionManager(null, null, null, this.listener, null);
    this.messageMetadata = MessageMetadata.newBuilder().withCorrelationUid("123456").build();
  }

  @Test
  public void returns3FirmwareVersionsForDsmr422Device() throws Exception {
    final DlmsDevice device = new DlmsDevice();

    final GetResult getResult1 = new GetResultBuilder().build();
    final GetResult getResult2 = new GetResultBuilder().build();
    final GetResult getResult3 = new GetResultBuilder().build();

    when(this.helperService.getAndCheck(
            same(this.connectionHolder),
            same(device),
            eq("retrieve firmware versions"),
            refEq(new AttributeAddress(CLASS_ID, OBIS_CODE_ACTIVE_FIRMWARE_VERSION, ATTRIBUTE_ID)),
            refEq(
                new AttributeAddress(
                    CLASS_ID, OBIS_CODE_MODULE_ACTIVE_FIRMWARE_VERSION, ATTRIBUTE_ID)),
            refEq(
                new AttributeAddress(
                    CLASS_ID,
                    OBIS_CODE_COMMUNICATION_MODULE_ACTIVE_FIRMWARE_VERSION,
                    ATTRIBUTE_ID))))
        .thenReturn(asList(getResult1, getResult2, getResult3));
    when(this.helperService.readString(
            getResult1.getResultData(), FirmwareModuleType.ACTIVE_FIRMWARE.getDescription()))
        .thenReturn("string1");
    when(this.helperService.readString(
            getResult2.getResultData(), FirmwareModuleType.MODULE_ACTIVE.getDescription()))
        .thenReturn("string2");
    when(this.helperService.readString(
            getResult3.getResultData(), FirmwareModuleType.COMMUNICATION.getDescription()))
        .thenReturn("string3");

    final List<FirmwareVersionDto> result =
        this.executor.execute(this.connectionHolder, device, null, this.messageMetadata);

    Assertions.assertThat(result)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactly(
            new FirmwareVersionDto(FirmwareModuleType.ACTIVE_FIRMWARE, "string1"),
            new FirmwareVersionDto(FirmwareModuleType.MODULE_ACTIVE, "string2"),
            new FirmwareVersionDto(FirmwareModuleType.COMMUNICATION, "string3"));
  }

  @Test
  public void returns4FirmwareVersionsForSmr51Device() throws Exception {
    final DlmsDevice device = new DlmsDevice();
    device.setProtocol(Protocol.SMR_5_1);

    final GetResult getResult1 = new GetResultBuilder().build();
    final GetResult getResult2 = new GetResultBuilder().build();
    final GetResult getResult3 = new GetResultBuilder().build();
    final GetResult getResult4 = new GetResultBuilder().build();

    when(this.helperService.getAndCheck(
            same(this.connectionHolder),
            same(device),
            eq("retrieve firmware versions"),
            refEq(new AttributeAddress(CLASS_ID, OBIS_CODE_ACTIVE_FIRMWARE_VERSION, ATTRIBUTE_ID)),
            refEq(
                new AttributeAddress(
                    CLASS_ID, OBIS_CODE_MODULE_ACTIVE_FIRMWARE_VERSION, ATTRIBUTE_ID)),
            refEq(
                new AttributeAddress(
                    CLASS_ID,
                    OBIS_CODE_COMMUNICATION_MODULE_ACTIVE_FIRMWARE_VERSION,
                    ATTRIBUTE_ID)),
            refEq(
                new AttributeAddress(
                    CLASS_ID, OBIS_CODE_MBUS_DRIVER_ACTIVE_FIRMWARE_VERSION, ATTRIBUTE_ID))))
        .thenReturn(asList(getResult1, getResult2, getResult3, getResult4));
    when(this.helperService.readString(
            getResult1.getResultData(), FirmwareModuleType.ACTIVE_FIRMWARE.getDescription()))
        .thenReturn("string1");
    when(this.helperService.readString(
            getResult2.getResultData(), FirmwareModuleType.MODULE_ACTIVE.getDescription()))
        .thenReturn("string2");
    when(this.helperService.readString(
            getResult3.getResultData(), FirmwareModuleType.COMMUNICATION.getDescription()))
        .thenReturn("string3");
    when(this.helperService.readString(
            getResult4.getResultData(), FirmwareModuleType.M_BUS_DRIVER_ACTIVE.getDescription()))
        .thenReturn("string4");

    final List<FirmwareVersionDto> result =
        this.executor.execute(this.connectionHolder, device, null, this.messageMetadata);

    Assertions.assertThat(result)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactly(
            new FirmwareVersionDto(FirmwareModuleType.ACTIVE_FIRMWARE, "string1"),
            new FirmwareVersionDto(FirmwareModuleType.MODULE_ACTIVE, "string2"),
            new FirmwareVersionDto(FirmwareModuleType.COMMUNICATION, "string3"),
            new FirmwareVersionDto(FirmwareModuleType.M_BUS_DRIVER_ACTIVE, "string4"));
  }
}
