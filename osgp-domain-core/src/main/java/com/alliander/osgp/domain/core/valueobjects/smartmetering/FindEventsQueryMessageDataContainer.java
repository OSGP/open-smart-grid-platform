package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.List;

public class FindEventsQueryMessageDataContainer implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -1633881200321783021L;

    private List<FindEventsQuery> findEventsQueryList;

    public FindEventsQueryMessageDataContainer(final List<FindEventsQuery> findEventsQueryList) {
        this.findEventsQueryList = findEventsQueryList;
    }

    public List<FindEventsQuery> getFindEventsQueryList() {
        return this.findEventsQueryList;
    }
}
