package com.alliander.osgp.dto.valueobject.smartmetering;

import java.io.Serializable;
import java.util.List;

public class FindEventsQueryMessageDataContainer implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 1917863566442592894L;

    private List<FindEventsQuery> findEventsQueryList;

    public FindEventsQueryMessageDataContainer(final List<FindEventsQuery> findEventsQueryList) {
        this.findEventsQueryList = findEventsQueryList;
    }

    public List<FindEventsQuery> getFindEventsQueryList() {
        return this.findEventsQueryList;
    }
}
