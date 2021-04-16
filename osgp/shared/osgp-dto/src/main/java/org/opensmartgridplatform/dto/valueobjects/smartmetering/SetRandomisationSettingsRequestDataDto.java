/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class SetRandomisationSettingsRequestDataDto implements ActionRequestDto {

  private static final long serialVersionUID = 8564943872758612188L;

  private final int directAttach;
  private final int randomisationStartWindow;
  private final int multiplicationFactor;
  private final int numberOfRetries;

  public SetRandomisationSettingsRequestDataDto(
      final int directAttach,
      final int randomisationStartWindow,
      final int multiplicationFactor,
      final int numberOfRetries) {

    this.directAttach = directAttach;
    this.randomisationStartWindow = randomisationStartWindow;
    this.multiplicationFactor = multiplicationFactor;
    this.numberOfRetries = numberOfRetries;
  }

  public int getDirectAttach() {
    return this.directAttach;
  }

  public int getRandomisationStartWindow() {
    return this.randomisationStartWindow;
  }

  public int getMultiplicationFactor() {
    return this.multiplicationFactor;
  }

  public int getNumberOfRetries() {
    return this.numberOfRetries;
  }

  @Override
  public String toString() {

    return String.format(
        "directAttach = %s, randomisationStartWindow = %s, multiplicationFactor = %s, "
            + "numberOfRetries = %s ",
        this.directAttach,
        this.randomisationStartWindow,
        this.multiplicationFactor,
        this.numberOfRetries);
  }
}
