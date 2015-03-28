package com.alliander.osgp.shared.hibernate;

public abstract class MutableUserType extends CustomUserType {

    @Override
    public boolean isMutable() {
        return true;
    }
}
