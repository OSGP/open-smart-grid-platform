// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.datadecoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.datadecoder.dlmsclassdatadecoder.DataExchangeClassesDecoder;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.datadecoder.dlmsclassdatadecoder.DlmsClassDataDecoder;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.datadecoder.dlmsclassdatadecoder.MeasurementDataClassesDecoder;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.datadecoder.dlmsclassdatadecoder.TimeAndEventsClassesDecoder;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeType;
import org.opensmartgridplatform.dlms.objectconfig.AccessType;
import org.opensmartgridplatform.dlms.objectconfig.Attribute;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.DlmsDataType;
import org.opensmartgridplatform.dlms.objectconfig.DlmsProfile;

class DataDecoderTest {
  private static final List<CosemObject> OBJECT_LIST = createObjectList();
  private static final List<AttributeAccessItem> ATTRIBUTE_ACCESS_ITEMS =
      List.of(
          new AttributeAccessItem(1, AccessType.R),
          new AttributeAccessItem(2, AccessType.W),
          new AttributeAccessItem(3, AccessType.RW));
  private static final DlmsProfile DLMS_PROFILE = mock(DlmsProfile.class);
  private static final int CLASS_ID_DATA = 1;
  private static final int CLASS_ID_GSM_DIAGNOSTIC = 47;
  private static final int VERSION_0 = 0;
  private static final int VERSION_1 = 1;
  private static final int VERSION_2 = 2;
  private static final String OBIS_1 = "1.0.1.1.1.255";
  private static final byte[] OBIS_1_BYTES = new ObisCode(OBIS_1).bytes();
  private static final String OBIS_2 = "1.0.1.2.3.255";
  private static final byte[] OBIS_2_BYTES = new ObisCode(OBIS_2).bytes();
  private static final String OBIS_3 = "1.0.2.3.4.255";
  private static final byte[] OBIS_3_BYTES = new ObisCode(OBIS_3).bytes();

  private final DlmsHelper dlmsHelper = new DlmsHelper();
  private final BasicDlmsDataDecoder basicDlmsDataDecoder =
      new BasicDlmsDataDecoder(this.dlmsHelper);
  private final DataExchangeClassesDecoder dataExchangeClassesDecoder =
      new DataExchangeClassesDecoder(this.dlmsHelper, this.basicDlmsDataDecoder);
  private final MeasurementDataClassesDecoder measurementDataClassesDecoder =
      new MeasurementDataClassesDecoder(this.dlmsHelper, this.basicDlmsDataDecoder);
  private final TimeAndEventsClassesDecoder timeAndEventsClassesDecoder =
      new TimeAndEventsClassesDecoder(this.dlmsHelper, this.basicDlmsDataDecoder);
  private final DlmsClassDataDecoder dlmsClassDataDecoder =
      new DlmsClassDataDecoder(
          this.dlmsHelper,
          this.basicDlmsDataDecoder,
          this.dataExchangeClassesDecoder,
          this.measurementDataClassesDecoder,
          this.timeAndEventsClassesDecoder);
  private final ProfileDataDecoder profileDataDecoder = new ProfileDataDecoder(this.dlmsHelper);
  private final DataDecoder decoder =
      new DataDecoder(
          this.dlmsHelper,
          this.basicDlmsDataDecoder,
          this.dlmsClassDataDecoder,
          this.profileDataDecoder);

  @BeforeAll
  static void Setup() {
    when(DLMS_PROFILE.getObjects()).thenReturn(OBJECT_LIST);
  }

  @Test
  void testDecodeObjectDataWithUnknownObis() {
    final ObjectListElement objectListElement =
        new ObjectListElement(CLASS_ID_DATA, VERSION_0, "0.0.0.0.0.0", ATTRIBUTE_ACCESS_ITEMS);

    final List<DataObject> attributeData =
        List.of(mock(DataObject.class), mock(DataObject.class), mock(DataObject.class));

    final CosemObject result =
        this.decoder.decodeObjectData(objectListElement, attributeData, DLMS_PROFILE);

    this.assertResultObject(result, CLASS_ID_DATA, "DATA", null, -1, 0);
  }

  @Test
  void testDecodeObjectDataWithBasicDlmsDataTypes() {
    final ObjectListElement objectListElement =
        new ObjectListElement(CLASS_ID_DATA, VERSION_0, OBIS_1, ATTRIBUTE_ACCESS_ITEMS);

    final List<DataObject> attributeData =
        List.of(
            DataObject.newOctetStringData(OBIS_1_BYTES),
            DataObject.newBoolData(true),
            DataObject.newInteger8Data((byte) 1));

    final CosemObject result =
        this.decoder.decodeObjectData(objectListElement, attributeData, DLMS_PROFILE);

    this.assertResultObject(result, CLASS_ID_DATA, "DATA", "Descr 1", VERSION_0, 3);
    final List<Attribute> attributes = result.getAttributes();
    this.asserResultAttribute(attributes.get(0), 1, "attr1-1", OBIS_1, AccessType.R);
    this.asserResultAttribute(attributes.get(1), 2, "attr1-2", "true", AccessType.W);
    this.asserResultAttribute(attributes.get(2), 3, "attr1-3", "1", AccessType.RW);
  }

