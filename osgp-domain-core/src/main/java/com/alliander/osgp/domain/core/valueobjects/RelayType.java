package com.alliander.osgp.domain.core.valueobjects;

public enum RelayType {
    LIGHT {
        @Override
        public DomainType domainType() {
            return DomainType.PUBLIC_LIGHTING;
        }
    },
    TARIFF {
        @Override
        public DomainType domainType() {
            return DomainType.TARIFF_SWITCHING;
        }
    },
    TARIFF_REVERSED {
        @Override
        public DomainType domainType() {
            return DomainType.TARIFF_SWITCHING;
        }
    };

    public abstract DomainType domainType();
}
