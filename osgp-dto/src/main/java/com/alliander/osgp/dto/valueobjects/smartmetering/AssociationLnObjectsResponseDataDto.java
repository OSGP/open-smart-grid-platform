package com.alliander.osgp.dto.valueobjects.smartmetering;

public class AssociationLnObjectsResponseDataDto extends ActionResponseDto {

    private static final long serialVersionUID = -1200919940530914061L;

    private final AssociationLnListTypeDto associationLnList;

    public AssociationLnObjectsResponseDataDto(final AssociationLnListTypeDto associationLnList) {
        this.associationLnList = associationLnList;
    }

    public AssociationLnListTypeDto getAssociationLnList() {
        return this.associationLnList;
    }
}
