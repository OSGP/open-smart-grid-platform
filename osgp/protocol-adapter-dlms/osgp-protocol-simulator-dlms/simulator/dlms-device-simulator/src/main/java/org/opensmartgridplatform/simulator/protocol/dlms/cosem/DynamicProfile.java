// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.cosem;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.IllegalAttributeAccessException;
import org.openmuc.jdlms.SelectiveAccessDescription;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ProfileGenericAttribute;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.GsmDiagnostic.CellInfo;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.CaptureObjectDefinition;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.CaptureObjectDefinitionCollection;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.CosemDateTimeProcessor;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.DataProcessor;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.GsmDiagnosticCellInfoProcessor;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.Integer32DataProcessor;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.UInteger16DataProcessor;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.UInteger32DataProcessor;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.UInteger8DataProcessor;
import org.opensmartgridplatform.simulator.protocol.dlms.util.DynamicValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DynamicProfile extends ProfileGeneric {
  private static final Logger LOGGER = LoggerFactory.getLogger(DynamicProfile.class);

  private static final int MAX_PROFILE_ENTRIES = 960;

  protected static final DataProcessor COSEM_DATE_TIME_PROCESSOR = new CosemDateTimeProcessor();
  protected static final DataProcessor GSM_DIAGNOSTIC_CELL_INFO_PROCESSOR =
      new GsmDiagnosticCellInfoProcessor();
  protected static final DataProcessor UNSIGNED_PROCESSOR = new UInteger8DataProcessor();
  protected static final DataProcessor LONG_UNSIGNED_PROCESSOR = new UInteger16DataProcessor();
  protected static final DataProcessor DOUBLE_LONG_PROCESSOR = new Integer32DataProcessor();
  protected static final DataProcessor DOUBLE_LONG_UNSIGNED_PROCESSOR =
      new UInteger32DataProcessor();

  private DataObject captureObjects;
  private final DynamicValues dynamicValues;
  private final Map<CaptureObject, DataProcessor> dataProcessorByCaptureObject;
  private final Calendar time;
  private final Integer maxNumberOfCaptureObjects;

  private final SecureRandom random = new SecureRandom();

  public DynamicProfile(
      final String instanceId,
      final DynamicValues dynamicValues,
      final Calendar time,
      final Integer maxNumberOfCaptureObjects,
      final List<CaptureObject> captureObjectList,
      final Map<CaptureObject, DataProcessor> dataProcessorByCaptureObject) {
    super(instanceId);
    this.dynamicValues = dynamicValues;
    this.maxNumberOfCaptureObjects = maxNumberOfCaptureObjects;
    this.time = time;
    this.dataProcessorByCaptureObject = dataProcessorByCaptureObject;

    this.captureObjects = this.newCaptureObjectsData(captureObjectList);
  }

  protected DataObject newCaptureObjectsData(final List<CaptureObject> captureObjectList) {
    final List<DataObject> dataObjectList = new ArrayList<>();
    for (final CaptureObject captureObject : captureObjectList) {
      dataObjectList.add(captureObject.asDataObject());
    }
    return DataObject.newArrayData(dataObjectList);
  }

  /** Initializes buffer with some data. */
  private void initBufferData() {

    final long numberOfProfileEntries = this.getProfileEntries().getValue();

    this.bufferData = new CircularFifoQueue<>((int) numberOfProfileEntries);

    final List<CaptureObject> captureObjectDefinitions = this.getCaptureObjectDefinitions();
    final int numberOfCaptureObjects = captureObjectDefinitions.size();
    final long capturePeriodSeconds = this.getCapturePeriod().getValue();

    for (int i = 0; i < numberOfProfileEntries; i++) {
      final Calendar cal = this.getNextDateTime((int) capturePeriodSeconds);
      final List<Object> profileEntryList = new ArrayList<>();
      profileEntryList.add(cal);
      for (int j = 1; j < numberOfCaptureObjects; j++) {
        final CaptureObject captureObject = captureObjectDefinitions.get(j);
        this.addProfileEntry(profileEntryList, captureObject, cal, this.random);
      }
      this.bufferData.add(profileEntryList);
    }
  }

  private void addProfileEntry(
      final List<Object> profileEntryList,
      final CaptureObject captureObject,
      final Calendar profileEntryTime,
      final Random random) {

    final DataProcessor processor = this.dataProcessorByCaptureObject.get(captureObject);
    if (processor instanceof CosemDateTimeProcessor) {
      profileEntryList.add(profileEntryTime);
    } else if (processor instanceof GsmDiagnosticCellInfoProcessor) {
      /*
       * Signal quality: 0-31 or 99
       * ber: 0-7 or 99
       */
      final CellInfo cellInfo =
          new CellInfo(1L, 1, (short) random.nextInt(31), (short) random.nextInt(7), 1, 1, 1);
      profileEntryList.add(cellInfo);
    } else if (processor instanceof UInteger8DataProcessor) {
      /*
       * Random value in the range of valid unsigned values [0 ..
       * 0xFF]
       */
      profileEntryList.add((short) random.nextInt(0xFF + 1));
    } else if (processor instanceof UInteger16DataProcessor) {
      /*
       * Random value in the range of valid long-unsigned values [0 ..
       * 0xFFFF]
       */
      profileEntryList.add(random.nextInt(0xFFFF + 1));
    } else if (processor instanceof Integer32DataProcessor) {
      /*
       * Random value in the range of valid double-long values (any int)
       */
      int next = random.nextInt();
      if (random.nextBoolean()) {
        next = -next;
      }
      profileEntryList.add(next);
    } else if (processor instanceof UInteger32DataProcessor) {
      /*
       * Random value in the range of valid double-long-unsigned values [0
       * .. 0xFFFFFFFFL]
       */
      profileEntryList.add(this.randomDoubleLongUnsigned());
    } else {
      throw new IllegalArgumentException("Unknown data processor class: " + processor.getClass());
    }
  }

  private long randomDoubleLongUnsigned() {
    final long minValue = 0;
    final long maxValue = 0xFFFFFFFFL;
    return minValue + (long) (this.random.nextDouble() * (maxValue - minValue));
  }

  private List<CaptureObject> getCaptureObjectDefinitions() {
    final List<DataObject> captureObjectList = this.captureObjects.getValue();
    final List<CaptureObject> captureObjectDefinitions = new ArrayList<>();
    for (final DataObject captureObject : captureObjectList) {
      captureObjectDefinitions.add(CaptureObject.newCaptureObject(captureObject));
    }
    return captureObjectDefinitions;
  }

  private Calendar getNextDateTime(final int capturePeriodSeconds) {
    final Calendar next = (Calendar) this.time.clone();
    this.time.add(Calendar.SECOND, capturePeriodSeconds);
    return next;
  }

  @Override
  protected CaptureObjectDefinitionCollection getCaptureObjectDefinitionCollection() {
    final List<DataObject> dataObjectList = this.captureObjects.getValue();
    final CaptureObjectDefinitionCollection definitions = new CaptureObjectDefinitionCollection();
    for (final DataObject dataObject : dataObjectList) {
      final CaptureObject captureObject = CaptureObject.newCaptureObject(dataObject);
      definitions.add(
          new CaptureObjectDefinition(
              captureObject, this.dataProcessorByCaptureObject.get(captureObject)));
    }
    return definitions;
  }

  @Override
  public DataObject getBuffer(final SelectiveAccessDescription selectiveAccessDescription) {
    if (this.bufferData == null) {
      this.initBufferData();
    }
    this.initBufferData();
    return super.getBuffer(selectiveAccessDescription);
  }

  @Override
  public DataObject getCaptureObjects() {
    return this.captureObjects;
  }

  public void setCaptureObjects(final DataObject captureObjects)
      throws IllegalAttributeAccessException {
    final List<DataObject> captureObjectList = captureObjects.getValue();
    final int numberOfCaptureObjects = captureObjectList.size();
    if (this.maxNumberOfCaptureObjects != null
        && numberOfCaptureObjects > this.maxNumberOfCaptureObjects) {
      LOGGER.error(
          "Number of capture objects larger than supported (max {}): {}",
          this.maxNumberOfCaptureObjects,
          numberOfCaptureObjects);
      throw new IllegalAttributeAccessException(
          AccessResultCode.OTHER_REASON,
          new IllegalArgumentException(
              "Number of capture objects larger than supported (max "
                  + this.maxNumberOfCaptureObjects
                  + "): "
                  + numberOfCaptureObjects));
    }
    this.reinitializeCaptureObjects(captureObjectList);
    this.captureObjects = captureObjects;
    /*
     * Setting the capture objects has an effect on the buffer. Make sure
     * the buffer will be reinitialized when getBuffer is called.
     */
    this.bufferData = null;
  }

  private void reinitializeCaptureObjects(final List<DataObject> captureObjectList)
      throws IllegalAttributeAccessException {
    for (final DataObject captureObject : captureObjectList) {
      final CaptureObject initCaptureObject;
      try {
        initCaptureObject = CaptureObject.newCaptureObject(captureObject);
      } catch (final RuntimeException e) {
        throw new IllegalAttributeAccessException(
            AccessResultCode.OTHER_REASON,
            new IllegalArgumentException(
                "Unable to create capture object from: " + captureObject, e));
      }
      if (!this.dataProcessorByCaptureObject.containsKey(initCaptureObject)) {
        LOGGER.error("No data processor configured for {}", initCaptureObject);
        throw new IllegalAttributeAccessException(
            AccessResultCode.OTHER_REASON,
            new IllegalArgumentException("No data processor configured for " + initCaptureObject));
      }
    }
  }

  public DataObject getCapturePeriod() {
    return this.dynamicValues.getDlmsAttributeValue(
        this, ProfileGenericAttribute.CAPTURE_PERIOD.attributeId());
  }

  public void setCapturePeriod(final DataObject capturePeriod) {
    this.dynamicValues.setDlmsAttributeValue(
        this, ProfileGenericAttribute.CAPTURE_PERIOD.attributeId(), capturePeriod);
  }

  public DataObject getProfileEntries() {
    return this.dynamicValues.getDlmsAttributeValue(
        this, ProfileGenericAttribute.PROFILE_ENTRIES.attributeId());
  }

  public void setProfileEntries(final DataObject profileEntries)
      throws IllegalAttributeAccessException {
    final long numberOfProfileEntries = profileEntries.getValue();
    if (numberOfProfileEntries > MAX_PROFILE_ENTRIES) {
      LOGGER.error(
          "Number of profile entries larger than supported (max {}): {}",
          MAX_PROFILE_ENTRIES,
          numberOfProfileEntries);
      throw new IllegalAttributeAccessException(
          AccessResultCode.OTHER_REASON,
          new IllegalArgumentException(
              "Number of profile entries larger than supported (max "
                  + MAX_PROFILE_ENTRIES
                  + "): "
                  + numberOfProfileEntries));
    }
    this.dynamicValues.setDlmsAttributeValue(
        this, ProfileGenericAttribute.PROFILE_ENTRIES.attributeId(), profileEntries);
    /*
     * Setting the number of profile entries has an effect on the buffer.
     * Make sure the buffer will be reinitialized when getBuffer is called.
     */
    this.bufferData = null;
  }
}
