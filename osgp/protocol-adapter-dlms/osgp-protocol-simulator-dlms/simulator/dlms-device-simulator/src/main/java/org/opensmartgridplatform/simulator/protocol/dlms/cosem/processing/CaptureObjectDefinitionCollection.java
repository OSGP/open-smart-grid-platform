// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.CaptureObject;

public class CaptureObjectDefinitionCollection {

  List<CaptureObjectDefinition> captureObjectProcessors = new ArrayList<>();

  public void add(final CaptureObjectDefinition processor) {
    this.captureObjectProcessors.add(processor);
  }

  public DataObject captureObjectsAsDataObject() {
    final List<DataObject> initList = new ArrayList<>();
    final List<CaptureObject> captureObjects = this.allCaptureObjects();
    for (final CaptureObject captureObject : captureObjects) {
      initList.add(captureObject.asDataObject());
    }
    return DataObject.newArrayData(initList);
  }

  public DataObject filterAndConvertBufferData(
      final List<DataObject> rangeDescriptor, final Collection<List<?>> bufferData) {

    final CaptureObject restrictingObject = CaptureObject.newCaptureObject(rangeDescriptor.get(0));

    final int filterColumn = this.indexOf(restrictingObject);
    final CaptureObjectDefinition processor = this.definitionFor(restrictingObject);
    final RangeDescriptorFilter filter = processor.provideFilter(rangeDescriptor);

    final List<Integer> selectedIndexes = this.selectedIndexes(rangeDescriptor);

    return this.filteredDataObjects(filter, filterColumn, selectedIndexes, bufferData);
  }

  private List<Integer> selectedIndexes(final List<DataObject> rangeDescriptor) {
    final List<Integer> selectedIndexes = new ArrayList<>();
    final List<CaptureObject> selectedValues = this.selectedCaptureObjects(rangeDescriptor);
    final boolean addAll = selectedValues.isEmpty();
    for (int i = 0; i < this.captureObjectProcessors.size(); i++) {
      final CaptureObject captureObject = this.captureObjectProcessors.get(i).getCaptureObject();
      if (addAll || selectedValues.contains(captureObject)) {
        selectedIndexes.add(i);
      }
    }
    return selectedIndexes;
  }

  private List<CaptureObject> selectedCaptureObjects(final List<DataObject> rangeDescriptor) {
    final List<CaptureObject> selectedObjects = new ArrayList<>();
    final List<DataObject> selectedValues = rangeDescriptor.get(3).getValue();
    for (final DataObject selectedValue : selectedValues) {
      selectedObjects.add(CaptureObject.newCaptureObject(selectedValue));
    }
    return selectedObjects;
  }

  public DataObject convertBufferData(final Collection<List<?>> bufferData) {
    final List<Integer> selectedIndexes = new ArrayList<>();
    for (int i = 0; i < this.captureObjectProcessors.size(); i++) {
      selectedIndexes.add(i);
    }
    return this.filteredDataObjects(new DisabledFilter(), 0, selectedIndexes, bufferData);
  }

  private List<CaptureObject> allCaptureObjects() {
    final List<CaptureObject> list = new ArrayList<>();
    for (final CaptureObjectDefinition processor : this.captureObjectProcessors) {
      list.add(processor.getCaptureObject());
    }
    return list;
  }

  private CaptureObjectDefinition definitionFor(final CaptureObject captureObject) {
    final int index = this.indexOf(captureObject);

    if (index >= 0) {
      return this.captureObjectProcessors.get(index);
    }

    return null;
  }

  private int indexOf(final CaptureObject captureObject) {
    final int size = this.captureObjectProcessors.size();
    for (int i = 0; i < size; i++) {
      if (this.captureObjectProcessors.get(i).getCaptureObject().equals(captureObject)) {
        return i;
      }
    }

    return -1;
  }

  private List<DataObjectCreator> getDataObjectConverters() {
    final List<DataObjectCreator> list = new ArrayList<>();
    for (final CaptureObjectDefinition processor : this.captureObjectProcessors) {
      list.add(processor.getDataObjectConverter());
    }
    return list;
  }

  private DataObject filteredDataObjects(
      final RangeDescriptorFilter filter,
      final int filterColumn,
      final List<Integer> selectedIndexes,
      final Collection<List<?>> bufferData) {
    final List<DataObjectCreator> converters = this.getDataObjectConverters();

    final List<DataObject> result = new ArrayList<>();
    for (final List<?> bufferDataEntry : bufferData) {

      if (filter.match(bufferDataEntry.get(filterColumn))) {
        final List<DataObject> resultEntry = new ArrayList<>();
        for (final Integer selectedIndex : selectedIndexes) {
          resultEntry.add(converters.get(selectedIndex).create(bufferDataEntry.get(selectedIndex)));
        }
        result.add(DataObject.newStructureData(resultEntry));
      }
    }

    return DataObject.newArrayData(result);
  }
}
