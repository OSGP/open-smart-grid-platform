// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.application.mapping.ConfigurationMapper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AdministrativeStatusTypeDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class GetAdministrativeStatusCommandExecutorTest {

  private GetAdministrativeStatusCommandExecutor executor;

  @Mock private DlmsMessageListener dlmsMessageListener;

  @Mock private DlmsConnectionManager connectionManager;

  @Mock private DlmsConnection dlmsConnection;

  private static final String OBIS_CODE = "0.1.94.31.0.255";
  private static final int CLASS_ID = 1;
  private static final int ATTRIBUTE_ID = 2;

  @BeforeEach
  public void setUp() throws IOException, ObjectConfigException {
    final ConfigurationMapper configurationMapper = new ConfigurationMapper();
    final ObjectConfigService objectConfigService = new ObjectConfigService();
    this.executor =
        new GetAdministrativeStatusCommandExecutor(objectConfigService, configurationMapper);
  }

  @Test
  void testExecute() throws Exception {

    final DlmsDevice testDevice = new DlmsDevice();
    testDevice.setProtocol(Protocol.DSMR_4_2_2);

    final AttributeAddress attributeAddress =
        new AttributeAddress(CLASS_ID, this.OBIS_CODE, ATTRIBUTE_ID, null);
    final int enumValue = AdministrativeStatusTypeDto.ON.ordinal();

    final GetResult getResult = mock(GetResult.class);
    final DataObject enumObj = DataObject.newEnumerateData(enumValue);
    when(getResult.getResultData()).thenReturn(enumObj);

    when(this.connectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.connectionManager.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.get(refEq(attributeAddress))).thenReturn(getResult);

    // CALL
    final AdministrativeStatusTypeDto result =
        this.executor.execute(
            this.connectionManager, testDevice, null, mock(MessageMetadata.class));

    // VERIFY contents of the return value
    assertThat(result).isEqualTo(AdministrativeStatusTypeDto.ON);
  }

  @Test
  void testExecuteNoObject() throws Exception {

    final DlmsDevice testDevice = new DlmsDevice();
    testDevice.setProtocol(Protocol.DSMR_2_2);

    // CALL
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          this.executor.execute(
              this.connectionManager, testDevice, null, mock(MessageMetadata.class));
        });
  }
}
