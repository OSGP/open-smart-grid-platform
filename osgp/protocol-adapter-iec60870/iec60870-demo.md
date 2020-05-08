%title: IEC-60870-5-104
%author: Sander van der Heijden
%date: 2020-03-18


-> # Content <-

  * IEC-60870
  * Definitions
  * Application data structure
  * Application functions
  * Roles & Components
  * IEC-60870 simulator features
  * OSGP features
  * SCADA interface features
  * SCADA configuration examples
  * Documentation


--------------------------------------------------------------------------------


-> # IEC-60870 <-

*IEC-60870*
  defines systems used for telecontrol 
  (supervisory control and data acquisition)

*IEC-60870-5*
  transmission protocols:
  provides a communication profile
  for sending basic telecontrol messages
  between a central telecontrol station (*controlling station*) 
  and telecontrol outstations (*controlled stations*),
  which uses permanent directly connected data circuits between the stations

*IEC-60870-5-101* 
  companion standard for basic telecontrol tasks

*IEC-60870-5-104*
  networked version of IEC-60870-5-101

--------------------------------------------------------------------------------


-> # Definitions <-

* *Control Direction*
	Direction of transmission 
	from the controlling station (SCADA) 
	to a controlled station (a station control system or a RTU)
* *Monitor Direction*
	Direction of transmission 
	from the controlled station 
	to a controlling station

* *Controlled Station* 
	(Outstation, Remote station, Remote Terminal Unit (RTU), Slave station)
	A station which is monitored by a master station
* *Controlling Station* 
	(Master station) 
	A location at which telecontrol of outstations is performed

* *Process Image*
	Most actual overview of all objects, 
	the objects shall represent the most actual status in the field 
	and includes the object value and its quality
* *Redundancy Group*
	Supports at least 2 logical connections. 
	Relies on one process image and one event buffer.
* *Common Address of Application Service Data Unit (CAA)*
	Common number used for all objects in one controlled station. 
	One controlled station can be either an entire station control system 
	or only a part of it (a logical unit)
* *Information Object Address (IOA)*
	Object address to identify information objects in tele control systems.
	The combination of CAA and IOA must be a unique identifier for 1 data object.

--------------------------------------------------------------------------------


-> # Application data structure <-

* Application Protocol Data Unit (APDU)

  * Application Protocol Control Information (APCI)

  * Application Service Data Unit (ASDU)

--------------------------------------------------------------------------------

-> # Application data structure - APCI <-

APCI consists of 6 8-bit fields:

  * Start Byte (0x68)
  * Length of APDU
  * Control Field 1 (CF1)
  * Control Field 2 (CF2)
  * Control Field 3 (CF3)
  * Control Field 4 (CF4)

<br>

3 frame formats identified by last 2 bits in CF1:

* I-format
  * information transfer format
  * last bit CF1: 0
  * variable length
  * contains ASDU
* S-format
  * numbered supervisory functions
  * last bits CF1: 01
  * contains no ASDU
* U-format
  * unnumbered control functions
  * last bits CF1: 11
  * contains no ASDU
  * used for TESTFR, STOPDT, STARTDT

<br>

U-format CF1 values:

  Test frame activation            | 0 | 1 | 0 | 0 | 0 | 0 | 1 | 1 |
  Test frame confirmation          | 1 | 0 | 0 | 0 | 0 | 0 | 1 | 1 |
  Stop data transfer activation    | 0 | 0 | 0 | 1 | 0 | 0 | 1 | 1 |
  Stop data transfer confirmation  | 0 | 0 | 1 | 0 | 0 | 0 | 1 | 1 |
  Start data transfer activation   | 0 | 0 | 0 | 0 | 0 | 1 | 1 | 1 |
  Start data transfer confirmation | 0 | 0 | 0 | 0 | 1 | 0 | 1 | 1 |

--------------------------------------------------------------------------------

-> # Application data structure - ASDU <-

* Data Unit Identifier
  * Type Identification
  * Variable Structure Qualifier
  * Cause of Transmission
  * Common Address
* Information Object(s)
  * Information Object Identifier
    * Information Object Type
    * Information Object Address
  * Set of Information Object Elements
  * Time Tag of Information Object


--------------------------------------------------------------------------------

-> # Application data structure - ASDU types <-

*ASDU types used by Alliander:*

