// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.microgrids.valueobjects;

import java.io.Serializable;

public class NodeIdentifier implements Serializable {

  private static final long serialVersionUID = 3933967809366650885L;

  private final int id;
  private final String node;

  public NodeIdentifier(final int id, final String node) {
    super();
    this.id = id;
    this.node = node;
  }

  public int getId() {
    return this.id;
  }

  public String getNode() {
    return this.node;
  }
}
