/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPushSetupDto implements ActionRequestDto {

  private static final long serialVersionUID = -1080411684155651756L;

  private final CosemObisCodeDto logicalName;
  private final List<CosemObjectDefinitionDto> pushObjectList;
  private final SendDestinationAndMethodDto sendDestinationAndMethod;
  private final List<WindowElementDto> communicationWindow;
  private final Integer randomisationStartInterval;
  private final Integer numberOfRetries;
  private final Integer repetitionDelay;

  protected AbstractPushSetupDto(final AbstractBuilder<?> builder) {
    this.logicalName = builder.logicalName;
    this.pushObjectList = builder.pushObjectList;
    this.sendDestinationAndMethod = builder.sendDestinationAndMethod;
    this.communicationWindow = builder.communicationWindow;
    this.randomisationStartInterval = builder.randomisationStartInterval;
    this.numberOfRetries = builder.numberOfRetries;
    this.repetitionDelay = builder.repetitionDelay;
  }

  public abstract static class AbstractBuilder<T extends AbstractBuilder<T>> {

    private CosemObisCodeDto logicalName = null;
    private List<CosemObjectDefinitionDto> pushObjectList = null;
    private SendDestinationAndMethodDto sendDestinationAndMethod = null;
    private List<WindowElementDto> communicationWindow = null;
    private Integer randomisationStartInterval = null;
    private Integer numberOfRetries = null;
    private Integer repetitionDelay = null;

    protected abstract T self();

    public abstract AbstractPushSetupDto build();

    public T withLogicalName(final CosemObisCodeDto logicalName) {
      this.logicalName = logicalName;
      return this.self();
    }

    public T withPushObjectList(final List<CosemObjectDefinitionDto> pushObjectList) {
      if (pushObjectList == null) {
        this.pushObjectList = null;
      } else {
        this.pushObjectList = new ArrayList<>(pushObjectList);
      }
      return this.self();
    }

    public T withSendDestinationAndMethod(
        final SendDestinationAndMethodDto sendDestinationAndMethod) {
      this.sendDestinationAndMethod = sendDestinationAndMethod;
      return this.self();
    }

    public T withCommunicationWindow(final List<WindowElementDto> communicationWindow) {
      if (communicationWindow == null) {
        this.communicationWindow = null;
      } else {
        this.communicationWindow = new ArrayList<>(communicationWindow);
      }
      return this.self();
    }

    public T withRandomisationStartInterval(final Integer randomisationStartInterval) {
      AbstractBuilder.checkRandomisationStartInterval(randomisationStartInterval);
      this.randomisationStartInterval = randomisationStartInterval;
      return this.self();
    }

    public T withNumberOfRetries(final Integer numberOfRetries) {
      AbstractBuilder.checkNumberOfRetries(numberOfRetries);
      this.numberOfRetries = numberOfRetries;
      return this.self();
    }

    public T withRepetitionDelay(final Integer repetitionDelay) {
      AbstractBuilder.checkRepetitionDelay(repetitionDelay);
      this.repetitionDelay = repetitionDelay;
      return this.self();
    }

    private static void checkRandomisationStartInterval(final Integer randomisationStartInterval) {
      if (randomisationStartInterval == null) {
        return;
      }
      if (randomisationStartInterval < 0 || randomisationStartInterval > 0xFFFF) {
        throw new IllegalArgumentException("randomisationStartInterval not in [0..65535]");
      }
    }

    private static void checkNumberOfRetries(final Integer numberOfRetries) {
      if (numberOfRetries == null) {
        return;
      }
      if (numberOfRetries < 0 || numberOfRetries > 0xFF) {
        throw new IllegalArgumentException("numberOfRetries not in [0..255]");
      }
    }

    private static void checkRepetitionDelay(final Integer repetitionDelay) {
      if (repetitionDelay == null) {
        return;
      }
      if (repetitionDelay < 0 || repetitionDelay > 0xFFFF) {
        throw new IllegalArgumentException("repetitionDelay not in [0..65535]");
      }
    }
  }

  @Override
  public String toString() {
    final StringBuilder sb =
        new StringBuilder(this.getClass().getSimpleName())
            .append("[")
            .append(System.lineSeparator());
    this.appendFieldInfo(sb, "logicalName", this.logicalName);
    this.appendFieldInfo(sb, "pushObjectList", this.pushObjectList);
    this.appendFieldInfo(sb, "sendDestinationAndMethod", this.sendDestinationAndMethod);
    this.appendFieldInfo(sb, "communicationWindow", this.communicationWindow);
    this.appendFieldInfo(sb, "randomisationStartInterval", this.randomisationStartInterval);
    this.appendFieldInfo(sb, "numberOfRetries", this.numberOfRetries);
    this.appendFieldInfo(sb, "repetitionDelay", this.repetitionDelay);
    return sb.append(']').toString();
  }

  private void appendFieldInfo(
      final StringBuilder sb, final String fieldName, final Object fieldValue) {
    sb.append('\t').append(fieldName).append('=').append(fieldValue).append(System.lineSeparator());
  }

  public boolean hasLogicalName() {
    return this.logicalName != null;
  }

  public CosemObisCodeDto getLogicalName() {
    return this.logicalName;
  }

  public boolean hasPushObjectList() {
    return this.pushObjectList != null;
  }

  public List<CosemObjectDefinitionDto> getPushObjectList() {
    if (this.pushObjectList == null) {
      return null;
    }
    return new ArrayList<>(this.pushObjectList);
  }

  public boolean hasSendDestinationAndMethod() {
    return this.sendDestinationAndMethod != null;
  }

  public SendDestinationAndMethodDto getSendDestinationAndMethod() {
    return this.sendDestinationAndMethod;
  }

  public boolean hasCommunicationWindow() {
    return this.communicationWindow != null;
  }

  public List<WindowElementDto> getCommunicationWindow() {
    if (this.communicationWindow == null) {
      return null;
    }
    return new ArrayList<>(this.communicationWindow);
  }

  public boolean hasRandomisationStartInterval() {
    return this.randomisationStartInterval != null;
  }

  public Integer getRandomisationStartInterval() {
    return this.randomisationStartInterval;
  }

  public boolean hasNumberOfRetries() {
    return this.numberOfRetries != null;
  }

  public Integer getNumberOfRetries() {
    return this.numberOfRetries;
  }

  public boolean hasRepetitionDelay() {
    return this.repetitionDelay != null;
  }

  public Integer getRepetitionDelay() {
    return this.repetitionDelay;
  }
}