|--------------------------------------------------------------------------------------------|
| ID    | Code      | Description                                                            |
|--------------------------------------------------------------------------------------------|
| *Process information in monitor direction*                                                   |
|--------------------------------------------------------------------------------------------|
|   <1> | M_SP_NA_1 | Single-point information                                               |
|   <3> | M_DP_NA_1 | Double-point information                                               |
|   <5> | M_ST_NA_1 | Step position information                                              |
|   <9> | M_ME_NA_1 | Measured value, normalized value                                       |
|  <13> | M_ME_NC_1 | Measured value, short floating point value                             |
|  <30> | M_SP_TB_1 | Single-point information with time tag CP56Time2a                      |
|  <31> | M_DP_TB_1 | Double-point information with time tag CP56Time2a                      |
|  <32> | M_ST_TB_1 | Step position information with time tag CP56Time2a                     |
|  <34> | M_ME_TD_1 | Measured value, normalized value with time tag CP56Time2a              |
|  <36> | M_ME_TF_1 | Measured value, short floating point value with time tag CP56Time2a    |
|  <37> | M_IT_TB_1 | Integrated totals with time tag CP56Time2a                             |
|--------------------------------------------------------------------------------------------|

--------------------------------------------------------------------------------

| *Process information in control direction*                                                   |
|--------------------------------------------------------------------------------------------|
|  <45> | C_SC_NA_1 | Single command                                                         |
|  <46> | C_DC_NA_1 | Double command                                                         |
|  <47> | C_RC_NA_1 | Regulating step command                                                |
|  <48> | C_SE_NA_1 | Set point command, normalized value                                    |
|  <50> | C_SE_NC_1 | Set point command, short floating point value                          |
|  <58> | C_SC_TA_1 | Single command with time tag CP56Time2a                                |
|  <59> | C_DC_TA_1 | Double command with time tag CP56Time2a                                |
|  <60> | C_RC_TA_1 | Regulating step command with time tag CP56Time2a                       |
|  <61> | C_SE_TA_1 | Set point command, normalized value with time tag CP56Time2a           |
|  <63> | C_SE_NC_1 | Set point command, short floating point value with time tag CP56Time2a |
|--------------------------------------------------------------------------------------------|

--------------------------------------------------------------------------------

| *System information in monitor direction*                                                    |
|--------------------------------------------------------------------------------------------|
|  <70> | M_EI_NA_1 | End of initialization                                                  |
|--------------------------------------------------------------------------------------------|
<br>
| *System information in control direction*                                                    |
|--------------------------------------------------------------------------------------------|
| <100> | C_IC_NA_1 | Interrogation command                                                  |
| <101> | C_CI_NA_1 | Counter interrogation command                                          |
| <107> | C_TS_TA_1 | Test command with time tag CP56Time2a                                  |
|--------------------------------------------------------------------------------------------|


--------------------------------------------------------------------------------


-> # Application functions <-

*1. Station initialization*
2. Data acquisition by polling
3. Cyclic data transmission
*4. Acquisition of events*
*5. General Interrogation*
6. Clock synchronization
*7. Command transmission*
*8. Transmission of integrated totals*
9. Parameter loading
*10. Test Procedure*
11. File Transfer
12. Acquisition of transmission delay

--------------------------------------------------------------------------------


-> # Roles & Components <-

*Roles*
|------------------------------------------------------------------|
|                   | Controlling station   | Controlled station   |
|-------------------|-----------------------|----------------------|
| OSGP Applications | SCADA                 | SCADA Interface      |
| OSGP              | Protocol Adapter      | Simulator, RTUs      |
|------------------------------------------------------------------|
<br>

*Components*
* OSGP Applications
  * da-backend
  * da-scada-interface
* OSGP
  * osgp-adapter-ws-distributionautomation
  * osgp-adapter-domain-distributionautomation
  * osgp-core
  * osgp-adapter-protocol-iec60870
  * osgp-domain-distributionautomation
  * osgp-iec60870
* Simulator
  * osgp-simulator-iec60870


--------------------------------------------------------------------------------


-> # IEC-60870 Simulator - Features <-

Feature: Station initialization
  *Support using different profiles*
  *Initialize station after simulator restart*

Feature: Event acquisition
  *Support updating values*
  *Send event after value update*
  Send events from event buffer after reconnect
  Send prioritized events from event buffer after reconnect
  Store event in event buffer when there is no active connection
  Handle event buffer overflow

Feature: General interrogation
  *Handle general interrogation request*
  Handle buffered events before general interrogation request
  Handle new event before general interrogation request
  Handle new event for already transmitted IOA while processing general interrogation request
  Handle new event for not yet transmitted IOA while processing general interrogation request
  Handle new event after general interrogation request completion

Feature: Command transmission
  Handle single command
  Handle double command

Feature: Transmission of integrated totals
  Handle transmission of integrated totals via spontaneous transmission
  Handle transmission of integrated totals via counter interrogation request

