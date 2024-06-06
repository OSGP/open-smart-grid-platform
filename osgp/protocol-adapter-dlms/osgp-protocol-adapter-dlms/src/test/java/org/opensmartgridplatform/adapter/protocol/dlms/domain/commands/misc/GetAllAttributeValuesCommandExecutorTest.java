// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.GetResultImpl;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.datadecoder.BasicDlmsDataDecoder;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.datadecoder.DataDecoder;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.datadecoder.ProfileDataDecoder;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.datadecoder.dlmsclassdatadecoder.DataExchangeClassesDecoder;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.datadecoder.dlmsclassdatadecoder.DlmsClassDataDecoder;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.datadecoder.dlmsclassdatadecoder.MeasurementDataClassesDecoder;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.datadecoder.dlmsclassdatadecoder.TimeAndEventsClassesDecoder;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class GetAllAttributeValuesCommandExecutorTest {
  private static final int CLASS_ID_REGISTER = 3;
  private static final int CLASS_ID_DATA = 1;
  private static final int VERSION_0 = 0;
  private static final int VERSION_1 = 1;
  private static final int NO_OF_ATTR_REGISTER = 3;
  private static final int NO_OF_ATTR_DATA = 2;
  private static final String KNOWN_OBIS = "1.0.1.8.0.255";
  private static final String UNKNOWN_OBIS = "1.1.1.1.1.255";
  private static final DlmsDevice DEVICE = createDevice(Protocol.SMR_5_2);

  private GetAllAttributeValuesCommandExecutor executor;

  @Mock private DlmsMessageListener dlmsMessageListener;

  @Mock private DlmsConnectionManager connectionManager;

  @Mock private DlmsConnection connection;

  private static final MessageMetadata MSG_METADATA =
      MessageMetadata.newBuilder().withCorrelationUid("123456").build();

  @BeforeEach
  public void setUp() throws IOException, ObjectConfigException {
    final DlmsHelper dlmsHelper = new DlmsHelper();
    final ObjectConfigService objectConfigService = new ObjectConfigService();
    final ObjectConfigServiceHelper objectConfigServiceHelper =
        new ObjectConfigServiceHelper(objectConfigService);
    final BasicDlmsDataDecoder basicDlmsDataDecoder = new BasicDlmsDataDecoder(dlmsHelper);
    final DataExchangeClassesDecoder dataExchangeClassesDecoder =
        new DataExchangeClassesDecoder(dlmsHelper);
    final MeasurementDataClassesDecoder measurementDataClassesDecoder =
        new MeasurementDataClassesDecoder(dlmsHelper);
    final TimeAndEventsClassesDecoder timeAndEventsClassesDecoder =
        new TimeAndEventsClassesDecoder(dlmsHelper, basicDlmsDataDecoder);
    final DlmsClassDataDecoder dlmsClassDataDecoder =
        new DlmsClassDataDecoder(
            basicDlmsDataDecoder,
            dataExchangeClassesDecoder,
            measurementDataClassesDecoder,
            timeAndEventsClassesDecoder);
    final ProfileDataDecoder profileDataDecoder = new ProfileDataDecoder();
    final DataDecoder dataDecoder =
        new DataDecoder(dlmsHelper, basicDlmsDataDecoder, dlmsClassDataDecoder, profileDataDecoder);

    this.executor =
        new GetAllAttributeValuesCommandExecutor(
            objectConfigService, objectConfigServiceHelper, dlmsHelper, dataDecoder);

    when(this.connectionManager.getConnection()).thenReturn(this.connection);
    when(this.connectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
  }

  @Test
  void testOneKnownObjectAndOneUnknownObject() throws Exception {
    final DataObject objListElementForKnownObject =
        this.createObjectListElement(CLASS_ID_REGISTER, VERSION_0, KNOWN_OBIS, NO_OF_ATTR_REGISTER);
    final DataObject objListElementForUnknownObject =
        this.createObjectListElement(CLASS_ID_DATA, VERSION_1, UNKNOWN_OBIS, NO_OF_ATTR_DATA);
    final DataObject objectList =
        DataObject.newArrayData(
            List.of(objListElementForKnownObject, objListElementForUnknownObject));
    final GetResultImpl getResultObjectList =
        new GetResultImpl(objectList, AccessResultCode.SUCCESS);

    when(this.connectionManager.getConnection().get(any(AttributeAddress.class)))
        .thenReturn(getResultObjectList);

    when(this.connectionManager.getConnection().get(ArgumentMatchers.anyList()))
        .thenReturn(this.createGetResultsForAttributes(KNOWN_OBIS, 25, 1, 30))
        .thenReturn(this.createGetResultsForAttributes(UNKNOWN_OBIS, 26, null, null));

    final String result = this.executor.execute(this.connectionManager, DEVICE, null, MSG_METADATA);

    assertThat(replaceNewLinesWithSystemNewLines(result))
        .isEqualToIgnoringNewLines(
            """
[ {
  "description" : "Active energy import (+A)",
  "dlmsClass" : "REGISTER",
  "version" : 0,
  "obis" : "1.0.1.8.0.255",
  "attributes" : [ {
    "id" : 1,
    "description" : "logical_name",
    "rawValue" : "OCTET_STRING, ByteArray, value=[B]: logical name: 1-0:1.8.0.255",
    "value" : "1.0.1.8.0.255",
    "access" : "R"
  }, {
    "id" : 2,
    "description" : "value",
    "datatype" : "double-long-unsigned",
    "rawValue" : "LONG_UNSIGNED, Number, value=[Integer]: 25",
    "value" : "25",
    "access" : "W"
  }, {
    "id" : 3,
    "description" : "scaler_unit",
    "datatype" : "scal_unit_type",
    "rawValue" : "STRUCTURE, Complex, value=[Arrays$ArrayList]: [\\tLONG_UNSIGNED, Number, value=[Integer]: 1\\tLONG_UNSIGNED, Number, value=[Integer]: 30]",
    "value" : "1, WH",
    "access" : "RW"
  } ],
  "class-id" : 3
}, {
  "dlmsClass" : "DATA",
  "version" : 1,
  "obis" : "1.1.1.1.1.255",
  "attributes" : [ {
    "id" : 1,
    "description" : "logical_name",
    "rawValue" : "OCTET_STRING, ByteArray, value=[B]: logical name: 1-1:1.1.1.255",
    "value" : "1.1.1.1.1.255",
    "access" : "R"
  }, {
    "id" : 2,
    "description" : "value",
    "rawValue" : "LONG_UNSIGNED, Number, value=[Integer]: 26",
    "value" : "26",
    "access" : "W"
  } ],
  "class-id" : 1
} ]
""");
  }

  @Test
  void testDecodingFailsForObject() throws Exception {
    final DataObject objListElementForFailingObject =
        this.createObjectListElement(CLASS_ID_DATA, VERSION_0, KNOWN_OBIS, NO_OF_ATTR_DATA);
    final DataObject objectList = DataObject.newArrayData(List.of(objListElementForFailingObject));
    final GetResultImpl getResultObjectList =
        new GetResultImpl(objectList, AccessResultCode.SUCCESS);

    when(this.connectionManager.getConnection().get(any(AttributeAddress.class)))
        .thenReturn(getResultObjectList);

    // Object of class 1 has unexpectedly more attributes, causing a decoding failure
    when(this.connectionManager.getConnection().get(ArgumentMatchers.anyList()))
        .thenReturn(this.createGetResultsForAttributes(KNOWN_OBIS, 26, 2, 31));

    final String result = this.executor.execute(this.connectionManager, DEVICE, null, MSG_METADATA);

    assertThat(replaceNewLinesWithSystemNewLines(result))
        .isEqualToIgnoringNewLines(
            """
[ {
  "dlmsClass" : "DATA",
  "version" : -1,
  "obis" : "1.0.1.8.0.255",
  "note" : "Decoding failed, raw data: STRUCTURE, Complex, value=[ImmutableCollections$ListN]: [\\tOCTET_STRING, ByteArray, value=[B]: logical name: 1-0:1.8.0.255\\tLONG_UNSIGNED, Number, value=[Integer]: 26\\tSTRUCTURE, Complex, value=[Arrays$ArrayList]: [\\tLONG_UNSIGNED, Number, value=[Integer]: 2\\tLONG_UNSIGNED, Number, value=[Integer]: 31]]",
  "class-id" : 1
} ]
""");
  }

  @Test
  void testDecodingFailsForOneAttribute() throws Exception {
    final DataObject objListElementForKnownObject =
        this.createObjectListElement(CLASS_ID_REGISTER, VERSION_0, KNOWN_OBIS, NO_OF_ATTR_REGISTER);
    final DataObject objectList = DataObject.newArrayData(List.of(objListElementForKnownObject));
    final GetResultImpl getResultObjectList =
        new GetResultImpl(objectList, AccessResultCode.SUCCESS);

    when(this.connectionManager.getConnection().get(any(AttributeAddress.class)))
        .thenReturn(getResultObjectList);

    when(this.connectionManager.getConnection().get(ArgumentMatchers.anyList()))
        .thenReturn(this.createGetResultsForAttributes(KNOWN_OBIS, 25, 1, 30, true));

    final String result = this.executor.execute(this.connectionManager, DEVICE, null, MSG_METADATA);

    assertThat(this.replaceNewLinesWithSystemNewLines(result))
        .isEqualToIgnoringNewLines(
            """
[ {
  "description" : "Active energy import (+A)",
  "dlmsClass" : "REGISTER",
  "version" : 0,
  "obis" : "1.0.1.8.0.255",
  "attributes" : [ {
    "id" : 1,
    "description" : "logical_name",
    "rawValue" : "OCTET_STRING, ByteArray, value=[B]: logical name: 1-0:1.8.0.255",
    "value" : "1.0.1.8.0.255",
    "access" : "R"
  }, {
    "id" : 2,
    "description" : "value",
    "datatype" : "double-long-unsigned",
    "rawValue" : "LONG_UNSIGNED, Number, value=[Integer]: 25",
    "value" : "25",
    "access" : "W"
  }, {
    "id" : 3,
    "description" : "scaler_unit",
    "datatype" : "scal_unit_type",
    "rawValue" : "STRUCTURE, Complex, value=[Arrays$ArrayList]: [\\tBOOLEAN, Boolean, value=[Boolean]: false\\tLONG_UNSIGNED, Number, value=[Integer]: 30]",
    "value" : "decoding scaler unit failed: Expected ResultData of Number, got: BOOLEAN, value type: java.lang.Boolean",
    "access" : "RW"
  } ],
  "class-id" : 3
} ]
""");
  }

  private static DlmsDevice createDevice(final Protocol protocol) {
    final DlmsDevice device = new DlmsDevice();
    device.setProtocol(protocol);
    device.setWithListMax(32);
    return device;
  }

  private DataObject createObjectListElement(
      final int classId, final int version, final String obis, final int noOfAttr) {
    final List<DataObject> attributeAccessItems = new ArrayList<>();

    for (int id = 1; id <= noOfAttr; id++) {
      attributeAccessItems.add(
          DataObject.newStructureData(
              DataObject.newInteger8Data((byte) id),
              DataObject.newEnumerateData(
                  (id - 1 % 3) + 1), // creates different rights: R, W, RW, R, ...
              DataObject.newNullData()));
    }

    final DataObject accessRights =
        DataObject.newStructureData(
            DataObject.newArrayData(attributeAccessItems), DataObject.newArrayData(List.of()));

    return DataObject.newStructureData(
        DataObject.newUInteger16Data(classId),
        DataObject.newUInteger8Data((short) version),
        DataObject.newOctetStringData((new ObisCode(obis).bytes())),
        accessRights);
  }

  private List<GetResult> createGetResultsForAttributes(
      final String obis, final int value, final Integer scaler, final Integer unit) {
    return this.createGetResultsForAttributes(obis, value, scaler, unit, false);
  }

  private List<GetResult> createGetResultsForAttributes(
      final String obis,
      final int value,
      final Integer scaler,
      final Integer unit,
      final boolean createDecodingFailure) {
    final List<GetResult> getResults = new ArrayList<>();

    getResults.add(
        new GetResultImpl(
            DataObject.newOctetStringData((new ObisCode(obis).bytes())), AccessResultCode.SUCCESS));

    getResults.add(
        new GetResultImpl(DataObject.newUInteger16Data(value), AccessResultCode.SUCCESS));

    if (scaler != null) {
      DataObject scalerObject = DataObject.newUInteger16Data(scaler);
      final DataObject unitObject = DataObject.newUInteger16Data(unit);
      if (createDecodingFailure) {
        scalerObject = DataObject.newBoolData(false);
      }

      getResults.add(
          new GetResultImpl(
              DataObject.newStructureData(scalerObject, unitObject), AccessResultCode.SUCCESS));
    }

    return getResults;
  }

  private static String replaceNewLinesWithSystemNewLines(final String text) {
    final String lineSeparator = System.lineSeparator();
    return text.replace("\\r\\n", lineSeparator).replace("\\n", lineSeparator);
  }
}
