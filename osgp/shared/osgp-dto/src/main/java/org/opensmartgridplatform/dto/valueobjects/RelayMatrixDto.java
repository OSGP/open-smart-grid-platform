// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects;

import java.io.Serializable;
import java.util.List;

public class RelayMatrixDto implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = 1679416098090362861L;

  private Integer masterRelayIndex;

  private boolean masterRelayOn;

  private List<Integer> indicesOfControlledRelaysOn;

  private List<Integer> indicesOfControlledRelaysOff;

  public RelayMatrixDto(final Integer masterRelayIndex, final boolean masterRelayOn) {
    this.masterRelayIndex = masterRelayIndex;
    this.masterRelayOn = masterRelayOn;
  }

  public Integer getMasterRelayIndex() {
    return this.masterRelayIndex;
  }

  public boolean isMasterRelayOn() {
    return this.masterRelayOn;
  }

  public List<Integer> getIndicesOfControlledRelaysOn() {
    return this.indicesOfControlledRelaysOn;
  }

  public void setIndicesOfControlledRelaysOn(final List<Integer> indicesOfControlledRelaysOn) {
    this.indicesOfControlledRelaysOn = indicesOfControlledRelaysOn;
  }

  public List<Integer> getIndicesOfControlledRelaysOff() {
    return this.indicesOfControlledRelaysOff;
  }

  public void setIndicesOfControlledRelaysOff(final List<Integer> indicesOfControlledRelaysOff) {
    this.indicesOfControlledRelaysOff = indicesOfControlledRelaysOff;
  }
}
