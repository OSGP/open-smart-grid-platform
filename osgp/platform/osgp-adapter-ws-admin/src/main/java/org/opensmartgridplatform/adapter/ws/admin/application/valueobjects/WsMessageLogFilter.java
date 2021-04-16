/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.admin.application.valueobjects;

import java.time.ZonedDateTime;

/** Class to hold the filter fields for finding device messages. */
public class WsMessageLogFilter {

  private String deviceIdentification;
  private String organisationIdentification;
  private ZonedDateTime startTime;
  private ZonedDateTime endTime;
  private Integer pageRequested;
  private String sortBy;
  private String sortDirection;

  public WsMessageLogFilter() {
    // Empty constructor, needed by Orika
  }

  public WsMessageLogFilter(final String deviceIdentification) {
    this.deviceIdentification = deviceIdentification;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public void setDeviceIdentification(final String deviceIdentification) {
    this.deviceIdentification = deviceIdentification;
  }

  public String getOrganisationIdentification() {
    return this.organisationIdentification;
  }

  public void setOrganisationIdentification(final String organisationIdentification) {
    this.organisationIdentification = organisationIdentification;
  }

  public ZonedDateTime getStartTime() {
    return this.startTime;
  }

  public void setStartTime(final ZonedDateTime startTime) {
    this.startTime = startTime;
  }

  public ZonedDateTime getEndTime() {
    return this.endTime;
  }

  public void setEndTime(final ZonedDateTime endTime) {
    this.endTime = endTime;
  }

  public Integer getPageRequested() {
    return this.pageRequested;
  }

  public void setPageRequested(final Integer pageRequested) {
    this.pageRequested = pageRequested;
  }

  public String getSortBy() {
    return this.sortBy;
  }

  public void setSortBy(final String sortBy) {
    this.sortBy = sortBy;
  }

  public String getSortDirection() {
    return this.sortDirection;
  }

  public void setSortDirection(final String sortDirection) {
    this.sortDirection = sortDirection;
  }

  @Override
  public String toString() {
    return "WsMessageLogFilter [deviceIdentification="
        + this.deviceIdentification
        + ", organisationIdentification="
        + this.organisationIdentification
        + ", startTime="
        + this.startTime
        + ", endTime="
        + this.endTime
        + ", pageRequested="
        + this.pageRequested
        + ", sortBy="
        + this.sortBy
        + ", sortDirection="
        + this.sortDirection
        + "]";
  }
}
