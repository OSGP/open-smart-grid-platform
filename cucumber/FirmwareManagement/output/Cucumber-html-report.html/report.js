$(document).ready(function() {var formatter = new CucumberHTML.DOMFormatter($('.cucumber-report'));formatter.uri("com/alliander/osgp/platform/cucumber/FirmwareManagement.feature");
formatter.feature({
  "id": "",
  "description": "As a grid operator\r\nI want to be able to get the firmware version from a device\r\nSo that I can see which firmware version is installed on the device",
  "name": "",
  "keyword": "Feature",
  "line": 1
});
formatter.scenario({
  "id": ";get-the-firmware-version-from-device",
  "description": "",
  "name": "Get the firmware version from device",
  "keyword": "Scenario",
  "line": 6,
  "type": "scenario"
});
formatter.step({
  "name": "a device with DeviceID \"E9998000014123414\"",
  "keyword": "Given ",
  "line": 7
});
formatter.step({
  "name": "an organisation with OrganisationID \"LianderNetManagement\"",
  "keyword": "And ",
  "line": 8
});
formatter.step({
  "name": "the get firmware version request is received",
  "keyword": "When ",
  "line": 9
});
formatter.step({
  "name": "the firmware version result should be returned",
  "keyword": "Then ",
  "line": 10
});
formatter.match({
  "arguments": [
    {
      "val": "E9998000014123414",
      "offset": 24
    }
  ],
  "location": "GetFirmwareVersion.aDeviceWithDeviceID(String)"
});
formatter.result({
  "duration": 1626619168,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "LianderNetManagement",
      "offset": 37
    }
  ],
  "location": "GetFirmwareVersion.an_organisation_with_OrganisationID(String)"
});
formatter.result({
  "duration": 183917,
  "status": "passed"
});
formatter.match({
  "location": "GetFirmwareVersion.theGetFirmwareVersionRequestIsReceived()"
});
formatter.result({
  "duration": 5002167372,
  "status": "passed"
});
formatter.match({
  "location": "GetFirmwareVersion.theFirmwareVersionResultShouldBeReturned()"
});
formatter.result({
  "duration": 17327839832,
  "status": "passed"
});
});