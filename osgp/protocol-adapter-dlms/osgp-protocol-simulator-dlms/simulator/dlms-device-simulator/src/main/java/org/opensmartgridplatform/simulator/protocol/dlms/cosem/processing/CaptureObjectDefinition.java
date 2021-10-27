/*
 * Copyright 2016 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing;

import java.util.List;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.CaptureObject;

public class CaptureObjectDefinition {

  private final CaptureObject captureObject;

  private final DataProcessor processor;

  /**
   * Constructor
   *
   * @param captureObject Definition of the CaptureObject.
   * @param processor DataProcessor to provide conversion and filtering of data in the buffer.
   */
  public CaptureObjectDefinition(final CaptureObject captureObject, final DataProcessor processor) {
    this.captureObject = captureObject;
    this.processor = processor;
  }

  public CaptureObject getCaptureObject() {
    return this.captureObject;
  }

  public DataObject convert(final Object data) {
    return this.processor.create(data);
  }

  /**
   * Provides a new Filter.
   *
   * @param rangeDescriptor RangeDescriptor from SelectiveAccessDescription.
   * @return A new filter based on the given RangeDescriptor.
   */
  public RangeDescriptorFilter provideFilter(final List<DataObject> rangeDescriptor) {
    return this.processor.provide(rangeDescriptor);
  }

  DataObjectCreator getDataObjectConverter() {
    return this.processor;
  }
}
