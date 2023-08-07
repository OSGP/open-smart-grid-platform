// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.monitoring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.AVERAGE_CURRENT_L1;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.AVERAGE_REACTIVE_POWER_IMPORT_L1;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.CLOCK;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.INSTANTANEOUS_VOLTAGE_L1;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.NUMBER_OF_VOLTAGE_SAGS_FOR_L1;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.NUMBER_OF_VOLTAGE_SAGS_FOR_L2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.GetResultImpl;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.ObjectConfigServiceHelper;
import org.opensmartgridplatform.dlms.objectconfig.Attribute;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dlms.objectconfig.ObjectProperty;
import org.opensmartgridplatform.dlms.objectconfig.PowerQualityRequest;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CaptureObjectDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetPowerQualityProfileResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PowerQualityProfileDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileEntryDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileEntryValueDto;

public abstract class GetPowerQualityProfileTest {
  protected static final String PROTOCOL_NAME = "SMR";
  protected static final String PROTOCOL_VERSION = "5.0.0";
  protected static final String PQ_DEFINABLE = "0.1.94.31.6.255";
  protected static final String PQ_PROFILE_1 = "0.1.99.1.1.255";
  protected static final String PQ_PROFILE_2 = "0.1.99.1.2.255";
  protected static final int PQ_DEFINABLE_INTERVAL = 15;
  protected static final int PQ_PROFILE_1_INTERVAL = 15;
  protected static final int PQ_PROFILE_2_INTERVAL = 10;
  private static final int CLASS_ID_DATA = 1;
  private static final int CLASS_ID_REGISTER = 3;
  private static final int CLASS_ID_PROFILE = 7;
  private static final int CLASS_ID_CLOCK = 8;
  private static final int PROFILE_CAPTURE_OBJECTS_ATTR_ID = 3;
  private static final int PROFILE_INTERVAL_ATTR_ID = 4;
  private static final int SECONDS_PER_MINUTE = 60;
  private static final String OBIS_INSTANTANEOUS_VOLTAGE_L1 = "1.0.32.7.0.255";
  private static final String OBIS_CLOCK = "0.0.1.0.0.255";
  private static final String UNIT_VOLT = "V";
  private static final String UNIT_UNDEFINED = "UNDEFINED";
  private static final int[] VALUES = new int[] {8, 7, 6, 5};

  public static CosemObject createObject(
      final int classId,
      final String obis,
      final String tag,
      final String scalerUnitValue,
      final boolean polyphase,
      final String publicOrPrivate) {

    final CosemObject object =
        ObjectConfigServiceHelper.createObject(classId, obis, tag, scalerUnitValue, polyphase);

    final Map<ObjectProperty, Object> properties = new HashMap<>();
    properties.put(ObjectProperty.PQ_PROFILE, publicOrPrivate);
    properties.put(
        ObjectProperty.PQ_REQUEST,
        List.of(PowerQualityRequest.PERIODIC_SP.name(), PowerQualityRequest.PERIODIC_PP.name()));
    object.setProperties(properties);

    return object;
  }

  protected Optional<CosemObject> createProfile(
      final String obis, final String tag, final int intervalInMinutes) {

    final CosemObject object =
        ObjectConfigServiceHelper.createObject(CLASS_ID_PROFILE, obis, tag, null, true);

    final Map<ObjectProperty, Object> properties = new HashMap<>();
    final List<String> stringProperties =
        this.getPropertyObjects().stream()
            .map(DlmsObjectType::toString)
            .collect(Collectors.toList());
    properties.put(ObjectProperty.SELECTABLE_OBJECTS, stringProperties);
    object.setProperties(properties);

    final List<Attribute> attributeList = new ArrayList<>();
    final Attribute attribute = new Attribute();
    attribute.setId(PROFILE_CAPTURE_OBJECTS_ATTR_ID);
    attribute.setValue("1");
    attributeList.add(attribute);
    final Attribute attributeInterval = new Attribute();
    attributeInterval.setId(PROFILE_INTERVAL_ATTR_ID);
    attributeInterval.setValue(String.valueOf(intervalInMinutes * SECONDS_PER_MINUTE));
    attributeList.add(attributeInterval);
    object.setAttributes(attributeList);

    return Optional.of(object);
  }

