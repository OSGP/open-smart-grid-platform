// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.infra.jms;

import java.io.Serializable;

public class RequestMessage implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = 8377090502244471258L;

  protected final String deviceIdentification;
  protected final String organisationIdentification;
  protected final String correlationUid;
  protected final String ipAddress;
  protected final Integer baseTransceiverStationId;
  protected final Integer cellId;
  protected final Serializable request;

  public RequestMessage(final MessageMetadata messageMetadata, final Serializable request) {
    this(
        messageMetadata.getCorrelationUid(),
        messageMetadata.getOrganisationIdentification(),
        messageMetadata.getDeviceIdentification(),
        messageMetadata.getIpAddress(),
        messageMetadata.getBaseTransceiverStationId(),
        messageMetadata.getCellId(),
        request);
  }

  public RequestMessage(final CorrelationIds ids, final Serializable request) {
    this(
        ids.getCorrelationUid(),
        ids.getOrganisationIdentification(),
        ids.getDeviceIdentification(),
        null,
        null,
        null,
        request);
  }

  public RequestMessage(
      final String correlationUid,
      final String organisationIdentification,
      final String deviceIdentification) {
    this(correlationUid, organisationIdentification, deviceIdentification, null, null, null, null);
  }

  public RequestMessage(
      final String correlationUid,
      final String organisationIdentification,
      final String deviceIdentification,
      final Serializable request) {
    this(
        correlationUid,
        organisationIdentification,
        deviceIdentification,
        null,
        null,
        null,
        request);
  }

  public RequestMessage(
      final String correlationUid,
      final String organisationIdentification,
      final String deviceIdentification,
      final String ipAddress,
      final Integer baseTransceiverStationId,
      final Integer cellId,
      final Serializable request) {
    this.correlationUid = correlationUid;
    this.organisationIdentification = organisationIdentification;
    this.deviceIdentification = deviceIdentification;
    this.ipAddress = ipAddress;
    this.baseTransceiverStationId = baseTransceiverStationId;
    this.cellId = cellId;
    this.request = request;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public String getOrganisationIdentification() {
    return this.organisationIdentification;
  }

  public String getCorrelationUid() {
    return this.correlationUid;
  }

  public String getIpAddress() {
    return this.ipAddress;
  }

  public Integer getBaseTransceiverStationId() {
    return this.baseTransceiverStationId;
  }

  public Integer getCellId() {
    return this.cellId;
  }

  public Serializable getRequest() {
    return this.request;
  }

  public MessageMetadata messageMetadata() {
    return MessageMetadata.newBuilder()
        .withDeviceIdentification(this.deviceIdentification)
        .withOrganisationIdentification(this.organisationIdentification)
        .withCorrelationUid(this.correlationUid)
        .withIpAddress(this.ipAddress)
        .withNetworkSegmentIds(this.baseTransceiverStationId, this.cellId)
        .build();
  }
}
