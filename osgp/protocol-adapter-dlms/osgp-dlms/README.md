<!--
SPDX-FileCopyrightText: Contributors to the GXF project

SPDX-License-Identifier: Apache-2.0
-->

# DLMS object config module
The DLMS Object Config defined by json files.

# Object config json file description

## Objectconfig

 | Path        | Description                                |
 |-------------|--------------------------------------------|
 | profile     | profile name for a protocol type, like SMR |
 | version     | version of the protocol                    |    
 | description | Decription of this profile                 |
 | properties  | General property for the complete config   |
 | objects     | List of CosemObjects                       |


## Object

 | Path        | Description                                                                                                                           |
 |-------------|---------------------------------------------------------------------------------------------------------------------------------------|
 | tag         | Tag to be able to lookup the object                                                                                                   |
 | description | Description of the CosemObject                                                                                                        |
 | note        | Special note to indicate exceptions, like specified in an Addendum                                                                    |
 | class-id    | The id of the used class                                                                                                              |
 | version     | The version of the clas                                                                                                               |
 | obis        | obiscode with or without channel replace character 'x'                                                                                |
 | group       | one of  Abstract objects (ABSTRACT), Electricity related objects (ELECTRICITY), M-bus related objects (MBUS) and Miscellaneous objects |
 | meterTypes  | Single Phase (SP) or Polyphase (PP)                                                                                                   |
 | attributes  | Attributes. Should match the attributes for this class, as defined in DLMS.                                                           |
 | properties  | Additional properties for handling this object                                                                                        |


## Attribute

 | Path              | Description                                         |
 |-------------------|-----------------------------------------------------|
 | id                | id of attribute                                     |
 | description       | description of attribute                            |
 | datatype          | datatype name                                       |
 | valuetype         | defines the source of the value                     |
 | value             | the fixed value or null                             |
 | valuebasedonmodel | defines the values if it depends on the devicemodel |
 | access            | access to attribute - read (R) write (W)            |

### ValueType

Valuetype can be one of the following:
- DYNAMIC: This value can be updated by the meter, e.g. a meter value
- FIXED_IN_PROFILE: A fixed value, defined in the profile, e.g. a scaler/unit defined in SMR5.0.
- FIXED_IN_METER: A fixed value set in the factory
- SET_BY_CLIENT: A fixed value set by the client during installation
- BASED_ON_MODEL: The value depends on the device model, as defined in the valuesbasedonmodel block
