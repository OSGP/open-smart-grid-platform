package com.alliander.osgp.shared.hibernate;

import org.hibernate.HibernateException;

public abstract class ImmutableUserType extends CustomUserType {

    @Override
    public final boolean isMutable() {
        return false;
    }

    @Override
    public Object deepCopy(final Object value) throws HibernateException {
        // for immutable objects, a reference to the original is fine
        return value;
    }
}
