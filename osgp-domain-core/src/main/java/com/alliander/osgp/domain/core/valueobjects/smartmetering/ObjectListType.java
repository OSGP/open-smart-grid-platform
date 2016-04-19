package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ObjectListType implements Serializable {
    private static final long serialVersionUID = 5540577793697751858L;

    private List<ObjectListElement> objectListElement;

    public ObjectListType(final List<ObjectListElement> objectListElement) {
        this.objectListElement = Collections.unmodifiableList(objectListElement);
    }

    public List<ObjectListElement> getObjectListElement() {
        return new ArrayList<>(this.objectListElement);
    }
}
