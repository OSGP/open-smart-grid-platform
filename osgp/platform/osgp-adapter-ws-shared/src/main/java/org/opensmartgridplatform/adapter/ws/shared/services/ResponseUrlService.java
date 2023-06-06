// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.shared.services;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public interface ResponseUrlService {

  /**
   * Store the combi correlId / responseUrl in a table, so that in can be retrieved later to notify
   * the give responseUrl
   *
   * @param correlId
   * @param responseUrl
   */
  void saveResponseUrl(
      @NotNull @NotEmpty final String correlId, @NotNull @NotEmpty final String responseUrl);

  /**
   * Store the combi correlId / responseUrl in a table, so that in can be retrieved later to notify
   * the give responseUrl, but only if both correlId and responseUrl are filled
   *
   * @param correlId
   * @param responseUrl
   */
  void saveResponseUrlIfNeeded(final String correlId, final String responseUrl);

  /**
   * this returns true if the table contains a record with the given correlid
   *
   * @param correlId
   * @return true, if the table contains a record with the given correlId
   */
  boolean hasResponseUrl(@NotNull @NotEmpty final String correlId);

  /**
   * Retrieve the responseUrl that belongs to the given correlId. This may return a null value!
   *
   * @param correlId
   * @return the repsponseUrl that belongs to the given correlId. If no records exists it returns:
   *     null
   */
  String findResponseUrl(@NotNull @NotEmpty final String correlId);

  /**
   * Delete the record with the correlId / responseUrl from the database.
   *
   * @param correlId
   */
  void deleteResponseUrl(@NotNull @NotEmpty final String correlId);

  /**
   * This combines the @see findResponseUrl and @see deleteResponseUrl.
   *
   * @param correlId
   * @return the repsponseUrl that belongs to the given correlId As a side-effect, the record is
   *     also deleted from the dbs. If no records exists it returns: null
   */
  String popResponseUrl(@NotNull @NotEmpty final String correlId);
}