Feature: Test procedure
  Handle test command
  Handle invalid test command

Feature: Redundancy
  *Handle active connections*
  *Handle stand-by connections*

Feature: Security - Secure connections
  *Support SSL/TLS connections (configurable)*

--------------------------------------------------------------------------------


-> # OSGP - Features <-


Feature: Station initialization

Feature: Event acquisition

Feature: General interrogation

Feature: Command transmission

Feature: Transmission of integrated totals

Feature: Test procedure

Feature: Communication Monitoring

Feature: Device Provisioning

Feature: Device Configuration

Feature: IEC60870 Configuration

Feature: Redundancy

Feature: Message templating

Feature: Security

Feature: Logging

Feature: Audit trail


--------------------------------------------------------------------------------


-> # SCADA Interface - Features <-

Feature: Station initialization
  *Initialize station, after restart of controlled station*

Feature: Event acquisition
  *Receive event from OSGP and send to SCADA*
  *Receive events from event buffer*
  *Receive prioritized events from event buffer*
  *Store event in event buffer*
  *Store event in event buffer when buffer is full*

Feature: General interrogation
  *Send general interrogation request*
  *Send general interrogation request while events in buffer*
  *Send general interrogation request, receive an event before sending the request*
  *Send general interrogation request, receive an event for already transmitted IOA* 
        *while waiting for general interrogation completion*
  *Send general interrogation request, receive an event for not yet transmitted IOA*
        *while waiting for general interrogation completion*
  *Send general interrogation request, receive an event after request completion*
  
--------------------------------------------------------------------------------

Feature: Command transmission
  Send single command
  Send double command

Feature: Transmission of integrated totals
  Receive integrated totals
  Request integrated totals

Feature: Test procedure
  *Send test command*
  *Send invalid test command*

Feature: Communication Monitoring

Feature: Device Provisioning
  Provision device

Feature: Device Configuration
  Configure device

Feature: IEC60870 Configuration

Feature: Redundancy
  *Active connection*
  *Stand-by connection*


--------------------------------------------------------------------------------


-> # SCADA Configuration Examples <-

*Configuration of RTUs in SCADA:*

<Rtus>
  <Rtu>
    <HostName>10.17.50.2</HostName>
    <PortNr>2404</PortNr>
    <RtuNr>10002</RtuNr>
  </Rtu>
</Rtus>

--------------------------------------------------------------------------------
*Mapping of other devices to IEC-60870*

<DeviceId>N001</DeviceId>
<MeasurementParameter>
  <MeasurementType>Energy1</MeasurementType>
  <ExternalId>
    { "RtuNr":"10002",
      "Ioa":"9217",
      "ObjectType":"MeasuredValueShortFloat",
      "WithTimeTag":"true" }
  </ExternalId>
</MeasurementParameter>
<DeviceId>N001</DeviceId>
<MeasurementParameter>
  <MeasurementType>Flowrate1</MeasurementType>
  <ExternalId>
    { "RtuNr":"10002",
      "Ioa":"9218", 
      "ObjectType":"MeasuredValueShortFloat",
      "WithTimeTag":"true" }
  </ExternalId>
</MeasurementParameter>
...

--------------------------------------------------------------------------------
*General IEC-60870 configuration*

<Hosts>
  <Host>
    <ParameterT1>15</ParameterT1>                        
    <ParameterT2>10</ParameterT2>
    <ParameterT3>20</ParameterT3>
    <ParameterW>8</ParameterW>
    <ParameterK>12</ParameterK>
    <Ipadress>10.17.124.1</Ipadress>
    <TimeZone>UTC</TimeZone>
  </Host>
</Hosts>

--------------------------------------------------------------------------------


-> # Documentation <-

* 60870-5-1 (Transmission Frame Formats).pdf
* 60870-5-2 (Link Transmission procedures).pdf
* 60870-5-3 (General structure of application data).pdf
* 60870-5-4 (Definition and coding of application information elements).pdf
* 60870-5-5 (Basic application functions).pdf
* 60870-5-7 (Security extensions).pdf
* 60870-5-101 (Companion standard for basic telecontrol tasks).pdf
* 60870-5-104 (Network access for 60870-5-101 using standard transport profiles).pdf

* TR-IEC104.pdf -  Description and analysis of IEC 104 Protocol
* S6038-_1.DOC -  Alliander Protocol Implementation Document for IEC 60870-5-104
* ART126 - 104 protocol implementation.docx - Description of 104 implementation in OSGP by Paul Houtman
* OSGP IEC104 Spectrum.docx - Spectrum SCADA examples by Rick Ekkelboom

