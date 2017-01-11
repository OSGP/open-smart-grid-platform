package com.alliander.osgp.adapter.ws.smartmetering.application.services;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

public interface ResponseUrlService {

    /**
     * Store the combi correlId / responseUrl in a table, so that in can be
     * retrieved later to notify the give responseUrl
     *
     * @param correlId
     * @param responseUrl
     */
    void saveResponseUrl(@NotNull @NotEmpty final String correlId, @NotNull @NotEmpty final String responseUrl);

    /**
     * Store the combi correlId / responseUrl in a table, so that in can be
     * retrieved later to notify the give responseUrl, but only if both correlId
     * and responseUrl are filled
     *
     * @param correlId
     * @param responseUrl
     */
    void saveResponseUrlIfNeeded(final String correlId, final String responseUrl);

    /**
     * this returns true if the table contains a record with the given correlid
     *
     * @param correlId
     * @return
     */
    boolean hasResponseUrl(@NotNull @NotEmpty final String correlId);

    /**
     * Retrieve the responseUrl that belongs to the given correlId. This may
     * return a null value!
     *
     * @param correlId
     * @return
     */
    String findResponseUrl(@NotNull @NotEmpty final String correlId);

    /**
     * Delete the record with the correlId / responseUrl from the database.
     *
     * @param correlId
     * @return
     */
    void deleteResponseUrl(@NotNull @NotEmpty final String correlId);
}
