package com.alliander.osgp.adapter.ws.smartmetering.application.services;

public interface ResponseUrlService {

    /**
     * Store the combi correlId / responseUrl in a table, so that in can be
     * retrieved later to notify the give responseUrl
     *
     * @param correlId
     * @param responseUrl
     */
    void saveResponseUrl(final String correlId, final String responseUrl);

    /**
     * this returns true if the table contains a record with the given correlid
     * 
     * @param correlId
     * @return
     */
    boolean hasResponseUrl(final String correlId);

    /**
     * Retrieve the responseUrl that belongs to the given correlId. This may
     * return a null value!
     *
     * @param correlId
     * @return
     */
    String findResponseUrl(final String correlId);

    /**
     * Delete the record with the correlId / responseUrl from the database.
     *
     * @param correlId
     * @return
     */
    void deleteResponseUrl(final String correlId);
}
