// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.openmuc.jdlms.AccessResultCode.OTHER_REASON;
import static org.openmuc.jdlms.AccessResultCode.SUCCESS;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.NotSupportedByProtocolException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ThdConfigurationDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class SetThdConfigurationCommandExecutorTest {

  private static final String THD_VALUE_THRESHOLD_OBIS = "1.0.11.35.124.255";
  private static final String THD_VALUE_HYSTERESIS_OBIS = "0.1.94.31.21.255";
  private static final String THD_MIN_DURATION_NORMAL_TO_OVER_OBIS = "0.1.94.31.22.255";
  private static final String THD_MIN_DURATION_OVER_TO_NORMAL_OBIS = "0.1.94.31.23.255";
  private static final String THD_TIME_THRESHOLD_OBIS = "1.0.11.44.124.255";

  private static final int CLASS_ID_REGISTER = 3;
  private static final int ATTRIBUTE_ID_VALUE = 2;

  private static final int VALUE_THRESHOLD = 5;
  private static final int VALUE_HYSTERESIS = 10;
  private static final long DURATION_NORMAL_TO_OVER = 15;
  private static final long DURATION_OVER_TO_NORMAL = 20;
  private static final long TIME_THRESHOLD = 25;

  private static final ThdConfigurationDto REQUEST =
      new ThdConfigurationDto.Builder()
          .withValueThreshold(VALUE_THRESHOLD)
          .withValueHysteresis(VALUE_HYSTERESIS)
          .withMinDurationNormalToOver(DURATION_NORMAL_TO_OVER)
          .withMinDurationOverToNormal(DURATION_OVER_TO_NORMAL)
          .withTimeThreshold(TIME_THRESHOLD)
          .build();

  private static final MessageMetadata MSG_META_DATA =
      MessageMetadata.newBuilder().withCorrelationUid("123456").build();

  @Mock private DlmsHelper dlmsHelper;

  @Mock private DlmsConnectionManager conn;

  @Mock private DlmsMessageListener dlmsMessageListener;

  @Captor private ArgumentCaptor<List<SetParameter>> setParameterArgumentCaptor;

  private SetThdConfigurationCommandExecutor executor;

  @BeforeEach
  public void before() throws ProtocolAdapterException, IOException, ObjectConfigException {

    final ObjectConfigService objectConfigService = new ObjectConfigService();
    final ObjectConfigServiceHelper objectConfigServiceHelper =
        new ObjectConfigServiceHelper(objectConfigService);

    this.executor =
        new SetThdConfigurationCommandExecutor(this.dlmsHelper, objectConfigServiceHelper);
  }

  @Test
  void testSuccess() throws ProtocolAdapterException {
    final DlmsDevice device = this.createDlmsDevice(Protocol.SMR_5_2);

    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.dlmsHelper.setWithList(
            eq(this.conn), eq(device), this.setParameterArgumentCaptor.capture()))
        .thenReturn(List.of(SUCCESS));

    final AccessResultCode result =
        this.executor.execute(this.conn, device, REQUEST, MSG_META_DATA);

    assertThat(result).isEqualTo(SUCCESS);

    final List<SetParameter> setParameters = this.setParameterArgumentCaptor.getValue();
    this.verifySetParameter(setParameters.get(0), THD_VALUE_THRESHOLD_OBIS, VALUE_THRESHOLD);
    this.verifySetParameter(setParameters.get(1), THD_VALUE_HYSTERESIS_OBIS, VALUE_HYSTERESIS);
    this.verifySetParameter(
        setParameters.get(2), THD_MIN_DURATION_NORMAL_TO_OVER_OBIS, DURATION_NORMAL_TO_OVER);
    this.verifySetParameter(
        setParameters.get(3), THD_MIN_DURATION_OVER_TO_NORMAL_OBIS, DURATION_OVER_TO_NORMAL);
    this.verifySetParameter(setParameters.get(4), THD_TIME_THRESHOLD_OBIS, TIME_THRESHOLD);

    assertThat(setParameters).isNotNull();
  }

  @Test
  void testFailureMeterReturnsError() throws ProtocolAdapterException {
    final DlmsDevice device = this.createDlmsDevice(Protocol.SMR_5_2);

    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.dlmsHelper.setWithList(
            eq(this.conn), eq(device), this.setParameterArgumentCaptor.capture()))
        .thenReturn(List.of(SUCCESS, SUCCESS, SUCCESS, OTHER_REASON, SUCCESS));

    assertThrows(
        ProtocolAdapterException.class,
        () -> this.executor.execute(this.conn, device, REQUEST, MSG_META_DATA));
  }

  @ParameterizedTest
  @EnumSource(
      value = Protocol.class,
      names = {"SMR_5_2", "SMR_5_5"},
      mode = EnumSource.Mode.EXCLUDE)
  void testFailureWrongProtocol(final Protocol protocol) {
    final DlmsDevice device = this.createDlmsDevice(protocol);

    assertThrows(
        NotSupportedByProtocolException.class,
        () -> this.executor.execute(this.conn, device, REQUEST, MSG_META_DATA));
  }

  private void verifySetParameter(
      final SetParameter setParameter, final String obis, final long value) {
    assertAddress(setParameter, obis);
    assertThat(setParameter.getData())
        .isEqualToComparingFieldByField(DataObject.newUInteger32Data(value));
  }

  private void verifySetParameter(
      final SetParameter setParameter, final String obis, final int value) {
    assertAddress(setParameter, obis);
    assertThat(setParameter.getData())
        .isEqualToComparingFieldByField(DataObject.newUInteger16Data(value));
  }

  private static void assertAddress(final SetParameter setParameter, final String obis) {
    final AttributeAddress address = setParameter.getAttributeAddress();
    assertThat(address.getClassId()).isEqualTo(CLASS_ID_REGISTER);
    assertThat(address.getInstanceId().asDecimalString()).isEqualTo(obis);
    assertThat(address.getId()).isEqualTo(ATTRIBUTE_ID_VALUE);
    assertThat(address.getAccessSelection()).isNull();
  }

  private DlmsDevice createDlmsDevice(final Protocol protocol) {
    final DlmsDevice device = new DlmsDevice();
    device.setProtocol(protocol);
    return device;
  }
}
