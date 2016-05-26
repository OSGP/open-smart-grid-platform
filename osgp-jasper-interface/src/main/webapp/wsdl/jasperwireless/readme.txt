/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
 
The JasperAPI.xsd, Terminal.wsdl and Sms.wsdl are retrieved from
http://api.jasperwireless.com/ws/schema/JasperAPI.xsd
http://api.jasperwireless.com/ws/schema/Terminal.wsdl
http://api.jasperwireless.com/ws/schema/Sms.wsdl

Currently the Sms.wsdl is used to be able to send an sms message to a device to wake-up the device.
Currently the Terminal.wsdl is used to be able to get session information for a device(s).

In order to get the Terminal.wsdl working the name attribute in binding -> operation -> input is removed.
Eclipse reported:
"The operation specified for the '<bindingname>' binding is not defined for port type '<porttype>'.
All operations specified in this binding must be defined in port type '<porttype>'."