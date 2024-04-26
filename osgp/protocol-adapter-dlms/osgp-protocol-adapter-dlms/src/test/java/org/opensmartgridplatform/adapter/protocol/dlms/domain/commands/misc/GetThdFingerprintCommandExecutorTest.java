// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.openmuc.jdlms.AccessResultCode.SUCCESS;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.GetResultImpl;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.NotSupportedByProtocolException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class GetThdFingerprintCommandExecutorTest {

  private GetThdFingerprintCommandExecutor executor;

  @Mock private DlmsMessageListener dlmsMessageListener;

  @Mock private DlmsConnectionManager connectionManager;

  @Mock private DlmsConnection dlmsConnection;

  private static final String OBIS_CODE = "0.1.94.31.0.255";

  private static final int CLASS_ID = 1;

  private static final int ATTRIBUTE_ID = 2;

  @BeforeEach
  public void setUp() throws IOException, ObjectConfigException {
    final ObjectConfigService objectConfigService = new ObjectConfigService();
    final ObjectConfigServiceHelper objectConfigServiceHelper =
        new ObjectConfigServiceHelper(objectConfigService);
    final DlmsHelper dlmsHelper = new DlmsHelper();
    this.executor = new GetThdFingerprintCommandExecutor(objectConfigServiceHelper, dlmsHelper);
  }

  @Test
  void testExecute() throws Exception {
    final DlmsDevice testDevice = new DlmsDevice();
    testDevice.setProtocol(Protocol.SMR_5_2);
    testDevice.setWithListMax(10);

    final GetResultImpl resultCurrentL1 = this.createValueResult(1, SUCCESS);
    final GetResultImpl resultCurrentL2 = this.createValueResult(2, SUCCESS);
    final GetResultImpl resultCurrentL3 = this.createValueResult(3, SUCCESS);

    final GetResultImpl resultFingerprintL1 = this.createFingerprintResult(100, 15, SUCCESS);
    final GetResultImpl resultFingerprintL2 = this.createFingerprintResult(200, 15, SUCCESS);
    final GetResultImpl resultFingerprintL3 = this.createFingerprintResult(300, 15, SUCCESS);

    final GetResultImpl resultCounterL1 = this.createValueResult(10, SUCCESS);
    final GetResultImpl resultCounterL2 = this.createValueResult(20, SUCCESS);
    final GetResultImpl resultCounterL3 = this.createValueResult(30, SUCCESS);

    when(this.connectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.connectionManager.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.get(ArgumentMatchers.anyList()))
        .thenReturn(
            List.of(
                resultCurrentL1,
                resultCurrentL2,
                resultCurrentL3,
                resultFingerprintL1,
                resultFingerprintL2,
                resultFingerprintL3,
                resultCounterL1,
                resultCounterL2,
                resultCounterL3));

    this.executor.execute(this.connectionManager, testDevice, null, mock(MessageMetadata.class));
  }

  @Test
  void testExecuteNoObject() throws Exception {

    final DlmsDevice testDevice = new DlmsDevice();
    testDevice.setProtocol(Protocol.DSMR_2_2);

    // CALL
    assertThrows(
        NotSupportedByProtocolException.class,
        () -> {
          this.executor.execute(
              this.connectionManager, testDevice, null, mock(MessageMetadata.class));
        });
  }

  private GetResultImpl createValueResult(final int value, final AccessResultCode resultCode) {
    return new GetResultImpl(DataObject.newUInteger16Data(value), resultCode);
  }

  private GetResultImpl createFingerprintResult(
      final int startValue, final int amount, final AccessResultCode resultCode) {

    final List<DataObject> values =
        IntStream.rangeClosed(1, amount)
            .boxed()
            .map(i -> DataObject.newUInteger16Data(startValue + i))
            .toList();

    final DataObject fingerprintArray = DataObject.newArrayData(values);
    return new GetResultImpl(fingerprintArray, resultCode);
  }
}
