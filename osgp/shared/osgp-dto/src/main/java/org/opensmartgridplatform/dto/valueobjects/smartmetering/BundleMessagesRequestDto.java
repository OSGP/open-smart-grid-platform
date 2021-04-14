/*
 * Copyright 2015 Smart Society Services B.V.
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

public class BundleMessagesRequestDto implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = -4268617729741836833L;

  private List<ActionDto> actionList;

  public BundleMessagesRequestDto(final List<ActionDto> actionList) {
    this.actionList = actionList;
  }

  public List<ActionDto> getActionList() {
    return new ArrayList<>(this.actionList);
  }

  public List<ActionResponseDto> getAllResponses() {
    final List<ActionResponseDto> responseDtoList = new ArrayList<>();

    for (final ActionDto actionDto : this.actionList) {
      final ActionResponseDto actionResponseDto = actionDto.getResponse();
      if (actionResponseDto != null) {
        responseDtoList.add(actionResponseDto);
      }
    }

    return responseDtoList;
  }
}