  @Test
  void testDecodeObjectDataWithSpecialDlmsClassDataTypes() {
    final ObjectListElement objectListElement =
        new ObjectListElement(CLASS_ID_GSM_DIAGNOSTIC, VERSION_1, OBIS_2, ATTRIBUTE_ACCESS_ITEMS);

    final List<DataObject> attributeData =
        List.of(
            DataObject.newOctetStringData(OBIS_2_BYTES),
            DataObject.newVisibleStringData("operator".getBytes()),
            DataObject.newEnumerateData(3));

    final CosemObject result =
        this.decoder.decodeObjectData(objectListElement, attributeData, DLMS_PROFILE);

    this.assertResultObject(
        result, CLASS_ID_GSM_DIAGNOSTIC, "GSM_DIAGNOSTIC", "Descr 2", VERSION_1, 3);
    final List<Attribute> attributes = result.getAttributes();
    this.asserResultAttribute(attributes.get(0), 1, "attr2-1", OBIS_2, AccessType.R);
    this.asserResultAttribute(attributes.get(1), 2, "attr2-2", "operator", AccessType.W);
    this.asserResultAttribute(
        attributes.get(2), 3, "attr2-3", "REGISTRATION_DENIED", AccessType.RW);
  }

  @Test
  void testDecodeObjectDataWithSpecialProfileDataType() {
    final ObjectListElement objectListElement =
        new ObjectListElement(CLASS_ID_DATA, VERSION_2, OBIS_3, ATTRIBUTE_ACCESS_ITEMS);

    final List<DataObject> attributeData =
        List.of(
            DataObject.newOctetStringData(OBIS_3_BYTES),
            DataObject.newOctetStringData(new byte[] {10, 11, 12, 13, 14, (byte) 255}));

    final CosemObject result =
        this.decoder.decodeObjectData(objectListElement, attributeData, DLMS_PROFILE);

    this.assertResultObject(result, CLASS_ID_DATA, "DATA", "Descr 3", VERSION_2, 2);
    final List<Attribute> attributes = result.getAttributes();
    this.asserResultAttribute(attributes.get(0), 1, "attr3-1", OBIS_3, AccessType.R);
    this.asserResultAttribute(attributes.get(1), 2, "attr3-2", "0a0b0c0d0eff", AccessType.W);
  }

  private static List<CosemObject> createObjectList() {
    final List<Attribute> attributes1 =
        List.of(
            createAttribute(1, "attr1-1", DlmsDataType.OCTET_STRING, null),
            createAttribute(2, "attr1-2", DlmsDataType.BOOLEAN, null),
            createAttribute(3, "attr1-3", DlmsDataType.INTEGER, null));
    final CosemObject object1 =
        new CosemObject(null, "Descr 1", 1, 0, OBIS_1, null, null, null, null, attributes1);
    final List<Attribute> attributes2 =
        List.of(
            createAttribute(1, "attr2-1", DlmsDataType.OCTET_STRING, null),
            createAttribute(2, "attr2-2", DlmsDataType.VISIBLE_STRING, null),
            createAttribute(3, "attr2-3", null, AttributeType.MODEM_REGISTRATION_STATUS));
    final CosemObject object2 =
        new CosemObject(null, "Descr 2", 47, 1, OBIS_2, null, null, null, null, attributes2);
    final List<Attribute> attributes3 =
        List.of(
            createAttribute(1, "attr3-1", DlmsDataType.OCTET_STRING, null),
            createAttribute(2, "attr3-2", null, AttributeType.SIGNATURE));
    final CosemObject object3 =
        new CosemObject(null, "Descr 3", 1, 2, OBIS_3, null, null, null, null, attributes3);

    return List.of(object1, object2, object3);
  }

  private static Attribute createAttribute(
      final int id, final String descr, final DlmsDataType dataType, final AttributeType type) {
    return new Attribute(id, descr, null, dataType, type, null, null, null, null, null);
  }

  private void assertResultObject(
      final CosemObject object,
      final int classId,
      final String dlmsClass,
      final String description,
      final int version,
      final int expectedNumberOfAttributes) {
    assertThat(object.getClassId()).isEqualTo(classId);
    assertThat(object.getDlmsClass().name()).isEqualTo(dlmsClass);
    assertThat(object.getDescription()).isEqualTo(description);
    assertThat(object.getVersion()).isEqualTo(version);
    if (expectedNumberOfAttributes > 0) {
      assertThat(object.getAttributes()).hasSize(expectedNumberOfAttributes);
    } else {
      assertThat(object.getAttributes()).isNull();
    }
  }

  private void asserResultAttribute(
      final Attribute attribute,
      final int id,
      final String descr,
      final String value,
      final AccessType access) {
    assertThat(attribute.getId()).isEqualTo(id);
    assertThat(attribute.getDescription()).isEqualTo(descr);
    assertThat(attribute.getValue()).isEqualTo(value);
    assertThat(attribute.getAccess()).isEqualTo(access);
  }
}
