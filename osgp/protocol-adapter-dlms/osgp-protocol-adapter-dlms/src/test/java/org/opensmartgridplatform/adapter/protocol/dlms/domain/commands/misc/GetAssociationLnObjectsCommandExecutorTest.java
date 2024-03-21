// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AssociationLnListElementDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AssociationLnListTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemObisCodeDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GetAssociationLnObjectsCommandExecutorTest {

  private GetAssociationLnObjectsCommandExecutor executor;

  @Mock private DlmsMessageListener dlmsMessageListener;

  @Mock private DlmsHelper dlmsHelper;

  @Mock private DlmsConnectionManager connectionManager;

  private static final String OBIS_CODE = "0.0.40.0.0.255";

  @BeforeEach
  public void setUp() throws IOException, ObjectConfigException {
    final ObjectConfigService objectConfigService = new ObjectConfigService();
    this.executor =
        new GetAssociationLnObjectsCommandExecutor(this.dlmsHelper, objectConfigService);

    when(this.connectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
  }

  @Test
  void testExecute() throws Exception {

    final DlmsDevice testDevice = new DlmsDevice();
    testDevice.setProtocol(Protocol.DSMR_4_2_2);

    final AttributeAddress attributeAddress = new AttributeAddress(15, this.OBIS_CODE, 2, null);

    // SETUP - mock return data objects
    final int nrOfObjects = 25;

    final int classId = 1;
    final int version = 2;
    final byte[] lnObjectObisBytes = new byte[] {1, 2, 3, 4, 5, 6};
    final String lnObjectObis = "1.2.3.4.5.6";

    final GetResult getResult = mock(GetResult.class);
    final DataObject classIdObj = DataObject.newUInteger16Data(classId);
    final DataObject versionObj = DataObject.newUInteger8Data((short) version);
    final DataObject obisObj = DataObject.newOctetStringData(lnObjectObisBytes);
    final DataObject accessObj = DataObject.newNullData();
    final DataObject lnObj =
        DataObject.newStructureData(List.of(classIdObj, versionObj, obisObj, accessObj));
    final DataObject resultData = DataObject.newArrayData(Collections.nCopies(nrOfObjects, lnObj));
    when(getResult.getResultData()).thenReturn(resultData);

    when(this.dlmsHelper.getAndCheck(
            eq(this.connectionManager), eq(testDevice), any(), refEq(attributeAddress)))
        .thenReturn(List.of(getResult));
    when(this.dlmsHelper.readLong(classIdObj, "classId")).thenReturn((long) classId);
    when(this.dlmsHelper.readLogicalName(obisObj, "AssociationLN Element"))
        .thenReturn(new CosemObisCodeDto(lnObjectObis));

    // CALL
    final AssociationLnListTypeDto result =
        this.executor.execute(
            this.connectionManager, testDevice, null, mock(MessageMetadata.class));

    // VERIFY contents of the return value
    final List<AssociationLnListElementDto> lnObjects = result.getAssociationLnListElement();
    assertThat(lnObjects).hasSize(nrOfObjects);
    final AssociationLnListElementDto lnObject = lnObjects.get(0);
    assertThat(lnObject.getClassId()).isEqualTo(classId);
    assertThat(lnObject.getVersion()).isEqualTo(version);
    assertThat(lnObject.getLogicalName()).hasToString(lnObjectObis);
  }
}