  protected List<CosemObject> getObjects(final boolean polyphase, final String publicOrPrivate) {
    final CosemObject clockObject =
        createObject(CLASS_ID_CLOCK, OBIS_CLOCK, CLOCK.name(), null, true, publicOrPrivate);

    final CosemObject dataObject =
        createObject(
            CLASS_ID_DATA,
            "1.0.0.0.0.1",
            NUMBER_OF_VOLTAGE_SAGS_FOR_L2.name(),
            null,
            polyphase,
            publicOrPrivate);

    final CosemObject registerVoltObject =
        createObject(
            CLASS_ID_REGISTER,
            OBIS_INSTANTANEOUS_VOLTAGE_L1,
            INSTANTANEOUS_VOLTAGE_L1.name(),
            "0, " + UNIT_VOLT,
            polyphase,
            publicOrPrivate);

    final CosemObject registerAmpereObject =
        createObject(
            CLASS_ID_REGISTER,
            "3.0.0.0.0.2",
            AVERAGE_CURRENT_L1.name(),
            "-1, A",
            polyphase,
            publicOrPrivate);

    final CosemObject registerVarObject =
        createObject(
            CLASS_ID_REGISTER,
            "3.0.0.0.0.3",
            AVERAGE_REACTIVE_POWER_IMPORT_L1.name(),
            "2, VAR",
            polyphase,
            publicOrPrivate);

    return new ArrayList<>(
        Arrays.asList(
            clockObject, dataObject, registerVoltObject, registerAmpereObject, registerVarObject));
  }

  protected void verifyResponseData(
      final GetPowerQualityProfileResponseDto response,
      final String obis,
      final int intervalInMinutes) {
    final Optional<PowerQualityProfileDataDto> profileData =
        response.getPowerQualityProfileResponseDatas().stream()
            .filter(data -> data.getLogicalName().toString().equals(obis))
            .findFirst();

    assertTrue(profileData.isPresent());
    assertThat(profileData.get().getCaptureObjects()).hasSize(2);

    final CaptureObjectDto captureObjectClock = profileData.get().getCaptureObjects().get(0);
    assertThat(captureObjectClock.getLogicalName()).isEqualTo(OBIS_CLOCK);
    assertThat(captureObjectClock.getUnit()).isEqualTo(UNIT_UNDEFINED);

    final CaptureObjectDto captureObject = profileData.get().getCaptureObjects().get(1);
    assertThat(captureObject.getLogicalName()).isEqualTo(OBIS_INSTANTANEOUS_VOLTAGE_L1);
    assertThat(captureObject.getUnit()).isEqualTo(UNIT_VOLT);

    final List<ProfileEntryDto> entries = profileData.get().getProfileEntries();
    assertThat(entries).hasSize(4);

    for (int index = 0; index < 4; index++) {
      this.verifyProfileEntry(entries.get(index), index, intervalInMinutes);
    }
  }

  private void verifyProfileEntry(
      final ProfileEntryDto entry, final int index, final int intervalInMinutes) {
    final List<ProfileEntryValueDto> values = entry.getProfileEntryValues();
    assertThat(values).hasSize(2);
    assertThat((Date) values.get(0).getValue())
        .isEqualTo(
            new DateTime(2023, 1, 12, 0, 0, 0, DateTimeZone.forID("Europe/Amsterdam"))
                .plusMinutes(index * intervalInMinutes)
                .toDate());
    assertThat((BigDecimal) values.get(1).getValue()).isEqualTo(BigDecimal.valueOf(VALUES[index]));
  }

