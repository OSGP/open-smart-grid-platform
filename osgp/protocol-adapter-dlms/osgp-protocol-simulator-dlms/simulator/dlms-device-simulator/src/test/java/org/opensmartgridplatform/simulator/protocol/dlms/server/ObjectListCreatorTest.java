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

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.openmuc.jdlms.CosemInterfaceObject;
import org.opensmartgridplatform.domain.smartmetering.service.DlmsObjectService;

public class ObjectListCreatorTest {

  private final ObjectListCreator objectListCreator = new ObjectListCreator();

  private final DlmsObjectService dlmsObjectService = new DlmsObjectService();

  @Test
  void createObjectList() throws IOException {
    final List<CosemInterfaceObject> objectList =
        this.objectListCreator.create(this.dlmsObjectService);

    assertThat(objectList).hasSize(7);
  }
}
