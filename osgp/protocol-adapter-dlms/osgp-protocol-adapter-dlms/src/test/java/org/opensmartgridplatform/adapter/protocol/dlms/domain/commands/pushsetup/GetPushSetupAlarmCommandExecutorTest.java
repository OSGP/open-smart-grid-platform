package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.pushsetup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openmuc.jdlms.AccessResultCode.SUCCESS;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.pushsetup.GetPushSetupCommandExecutorTest.createExpectedAttributeAddresses;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.ObisCode;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemObjectDefinitionDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushSetupAlarmDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.TransportServiceTypeDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class GetPushSetupAlarmCommandExecutorTest extends GetPushSetupCommandExecutorTest {

  private static final ObisCode OBIS_CODE = new ObisCode("0.1.25.9.0.255");

  @Mock private DlmsConnectionManager conn;
  @Mock private DlmsMessageListener dlmsMessageListener;
  @Mock private DlmsConnection dlmsConnection;
  @InjectMocks GetPushSetupAlarmCommandExecutor executor;
  @Captor ArgumentCaptor<AttributeAddress[]> attributeAddressesCaptor;

  private MessageMetadata messageMetadata;

  @BeforeEach
  public void init() throws IOException, ObjectConfigException {
    this.messageMetadata = MessageMetadata.newBuilder().withCorrelationUid("123456").build();
    final ObjectConfigService objectConfigService = new ObjectConfigService();
    final ObjectConfigServiceHelper objectConfigServiceHelper =
        new ObjectConfigServiceHelper(objectConfigService);
    final DlmsHelper dlmsHelper = new DlmsHelper();
    this.executor = new GetPushSetupAlarmCommandExecutor(dlmsHelper, objectConfigServiceHelper);
  }

  @ParameterizedTest
  @EnumSource(
      value = Protocol.class,
      names = {"DSMR_2_2", "OTHER_PROTOCOL"},
      mode = Mode.EXCLUDE)
  void execute(final Protocol protocol) throws ProtocolAdapterException, IOException {

    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.conn.getConnection()).thenReturn(this.dlmsConnection);

    final PushSetupBuilder pushSetupBuilder =
        new PushSetupBuilder(SUCCESS, OBIS_CODE, TransportServiceTypeDto.TCP);
    when(this.dlmsConnection.get(ArgumentMatchers.anyList()))
        .thenReturn(
            List.of(
                pushSetupBuilder.buildPushObjectList(),
                pushSetupBuilder.buildSendDestinationAndMethod(),
                pushSetupBuilder.buildCommunicationWindow(),
                this.longUnsigned(1L, SUCCESS),
                this.unsigned(2, SUCCESS),
                this.longUnsigned(3L, SUCCESS)));

    final DlmsDevice device = new DlmsDevice();
    device.setProtocol(protocol);
    device.setWithListMax(32);
    final PushSetupAlarmDto result =
        this.executor.execute(this.conn, device, null, this.messageMetadata);

    this.assertWithListAddresses();
    this.assertResult(result);
  }

  @ParameterizedTest
  @EnumSource(
      value = Protocol.class,
      names = {"DSMR_2_2", "OTHER_PROTOCOL"},
      mode = Mode.INCLUDE)
  void protocolNotSupported(final Protocol protocol) {
    final DlmsDevice device = new DlmsDevice();
    device.setProtocol(protocol);
    assertThatExceptionOfType(ProtocolAdapterException.class)
        .isThrownBy(() -> this.executor.execute(this.conn, device, null, this.messageMetadata));
  }

  private void assertWithListAddresses() throws IOException {
    final List<AttributeAddress> expectedAttributeAddresses =
        createExpectedAttributeAddresses(OBIS_CODE);

    final ArgumentCaptor<List<AttributeAddress>> attributeAddressesCaptor =
        ArgumentCaptor.forClass(List.class);
    verify(this.dlmsConnection).get(attributeAddressesCaptor.capture());
    final List<AttributeAddress> capturedAttributeAddresses = attributeAddressesCaptor.getValue();

    assertThat(capturedAttributeAddresses).hasSize(6);
    assertThat(capturedAttributeAddresses)
        .usingRecursiveComparison()
        .isEqualTo(expectedAttributeAddresses);
  }

  private void assertResult(final PushSetupAlarmDto result) {
    final List<CosemObjectDefinitionDto> cosemObjectDefinitionDtos = result.getPushObjectList();
  }
}
