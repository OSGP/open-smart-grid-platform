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
 | properties  |                                            |
 | objects     | List of CosemObjects                       |


## Object

 | Path        | Description                                                                                                                            |
 |-------------|----------------------------------------------------------------------------------------------------------------------------------------|
 | tag         | Tag to be able to lookup the object                                                                                                    |
 | description | Description of the CosemObject                                                                                                         |
 | note        | Special note to indicate exceptions, like specified in an Addendum                                                                     |
 | class-id    | The id of the used class                                                                                                               |
 | version     | The version of the clas                                                                                                                |
 | obis        | obiscode with or without channel replace character 'x'                                                                                 |
 | group       | one of  Abstract objects (ABSTRACT), Electricity related objects (ELECTRICITY), M-bus related objects (MBUS) and Miscellaneous objects |
 | meterTypes  | Single Phase (SP) or Polyphase (PP)                                                                                                    |
 | attributes  | attributes                                                                                                                             |


## Attribute

 | Path        | Description                              |
 |-------------|------------------------------------------|
 | id          | id of attribute                          |
 | description | description of attribute                 |
 | datatype    | datatype name                            |
 | valuetype   | can be updated by meter (DYNAMIC)        |
 | value       | standard value                           |
 | access      | access to attribute - read (R) write (W) |
