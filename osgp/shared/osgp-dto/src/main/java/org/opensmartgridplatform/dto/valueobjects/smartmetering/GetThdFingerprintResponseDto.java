// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serial;
import java.util.List;

public class GetThdFingerprintResponseDto extends ActionResponseDto {

  @Serial
  private static final long serialVersionUID = -2437923107190171721L;

  private final int thdInstantaneousCurrentL1;

  private final int thdInstantaneousCurrentL2;

  private final int thdInstantaneousCurrentL3;

  private final List<Integer> thdInstantaneousCurrentFingerprintL1;

  private final List<Integer> thdInstantaneousCurrentFingerprintL2;

  private final List<Integer> thdInstantaneousCurrentFingerprintL3;

  private final int thdCurrentOverLimitCounterL1;

  private final int thdCurrentOverLimitCounterL2;

  private final int thdCurrentOverLimitCounterL3;

  public GetThdFingerprintResponseDto(
      final int thdInstantaneousCurrentL1,
      final int thdInstantaneousCurrentL2,
      final int thdInstantaneousCurrentL3,
      final List<Integer> thdInstantaneousCurrentFingerprintL1,
      final List<Integer> thdInstantaneousCurrentFingerprintL2,
      final List<Integer> thdInstantaneousCurrentFingerprintL3,
      final int thdCurrentOverLimitCounterL1,
      final int thdCurrentOverLimitCounterL2,
      final int thdCurrentOverLimitCounterL3) {
    this.thdInstantaneousCurrentL1 = thdInstantaneousCurrentL1;
    this.thdInstantaneousCurrentL2 = thdInstantaneousCurrentL2;
    this.thdInstantaneousCurrentL3 = thdInstantaneousCurrentL3;
    this.thdInstantaneousCurrentFingerprintL1 = thdInstantaneousCurrentFingerprintL1;
    this.thdInstantaneousCurrentFingerprintL2 = thdInstantaneousCurrentFingerprintL2;
    this.thdInstantaneousCurrentFingerprintL3 = thdInstantaneousCurrentFingerprintL3;
    this.thdCurrentOverLimitCounterL1 = thdCurrentOverLimitCounterL1;
    this.thdCurrentOverLimitCounterL2 = thdCurrentOverLimitCounterL2;
    this.thdCurrentOverLimitCounterL3 = thdCurrentOverLimitCounterL3;
  }

  public int getThdInstantaneousCurrentL1() {
    return this.thdInstantaneousCurrentL1;
  }

  public int getThdInstantaneousCurrentL2() {
    return this.thdInstantaneousCurrentL2;
  }

  public int getThdInstantaneousCurrentL3() {
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

  public int getThdCurrentOverLimitCounterL1() {
    return this.thdCurrentOverLimitCounterL1;
  }

  public int getThdCurrentOverLimitCounterL2() {
    return this.thdCurrentOverLimitCounterL2;
  }

  public int getThdCurrentOverLimitCounterL3() {
    return this.thdCurrentOverLimitCounterL3;
  }
}
