/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class SynchronizeTimeRequestDto implements ActionRequestDto {

  private static final long serialVersionUID = -4724182593235620894L;

  private final int deviation;

  private final boolean dst;

  public SynchronizeTimeRequestDto(final int deviation, final boolean dst) {
    this.deviation = deviation;
    this.dst = dst;
  }

  public int getDeviation() {
    return this.deviation;
  }

  public boolean isDst() {
    return this.dst;
  }
}
