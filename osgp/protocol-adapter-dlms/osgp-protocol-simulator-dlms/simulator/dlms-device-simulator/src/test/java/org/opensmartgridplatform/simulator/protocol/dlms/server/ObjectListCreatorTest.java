/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.server;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.openmuc.jdlms.CosemInterfaceObject;

public class ObjectListCreatorTest {

  private final ObjectListCreator objectListCreator = new ObjectListCreator();

  @Test
  void createObjectList() {
    final List<CosemInterfaceObject> objectList = this.objectListCreator.create();

    assertThat(objectList).hasSize(7);
  }
}
