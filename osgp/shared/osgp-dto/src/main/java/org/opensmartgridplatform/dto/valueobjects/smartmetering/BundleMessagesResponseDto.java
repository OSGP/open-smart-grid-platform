/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BundleMessagesResponseDto implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = 9084545073121917337L;

  private List<ActionResponseDto> actionValueObjectResponseDtoList;

  public BundleMessagesResponseDto(final List<ActionResponseDto> actionValueObjectResponseDtoList) {
    this.actionValueObjectResponseDtoList = actionValueObjectResponseDtoList;
  }

  public List<ActionResponseDto> getAllResponses() {
    return new ArrayList<>(this.actionValueObjectResponseDtoList);
  }
}
