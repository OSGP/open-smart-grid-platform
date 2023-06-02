//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.simulator.protocol.dlms.cosem.e650;

import java.util.Arrays;
import java.util.Calendar;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.openmuc.jdlms.AttributeAccessMode;
import org.openmuc.jdlms.CosemAttribute;
import org.openmuc.jdlms.CosemClass;
import org.openmuc.jdlms.SelectiveAccessDescription;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.CaptureObject;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.ProfileWithTime;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.SortMethod;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.CaptureObjectDefinition;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.CaptureObjectDefinitionCollection;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.CosemDateTimeProcessor;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.Integer64DataProcessor;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.OctetStringDataProcessor;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.UInteger32DataProcessor;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.UInteger8DataProcessor;

@CosemClass(id = 7, version = 1)
public class E650EventLog extends ProfileWithTime {

  private static final int PROFILE_ENTRIES = 64;

  private static final CaptureObjectDefinitionCollection CAPTURE_OBJECT_DEFINITIONS =
      initCaptureObjects();

  /**
   * Only for cosem attribute definition, data remains untouched. Attribute data is gathered from
   * {@link #bufferData}.
   */
  @CosemAttribute(
      id = 2,
      type = Type.ARRAY,
      accessMode = AttributeAccessMode.READ_ONLY,
      snOffset = 0x08,
      selector = {1, 2})
  private final DataObject buffer;

  /**
   * Only for cosem attribute definition, data remains untouched. Attribute data is gathered from
   * captureObjectDefinitions
   */
  @CosemAttribute(
      id = 3,
      type = Type.ARRAY,
      accessMode = AttributeAccessMode.READ_ONLY,
      snOffset = 0x10)
  private final DataObject captureObjects;

  @CosemAttribute(
      id = 4,
      type = Type.DOUBLE_LONG_UNSIGNED,
      accessMode = AttributeAccessMode.READ_ONLY,
      snOffset = 0x18)
  public DataObject capturePeriod;

  @CosemAttribute(
      id = 5,
      type = Type.ENUMERATE,
      accessMode = AttributeAccessMode.READ_ONLY,
      snOffset = 0x20)
  public DataObject sortMethod;

  @CosemAttribute(
      id = 6,
      type = Type.STRUCTURE,
      accessMode = AttributeAccessMode.READ_ONLY,
      snOffset = 0x28)
  public DataObject sortObject;

  /**
   * Only for cosem attribute definition, data remains untouched. Attribute data is gathered from
   * size of {@link #bufferData}
   */
  @CosemAttribute(
      id = 7,
      type = Type.DOUBLE_LONG_UNSIGNED,
      accessMode = AttributeAccessMode.READ_ONLY,
      snOffset = 0x30)
  private final DataObject entriesInUse;

  @CosemAttribute(
      id = 8,
      type = Type.DOUBLE_LONG_UNSIGNED,
      accessMode = AttributeAccessMode.READ_ONLY,
      snOffset = 0x38)
  public DataObject profileEntries;

  public E650EventLog(final Calendar time) {
    super(time, 0x60E0, "1.0.99.98.0.255");
    this.buffer = DataObject.newNullData();
    this.captureObjects = DataObject.newNullData();
    this.capturePeriod = DataObject.newUInteger32Data(CAPTURE_PERIOD);
    this.sortMethod = DataObject.newEnumerateData(SortMethod.FIFO.value());
    this.sortObject = DataObject.newNullData();
    this.entriesInUse = DataObject.newUInteger32Data(0);
    this.profileEntries = DataObject.newUInteger32Data(PROFILE_ENTRIES);

    this.initBufferData();
  }

  private static CaptureObjectDefinitionCollection initCaptureObjects() {
    final CaptureObjectDefinitionCollection definitions = new CaptureObjectDefinitionCollection();

    // Clock
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(8, "0.0.1.0.0.255", (byte) 2, 0), new CosemDateTimeProcessor()));

    // Event Register, EDIS_Status_EvLo
    final String eventRegisterObisCode = "0.0.96.240.12.255";
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(3, eventRegisterObisCode, (byte) 19, 0),
            new UInteger32DataProcessor()));

    // Event Register, CurrentValue
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(3, eventRegisterObisCode, (byte) 2, 0),
            new UInteger8DataProcessor()));

    // Event Register, EventStatus
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(3, eventRegisterObisCode, (byte) 12, 0),
            new OctetStringDataProcessor()));

    // Error Register, CurrentValue
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(3, "0.0.97.97.0.255", (byte) 2, 0), new OctetStringDataProcessor()));

    // EnergyTotalRegisterM1, CurrentValue
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(3, "1.1.1.8.0.255", (byte) 2, 0), new Integer64DataProcessor()));

    // EnergyTotalRegisterM4, CurrentValue
    definitions.add(
        new CaptureObjectDefinition(
            new CaptureObject(3, "1.1.2.8.0.255", (byte) 2, 0), new Integer64DataProcessor()));

    return definitions;
  }

  private void initBufferData() {

    this.bufferData = new CircularFifoQueue<>(PROFILE_ENTRIES);

    long edisStatusEvLo = 64;
    final short eventRegisterCurrentValue = (short) 24;
    final byte[] eventStatus = new byte[25];
    eventStatus[2] = (byte) 83;
    eventStatus[6] = (byte) 7;
    eventStatus[7] = (byte) 82;
    eventStatus[12] = (byte) 4;
    eventStatus[13] = (byte) 9;
    final byte[] errorRegisterCurrentValue = new byte[4];
    long energyTotalRegisterM1CurrentValue = 0L;
    long energyTotalRegisterM4CurrentValue = 0L;

    for (int i = 0; i < PROFILE_ENTRIES; i++) {
      final Calendar cal = this.getDateTime();
      this.forwardTime();
      eventStatus[0] = (byte) i;
      errorRegisterCurrentValue[1] = (byte) i;
      edisStatusEvLo++;
      energyTotalRegisterM1CurrentValue++;
      energyTotalRegisterM4CurrentValue += 4;
      this.bufferData.add(
          Arrays.asList(
              cal,
              edisStatusEvLo,
              eventRegisterCurrentValue,
              eventStatus,
              errorRegisterCurrentValue,
              energyTotalRegisterM1CurrentValue,
              energyTotalRegisterM4CurrentValue));
    }
  }

  @Override
  public DataObject getBuffer(final SelectiveAccessDescription selectiveAccessDescription) {
    if (this.bufferData == null) {
      this.initBufferData();
    }
    return super.getBuffer(selectiveAccessDescription);
  }

  @Override
  public DataObject getCaptureObjects() {
    return this.getCaptureObjectDefinitionCollection().captureObjectsAsDataObject();
  }

  @Override
  protected CaptureObjectDefinitionCollection getCaptureObjectDefinitionCollection() {
    return CAPTURE_OBJECT_DEFINITIONS;
  }
}
