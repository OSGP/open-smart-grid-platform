// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling.web.api;

import java.util.Optional;
import org.opensmartgridplatform.throttling.SegmentedNetworkThrottler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "permits", produces = "application/json")
public class PermitController {

  private static final int NO_ID_PROVIDED = -1;

  private static final Logger LOGGER = LoggerFactory.getLogger(PermitController.class);

  private final SegmentedNetworkThrottler segmentedNetworkThrottler;

  public PermitController(final SegmentedNetworkThrottler segmentedNetworkThrottler) {
    this.segmentedNetworkThrottler = segmentedNetworkThrottler;
  }

  /**
   * Request a permit for concurrent access to a network. If the base transceiver station and cell
   * are known, IDs should be provided to identify the network segment for the known cell.
   *
   * <p>If after requesting a permit, somehow the client does not receive an HTTP response,
   * resulting in an unclear status of the requested permit, the client can use its unique {@code
   * requestId} and {@code clientId} to {@link #discardPermit(int, int) discard} the permit, in case
   * it would have been granted.
   *
   * @param throttlingConfigId the ID of a throttling configuration
   * @param clientId the ID the throttling client requesting this permit got upon registration
   * @param baseTransceiverStationId the ID of a base transceiver station for which the permit for
   *     access is requested
   * @param cellId the ID of a cell on the base transceiver station identified by {@code
   *     baseTransceiverStationId}
   * @param requestId a unique ID for this permit request in the context of the client identified by
   *     {@code clientId}
   * @return an entity with the int value for the number of permits granted based on this request:
   *     {@code 1} with HTTP status {@code 200 OK} if the permit is granted, {@code 0} with HTTP
   *     status {@code 409 CONFLICT} if the request is denied
   */
  @PostMapping(
      path = {
        "/{throttlingConfigId:[1-9]\\d*+}/{clientId:[1-9]\\d*+}",
        "/{throttlingConfigId:[1-9]\\d*+}/{clientId:[1-9]\\d*+}/{baseTransceiverStationId:0|[1-9]\\d*+}/{cellId:0|[1-9]\\d*+}"
      })
  public ResponseEntity<Integer> requestPermit(
      @PathVariable final short throttlingConfigId,
      @PathVariable final int clientId,
      @PathVariable(required = false) final Optional<Integer> baseTransceiverStationId,
      @PathVariable(required = false) final Optional<Integer> cellId,
      @RequestBody(required = false) final Optional<Integer> requestId) {

    final int actualBaseTransceiverStationId = baseTransceiverStationId.orElse(NO_ID_PROVIDED);
    final int actualCellId = cellId.orElse(NO_ID_PROVIDED);
    final int actualRequestId = requestId.orElse(NO_ID_PROVIDED);

    final boolean granted =
        this.segmentedNetworkThrottler.requestPermit(
            throttlingConfigId,
            clientId,
            actualBaseTransceiverStationId,
            actualCellId,
            actualRequestId);

    LOGGER.debug(
        "Requesting permit for network segment ({}, {}) using requestId {} for clientId {} and throttlingConfigId {}, granted: {}",
        actualBaseTransceiverStationId,
        actualCellId,
        actualRequestId,
        clientId,
        throttlingConfigId,
        granted);

    final int numberOfPermitsGranted;
    final HttpStatus status;
    if (granted) {
      numberOfPermitsGranted = 1;
      status = HttpStatus.OK;
    } else {
      numberOfPermitsGranted = 0;
      status = HttpStatus.CONFLICT;
    }
    return ResponseEntity.status(status).body(numberOfPermitsGranted);
  }

  /**
   * Release a permit that was granted to the client earlier and has not been released or discarded
   * yet.
   *
   * <p>If the base transceiver station and cell were not known and therefore omitted when the
   * permit was requested, their IDs should be omitted when releasing the permit.
   *
   * @param throttlingConfigId the ID of the throttling configuration for the permit to be released
   * @param clientId the ID the throttling client that earlier successfully requested the permit to
   *     be released
   * @param baseTransceiverStationId the ID of a base transceiver station for which the permit is to
   *     be released
   * @param cellId the ID of a cell on the base transceiver station identified by {@code
   *     baseTransceiverStationId} for which the permit is to be released
   * @param requestId the unique ID from the client for which the permit is to be released
   * @return status {@code 200 OK} if a permit for the provided inputs has been released, or status
   *     {@code 404 NOT FOUND} if the throttling service did not have a permit matching the inputs
   *     that could be released
   */
  @DeleteMapping(
      path = {
        "/{throttlingConfigId:[1-9]\\d*+}/{clientId:[1-9]\\d*+}",
        "/{throttlingConfigId:[1-9]\\d*+}/{clientId:[1-9]\\d*+}/{baseTransceiverStationId:0|[1-9]\\d*+}/{cellId:0|[1-9]\\d*+}"
      })
  public ResponseEntity<Void> releasePermit(
      @PathVariable final short throttlingConfigId,
      @PathVariable final int clientId,
      @PathVariable(required = false) final Optional<Integer> baseTransceiverStationId,
      @PathVariable(required = false) final Optional<Integer> cellId,
      @RequestBody(required = false) final Optional<Integer> requestId) {

    final int actualBaseTransceiverStationId = baseTransceiverStationId.orElse(NO_ID_PROVIDED);
    final int actualCellId = cellId.orElse(NO_ID_PROVIDED);
    final int actualRequestId = requestId.orElse(NO_ID_PROVIDED);

    final boolean released =
        this.segmentedNetworkThrottler.releasePermit(
            throttlingConfigId,
            clientId,
            actualBaseTransceiverStationId,
            actualCellId,
            actualRequestId);

    LOGGER.debug(
        "Releasing permit for network segment ({}, {}) using requestId {} for clientId {} and throttlingConfigId {}, released: {}",
        actualBaseTransceiverStationId,
        actualCellId,
        actualRequestId,
        clientId,
        throttlingConfigId,
        released);

    final HttpStatus status = released ? HttpStatus.OK : HttpStatus.NOT_FOUND;
    return ResponseEntity.status(status).build();
  }

  /**
   * Discard a permit identified by the client {@code requestId} that may or may not have been
   * granted by the throttling service.
   *
   * @param clientId the ID of the client wishing to discard a permit
   * @param requestId the ID used by the client to identify a permit request for which the result is
   *     unknown to the client
   * @return status {@code 200 OK} if the discarded permit has been released, or status {@code 404
   *     NOT FOUND} if the throttling service does not have a permit matching the {@code clientId}
   *     and {@code requestId}
   */
  @DeleteMapping(path = "/discard/{clientId:[1-9]\\d*+}/{requestId:0|[1-9]\\d*+}")
  public ResponseEntity<Void> discardPermit(
      @PathVariable final int clientId, @PathVariable final int requestId) {

    final boolean discarded = this.segmentedNetworkThrottler.discardPermit(clientId, requestId);

    LOGGER.debug(
        "Discarding permit using requestId {} for clientId {}, discarded: {}",
        requestId,
        clientId,
        discarded);

    final HttpStatus status = discarded ? HttpStatus.OK : HttpStatus.NOT_FOUND;
    return ResponseEntity.status(status).build();
  }
}
