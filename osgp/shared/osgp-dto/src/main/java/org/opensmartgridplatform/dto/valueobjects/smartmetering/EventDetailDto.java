/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class EventDetailDto implements Serializable {

  private static final long serialVersionUID = 6866270221990320428L;

  private final String name;
  private final String value;

  public EventDetailDto(final String name, final String value) {
    this.name = name;
    this.value = value;
  }
}
