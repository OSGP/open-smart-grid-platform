There are two ways to start simulators:
- **Stand-alone simulator**: for a single configuration provided by command-line arguments. 
I.e. for every configuration a separate process is started.
- **With the simulator starter**: for one or multiple configurations provided by a configuration file.
I.e. multiple configurations can be handled in a single process.
In addition, the simulator starter can be used as well to insert/delete matching records in the database.

Most of the documentation below describes the stand-alone simulator. 
At the the bottom there is more about the simulator starter.


# QUICKSTART

    $ cd scripts
    $ sudo ./makeSimulatorKey.sh <device identification>
    $ cd ..
    $ mvn clean package
    $ ./startDlmsDeviceSimulator.sh

Point <dlms_device id> to localhost in device table in osgp-core.

# Manual

Import this project as maven project in eclipse.
Point a device to localhost in device table in osgp-core.

When running the device simulator with security level 5 (default) you will need decrypted keys:

    $HOME/keys/authkeydecrypted
    $HOME/keys/enckeydecrypted
    $HOME/keys/masterkeydecrypted

Optionally you can provide keys per logicaldevice: $HOME/keys/authkeydecrypted<logicaldeviceid>. If you do this the database needs to use the same keys for the logicaldevice.

These keys can be created with the following command:

    $ cd scripts
    $ ./makeSimulatorKey.sh

*NOTE! the script does not create keys specific for a logical device, so you have to rename created keys as needed!*

Start DeviceServer as Java application, from the simulator project, first package and then run:

    $ mvn clean package
    $ ./startDlmsDeviceSimulator.sh

or for a device simulator without security enabled:

    $ mvn clean package
    $ ./startUnencryptedDlmsDeviceSimulator.sh	

or for a device simulator using security level 1, short names and hdlc:

    $ mvn clean package
    $ ./startE650DlmsDeviceSimulator.sh	

## Properties

This project is based on Spring-boot, and therefore supports a broad set of configuration options. Profiles are used to configure differences in behaviour.

Spring profile for different configuration settings:
> spring.profiles.active=profile name

Current profiles supported are default, minimumMemory, e650 and smr5, these implementations must be implemented further. When no active profile is passed default is used. minimumMemory is an extension of default and should be used as:

> spring.profiles.active=default,minimumMemory

E650 is a replacement of default and should be used as:

> spring.profiles.active=e650

smr5 Extends other profiles in order to simulate an SMR 5.0.0 device:

> spring.profiles.active=default,minimumMemory,smr5

smr51 Extends other profiles in order to simulate an SMR 5.1 device:

> spring.profiles.active=default,minimumMemory,smr5,smr51

smr52 Extends other profiles in order to simulate an SMR 5.2 device:

> spring.profiles.active=default,minimumMemory,smr5,smr51,smr52

The default and e650 profiles will both cause the connection setup to take a random amount of time. This is currently configured to be between 500 and 5.000 milliseconds.
The minimumMemory profile ensures that the memory load is minimal. Also minimumMemory has no delay in setting up a connection with the meter.


All properties have default settings, which are included in property files in the JAR resources. 

Properties can also be overruled by command line parameters or environment variables.  Configuration properties and parameters are both injectable in code with the @Value annotation.

For more information, please take a look at the Spring-Boot manual [here](http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-profiles.html) and [here](http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html#boot-features-external-config-profile-specific-properties).


### Port
> port=1024

To use a device simulator with other then default properties, you have insert these values in the protocol-adapter: dlms-device database.
For example:
UPDATE dlms_device SET port=4444, logical_id=2 WHERE device_identification= 'EXXXX001692675614';

### Logical device id
Logical device ids to be started as list or range:
> logicalDeviceIds=2

> logicalDeviceIds=2,3,4,9

> logicalDeviceIds=20..124

### Security and Keys
Indicates the level of security. 0 means no security, 1 LLS1; 5 (Default) HLS5. Keys must be configured when HLS5 is used.

> security.level = 0

> security.level = 1

> security.level = 5

Paths of key files:
> simulator.keys.authentication.path

> simulator.keys.encryption.path

### Referencing method
A logical name is actually an OBIS code. It sometimes also called instance ID. It is a 6 byte number that uniquely identifies an object in a logical device. For example, the clock of a smart meter is always reachable under the address [0, 0, 1, 0, 0, 255]. The default referencing method is logical.

The second way to address an attribute or method is by means of the so called short address. Short addresses are used for small devices and should only be used if the connected smart meter cannot communicate using logical names.

When short name referencing is used the meter still holds a unique logical name (i.e. Obis code) for each of its objects. In addition each object has 2 byte short name that maps to the logical name. Thus a client can address each attribute or method using 2 bytes only.

> referencing.method=logical

> referencing.method=short

### Communication
In most cases DLMS/COSEM communication uses either HDLC or a special Wrapper Layer to add addressing information to the DLMS application layer PDUs.
HDLC is not used by default, if you want to use it, set use_hdlc to true

> use.hdlc=false

> use.hdlc=true
=======
### Connection open delay
It is possible to add a connection open delay to the simulator. The number used is in milliseconds; the following example sets the minimum delay to 20 seconds and the maximum to 40 seconds:

> connection.open.delay.min=20000

> connection.open.delay.max=40000

# Other notes
- It is possible to make multiple connections on 1 simulator using the same port.
- Logical ID's are limited to 16383 (0x3FFF, 14 bits)

# Simulator Starter
The simulator starter can be started using a single jar, which is provided as an artifact of open-smart-grid-platform.

To start the simulator:

> java -jar dlms-device-simulator-starter-XXXX.jar _simulator-configuration.json_ start

Where _simulator-configuration.json_ is the path to the file containing the configuration for the simulated devices.
The configuration file can contain multiple configurations, and each configuration can contain the same
entries as those described above as commandline arguments for the stand-alone simulator.

For an example of a simulator-configuration file see:
https://github.com/OSGP/open-smart-grid-platform/blob/development/osgp/protocol-adapter-dlms/osgp-protocol-simulator-dlms/simulator/simulator-configurations/example.json
