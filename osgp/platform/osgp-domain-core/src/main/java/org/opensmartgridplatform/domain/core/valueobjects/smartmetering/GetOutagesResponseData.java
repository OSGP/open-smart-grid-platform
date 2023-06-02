//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.List;

public class GetOutagesResponseData extends ActionResponse implements Serializable {

  private static final long serialVersionUID = 4966055518516878043L;

  private List<Outage> outages;

  public GetOutagesResponseData(final List<Outage> outages) {
    this.outages = outages;
  }

  public List<Outage> getOutages() {
    return this.outages;
  }
}