  protected List<DlmsObjectType> getPropertyObjects() {
    final List<DlmsObjectType> objects = new ArrayList<>();
    objects.add(CLOCK);
    objects.add(INSTANTANEOUS_VOLTAGE_L1);
    objects.add(NUMBER_OF_VOLTAGE_SAGS_FOR_L1);
    objects.add(NUMBER_OF_VOLTAGE_SAGS_FOR_L2);
    objects.add(AVERAGE_CURRENT_L1);
    objects.add(AVERAGE_REACTIVE_POWER_IMPORT_L1);
    return objects;
  }

  protected List<GetResult> createProfileEntries(final boolean selectiveAccess) {
    // Timestamp in DLMS datetime format for January 1, 2023
    final byte[] timestamp =
        new byte[] {
          (byte) 0x07,
          (byte) 0xE7,
          (byte) 0x01,
          (byte) 0x0C,
          (byte) 0x04,
          (byte) 0x00,
          (byte) 0x00,
          (byte) 0x00,
          (byte) 0x00,
          (byte) 0xFF,
          (byte) 0xC4,
          (byte) 0x00
        };

    final DataObject entry1 = this.createEntry(timestamp, 2335, VALUES[0], 10, selectiveAccess);
    final DataObject entry2 = this.createEntry(null, 2336, VALUES[1], 11, selectiveAccess);
    final DataObject entry3 = this.createEntry(null, 2337, VALUES[2], 12, selectiveAccess);
    final DataObject entry4 = this.createEntry(null, 2338, VALUES[3], 13, selectiveAccess);

    final GetResult getResult =
        new GetResultImpl(DataObject.newArrayData(List.of(entry1, entry2, entry3, entry4)));

    return List.of(getResult);
  }

  private DataObject createEntry(
      final byte[] timestamp,
      final int value1,
      final int value2,
      final int value3,
      final boolean selectiveAccess) {
    final List<DataObject> values = new ArrayList<>();

    if (timestamp != null) {
      values.add(DataObject.newOctetStringData(timestamp));
    } else {
      values.add(DataObject.newNullData()); // null means timestamp is calculated
    }

    if (!selectiveAccess) {
      values.add(DataObject.newUInteger16Data(value1));
    }

    values.add(DataObject.newUInteger8Data((short) value2));

    if (!selectiveAccess) {
      values.add(DataObject.newUInteger16Data(value3));
    }

    return DataObject.newStructureData(values);
  }

  protected List<GetResult> createPartialNotAllowedCaptureObjects() {
    final DataObject allowedCaptureObjectClock =
        DataObject.newStructureData(
            DataObject.newUInteger32Data(8),
            DataObject.newOctetStringData(new byte[] {0, 0, 1, 0, 0, (byte) 255}),
            DataObject.newInteger32Data(2),
            DataObject.newUInteger32Data(0));

    final DataObject allowedCaptureObjectINSTANTANEOUS_VOLTAGE_L1 =
        DataObject.newStructureData(
            DataObject.newUInteger32Data(1),
            DataObject.newOctetStringData(new byte[] {1, 0, 32, 7, 0, (byte) 255}),
            DataObject.newInteger32Data(2),
            DataObject.newUInteger32Data(0));

    final DataObject nonAllowedCaptureObject1 =
        DataObject.newStructureData(
            DataObject.newUInteger32Data(1),
            DataObject.newOctetStringData(new byte[] {80, 0, 32, 32, 0, (byte) 255}),
            DataObject.newInteger32Data(2),
            DataObject.newUInteger32Data(0));
    final DataObject nonAllowedCaptureObject2 =
        DataObject.newStructureData(
            DataObject.newUInteger32Data(1),
            DataObject.newOctetStringData(new byte[] {1, 0, 52, 32, 0, (byte) 255}),
            DataObject.newInteger32Data(2),
            DataObject.newUInteger32Data(0));

    final GetResult getResult =
        new GetResultImpl(
            DataObject.newArrayData(
                List.of(
                    allowedCaptureObjectClock,
                    nonAllowedCaptureObject1,
                    allowedCaptureObjectINSTANTANEOUS_VOLTAGE_L1,
                    nonAllowedCaptureObject2)));

    return List.of(getResult);
  }
}
