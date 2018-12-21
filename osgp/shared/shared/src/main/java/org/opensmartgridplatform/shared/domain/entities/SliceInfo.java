/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.domain.entities;

import java.util.List;

public class SliceInfo<T> {

    private final boolean next;

    private final List<T> contents;

    public SliceInfo(final boolean next, final List<T> contents) {
        this.next = next;
        this.contents = contents;
    }

    public boolean isNext() {
        return this.next;
    }

    public List<T> getContents() {
        return this.contents;
    }
}
