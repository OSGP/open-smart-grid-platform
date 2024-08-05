// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serial;
import java.util.List;

public class GetThdFingerprintResponseDto extends ActionResponseDto {

  @Serial private static final long serialVersionUID = -2437923107190171721L;

  private final Integer thdInstantaneousCurrentL1;

  private final Integer thdInstantaneousCurrentL2;

  private final Integer thdInstantaneousCurrentL3;

  private final List<Integer> thdInstantaneousCurrentFingerprintL1;

  private final List<Integer> thdInstantaneousCurrentFingerprintL2;

  private final List<Integer> thdInstantaneousCurrentFingerprintL3;

  private final Integer thdCurrentOverLimitCounterL1;

  private final Integer thdCurrentOverLimitCounterL2;

  private final Integer thdCurrentOverLimitCounterL3;

  public GetThdFingerprintResponseDto(
      final Object thdInstantaneousCurrentL1,
      final Object thdInstantaneousCurrentL2,
      final Object thdInstantaneousCurrentL3,
      final Object thdInstantaneousCurrentFingerprintL1,
      final Object thdInstantaneousCurrentFingerprintL2,
      final Object thdInstantaneousCurrentFingerprintL3,
      final Object thdCurrentOverLimitCounterL1,
      final Object thdCurrentOverLimitCounterL2,
      final Object thdCurrentOverLimitCounterL3) {
    this.thdInstantaneousCurrentL1 = (Integer) thdInstantaneousCurrentL1;
    this.thdInstantaneousCurrentL2 = (Integer) thdInstantaneousCurrentL2;
    this.thdInstantaneousCurrentL3 = (Integer) thdInstantaneousCurrentL3;
    this.thdInstantaneousCurrentFingerprintL1 =
        (List<Integer>) thdInstantaneousCurrentFingerprintL1;
    this.thdInstantaneousCurrentFingerprintL2 =
        (List<Integer>) thdInstantaneousCurrentFingerprintL2;
    this.thdInstantaneousCurrentFingerprintL3 =
        (List<Integer>) thdInstantaneousCurrentFingerprintL3;
    this.thdCurrentOverLimitCounterL1 = (Integer) thdCurrentOverLimitCounterL1;
    this.thdCurrentOverLimitCounterL2 = (Integer) thdCurrentOverLimitCounterL2;
    this.thdCurrentOverLimitCounterL3 = (Integer) thdCurrentOverLimitCounterL3;
  }

  public Integer getThdInstantaneousCurrentL1() {
    return this.thdInstantaneousCurrentL1;
  }

  public Integer getThdInstantaneousCurrentL2() {
    return this.thdInstantaneousCurrentL2;
  }

  public Integer getThdInstantaneousCurrentL3() {
    return this.thdInstantaneousCurrentL3;
  }

  public List<Integer> getThdInstantaneousCurrentFingerprintL1() {
    return this.thdInstantaneousCurrentFingerprintL1;
  }

  public List<Integer> getThdInstantaneousCurrentFingerprintL2() {
    return this.thdInstantaneousCurrentFingerprintL2;
  }

  public List<Integer> getThdInstantaneousCurrentFingerprintL3() {
    return this.thdInstantaneousCurrentFingerprintL3;
  }

  public Integer getThdCurrentOverLimitCounterL1() {
    return this.thdCurrentOverLimitCounterL1;
  }

  public Integer getThdCurrentOverLimitCounterL2() {
    return this.thdCurrentOverLimitCounterL2;
  }

  public Integer getThdCurrentOverLimitCounterL3() {
    return this.thdCurrentOverLimitCounterL3;
  }
}
